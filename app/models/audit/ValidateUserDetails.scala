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
import models.dateofbirth.DateOfBirth
import models.journeymodels.JourneyType
import models.attemptmodels.NumberOfAttempts
import models.{AmountInPence, P800Reference, Nino}
import nps.models.{ReconciliationIdentifier, PayeNumber, TaxDistrictNumber, AssociatedPayableNumber, CustomerAccountNumber, CurrentOptimisticLock}
import play.api.libs.json.{Json, OFormat, Format}

final case class ValidateUserDetails(
    outcome:              Outcome,
    userEnteredDetails:   UserEnteredDetails,
    repaymentAmount:      Option[AmountInPence],
    repaymentInformation: Option[RepaymentInformation],
    name:                 Option[Name],
    address:              Option[Address]
) extends AuditDetail {
  val auditType: String = "ValidateUserDetails"
}

object ValidateUserDetails {
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val format: OFormat[ValidateUserDetails] = Json.format[ValidateUserDetails]
}

final case class Outcome(
    isSuccessful:             IsSuccessful,
    attemptsOnRecord:         Option[NumberOfAttempts],
    lockout:                  LockedOut,
    apiResponsibleForFailure: Option[String],
    reasons:                  Seq[String]
)

object Outcome {
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val format: OFormat[Outcome] = Json.format[Outcome]
}

final case class IsSuccessful(value: Boolean) extends AnyVal

object IsSuccessful {
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val format: Format[IsSuccessful] = Json.valueFormat[IsSuccessful]
}

final case class LockedOut(value: Boolean) extends AnyVal

object LockedOut {
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val format: Format[LockedOut] = Json.valueFormat[LockedOut]
}

final case class UserEnteredDetails(
    repaymentMethod: JourneyType,
    p800Reference:   P800Reference,
    nino:            Nino,
    dob:             Option[DateOfBirth]
)

object UserEnteredDetails {
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val format: OFormat[UserEnteredDetails] = Json.format[UserEnteredDetails]
}

final case class RepaymentInformation(
    reconciliationIdentifier: Option[ReconciliationIdentifier],
    paymentNumber:            P800Reference,
    payeNumber:               Option[PayeNumber],
    taxDistrictNumber:        Option[TaxDistrictNumber],
    associatedPayableNumber:  AssociatedPayableNumber,
    customerAccountNumber:    CustomerAccountNumber,
    currentOptimisticLock:    CurrentOptimisticLock
)

object RepaymentInformation {
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val format: OFormat[RepaymentInformation] = Json.format[RepaymentInformation]
}

final case class Name(
    title:          Option[String],
    firstForename:  Option[String],
    secondForename: Option[String],
    surname:        String
)

object Name {
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val format: OFormat[Name] = Json.format[Name]
}

final case class Address(
    addressLine1:    String,
    addressLine2:    String,
    addressPostcode: Postcode
)

object Address {
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val format: OFormat[Address] = Json.format[Address]
}

