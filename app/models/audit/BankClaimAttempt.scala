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

import models.dateofbirth.DateOfBirth
import models.ecospend.BankFriendlyName
import models.journeymodels.JourneyType
import models.{AmountInPence, P800Reference, Nino}
import play.api.libs.json.{Json, OWrites, Writes, JsString}

final case class BankClaimAttempt(
    outcome:              BankClaimOutcome,
    userEnteredDetails:   BankClaimUserEnteredDetails,
    repaymentAmount:      Option[AmountInPence],
    repaymentInformation: Option[RepaymentInformation],
    name:                 Option[Name],
    address:              Option[Address]
) extends AuditDetail {
  val auditType: String = "BankClaimAttemptMade"
}

object BankClaimAttempt {
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val writes: OWrites[BankClaimAttempt] = Json.writes[BankClaimAttempt]
}

final case class BankClaimOutcome(
    isSuccessful:   IsSuccessful,
    actionsOutcome: BankActionsOutcome,
    failureReasons: Option[Seq[String]]
)

object BankClaimOutcome {
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val writes: OWrites[BankClaimOutcome] = Json.writes[BankClaimOutcome]
}

final case class BankActionsOutcome(
    ecospendFraudCheckIsSuccessful: Option[IsSuccessful] = None,
    fuzzyNameMatchingIsSuccessful:  Option[IsSuccessful] = None,
    hmrcFraudCheckIsSuccessful:     Option[IsSuccessful] = None,
    claimOverpaymentIsSuccessful:   Option[IsSuccessful] = None
) {
  def overallResult: IsSuccessful = IsSuccessful(
    ecospendFraudCheckIsSuccessful.map(_.value).getOrElse(false) &&
      fuzzyNameMatchingIsSuccessful.map(_.value).getOrElse(false) &&
      hmrcFraudCheckIsSuccessful.map(_.value).getOrElse(false) &&
      claimOverpaymentIsSuccessful.map(_.value).getOrElse(false)
  )
}

object BankActionsOutcome {
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val writes: OWrites[BankActionsOutcome] = Json.writes[BankActionsOutcome]
}

final case class BankClaimUserEnteredDetails(
    repaymentMethod: JourneyType,
    chosenBank:      Option[BankFriendlyName],
    p800Reference:   P800Reference,
    nino:            Nino,
    dob:             Option[DateOfBirth]
)

object BankClaimUserEnteredDetails {
  implicit val journeyTypeWrites: Writes[JourneyType] = Writes(journeyType => journeyType match {
    case JourneyType.BankTransfer => JsString("bank")
    case JourneyType.Cheque       => JsString("cheque")
  })

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val writes: OWrites[BankClaimUserEnteredDetails] = Json.writes[BankClaimUserEnteredDetails]
}
