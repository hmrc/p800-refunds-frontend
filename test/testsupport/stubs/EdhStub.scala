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
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import edh.{ClaimId, ClientUId, CaseManagementRequest, GetBankDetailsRiskResultRequest, GetBankDetailsRiskResultResponse}
import play.api.http.Status
import play.api.libs.json.Json

object EdhStub {

  def getBankDetailsRiskResult(request: GetBankDetailsRiskResultRequest, response: GetBankDetailsRiskResultResponse): StubMapping = {
    WireMockHelpers.Post.stubForPost(
      url             = url(ClaimId(request.header.transactionID.value)), //HINT: ClaimId is the same as TransactionID according to spec for this API
      responseBody    = Json.prettyPrint(Json.toJson(response)),
      responseStatus  = Status.OK,
      requestBodyJson = Some(Json.prettyPrint(Json.toJson(request))),
      requiredHeaders = requiredEdhHeaders
    )
  }

  def getBankDetailsRiskResult5xx(request: GetBankDetailsRiskResultRequest): StubMapping = {
    WireMockHelpers.Post.stubForPost(
      url             = url(ClaimId(request.header.transactionID.value)), //HINT: ClaimId is the same as TransactionID according to spec for this API
      responseBody    = """{"reason" : "Dependent systems are currently not responding"}""",
      responseStatus  = Status.SERVICE_UNAVAILABLE,
      requestBodyJson = Some(Json.prettyPrint(Json.toJson(request))),
      requiredHeaders = requiredEdhHeaders
    )
  }

  def verifyGetBankDetailsRiskResult(claimId: ClaimId, numberOfRequests: Int = 1): Unit =
    verify(exactly(numberOfRequests), postRequestedFor(urlPathEqualTo(url(claimId))))

  private def url(claimId: ClaimId) = s"/risking/claims/${claimId.value}/bank-details"

  object CaseManagement {

    def notifyCaseManagement2xx(clientUId: ClientUId, request: CaseManagementRequest): StubMapping = {
      WireMockHelpers.Post.stubForPost(
        url             = caseManagementUrl(clientUId),
        responseBody    = "",
        responseStatus  = Status.OK,
        requestBodyJson = Some(Json.prettyPrint(Json.toJson(request))),
        requiredHeaders = EdhStub.requiredEdhHeaders
      )
    }

    def notifyCaseManagement4xx(clientUId: ClientUId, request: CaseManagementRequest): StubMapping = {
      WireMockHelpers.Post.stubForPost(
        url             = caseManagementUrl(clientUId),
        responseBody    = """{"reason": "Invalid JSON"}""",
        responseStatus  = Status.BAD_REQUEST,
        requestBodyJson = Some(Json.prettyPrint(Json.toJson(request))),
        requiredHeaders = EdhStub.requiredEdhHeaders
      )
    }

    def notifyCaseManagement5xx(clientUId: ClientUId, request: CaseManagementRequest): StubMapping = {
      WireMockHelpers.Post.stubForPost(
        url             = caseManagementUrl(clientUId),
        responseBody    = "",
        responseStatus  = Status.INTERNAL_SERVER_ERROR,
        requestBodyJson = Some(Json.prettyPrint(Json.toJson(request))),
        requiredHeaders = EdhStub.requiredEdhHeaders
      )
    }

    def verifyNotifyCaseManagement(clientUId: ClientUId, numberOfRequests: Int = 1): Unit =
      verify(exactly(numberOfRequests), postRequestedFor(urlPathEqualTo(caseManagementUrl(clientUId))))

    private def caseManagementUrl(clientUId: ClientUId): String = s"/risking/exceptions/${clientUId.value}"
  }

  private val requiredEdhHeaders: Seq[(String, StringValuePattern)] = Seq(
    ("RequesterId", matching("Repayment Service")),
    ("CorrelationId", matching(".*")),
    ("Environment", matching(".*")),
    ("Authorization", matching("Bearer .*"))
  )
}
