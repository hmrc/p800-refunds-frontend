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

import models.ecospend.BankId
import models.ecospend.account._
import play.api.libs.json.{Format, JsSuccess, Json, Reads}
import testsupport.UnitSpec

import java.util.{Currency, UUID}
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class BankAccountSummaryResponseSpec extends UnitSpec {
  private val localDateTime: LocalDateTime = {
    LocalDateTime.parse("2024-02-13T12:52:45.081236", DateTimeFormatter.ISO_DATE_TIME)
  }

  "Survive round-trip JSON serialisation" in {
    val testBankAccountSummaryResponse = BankAccountSummaryResponse(List(BankAccountSummary(
      id                    = UUID.fromString("cddd0273-b709-4ee7-b73d-7113dd7a7d66"),
      bankId                = BankId("obie-barclays-personal"),
      merchantId            = None,
      merchantUserId        = None,
      ttype                 = BankAccountType.Personal,
      subType               = BankAccountSubType.CurrentAccount,
      currency              = Currency.getInstance("GBP"),
      accountFormat         = BankAccountFormat.SortCode,
      accountIdentification = BankAccountIdentification("44556610002333"),
      calculatedOwnerName   = CalculatedOwnerName("Greg Greggson"),
      accountOwnerName      = BankAccountOwnerName("Greg Greggson"),
      displayName           = BankAccountDisplayName("Greg G Greggson"),
      balance               = 123.7,
      lastUpdateTime        = localDateTime,
      parties               = List(BankAccountParty(
        name          = BankPartyName("Greg Greggson"),
        fullLegalName = BankPartyFullLegalName("Greg Greggory Greggson")
      ))
    )))

    val json = Json.toJson(testBankAccountSummaryResponse)

    implicitly[Format[BankAccountSummaryResponse]].reads(json) shouldBe JsSuccess(testBankAccountSummaryResponse)
  }

  "Deserialise from Ecospend example JSON" in {
    val testJson = Json.parse(
      """
        |[
        |  {
        |    "id": "cddd0273-b709-4ee7-b73d-7113dd7a7d66",
        |    "bank_id": "obie-barclays-personal",
        |    "type": "Personal",
        |    "sub_type": "CurrentAccount",
        |    "currency": "GBP",
        |    "account_format": "SortCode",
        |    "account_identification": "abc:123",
        |    "calculated_owner_name": "Greg Greggson",
        |    "account_owner_name": "Greg Greggson",
        |    "display_name": "Greg G Greggson",
        |    "balance": 123.7,
        |    "last_update_time": "2024-02-13T12:52:45.081236",
        |    "parties": [
        |      {
        |        "name": "Greg Greggson",
        |        "full_legal_name": "Greg Greggory Greggson"
        |      }
        |    ]
        |  }
        |]""".stripMargin
    )

    val expectedCaseClass: BankAccountSummaryResponse = BankAccountSummaryResponse(List(BankAccountSummary(
      id                    = UUID.fromString("cddd0273-b709-4ee7-b73d-7113dd7a7d66"),
      bankId                = BankId("obie-barclays-personal"),
      merchantId            = None,
      merchantUserId        = None,
      ttype                 = BankAccountType.Personal,
      subType               = BankAccountSubType.CurrentAccount,
      currency              = Currency.getInstance("GBP"),
      accountFormat         = BankAccountFormat.SortCode,
      accountIdentification = BankAccountIdentification("abc:123"),
      calculatedOwnerName   = CalculatedOwnerName("Greg Greggson"),
      accountOwnerName      = BankAccountOwnerName("Greg Greggson"),
      displayName           = BankAccountDisplayName("Greg G Greggson"),
      balance               = 123.7,
      lastUpdateTime        = localDateTime,
      parties               = List(BankAccountParty(
        name          = BankPartyName("Greg Greggson"),
        fullLegalName = BankPartyFullLegalName("Greg Greggory Greggson")
      ))
    )))

    implicitly[Reads[BankAccountSummaryResponse]].reads(testJson) shouldBe JsSuccess(expectedCaseClass)
  }
}

