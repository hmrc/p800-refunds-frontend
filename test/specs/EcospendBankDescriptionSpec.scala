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

import org.apache.pekko.http.scaladsl.model.Uri
import models.ecospend.{BankId, EcospendBankAbilities, EcospendBankDescription, BankName, BankFriendlyName, BankGroup}
import play.api.libs.json.{Format, JsSuccess, Json, Reads}
import testsupport.UnitSpec

class EcospendBankDescriptionSpec extends UnitSpec {
  "Survive round-trip JSON serialisation" in {
    val testBankDescription = EcospendBankDescription(
      bankId         = BankId("1234567890"),
      name           = BankName("Natwest"),
      friendlyName   = BankFriendlyName("Friendly Natwest"),
      isSandbox      = false,
      logoUrl        = Uri("http://example.com"),
      standard       = Some("obie"),
      countryIsoCode = Some(""),
      division       = Some("personal"),
      group          = BankGroup("Natwest"),
      order          = 1,
      abilities      = EcospendBankAbilities(
        domestic                   = true,
        domesticScheduled          = false,
        domesticStandingOrder      = true,
        international              = false,
        internationalScheduled     = true,
        internationalStandingOrder = false
      ),
      serviceStatus  = true,
      iconUrl        = Uri("https://icon.com")
    )

    val json = Json.toJson(testBankDescription)

    implicitly[Format[EcospendBankDescription]].reads(json) shouldBe JsSuccess(testBankDescription)
  }

  "Deserialise from Ecospend example JSON" in {
    val testJson = Json.parse(
      """
        |{
        |  "bank_id": "obie-barclays-production",
        |  "name": "Barclays Personal",
        |  "friendly_name": "Barclays",
        |  "is_sandbox": false,
        |  "logo": "https://uri",
        |  "standard": "obie",
        |  "country_iso_code": "",
        |  "division": "GB",
        |  "group": "Barclays",
        |  "order": 0,
        |  "abilities": {
        |      "domestic_payment": true,
        |      "domestic_scheduled_payment": true,
        |      "domestic_standing_order": true,
        |      "international_payment": true,
        |      "international_scheduled_payment": true,
        |      "international_standing_order": true
        |  },
        |  "service_status": true,
        |  "icon": "https://icon.com"
        |}
      """.stripMargin
    )

    val expectedBankAbilities = EcospendBankAbilities(
      domestic                   = true, domesticScheduled = true, domesticStandingOrder = true,
      international              = true, internationalScheduled = true, internationalStandingOrder = true
    )

    val expectedCaseClass = EcospendBankDescription(
      bankId         = BankId("obie-barclays-production"), name = BankName("Barclays Personal"), friendlyName = BankFriendlyName("Barclays"),
      isSandbox      = false, logoUrl = Uri("https://uri"), standard = Some("obie"), countryIsoCode = Some(""),
      division       = Some("GB"), group = BankGroup("Barclays"), order = 0, abilities = expectedBankAbilities, serviceStatus = true,
      iconUrl        = "https://icon.com"
    )

    implicitly[Reads[EcospendBankDescription]].reads(testJson) shouldBe JsSuccess(expectedCaseClass)
  }
}

