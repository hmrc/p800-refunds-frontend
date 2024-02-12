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

final case class Journey(
    _id:                  JourneyId,
    createdAt:            Instant,
    hasFinished:          HasFinished,
    journeyType:          Option[JourneyType],
    p800Reference:        Option[P800Reference],
    nino:                 Option[Nino],
    isChanging:           IsChanging,
    dateOfBirth:          Option[DateOfBirth],
    referenceCheckResult: Option[ReferenceCheckResult], //reset this field upon changes of dependant fields
    bankDescription:      Option[BankDescription],
    bankConsentResponse:  Option[BankConsentResponse]
) {

  /*
   * Convenient updates methods which automatically reset relevant API responses.
   */
  def update(nino: Nino): Journey =
    this
      .resetCheckReferenceApiResponses()
      .copy(
        nino = Some(nino)
      )

  def update(dateOfBirth: DateOfBirth): Journey =
    this
      .resetCheckReferenceApiResponses()
      .copy(
        dateOfBirth = Some(dateOfBirth)
      )

  def update(p800Reference: P800Reference): Journey =
    this
      .resetCheckReferenceApiResponses()
      .copy(
        p800Reference = Some(p800Reference)
      )

  def update(referenceCheckResult: ReferenceCheckResult): Journey =
    this
      .resetCheckReferenceApiResponses()
      .copy(
        referenceCheckResult = Some(referenceCheckResult)
      //TODO reset individual trace API and other API results which happen after that call
      )

  def update(bankDescription: BankDescription): Journey =
    this
      .copy(
        bankDescription     = Some(bankDescription),
        bankConsentResponse = None
      //TODO: reset other API responses populated bankdDescription
      )

  def update(bankConsentResponse: BankConsentResponse): Journey =
    this
      .copy(
        bankConsentResponse = Some(bankConsentResponse)
      //TODO: reset other API responses populated bankConsentResponse
      )

  private def resetCheckReferenceApiResponses(): Journey = this.copy(
    referenceCheckResult = None,
    bankDescription      = None,
    bankConsentResponse  = None
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
