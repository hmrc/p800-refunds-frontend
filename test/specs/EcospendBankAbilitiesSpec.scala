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

package specs

import models.ecospend.EcospendBankAbilities
import play.api.libs.json.{Format, JsSuccess, Json, Reads}
import testsupport.UnitSpec

class EcospendBankAbilitiesSpec extends UnitSpec {
  "Survive round-trip JSON serialisation" in {
    val testBankAbilities = EcospendBankAbilities(
      account           = true,
      balance           = false,
      transactions      = true,
      directDebits      = false,
      standingOrders    = true,
      parties           = false,
      scheduledPayments = true,
      statements        = false,
      offers            = true
    )

    val json = Json.toJson(testBankAbilities)

    implicitly[Format[EcospendBankAbilities]].reads(json) shouldBe JsSuccess(testBankAbilities)
  }

  "Deserialise from Ecospend example JSON" in {
    val testJson = Json.parse(
      //language=JSON
      """
        {
          "account": true,
          "balance": true,
          "transactions": true,
          "direct_debits": true,
          "standing_orders": true,
          "parties": true,
          "scheduled_payments": true,
          "statements": true,
          "offers": true
        }
      """.stripMargin
    )

    val expectedBankAbilities = EcospendBankAbilities(
      account           = true,
      balance           = true,
      transactions      = true,
      directDebits      = true,
      standingOrders    = true,
      parties           = true,
      scheduledPayments = true,
      statements        = true,
      offers            = true
    )

    implicitly[Reads[EcospendBankAbilities]].reads(testJson) shouldBe JsSuccess(expectedBankAbilities)
  }
}
