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

package models.journeymodels

import edh.GetBankDetailsRiskResultResponse
import models.dateofbirth.DateOfBirth
import models.ecospend.BankDescription
import models.ecospend.account.BankAccountSummary
import models.ecospend.consent.BankConsentResponse
import models.namematching.NameMatchingResult
import models.p800externalapi.EventValue
import models.{AmountInPence, CorrelationId, Nino, UserEnteredP800Reference}
import nps.models.TraceIndividualResponse.TracedIndividual
import nps.models.ValidateReferenceResult
import play.api.libs.json.OFormat
import play.api.mvc.RequestHeader
import util.Errors

import java.time.{Clock, Instant}

final case class Journey(
    createdAt:          Instant,
    _id:                JourneyId,
    correlationId:      CorrelationId,
    hasFinished:        HasFinished,
    journeyType:        Option[JourneyType],
    p800Reference:      Option[UserEnteredP800Reference],
    nino:               Option[Nino],
    isChanging:         IsChanging,
    dateOfBirth:        Option[DateOfBirth],
    nameMatchingResult: Option[NameMatchingResult],
    // below, API Responses only
    referenceCheckResult:          Option[ValidateReferenceResult], //reset this field upon changes of dependant fields
    traceIndividualResponse:       Option[TracedIndividual], //reset this field upon changes of dependant fields
    bankDescription:               Option[BankDescription],
    bankConsentResponse:           Option[BankConsentResponse],
    bankAccountSummary:            Option[BankAccountSummary],
    isValidEventValue:             Option[EventValue],
    bankDetailsRiskResultResponse: Option[GetBankDetailsRiskResultResponse]
) {

  /*
   * Convenient updates methods which automatically reset relevant API responses.
   */
  def update(nino: Nino): Journey =
    this
      .resetAllApiResponses()
      .copy(
        nino = Some(nino)
      )

  def update(dateOfBirth: DateOfBirth): Journey =
    this
      .resetAllApiResponses()
      .copy(
        dateOfBirth = Some(dateOfBirth)
      )

  def update(p800Reference: UserEnteredP800Reference): Journey =
    this
      .resetAllApiResponses()
      .copy(
        p800Reference = Some(p800Reference)
      )

  def update(referenceCheckResult: ValidateReferenceResult): Journey =
    this
      .resetAllApiResponses()
      .copy(
        referenceCheckResult    = Some(referenceCheckResult),
        traceIndividualResponse = None
      )

  def update(maybeTraceIndividualResponse: Option[TracedIndividual]): Journey =
    this
      .copy(
        traceIndividualResponse = maybeTraceIndividualResponse,
        bankDescription         = None,
        bankConsentResponse     = None,
        bankAccountSummary      = None
      )

  def update(bankDescription: BankDescription): Journey =
    this
      .copy(
        bankDescription     = Some(bankDescription),
        bankConsentResponse = None,
        bankAccountSummary  = None
      //TODO: reset other API responses populated bankDescription
      )

  def update(bankConsentResponse: BankConsentResponse): Journey =
    this
      .copy(
        bankConsentResponse = Some(bankConsentResponse),
        bankAccountSummary  = None
      //TODO: reset other API responses populated bankConsentResponse
      )

  def update(
      eventValue:                    EventValue,
      bankAccountSummary:            BankAccountSummary,
      bankDetailsRiskResultResponse: GetBankDetailsRiskResultResponse,
      nameMatchingResult:            NameMatchingResult
  ): Journey = copy(
    bankAccountSummary            = Some(bankAccountSummary),
    bankDetailsRiskResultResponse = Some(bankDetailsRiskResultResponse),
    isValidEventValue             = Some(eventValue),
    nameMatchingResult            = Some(nameMatchingResult)
  )

  def update(hasFinished: HasFinished): Journey = this.copy(hasFinished = hasFinished)

  private def resetAllApiResponses(): Journey = this.copy(
    referenceCheckResult          = None,
    traceIndividualResponse       = None,
    bankDescription               = None,
    bankConsentResponse           = None,
    bankAccountSummary            = None,
    isValidEventValue             = None,
    bankDetailsRiskResultResponse = None
  )

  /* derived stuff: */
  def id: JourneyId = _id

  def journeyId: JourneyId = _id

  def getJourneyType(implicit request: RequestHeader): JourneyType = journeyType.getOrElse(Errors.throwServerErrorException(s"Expected 'journeyType' to be defined but it was None [${journeyId.toString}] "))

  def getP800Reference(implicit request: RequestHeader): UserEnteredP800Reference = p800Reference.getOrElse(Errors.throwServerErrorException(s"Expected 'p800Reference' to be defined but it was None [${journeyId.toString}] "))

  def getDateOfBirth(implicit request: RequestHeader): DateOfBirth = dateOfBirth.getOrElse(Errors.throwServerErrorException(s"Expected 'dateOfBirth' to be defined but it was None [${journeyId.toString}] "))

  def getNino(implicit request: RequestHeader): Nino = nino.getOrElse(Errors.throwServerErrorException(s"Expected 'nationalInsuranceNumber' to be defined but it was None [${journeyId.toString}] "))

  def getBankDescription(implicit request: RequestHeader): BankDescription = bankDescription.getOrElse(Errors.throwServerErrorException(s"Expected 'bankDescription' to be defined but it was None [${journeyId.toString}] "))

  def getBankConsent(implicit request: RequestHeader): BankConsentResponse = bankConsentResponse.getOrElse(Errors.throwBadRequestException(s"Expected 'bankConsent' to be defined but it was None [${journeyId.toString}] "))

  def getBankAccountSummary(implicit request: RequestHeader): BankAccountSummary = bankAccountSummary.getOrElse(Errors.throwBadRequestException(s"Expected 'bankAccountSummary' to be defined but it was None [${journeyId.toString}] "))

  def getTraceIndividualResponse(implicit request: RequestHeader): TracedIndividual = traceIndividualResponse.getOrElse(Errors.throwServerErrorException(s"Expected 'traceIndividualResponse' to be defined but it was None [${journeyId.toString}] "))

  def getReferenceCheckResult(implicit request: RequestHeader): ValidateReferenceResult = referenceCheckResult.getOrElse(Errors.throwServerErrorException(s"Expected 'referenceCheckResult' to be defined but it was None [${journeyId.toString}] "))

  def getP800ReferenceChecked(implicit request: RequestHeader): ValidateReferenceResult.P800ReferenceChecked = getReferenceCheckResult match {
    case r: ValidateReferenceResult.P800ReferenceChecked => r
    case r => Errors.throwServerErrorException(s"Expected 'referenceCheckResult' to be 'P800ReferenceChecked' to be defined but it was '${r.toString}' [${journeyId.toString}] ")
  }

  def getBankDetailsRiskResultResponse(implicit request: RequestHeader): GetBankDetailsRiskResultResponse = bankDetailsRiskResultResponse.getOrElse(Errors.throwServerErrorException(s"Expected 'getBankDetailsRiskResultResponse' to be defined but it was None [${journeyId.toString}]"))

  def getAmount(implicit request: RequestHeader): AmountInPence = AmountInPence(getP800ReferenceChecked.paymentAmount)

  def isIdentityVerified: Boolean = referenceCheckResult.exists {
    case _: ValidateReferenceResult.P800ReferenceChecked => true
    case ValidateReferenceResult.RefundAlreadyTaken      => false
    case ValidateReferenceResult.ReferenceDidntMatchNino => false
  }

  val lastUpdated: Instant = Instant.now(Clock.systemUTC())

}

object Journey {
  implicit val format: OFormat[Journey] = JourneyFormat.format

  def deriveRedirectByJourneyType[A](journeyType: JourneyType, chequeJourneyRedirect: A, bankJourneyRedirect: A): A = journeyType match {
    case JourneyType.Cheque       => chequeJourneyRedirect
    case JourneyType.BankTransfer => bankJourneyRedirect
  }
}
