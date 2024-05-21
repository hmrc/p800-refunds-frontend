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

package models.audit

import edh.Postcode
import models.attemptmodels.NumberOfAttempts
import models.dateofbirth.DateOfBirth
import models.journeymodels.JourneyType
import models.{P800Reference, Nino}
import play.api.libs.json.{Json, OWrites, Writes, JsString}

final case class ValidateUserDetails(
    outcome:              Outcome,
    userEnteredDetails:   UserEnteredDetails,
    repaymentAmount:      Option[BigDecimal],
    repaymentInformation: Option[RepaymentInformation],
    name:                 Option[Name],
    address:              Option[Address]
) extends AuditDetail {
  val auditType: String = "ValidateUserDetails"
}

object ValidateUserDetails {
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val writes: OWrites[ValidateUserDetails] = Json.writes[ValidateUserDetails]
}

final case class Outcome(
    isSuccessful:             IsSuccessful,
    attemptsOnRecord:         Option[NumberOfAttempts],
    lockout:                  LockedOut,
    apiResponsibleForFailure: Option[ApiResponsibleForFailure],
    reasons:                  Seq[String]
)

object Outcome {
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val writes: OWrites[Outcome] = Json.writes[Outcome]
}

final case class LockedOut(value: Boolean) extends AnyVal

object LockedOut {
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val writes: Writes[LockedOut] = Json.valueWrites[LockedOut]
}

sealed trait ApiResponsibleForFailure

object ApiResponsibleForFailure {
  case object P800ReferenceCheck extends ApiResponsibleForFailure
  case object TraceIndividual extends ApiResponsibleForFailure

  implicit val writes: Writes[ApiResponsibleForFailure] = Writes(_ match {
    case P800ReferenceCheck => JsString("p800 reference check")
    case TraceIndividual    => JsString("p800 reference check")
  })
}

final case class UserEnteredDetails(
    repaymentMethod: JourneyType,
    p800Reference:   P800Reference,
    nino:            Nino,
    dob:             Option[DateOfBirth]
)

object UserEnteredDetails {
  implicit val journeyTypeWrites: Writes[JourneyType] = Writes {
    case JourneyType.BankTransfer => JsString("bank")
    case JourneyType.Cheque       => JsString("cheque")
  }

  implicit val dateOfBirthWrites: Writes[DateOfBirth] = Writes(dateOfBirth => JsString(dateOfBirth.`formatYYYY-MM-DD`))

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val writes: OWrites[UserEnteredDetails] = Json.writes[UserEnteredDetails]
}

final case class Name(
    title:          Option[String],
    firstForename:  Option[String],
    secondForename: Option[String],
    surname:        Option[String]
)

object Name {
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val writes: OWrites[Name] = Json.writes[Name]
}

final case class Address(
    addressLine1:    Option[String],
    addressLine2:    Option[String],
    addressPostcode: Option[Postcode]
)

object Address {
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val writes: OWrites[Address] = Json.writes[Address]
}

