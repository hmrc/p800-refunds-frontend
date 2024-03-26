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

package models.ecospend.account

import _root_.util.SafeEquals.EqualsOps
import models.ecospend.BankId
import play.api.libs.json.{Json, Format, OFormat, JsonConfiguration, JsonNaming}
import util.CurrencyFormat

import java.util.{UUID, Currency}
import java.time.LocalDateTime

final case class BankAccountSummaryResponse(value: List[BankAccountSummary]) extends AnyVal

object BankAccountSummaryResponse {
  implicit val format: Format[BankAccountSummaryResponse] = Json.valueFormat[BankAccountSummaryResponse]
}

final case class BankAccountSummary(
    id:                    UUID,
    bankId:                BankId,
    merchantId:            Option[String],
    merchantUserId:        Option[String],
    ttype:                 BankAccountType,
    subType:               BankAccountSubType,
    currency:              Currency,
    accountFormat:         BankAccountFormat,
    accountIdentification: BankAccountIdentification,
    calculatedOwnerName:   CalculatedOwnerName,
    accountOwnerName:      BankAccountOwnerName,
    displayName:           BankAccountDisplayName,
    balance:               Double,
    lastUpdateTime:        LocalDateTime,
    parties:               List[BankAccountParty]
)

@SuppressWarnings(Array("org.wartremover.warts.Any"))
object BankAccountSummary {
  implicit val currencyFormat: Format[Currency] = CurrencyFormat.format

  // Converts the name "ttype" to the string "type" for JSON without needing to do custom Reads, Writes & Format
  // As "type" is a keyword we are unable to use it in the case class directly.
  object SnakeCaseCustom extends JsonNaming {
    override def apply(property: String): String = {
      if (property === "ttype")
        "type"
      else
        JsonNaming.SnakeCase.apply(property)
    }

    override val toString = "SnakeCaseCustom"
  }

  implicit val format: OFormat[BankAccountSummary] = {
    implicit val config: JsonConfiguration = JsonConfiguration(BankAccountSummary.SnakeCaseCustom)

    Json.format[BankAccountSummary]
  }
}

