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
      domestic                   = true,
      domesticScheduled          = false,
      domesticStandingOrder      = true,
      international              = false,
      internationalScheduled     = true,
      internationalStandingOrder = false
    )

    val json = Json.toJson(testBankAbilities)

    implicitly[Format[EcospendBankAbilities]].reads(json) shouldBe JsSuccess(testBankAbilities)
  }

  "Deserialise from Ecospend example JSON" in {
    val testJson = Json.parse(
      """
        |{
        |  "domestic_payment": true,
        |  "domestic_scheduled_payment": true,
        |  "domestic_standing_order": true,
        |  "international_payment": true,
        |  "international_scheduled_payment": true,
        |  "international_standing_order": true
        |}
      """.stripMargin
    )

    val expectedBankAbilities = EcospendBankAbilities(
      domestic                   = true,
      domesticScheduled          = true,
      domesticStandingOrder      = true,
      international              = true,
      internationalScheduled     = true,
      internationalStandingOrder = true
    )

    implicitly[Reads[EcospendBankAbilities]].reads(testJson) shouldBe JsSuccess(expectedBankAbilities)
  }
}
