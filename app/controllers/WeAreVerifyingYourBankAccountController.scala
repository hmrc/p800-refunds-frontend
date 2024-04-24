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
import casemanagement._
import connectors.{P800RefundsBackendConnector, P800RefundsExternalApiConnector}
import edh._
import models.ecospend.account.BankAccountSummary
import models.ecospend.consent.{BankReferenceId, ConsentId, ConsentStatus}
import models.journeymodels._
import models.p800externalapi.EventValue
import nps.models.ValidateReferenceResult.P800ReferenceChecked
import nps.models._
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
    journeyService:                  JourneyService,
    mcc:                             MessagesControllerComponents,
    p800RefundsBackendConnector:     P800RefundsBackendConnector,
    p800RefundsExternalApiConnector: P800RefundsExternalApiConnector,
    views:                           Views,
    claimIdGenerator:                ClaimIdGenerator
)(implicit ec: ExecutionContext) extends FrontendController(mcc) {

  def get(status: Option[ConsentStatus], consent_id: Option[ConsentId], bank_reference_id: Option[BankReferenceId]): Action[AnyContent] = actions.journeyInProgress.async { implicit request: JourneyRequest[_] =>
    val journey: Journey = request.journey
    sanityChecks(consent_id, bank_reference_id, journey)

    val consentStatus: ConsentStatus =
      status.getOrElse(Errors.throwServerErrorException("This endpoint requires a status parameter."))

    for {
      isValidEventValue: EventValue <- obtainIsValid(journey)
      bankAccountSummary: BankAccountSummary <- obtainBankAccountSummary(journey)
      bankDetailsRiskResultResponse: GetBankDetailsRiskResultResponse <- obtainGetBankDetailsRiskResultResponse(journey, bankAccountSummary)
      //TODO: Fuzzy Name Matching
      newJourney = journey.update(isValidEventValue, bankAccountSummary, bankDetailsRiskResultResponse)
      (result, newJourney) <- next(newJourney, isValidEventValue, bankDetailsRiskResultResponse, bankAccountSummary, consentStatus)
      _ <- journeyService.upsert(newJourney)
    } yield result
  }

  private def next(
      journey:                       Journey,
      isValidEventValue:             EventValue,
      bankDetailsRiskResultResponse: GetBankDetailsRiskResultResponse,
      bankAccountSummary:            BankAccountSummary,
      consentStatus:                 ConsentStatus
  )(implicit request: RequestHeader): Future[(Result, Journey)] = (isValidEventValue, consentStatus) match {
    case (_, ConsentStatus.Failed) => Future.successful {
      JourneyLogger.info(s"User failed consent flow. [ConsentStatus: Failed]")
      (
        Redirect(routes.RefundRequestNotSubmittedController.get),
        journey
      )
    }
    case (_, ConsentStatus.Canceled) => Future.successful {
      JourneyLogger.info(s"User Canceled consent flow. [ConsentStatus: Canceled]")
      (
        Redirect(routes.RefundRequestNotSubmittedController.get),
        journey
      )
    }
    case (EventValue.NotReceived, ConsentStatus.Authorised) => Future.successful(
      (
        Ok(views.weAreVerifyingYourBankAccountPage(journey, Some(consentStatus), Some(journey.getBankConsent.id), Some(journey.getBankConsent.bankReferenceId))),
        journey
      )
    )
    case (EventValue.NotValid, ConsentStatus.Authorised) => Future.successful {
      JourneyLogger.info(s"Account assessment failed.")
      (
        Redirect(routes.RefundRequestNotSubmittedController.get),
        journey.update(HasFinished.YesRefundNotSubmitted)
      )
    }
    case (EventValue.Valid, ConsentStatus.Authorised) =>
      JourneyLogger.info(s"Account assessment succeeded.")
      bankDetailsRiskResultResponse.overallRiskResult.nextAction match {
        case NextAction.DoNotPay => handleDoNotPay(journey)
        case NextAction.Pay      => handlePay(journey, bankAccountSummary)
      }
    case (_, consentStatus) =>
      Errors.throwServerErrorException(s"Unexpected [consentStatus: ${consentStatus.toString}]. Expected status to be one of [Authorised, Canceled, Failed].")
  }

  private def handleDoNotPay(journey: Journey)(implicit request: RequestHeader): Future[(Result, Journey)] = {

    val suspendOverpaymentRequest = SuspendOverpaymentRequest(
      paymentNumber            = journey.getP800Reference.sanitiseReference,
      currentOptimisticLock    = journey.getP800ReferenceChecked.currentOptimisticLock,
      reconciliationIdentifier = journey.getP800ReferenceChecked.reconciliationIdentifier,
      associatedPayableNumber  = journey.getP800ReferenceChecked.associatedPayableNumber,
      payeeBankAccountNumber   = journey.getBankAccountSummary.accountIdentification.asPayeeBankAccountNumber,
      payeeBankSortCode        = journey.getBankAccountSummary.accountIdentification.asPayeeBankSortCode,
      payeeBankAccountName     = PayeeBankAccountName(journey.getBankAccountSummary.displayName.value),
      designatedPayeeAccount   = DesignatedPayeeAccount(false)
    )

    for {
      _ <- notifyCaseManagement(journey)
      _ <- p800RefundsBackendConnector.suspendOverpayment(journey.getNino, suspendOverpaymentRequest, journey.correlationId)
    } yield (
      Redirect(routes.RequestReceivedController.getBankTransfer),
      journey.update(hasFinished = HasFinished.YesSentToCaseManagement)
    )
  }

  private def handlePay(journey: Journey, bankAccountSummary: BankAccountSummary)(implicit request: RequestHeader): Future[(Result, Journey)] = {
    makeBacsRepayment(journey, bankAccountSummary).map{ _ =>
      (
        Redirect(routes.RequestReceivedController.getBankTransfer),
        journey.update(hasFinished = HasFinished.YesSucceeded)
      )
    }
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

    lazy val claimId: ClaimId = claimIdGenerator.nextClaimId()
    lazy val bankDetailsRiskResultRequest: GetBankDetailsRiskResultRequest = {

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
            bankAccountNumber     = Some(bankAccountSummary.accountIdentification.asBankAccountNumber),
            bankSortCode          = Some(bankAccountSummary.accountIdentification.asBankSortCode),
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
      .bankDetailsRiskResultResponse
      .map(Future.successful)
      .getOrElse(p800RefundsBackendConnector.getBankDetailsRiskResult(claimId, bankDetailsRiskResultRequest, journey.correlationId))
  }

  private def notifyCaseManagement(journey: Journey)(implicit requestHeader: RequestHeader): Future[Unit] = {

    val bankAccountSummary: BankAccountSummary = journey.getBankAccountSummary
    val accountNumber: BankAccountNumber = bankAccountSummary.accountIdentification.asBankAccountNumber
    val sortCode: BankSortCode = bankAccountSummary.accountIdentification.asBankSortCode
    val bankAccountName: BankAccountName = BankAccountName(bankAccountSummary.displayName.value)

    val bankDetailsRiskResult: GetBankDetailsRiskResultResponse = journey.getBankDetailsRiskResultResponse
    val clientUId: ClientUId = ClientUId(bankDetailsRiskResult.header.transactionID.value)

    val request: CaseManagementRequest = {
      val r: CaseManagementRequest = CaseManagementRequest(
        clientUId             = clientUId,
        clientSystemId        = ClientSystemId("MDTP"),
        nino                  = journey.getNino,
        bankSortCode          = sortCode,
        bankAccountNumber     = accountNumber,
        bankAccountName       = bankAccountName,
        designatedAccountFlag = 1, // TODO: Confirm what this should be
        contact               = List(
          CaseManagementContact(
            `type`    = PersonType.Customer,
            firstName = journey.getTraceIndividualResponse.firstForename.getOrElse(""),
            surname   = journey.getTraceIndividualResponse.surname,
            address   = List(
              CaseManagementAddress(
                `type`       = NPSAddress,
                addressLine1 = Some(journey.getTraceIndividualResponse.addressLine1),
                addressLine2 = Some(journey.getTraceIndividualResponse.addressLine2),
                postcode     = Some(journey.getTraceIndividualResponse.addressPostcode)
              )
            )
          )
        ),
        currency              = bankAccountSummary.currency,
        paymentAmount         = journey.getAmount.inPounds,
        overallRiskResult     = bankDetailsRiskResult.overallRiskResult.ruleScore,
        ruleResults           = Some(
          bankDetailsRiskResult.riskResults.getOrElse(List.empty).map(risk =>
            CaseManagementRuleResult(
              ruleId          = Some(risk.ruleId),
              ruleInformation = risk.ruleInformation,
              ruleScore       = Some(risk.ruleScore)
            ))
        ),
        nameMatches           = None,
        addressMatches        = None,
        accountExists         = None,
        subjectHasDeceased    = None,
        nonConsented          = None,
        reconciliationId      = Some(journey.getP800ReferenceChecked.reconciliationIdentifier),
        taxDistrictNumber     = Some(journey.getP800ReferenceChecked.taxDistrictNumber),
        payeNumber            = Some(journey.getP800ReferenceChecked.payeNumber)
      )

      r.validate.fold(())(validationProblem =>
        JourneyLogger.warn(s"Internal validation of CaseManagementRequest failed: [$validationProblem]"))
      r
    }

    p800RefundsBackendConnector
      .notifyCaseManagement(clientUId, request, journey.correlationId)
      .map(_ => ())
  }

  private def makeBacsRepayment(journey: Journey, bankAccountSummary: BankAccountSummary)(implicit request: RequestHeader): Future[Unit] = {
    val p800ReferenceCheckResult: P800ReferenceChecked = journey.getP800ReferenceChecked

    val makeBacsRepaymentRequest: MakeBacsRepaymentRequest = MakeBacsRepaymentRequest(
      paymentNumber            = journey.getP800Reference.sanitiseReference,
      currentOptimisticLock    = p800ReferenceCheckResult.currentOptimisticLock,
      reconciliationIdentifier = p800ReferenceCheckResult.reconciliationIdentifier,
      associatedPayableNumber  = p800ReferenceCheckResult.associatedPayableNumber,
      payeeBankAccountNumber   = bankAccountSummary.accountIdentification.asPayeeBankAccountNumber,
      payeeBankSortCode        = bankAccountSummary.accountIdentification.asPayeeBankSortCode,
      payeeBankAccountName     = PayeeBankAccountName(bankAccountSummary.displayName.value),
      designatedPayeeAccount   = DesignatedPayeeAccount(true)
    )

    p800RefundsBackendConnector
      .makeBacsRepayment(journey.getNino, makeBacsRepaymentRequest, journey.correlationId)
      .map(_ => ())
  }

  @inline private def sanityChecks(consent_id: Option[ConsentId], bank_reference_id: Option[BankReferenceId], journey: Journey)(implicit request: RequestHeader): Unit = {
    Errors.require(
      journey.getJourneyType === JourneyType.BankTransfer,
      "This endpoint supports only BankTransfer journey"
    )

    consent_id.fold(
      Errors.throwBadRequestException("This endpoint requires a valid 'consent_id' query parameter")
    ){ consentId: ConsentId =>
        Errors.require(
          journey.getBankConsent.id === consentId,
          "The 'consent_id' supplied via the query parameter must match that stored in the journey. This should be investigated"
        )
      }

    bank_reference_id.fold(
      Errors.throwBadRequestException("This endpoint requires a valid 'bank_reference_id' query parameter")
    ) { bankReferenceId: BankReferenceId =>
        Errors.require(
          journey.getBankConsent.bankReferenceId === bankReferenceId,
          "The 'bank_reference_id' supplied via the query parameter must match that stored in the journey. This should be investigated"
        )
      }

  }

}
