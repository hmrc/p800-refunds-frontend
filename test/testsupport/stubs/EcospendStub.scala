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

package testsupport.stubs

import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import models.ecospend.BankId
import models.ecospend.consent.BankConsentRequest
import models.ecospend.consent.ConsentStatus
import models.ecospend.verification.{BankVerificationRequest, VerificationStatus}
import play.api.http.Status
import testsupport.ItSpec

import java.util.UUID

object EcospendStub {
  val authUrl: String = "/connect/token"
  val banksUrl: String = "/api/v2.0/banks"
  val validateUrl: String = "/api/v2.0/validate"
  val consentUrl: String = "/api/v2.0/consents"
  val accountSummaryUrl: String = "/api/v2.0/accounts/summary"

  val authJsonResponseBody2xx: String =
    //language=JSON
    """{
      "access_token": "1234567890",
      "expires_in": 300,
      "token_type": "client_credentials",
      "scope": "px01.ecospend.pis.sandbox"
    }""".stripMargin

  val authJsonResponseBodyInvalid4xx: String =
    //language=JSON
    """{
      "error": "INVALID_CLIENT_CREDENTIALS",
      "description": "Client credentials are missing or invalid",
      "details": {}
    }""".stripMargin

  def stubEcospendAuth2xxSucceeded: StubMapping = WireMockHelpers.stubForPostWithResponseBody(authUrl, authJsonResponseBody2xx)
  def stubEcospendAuth4xxUnauthorized: StubMapping = WireMockHelpers.stubForPostWithResponseBody(authUrl, authJsonResponseBodyInvalid4xx, Status.UNAUTHORIZED)

  val banksResponseJson: String =
    //language=JSON
    s"""{
          "data": [
            {
              "bank_id": "obie-barclays-personal",
              "name": "Barclays Personal",
              "friendly_name": "Barclays Personal",
              "is_sandbox": false,
              "logo": "https://logo.com",
              "icon": "https://public.ecospend.com/images/banks/Barclays_icon.svg",
              "standard": "obie",
              "country_iso_code": "",
              "division": "GB",
              "group": "Barclays",
              "order": 0,
              "abilities": {
                "domestic_payment": true,
                "domestic_scheduled_payment": true,
                "domestic_standing_order": true,
                "international_payment": true,
                "international_scheduled_payment": true,
                "international_standing_order": true
              },
              "service_status": true
            },
            {
              "bank_id": "obie-barclays-business",
              "name": "Barclays Business",
              "friendly_name": "Barclays Business",
              "is_sandbox": false,
              "logo": "https://logo.com",
              "icon": "https://public.ecospend.com/images/banks/Barclays_icon.svg",
              "standard": "obie",
              "country_iso_code": "",
              "division": "GB",
              "group": "Barclays",
              "order": 0,
              "abilities": {
                "domestic_payment": true,
                "domestic_scheduled_payment": true,
                "domestic_standing_order": true,
                "international_payment": true,
                "international_scheduled_payment": true,
                "international_standing_order": true
              },
              "service_status": true
            },
            {
              "bank_id": "obie-lloyds-personal",
              "name": "Lloyds Personal",
              "friendly_name": "Lloyds Personal",
              "is_sandbox": false,
              "logo": "https://logo.com",
              "icon": "https://public.ecospend.com/images/banks/Lloyds_icon.svg",
              "standard": "obie",
              "country_iso_code": "",
              "division": "GB",
              "group": "Lloyds",
              "order": 0,
              "abilities": {
                "domestic_payment": true,
                "domestic_scheduled_payment": true,
                "domestic_standing_order": true,
                "international_payment": true,
                "international_scheduled_payment": true,
                "international_standing_order": true
              },
              "service_status": true
            }
          ]
        }""".stripMargin

  def stubEcospendGetBanks2xx: StubMapping = WireMockHelpers.stubForGetWithResponseBody(banksUrl, banksResponseJson)
  def stubEcospendGetBanks4xx: StubMapping = WireMockHelpers.stubForGetWithResponseBody(banksUrl, """{ "error": "ERROR" }""")

