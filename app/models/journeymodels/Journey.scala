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
import models.{AmountInPence, Nino, P800Reference}
import nps.models.ReferenceCheckResult
import play.api.libs.json.OFormat
import play.api.mvc.Request
import util.Errors

import java.time.{Clock, Instant}

class Journey(val internal: JourneyInternal) {

  /* proxy methods for convenience*/
  def _id: JourneyId = internal._id

  def createdAt: Instant = internal.createdAt

  def hasFinished: HasFinished = internal.hasFinished

  def journeyType: Option[JourneyType] = internal.journeyType

  def p800Reference: Option[P800Reference] = internal.p800Reference

  def nino: Option[Nino] = internal.nino

  def isChanging: Boolean = internal.isChanging

  def dateOfBirth: Option[DateOfBirth] = internal.dateOfBirth

  def referenceCheckResult: Option[ReferenceCheckResult] = internal.referenceCheckResult

  def bankDescription: Option[BankDescription] = internal.bankDescription

  def bankConsent: Option[BankConsentResponse] = internal.bankConsentResponse

  /* derived stuff: */
  def id: JourneyId = _id

  def journeyId: JourneyId = _id

  def getJourneyType(implicit request: Request[_]): JourneyType = journeyType.getOrElse(Errors.throwServerErrorException(s"Expected 'journeyType' to be defined but it was None [${journeyId.toString}] "))

  def getP800Reference(implicit request: Request[_]): P800Reference = p800Reference.getOrElse(Errors.throwServerErrorException(s"Expected 'p800Reference' to be defined but it was None [${journeyId.toString}] "))

  def getDateOfBirth(implicit request: Request[_]): DateOfBirth = dateOfBirth.getOrElse(Errors.throwServerErrorException(s"Expected 'dateOfBirth' to be defined but it was None [${journeyId.toString}] "))

  def getNino(implicit request: Request[_]): Nino = nino.getOrElse(Errors.throwServerErrorException(s"Expected 'nationalInsuranceNumber' to be defined but it was None [${journeyId.toString}] "))

  def getBankDescription(implicit request: Request[_]): BankDescription = bankDescription.getOrElse(Errors.throwServerErrorException(s"Expected 'bankDescription' to be defined but it was None [${journeyId.toString}] "))

  def getBankConsent(implicit request: Request[_]): BankConsentResponse = bankConsent.getOrElse(Errors.throwBadRequestException("Expected 'bankConsent' to be defined but it was None [${journeyId.toString}] "))

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

  def update(hasFinished: HasFinished): Journey = new Journey(internal.copy(
    hasFinished = hasFinished
  ))

  def update(journeyType: JourneyType): Journey = new Journey(internal.copy(
    journeyType = Some(journeyType)
  ))

  def update(nino: Nino): Journey = new Journey(
    internal
      .resetApiResponses()
      .copy(
        nino = Some(nino)
      )
  )

  def update(dateOfBirth: DateOfBirth): Journey = new Journey(
    internal
      .resetApiResponses()
      .copy(
        dateOfBirth = Some(dateOfBirth)
      )
  )

  def update(isChanging: Boolean): Journey = new Journey(internal.copy(
    isChanging = isChanging
  ))

  def update(p800Reference: P800Reference): Journey = new Journey(
    internal
      .resetApiResponses()
      .copy(
        p800Reference = Some(p800Reference)
      )
  )

  def update(referenceCheckResult: ReferenceCheckResult): Journey = new Journey(internal.copy(
    referenceCheckResult = Some(referenceCheckResult)
  ))

  def update(bankDescription: BankDescription): Journey = new Journey(
    internal
      .copy(
        bankDescription     = Some(bankDescription),
        bankConsentResponse = None
      //TODO: reset other API responses populated bankdDescription
      )
  )

  def update(bankConsentResponse: BankConsentResponse): Journey = new Journey(
    internal
      .copy(
        bankConsentResponse = Some(bankConsentResponse)
      //TODO: reset other API responses populated bankConsentResponse
      )
  )

  private implicit class JourneyOps(i: JourneyInternal) {
    def resetApiResponses(): JourneyInternal = i.copy(
      referenceCheckResult = None,
      bankDescription      = None,
      bankConsentResponse  = None
    )
  }
}

object Journey {
  def deriveRedirectByJourneyType[A](journeyType: JourneyType, chequeJourneyRedirect: A, bankJourneyRedirect: A): A = journeyType match {
    case JourneyType.Cheque       => chequeJourneyRedirect
    case JourneyType.BankTransfer => bankJourneyRedirect
  }
}

final case class JourneyInternal(
    _id:                  JourneyId,
    createdAt:            Instant,
    hasFinished:          HasFinished,
    journeyType:          Option[JourneyType],
    p800Reference:        Option[P800Reference],
    nino:                 Option[Nino],
    isChanging:           Boolean, //flag which is set on change check-your-answers page when user clicks "change" link
    dateOfBirth:          Option[DateOfBirth],
    referenceCheckResult: Option[ReferenceCheckResult],
    bankDescription:      Option[BankDescription],
    bankConsentResponse:  Option[BankConsentResponse]
) {
  val lastUpdated: Instant = Instant.now(Clock.systemUTC())
}

object JourneyInternal {
  implicit val format: OFormat[JourneyInternal] = JourneyFormat.format
}
