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

import play.api.libs.json.{Json, Format, OFormat, JsonConfiguration, JsonNaming}

final case class BankAccountParty(
    name:          Option[BankPartyName],
    fullLegalName: Option[BankPartyFullLegalName]
)

@SuppressWarnings(Array("org.wartremover.warts.Any"))
object BankAccountParty {
  implicit val format: OFormat[BankAccountParty] = {
    implicit val config: JsonConfiguration = JsonConfiguration(JsonNaming.SnakeCase)

    Json.format[BankAccountParty]
  }
}

final case class BankPartyName(value: String) extends AnyVal
object BankPartyName {
  implicit val format: Format[BankPartyName] = Json.valueFormat[BankPartyName]
}

final case class BankPartyFullLegalName(value: String) extends AnyVal
object BankPartyFullLegalName {
  implicit val format: Format[BankPartyFullLegalName] = Json.valueFormat[BankPartyFullLegalName]
}

