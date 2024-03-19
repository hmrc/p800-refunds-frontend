/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers

import action.{Actions, JourneyRequest}
import connectors.P800RefundsExternalApiConnector
import edh._
import models.ecospend.account.BankAccountSummary
import models.ecospend.consent.{BankReferenceId, ConsentId, ConsentStatus}
import models.journeymodels._
import models.p800externalapi.EventValue
import play.api.mvc._
import services.{EcospendService, JourneyService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import util.SafeEquals.EqualsOps
import util.{Errors, JourneyLogger}
import views.Views

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class WeAreVerifyingYourBankAccountController @Inject() (
    actions:                         Actions,
    ecospendService:                 EcospendService,
    edhConnector:                    EdhConnector,
    journeyService:                  JourneyService,
    mcc:                             MessagesControllerComponents,
    p800RefundsExternalApiConnector: P800RefundsExternalApiConnector,
    views:                           Views
)(implicit ec: ExecutionContext) extends FrontendController(mcc) {

  def get(status: Option[ConsentStatus], consent_id: Option[ConsentId], bank_reference_id: Option[BankReferenceId]): Action[AnyContent] = actions.journeyInProgress.async { implicit request: JourneyRequest[_] =>
    val journey: Journey = request.journey
    //sanity checks
    Errors.require(journey.getJourneyType === JourneyType.BankTransfer, "This endpoint supports only BankTransfer journey")
    consent_id.fold(Errors.throwBadRequestException("This endpoint requires a valid consent_id query parameter")) { consentId: ConsentId =>
      Errors.require(journey.getBankConsent.id === consentId, "The consent_id supplied via the query parameter must match that stored in the journey. This should be investigated")
    }
    bank_reference_id.fold(Errors.throwBadRequestException("This endpoint requires a valid bank_reference_id query parameter")) { bankReferenceId: BankReferenceId =>
      Errors.require(journey.getBankConsent.bankReferenceId === bankReferenceId, "The bank_reference_id supplied via the query parameter must match that stored in the journey. This should be investigated")
    }

      def next(journey: Journey, isValidEventValue: EventValue, getBankDetailsRiskResultResponse: GetBankDetailsRiskResultResponse): Future[(Result, Journey)] = isValidEventValue match {
        case EventValue.NotReceived => Future.successful((Ok(views.weAreVerifyingYourBankAccountPage(status, consent_id, bank_reference_id)), journey))
        case EventValue.NotValid => Future.successful {
          JourneyLogger.info(s"Account assessment failed.")
          (Redirect(routes.RefundRequestNotSubmittedController.get), journey.update(HasFinished.YesRefundNotSubmitted))
        }
        case EventValue.Valid =>
          JourneyLogger.info(s"Account assessment succeeded.")
          getBankDetailsRiskResultResponse.overallRiskResult.nextAction match {
            case NextAction.DoNotPay => handleDoNotPay(journey)
            case NextAction.Pay      => handlePay(journey)
          }
      }

      def handleDoNotPay(journey: Journey): Future[(Result, Journey)] = Future.successful {
        // TODO: If API#1133 or API#JF72745 fails, call (JF72755) Suspend Overpayment
        // TODO: If API#1133 or API#JF72745 fails, call API#1132 (EPID0771) Case Management Notified
        (
          Redirect(routes.RefundRequestNotSubmittedController.get),
          journey.update(hasFinished = HasFinished.YesRefundNotSubmitted)
        )
      }

      def handlePay(journey: Journey): Future[(Result, Journey)] = {
        // TODO: (Myles) Call API#JF72745 Claim Overpayment
        Future.successful((
          Redirect(routes.RequestReceivedController.getBankTransfer),
          journey.update(hasFinished = HasFinished.YesSucceeded)
        ))
      }

    for {
      isValidEventValue: EventValue <- obtainIsValid(journey)
      bankAccountSummary: BankAccountSummary <- obtainBankAccountSummary(journey)
      getBankDetailsRiskResultResponse: GetBankDetailsRiskResultResponse <- obtainGetBankDetailsRiskResultResponse(journey, bankAccountSummary)
      //TODO: Fuzzy Name Matching
      newJourney = journey.update(isValidEventValue, bankAccountSummary, getBankDetailsRiskResultResponse)
      (result, newJourney) <- next(newJourney, isValidEventValue, getBankDetailsRiskResultResponse)
      _ <- journeyService.upsert(newJourney)
    } yield result
  }

  private def obtainIsValid(journey: Journey)(implicit request: Request[_]): Future[EventValue] = {
    JourneyLogger.debug("Obtaining EventValue ...")
    journey.isValidEventValue match {
      case None | Some(EventValue.NotReceived) =>
        p800RefundsExternalApiConnector.isValid(journey.getBankConsent.id).map { ev =>
          JourneyLogger.info(s"Received EventValue from backend: [${ev.toString}]")
          ev
        }
      case Some(ev) => Future.successful {
        JourneyLogger.info(s"Got EventValue from journey: [${ev.toString}]")
        ev
      }
    }
  }

  private def obtainBankAccountSummary(journey: Journey)(implicit request: RequestHeader): Future[BankAccountSummary] = {
    JourneyLogger.debug("Obtaining BankAccountSummary ...")
    journey
      .bankAccountSummary
      .map(Future.successful)
      .getOrElse(ecospendService.getAccountSummary(journey))
  }

  //See https://confluence.tools.tax.service.gov.uk/pages/viewpage.action?pageId=770835142 for data mapping
  private def obtainGetBankDetailsRiskResultResponse(journey: Journey, bankAccountSummary: BankAccountSummary)(implicit request: Request[_]): Future[GetBankDetailsRiskResultResponse] = {

    lazy val claimId: ClaimId = ClaimId.next()
    lazy val getBankDetailsRiskResultRequest: GetBankDetailsRiskResultRequest = {

      val r = GetBankDetailsRiskResultRequest(
        header       = Header(
          transactionID = claimId.asTransactionId,
          requesterID   = RequesterID("Repayment Service"),
          serviceID     = ServiceID("P800")
        ),
        paymentData  = Some(PaymentData(
          paymentAmount = Some(journey.getAmount.inPounds), //TODO: according to analysis this comes form repayment status API, but we don't call it.
          paymentNumber = Some(journey.getP800Reference.sanitiseReference.value.toInt) //TODO: according to analysis this comes form repayment status API, but we don't call it.
        )),
        employerData = None, //TODO: according to the analysis: confirm this is not needed

        riskData                     = List(RiskDataObject(
          personType  = PersonType.Customer,
          person      = Some(Person(
            //TODO: according to the analysis all those fields come from the CitisenDetails API, which we don't call
            surname                 = Surname(journey.getTraceIndividualResponse.surname),
            firstForenameOrInitial  = journey.getTraceIndividualResponse.firstForename.map(FirstForenameOrInitial.apply),
            secondForenameOrInitial = journey.getTraceIndividualResponse.secondForename.map(SecondForenameOrInitial.apply),
            nino                    = journey.getNino,
            dateOfBirth             = DateOfBirth(journey.getDateOfBirth.`formatYYYY-MM-DD`),
            title                   = journey.getTraceIndividualResponse.title.map(Title.apply),
            address                 = None
          )),
          bankDetails = Some(BankDetails(
            bankAccountNumber     = Some(BankAccountNumber(bankAccountSummary.accountIdentification.sortCode)),
            bankSortCode          = Some(BankSortCode(bankAccountSummary.accountIdentification.bankAccountNumber)),
            bankAccountName       = Some(BankAccountName(bankAccountSummary.displayName.value)),
            buildingSocietyRef    = None, //TODO: this has not been analysed
            designatedAccountFlag = None, //TODO: according to the analysis: confirm this is not needed, Collected from user Journey, Is the same value as personType
            currency              = None //TODO: according to the analysis: confirm this is not needed, Always "GBP"
          ))
        )),
        bankValidationResults        = None, //as agreed we don't call BARS to obtain those data and won't pass anything here
        transactionMonitoringResults = None //TODO: this has not been analysed so don't know what needs to passed here
      )
      r.validate.fold(())(validationProblem =>
        JourneyLogger.warn(s"Internal validation of GetBankDetailsRiskResultRequest failed: [$validationProblem]"))
      r
    }

    journey
      .getBankDetailsRiskResultResponse
      .map(Future.successful)
      .getOrElse(edhConnector.getBankDetailsRiskResult(claimId, getBankDetailsRiskResultRequest))
  }

}
