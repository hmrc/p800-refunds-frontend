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

package models.ecospend

import play.api.libs.json._

final case class EcospendBankAbilities(
    account:           Boolean,
    balance:           Boolean,
    transactions:      Boolean,
    directDebits:      Boolean,
    standingOrders:    Boolean,
    parties:           Boolean,
    scheduledPayments: Boolean,
    statements:        Boolean,
    offers:            Boolean
)

@SuppressWarnings(Array("org.wartremover.warts.Any"))
object EcospendBankAbilities {
  implicit val format: OFormat[EcospendBankAbilities] = {
    implicit val config: JsonConfiguration = JsonConfiguration(JsonNaming.SnakeCase)

    Json.format[EcospendBankAbilities]
  }
}

