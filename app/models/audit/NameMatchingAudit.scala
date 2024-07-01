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

import models.ecospend.{BankId, BankFriendlyName, BankName}
import play.api.libs.json.{Json, OWrites}

final case class NameMatchingAudit(
    outcome:             NameMatchOutcome,
    rawNpsName:          RawNpsName,
    rawBankName:         Option[String],
    transformedNpsName:  Option[String],
    transformedBankName: Option[String],
    chosenBank:          ChosenBank,
    levenshteinDistance: Option[Int]      = None,
    partiesArrayUsed:    Boolean          = true
) extends AuditDetail {
  override val auditType: String = "FuzzyNameMatchingEvent"
}

object NameMatchingAudit {
  implicit val writes: OWrites[NameMatchingAudit] = Json.writes[NameMatchingAudit]
}

final case class RawNpsName(
    firstForename:  Option[String],
    secondForename: Option[String],
    surname:        Option[String]
)

object RawNpsName {
  implicit val writes: OWrites[RawNpsName] = Json.writes[RawNpsName]
}

final case class NameMatchOutcome(isSuccessful: Boolean, category: String)

object NameMatchOutcome {
  implicit val writes: OWrites[NameMatchOutcome] = Json.writes[NameMatchOutcome]
}

final case class ChosenBank(
    bankId:       BankId,
    name:         BankName,
    friendlyName: BankFriendlyName
)

object ChosenBank {
  implicit val writes: OWrites[ChosenBank] = Json.writes[ChosenBank]
}
