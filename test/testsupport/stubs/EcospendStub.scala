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
import models.ecospend.verification.{BankVerificationRequest, VerificationStatus}
import play.api.http.Status

object EcospendStub {
  val authUrl: String = "/connect/token"
  val banksUrl: String = "/api/v2.0/banks"
  val validateUrl: String = "/api/v2.0/validate"

  val authJsonResponseBody2xx: String =
    """{
      "access_token": "1234567890",
      "expires_in": 300,
      "token_type": "client_credentials",
      "scope": "px01.ecospend.pis.sandbox"
    }""".stripMargin

  val authJsonResponseBodyInvalid4xx: String =
    """{
      "error": "INVALID_CLIENT_CREDENTIALS",
      "description": "Client credentials are missing or invalid",
      "details": {}
    }""".stripMargin

  def stubEcospendAuth2xxSucceeded: StubMapping = WireMockHelpers.stubForPostWithResponseBody(authUrl, authJsonResponseBody2xx)
  def stubEcospendAuth4xxUnauthorized: StubMapping = WireMockHelpers.stubForPostWithResponseBody(authUrl, authJsonResponseBodyInvalid4xx, Status.UNAUTHORIZED)

  val banksResponseJson: String =
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

}
