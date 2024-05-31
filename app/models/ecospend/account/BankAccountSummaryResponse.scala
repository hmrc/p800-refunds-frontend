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

import models.ecospend.BankId
import play.api.libs.json.{Json, Format, OFormat, JsonConfiguration, JsonNaming}
import util.{CurrencyFormat, Errors}
import play.api.mvc.RequestHeader

import java.util.{UUID, Currency}
import java.time.LocalDateTime

final case class BankAccountSummaryResponse(value: List[BankAccountSummary]) extends AnyVal

object BankAccountSummaryResponse {
  implicit val format: Format[BankAccountSummaryResponse] = Json.valueFormat[BankAccountSummaryResponse]
}

final case class BankAccountSummary(
    id:                    UUID,
    bankId:                Option[BankId],
    merchantId:            Option[String],
    merchantUserId:        Option[String],
    `type`:                BankAccountType,
    subType:               BankAccountSubType,
    currency:              Currency,
    accountFormat:         BankAccountFormat,
    accountIdentification: Option[BankAccountIdentification],
    calculatedOwnerName:   Option[CalculatedOwnerName],
    accountOwnerName:      Option[BankAccountOwnerName],
    displayName:           Option[BankAccountDisplayName],
    balance:               Double,
    lastUpdateTime:        LocalDateTime,
    parties:               Option[List[BankAccountParty]]
) {
  def getAccountIdentification(implicit requestHeader: RequestHeader): BankAccountIdentification =
    accountIdentification.getOrElse(Errors.throwServerErrorException(s"Expected 'accountIdentification' to be defined but it was None [bankId: ${id.toString}]"))

  def getDisplayName(implicit requestHeader: RequestHeader): BankAccountDisplayName =
    displayName.getOrElse(Errors.throwServerErrorException(s"Expected 'displayName' to be defined but it was None [bankId: ${id.toString}]"))
}

@SuppressWarnings(Array("org.wartremover.warts.Any"))
object BankAccountSummary {
  implicit val currencyFormat: Format[Currency] = CurrencyFormat.format

  implicit val format: OFormat[BankAccountSummary] = {
    implicit val config: JsonConfiguration = JsonConfiguration(JsonNaming.SnakeCase)

    Json.format[BankAccountSummary]
  }
}

