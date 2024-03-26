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

package edh

import models.Nino
import play.api.libs.json.{Json, OFormat, Format}
import util.CurrencyFormat

import java.util.Currency

final case class CaseManagementRequest(
    clientUId:             ClientUId,
    clientSystemId:        ClientSystemId,
    nino:                  Nino,
    bankSortCode:          BankSortCode,
    bankAccountNumber:     BankAccountNumber,
    bankAccountName:       BankAccountName,
    designatedAccountFlag: Int,
    contact:               List[CaseManagementContact],
    currency:              Currency,
    paymentAmount:         BigDecimal,
    overallRiskResult:     Int,
    nameMatches:           Option[String],
    addressMatches:        Option[String],
    accountExists:         Option[String],
    subjectHasDeceased:    Option[String],
    nonConsented:          Option[String],
    ruleResults:           Option[List[CaseManagementRuleResult]],
    reconciliationId:      Option[Int],
    taxDistrictNumber:     Option[Int],
    payeNumber:            Option[String]
) {
  def validate: Option[String] =
    clientUId.validate
      .orElse(bankAccountNumber.validate)
      .orElse(bankSortCode.validate)
}

object CaseManagementRequest {
  implicit val currencyFormat: Format[Currency] = CurrencyFormat.format

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val format: OFormat[CaseManagementRequest] = Json.format[CaseManagementRequest]
}

final case class ClientSystemId(value: String) extends AnyVal

object ClientSystemId {
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val format: Format[ClientSystemId] = Json.valueFormat[ClientSystemId]
}

final case class CaseManagementRuleResult(
    ruleId:          Option[String],
    ruleInformation: Option[String],
    ruleScore:       Option[Int]
)

object CaseManagementRuleResult {
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val format: OFormat[CaseManagementRuleResult] = Json.format[CaseManagementRuleResult]
}

final case class CaseManagementContact(
    `type`:    PersonType,
    firstName: String,
    surname:   String,
    address:   List[CaseManagementAddress]
)

object CaseManagementContact {
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val format: OFormat[CaseManagementContact] = Json.format[CaseManagementContact]
}

final case class CaseManagementAddress(
    `type`:       AddressType,
    addressLine1: Option[String],
    addressLine2: Option[String],
    addressLine3: Option[String],
    addressLine4: Option[String],
    addressLine5: Option[String],
    postcode:     Option[Postcode]
)

object CaseManagementAddress {
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val format: OFormat[CaseManagementAddress] = Json.format[CaseManagementAddress]
}

