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

import casemanagement.{CaseManagementRequest, ClientUId}
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.matching.StringValuePattern
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import models.CorrelationId
import play.api.http.Status
import play.api.libs.json.Json

object CaseManagementStub {

  def notifyCaseManagement2xx(clientUId: ClientUId, request: CaseManagementRequest): StubMapping = {
    WireMockHelpers.Post.stubForPost(
      url             = url(clientUId),
      responseBody    = "",
      responseStatus  = Status.OK,
      requestBodyJson = Some(Json.prettyPrint(Json.toJson(request))),
      requiredHeaders = requiredHeaders
    )
  }

  def notifyCaseManagement4xx(clientUId: ClientUId, request: CaseManagementRequest): StubMapping = {
    WireMockHelpers.Post.stubForPost(
      url             = url(clientUId),
      responseBody    = """{"reason": "Invalid JSON"}""",
      responseStatus  = Status.BAD_REQUEST,
      requestBodyJson = Some(Json.prettyPrint(Json.toJson(request))),
      requiredHeaders = requiredHeaders
    )
  }

  def notifyCaseManagement5xx(clientUId: ClientUId, request: CaseManagementRequest): StubMapping = {
    WireMockHelpers.Post.stubForPost(
      url             = url(clientUId),
      responseBody    = "",
      responseStatus  = Status.INTERNAL_SERVER_ERROR,
      requestBodyJson = Some(Json.prettyPrint(Json.toJson(request))),
      requiredHeaders = requiredHeaders
    )
  }

  def verifyNotifyCaseManagement(clientUId: ClientUId, correlationId: CorrelationId, numberOfRequests: Int = 1): Unit =
    verify(
      exactly(numberOfRequests),
      postRequestedFor(urlPathEqualTo(url(clientUId)))
        .withHeader("correlationid", matching(correlationId.value.toString))
    )

  private def url(clientUId: ClientUId): String = s"/p800-refunds-backend/risking/exceptions/${clientUId.value}"

  private val requiredHeaders: Seq[(String, StringValuePattern)] = Seq(
    ("CorrelationId", matching(".*"))
  )
}
