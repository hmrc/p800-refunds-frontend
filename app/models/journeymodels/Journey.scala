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

import models.dateofbirth.DateOfBirth
import models.ecospend.BankDescription
import models.ecospend.consent.BankConsentResponse
import models.ecospend.account.BankAccountSummary
import models.p800externalapi.EventValue
import models.{AmountInPence, Nino, P800Reference}
import nps.models.{ReferenceCheckResult, TraceIndividualResponse}
import play.api.libs.json.OFormat
import play.api.mvc.Request
import util.Errors

import java.time.{Clock, Instant}

final case class Journey(
    _id:           JourneyId,
    createdAt:     Instant,
    hasFinished:   HasFinished,
    journeyType:   Option[JourneyType],
    p800Reference: Option[P800Reference],
    nino:          Option[Nino],
    isChanging:    IsChanging,
    dateOfBirth:   Option[DateOfBirth],
    // below, API Responses only
    referenceCheckResult:    Option[ReferenceCheckResult], //reset this field upon changes of dependant fields
    traceIndividualResponse: Option[TraceIndividualResponse], //reset this field upon changes of dependant fields
    bankDescription:         Option[BankDescription],
    bankConsentResponse:     Option[BankConsentResponse],
    bankAccountSummary:      Option[BankAccountSummary],
    isValidEventValue:       Option[EventValue]
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

  def update(p800Reference: P800Reference): Journey =
    this
      .resetAllApiResponses()
      .copy(
        p800Reference = Some(p800Reference)
      )

  def update(referenceCheckResult: ReferenceCheckResult): Journey =
    this
      .resetAllApiResponses()
      .copy(
        referenceCheckResult    = Some(referenceCheckResult),
        traceIndividualResponse = None
      )

  def update(maybeTraceIndividualResponse: Option[TraceIndividualResponse]): Journey =
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

  def update(bankAccountSummary: BankAccountSummary): Journey =
    this
      .copy(
        bankAccountSummary = Some(bankAccountSummary)
      //TODO: reset other API responses populated bankAccountSummary
      )

  def update(eventValue: EventValue): Journey = this.copy(isValidEventValue = Some(eventValue))

  def update(hasFinished: HasFinished): Journey = this.copy(hasFinished = hasFinished)

  private def resetAllApiResponses(): Journey = this.copy(
    referenceCheckResult    = None,
    traceIndividualResponse = None,
    bankDescription         = None,
    bankConsentResponse     = None,
    bankAccountSummary      = None,
    isValidEventValue       = None
  )

  /* derived stuff: */
  def id: JourneyId = _id

  def journeyId: JourneyId = _id

  def getJourneyType(implicit request: Request[_]): JourneyType = journeyType.getOrElse(Errors.throwServerErrorException(s"Expected 'journeyType' to be defined but it was None [${journeyId.toString}] "))

  def getP800Reference(implicit request: Request[_]): P800Reference = p800Reference.getOrElse(Errors.throwServerErrorException(s"Expected 'p800Reference' to be defined but it was None [${journeyId.toString}] "))

  def getDateOfBirth(implicit request: Request[_]): DateOfBirth = dateOfBirth.getOrElse(Errors.throwServerErrorException(s"Expected 'dateOfBirth' to be defined but it was None [${journeyId.toString}] "))

  def getNino(implicit request: Request[_]): Nino = nino.getOrElse(Errors.throwServerErrorException(s"Expected 'nationalInsuranceNumber' to be defined but it was None [${journeyId.toString}] "))

  def getBankDescription(implicit request: Request[_]): BankDescription = bankDescription.getOrElse(Errors.throwServerErrorException(s"Expected 'bankDescription' to be defined but it was None [${journeyId.toString}] "))

  def getBankConsent(implicit request: Request[_]): BankConsentResponse = bankConsentResponse.getOrElse(Errors.throwBadRequestException("Expected 'bankConsent' to be defined but it was None [${journeyId.toString}] "))

  def getBankAccountSummary(implicit request: Request[_]): BankAccountSummary = bankAccountSummary.getOrElse(Errors.throwBadRequestException("Expected 'bankAccountSummary' to be defined but it was None [${journeyId.toString}] "))

  def getReferenceCheckResult(implicit request: Request[_]): ReferenceCheckResult = referenceCheckResult.getOrElse(Errors.throwServerErrorException(s"Expected 'referenceCheckResult' to be defined but it was None [${journeyId.toString}] "))

  def getP800ReferenceChecked(implicit request: Request[_]): ReferenceCheckResult.P800ReferenceChecked = getReferenceCheckResult match {
    case r: ReferenceCheckResult.P800ReferenceChecked => r
    case r => Errors.throwServerErrorException(s"Expected 'referenceCheckResult' to be 'P800ReferenceChecked' to be defined but it was '${r.toString}' [${journeyId.toString}] ")
  }

  def getAmount(implicit request: Request[_]): AmountInPence = AmountInPence(getP800ReferenceChecked.paymentAmount)

  def isIdentityVerified: Boolean = referenceCheckResult.exists {
    case _: ReferenceCheckResult.P800ReferenceChecked => true
    case ReferenceCheckResult.RefundAlreadyTaken      => false
    case ReferenceCheckResult.ReferenceDidntMatchNino => false
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
