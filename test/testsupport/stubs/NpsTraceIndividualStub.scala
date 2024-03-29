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
import nps.models.{TraceIndividualRequest, TraceIndividualResponse}
import play.api.http.Status
import play.api.libs.json.Json
import testsupport.stubs.NpsHeaders.npsHeaders

object NpsTraceIndividualStub {

  def traceIndividual(request: TraceIndividualRequest, response: TraceIndividualResponse): StubMapping = {
    WireMockHelpers.Post.stubForPost(
      url             = url,
      responseBody    = Json.prettyPrint(Json.toJson(response)),
      responseStatus  = Status.OK,
      requestBodyJson = Some(Json.prettyPrint(Json.toJson(request))),
      queryParams     = Map("exactMatch" -> matching("true"), "returnRealName" -> matching("true")),
      requiredHeaders = npsHeaders
    )
  }

  def verifyTraceIndividual(): Unit =
    verify(exactly(1), postRequestedFor(urlPathEqualTo(url)))

  private val url: String = s"/p800-refunds-backend/nps-json-service/nps/v1/api/individual/trace-individual"

}
