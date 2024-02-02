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

import julienrf.json.derived
import models.dateofbirth.DateOfBirth
import models.ecospend.BankDescription
import models.{IdentityVerificationResponse, NationalInsuranceNumber, P800Reference}
import play.api.libs.json.OFormat
import play.api.mvc.Request
import util.Errors

import java.time.{Clock, Instant}

sealed trait HasFinished
object HasFinished {

  def hasFinished(hasFinished: HasFinished): Boolean = hasFinished match {
    case No                 => false
    case YesSucceeded       => true
    case RefundNotSubmitted => true
    case LockedOut          => true
  }

  def isInProgress(hasFinished: HasFinished): Boolean = !HasFinished.hasFinished(hasFinished)

  case object No extends HasFinished
  case object YesSucceeded extends HasFinished
  case object RefundNotSubmitted extends HasFinished
  case object LockedOut extends HasFinished

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val format: OFormat[HasFinished] = derived.oformat()
}

/**
 * This is internal representation of the journey. Don't use it.
 */
final case class Journey(
    _id:                          JourneyId,
    createdAt:                    Instant,
    hasFinished:                  HasFinished,
    journeyType:                  Option[JourneyType],
    p800Reference:                Option[P800Reference],
    nationalInsuranceNumber:      Option[NationalInsuranceNumber],
    isChanging:                   Boolean, //flag which is set on change check-your-answers page when user clicks "change" link
    dateOfBirth:                  Option[DateOfBirth],
    identityVerificationResponse: Option[IdentityVerificationResponse], //reset this field upon changes of dependant fields
    bankDescription:              Option[BankDescription]
) {

  /* derived stuff: */
  def id: JourneyId = _id

  def journeyId: JourneyId = _id

  def getJourneyType(implicit request: Request[_]): JourneyType = journeyType.getOrElse(Errors.throwServerErrorException(s"Expected 'journeyType' to be defined but it was None [${journeyId.toString}] "))

  def getP800Reference(implicit request: Request[_]): P800Reference = p800Reference.getOrElse(Errors.throwServerErrorException(s"Expected 'p800Reference' to be defined but it was None [${journeyId.toString}] "))

  def getDateOfBirth(implicit request: Request[_]): DateOfBirth = dateOfBirth.getOrElse(Errors.throwServerErrorException(s"Expected 'dateOfBirth' to be defined but it was None [${journeyId.toString}] "))

  def getNationalInsuranceNumber(implicit request: Request[_]): NationalInsuranceNumber = nationalInsuranceNumber.getOrElse(Errors.throwServerErrorException(s"Expected 'nationalInsuranceNumber' to be defined but it was None [${journeyId.toString}] "))

  def getIdentityVerificationResponse(implicit request: Request[_]): IdentityVerificationResponse = identityVerificationResponse.getOrElse(Errors.throwServerErrorException(s"Expected 'identityVerificationResponse' to be defined but it was None [${journeyId.toString}] "))

  def getBankDescription(implicit request: Request[_]): BankDescription = bankDescription.getOrElse(Errors.throwServerErrorException(s"Expected 'bankDescription' to be defined but it was None [${journeyId.toString}] "))

  def isIdentityVerified: Boolean = identityVerificationResponse.map(_.identityVerified.value).getOrElse(false)

  val lastUpdated: Instant = Instant.now(Clock.systemUTC())

}

object Journey {
  implicit val format: OFormat[Journey] = JourneyFormat.format

  def deriveRedirectByJourneyType[A](journeyType: JourneyType, chequeJourneyRedirect: A, bankJourneyRedirect: A): A = journeyType match {
    case JourneyType.Cheque       => chequeJourneyRedirect
    case JourneyType.BankTransfer => bankJourneyRedirect
  }
}