  def verifyEcospendAccessToken(numberOfRequests: Int = 1): Unit =
    verify(exactly(numberOfRequests), postRequestedFor(urlPathEqualTo(authUrl)))

  def verifyEcospendGetBanks(numberOfRequests: Int = 1): Unit =
    verify(exactly(numberOfRequests), getRequestedFor(urlPathEqualTo(banksUrl)))

  object ValidateStubs {

    def stubValidateNotValidatedYet: StubMapping = WireMockHelpers.stubForPostNoResponseBody(validateUrl, Status.PAYMENT_REQUIRED)

    def stubValidatePaymentSuccessful(identifier: String = "AB123456C"): StubMapping =
      WireMockHelpers.stubForPostWithResponseBody(validateUrl, validateBankVerificationResponseJson(identifier, VerificationStatus.Successful))

    def stubValidatePaymentUnSuccessful(identifier: String = "AB123456C"): StubMapping =
      WireMockHelpers.stubForPostWithResponseBody(validateUrl, validateBankVerificationResponseJson(identifier, VerificationStatus.UnSuccessful))

    def validateBankVerificationResponseJson(identifier: String, verificationStatus: VerificationStatus): String =
      //language=JSON
      s"""{"identifier":"$identifier", "verificationStatus":"${verificationStatus.entryName}"}"""

    def verifyValidate(numberOfRequests: Int = 1): Unit = WireMockHelpers.verifyExactlyWithBodyParse(validateUrl, numberOfRequests)(BankVerificationRequest.format)

  }

  object ConsentStubs {
    def stubConsent2xxSucceeded(bankId: BankId): StubMapping =
      WireMockHelpers.stubForPostWithResponseBody(consentUrl, validateBankConsentResponseJson(bankId, ConsentStatus.AwaitingAuthorization))

    def validateBankConsentResponseJson(bankId: BankId, consentStatus: ConsentStatus): String =
      //language=JSON
      s"""
        {
          "id": "00000000-0000-0000-0000-000000000000",
          "bank_reference_id": "MyBank-129781876126",
          "bank_consent_url": "http://localhost:${ItSpec.testServerPort.toString}/get-an-income-tax-refund/test-only/bank-page",
          "bank_id": "${bankId.value}",
          "status": "${consentStatus.toString}",
          "redirect_url": "http://localhost:${ItSpec.testServerPort.toString}/get-an-income-tax-refund/bank-transfer/verifying-your-bank-account",
          "consent_end_date": "2059-11-25T16:33:51.880",
          "consent_expiry_date": "2059-11-25T16:33:51.880",
          "permissions": [
              "Account",
              "Balance",
              "Transactions",
              "DirectDebits",
              "StandingOrders",
              "Parties"
          ]
        }""".stripMargin

    def consentValidate(numberOfRequests: Int = 1): Unit =
      WireMockHelpers.verifyExactlyWithBodyParse(consentUrl, numberOfRequests)(BankConsentRequest.format)
  }

  object AccountStub {
    def stubAccountSummary2xxSucceeded(consentId: UUID): StubMapping =
      WireMockHelpers.stubForGetWithResponseBody(accountSummaryUrl, validateBankAccountSummaryResponseJson(consentId))

    def accountSummaryValidate(numberOfRequests: Int = 1): Unit =
      WireMockHelpers.verifyExactlyWithHeader(accountSummaryUrl, "consent_id", numberOfRequests)

    def validateBankAccountSummaryResponseJson(consentId: UUID): String =
      //language=JSON
      s"""
        [
          {
            "id": "${consentId.toString}",
            "bank_id": "obie-barclays-personal",
            "type": "Personal",
            "sub_type": "CurrentAccount",
            "currency": "GBP",
            "account_format": "SortCode",
            "account_identification": "44556610002333",
            "calculated_owner_name": "Greg Greggson",
            "account_owner_name": "Greg Greggson",
            "display_name": "Greg G Greggson",
            "balance": 123.7,
            "last_update_time": "2059-11-25T16:33:51.880",
            "parties": [
              {
                "name": "Greg Greggson",
                "full_legal_name": "Greg Greggory Greggson"
              }
            ]
          }
        ]""".stripMargin
  }
}
