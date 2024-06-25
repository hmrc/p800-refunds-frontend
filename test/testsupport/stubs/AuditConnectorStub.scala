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

package testsupport.stubs

import com.github.tomakehurst.wiremock.client.WireMock._
import play.api.libs.json.JsObject
import play.mvc.Http.Status

object AuditConnectorStub {

  val auditUrl: String = "/write/audit"

  def verifyEventAudited(auditType: String, auditEvent: JsObject): Unit =
    verify(
      postRequestedFor(urlPathEqualTo(auditUrl))
        .withRequestBody(
          equalToJson(s"""{ "auditType": "${auditType}" }""", true, true)
        )
        .withRequestBody(
          equalToJson("""{ "auditSource": "p800-refunds-frontend" }""", true, true)
        )
        .withRequestBody(
          equalToJson(s"""{ "detail": ${auditEvent.toString} }""", true, true)
        )
    )

  def verifyNoAuditEvent(auditType: String): Unit =
    verify(
      exactly(0),
      postRequestedFor(urlPathEqualTo(auditUrl))
        .withRequestBody(
          equalToJson(s"""{ "auditType": "${auditType}" }""", true, true)
        )
    )

  def verifyExactlyAuditEvent(auditType: String, number: Int): Unit =
    verify(
      exactly(number),
      postRequestedFor(urlPathEqualTo(auditUrl))
        .withRequestBody(
          equalToJson(s"""{ "auditType": "${auditType}" }""", true, true)
        )
    )

  val userLoginSelectionAuditType: String = "UserLoginSelection"
  val bankClaimAttemptMadeAuditType: String = "BankClaimAttemptMade"
  val validateUserDetailsAuditType: String = "ValidateUserDetails"
  val chequeClaimAttemptMadeAuditType: String = "ChequeClaimAttemptMade"
  val nameMatchingAuditType: String = "FuzzyNameMatchingEvent"

  /*
   * Helper method to stop wiremock 'stub not found'
   * being spammed into the logs when testing.
   * Just creates a basic stub for implicit requests.
   */
  def stubImplicitAuditEvents(): Unit = {
    stubFor(post(urlEqualTo(s"$auditUrl")).withRequestBody(equalToJson(s"""{"auditType": "RequestReceived"}""", true, true)).willReturn(aResponse().withStatus(Status.OK)))
    stubFor(post(urlEqualTo(s"$auditUrl")).withRequestBody(equalToJson(s"""{"auditType": "OutboundCall"}""", true, true)).willReturn(aResponse().withStatus(Status.OK)))
    stubFor(post(urlEqualTo(s"$auditUrl/merged")).willReturn(aResponse().withStatus(Status.OK)))
    ()
  }
}
