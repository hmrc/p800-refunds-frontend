/*
 * Copyright 2024 HM Revenue & Customs
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

package services

import connectors.P800RefundsBackendConnector
import edh._
import models.audit.{BankActionsOutcome, IsSuccessful}
import models.ecospend.account.BankAccountSummary
import models.journeymodels.Journey
import models.p800externalapi.EventValue
import play.api.mvc._
import requests.RequestSupport
import uk.gov.hmrc.http.{HttpException, UpstreamErrorResponse}
import util.JourneyLogger

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GetBankDetailsRiskResultService @Inject() (
    auditService:                AuditService,
    p800RefundsBackendConnector: P800RefundsBackendConnector,
    claimIdGenerator:            ClaimIdGenerator
)(implicit ec: ExecutionContext) {

  import RequestSupport._

  def getBankDetailsRiskResult(journey: Journey, bankAccountSummary: BankAccountSummary, eventValue: EventValue)(implicit reqest: Request[_]): Future[GetBankDetailsRiskResultResponse] =
    obtainGetBankDetailsRiskResultResponse(journey, bankAccountSummary)
      .recover {
        case err @ (_: UpstreamErrorResponse | _: HttpException) =>
          auditService.auditBankClaimAttempt(
            journey        = journey,
            actionsOutcome = BankActionsOutcome(
              getAccountDetailsIsSuccessful  = IsSuccessful.yes,
              ecospendFraudCheckIsSuccessful = eventValue match {
                case EventValue.Valid       => Some(IsSuccessful.yes)
                case EventValue.NotValid    => Some(IsSuccessful.no)
                case EventValue.NotReceived => None
              },
              hmrcFraudCheckIsSuccessful     = Some(IsSuccessful.no)
            ),
            failureReasons = Some(Seq(err.getMessage))
          )
          throw err
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
          paymentNumber = Some(journey.getP800Reference.sanitiseReference.value) //TODO: according to analysis this comes form repayment status API, but we don't call it.
        )),
        employerData = None, //TODO: according to the analysis: confirm this is not needed

        riskData                     = List(RiskDataObject(
          personType  = PersonType.Customer,
          person      = Some(Person(
            //TODO: according to the analysis all those fields come from the CitisenDetails API, which we don't call
            surname                 = journey.getTraceIndividualResponse.surname.map(Surname.apply),
            firstForenameOrInitial  = journey.getTraceIndividualResponse.firstForename.map(FirstForenameOrInitial.apply),
            secondForenameOrInitial = journey.getTraceIndividualResponse.secondForename.map(SecondForenameOrInitial.apply),
            nino                    = journey.getNino,
            dateOfBirth             = DateOfBirth(journey.getDateOfBirth.`formatYYYY-MM-DD`),
            title                   = journey.getTraceIndividualResponse.title.map(Title.apply),
            address                 = None
          )),
          bankDetails = Some(BankDetails(
            bankAccountNumber     = Some(bankAccountSummary.getAccountIdentification.asBankAccountNumber),
            bankSortCode          = Some(bankAccountSummary.getAccountIdentification.asBankSortCode),
            bankAccountName       = None, // Note: Check the spec as this field is limited to 28 characters & has a regex
            buildingSocietyRef    = None,
            designatedAccountFlag = None,
            currency              = None
          ))
        )),
        bankValidationResults        = None, // NOTE: As we have agreed to not call BARS we can't pass anything here
        transactionMonitoringResults = None
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

}
