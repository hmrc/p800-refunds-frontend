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
import models.CorrelationId
import nps.models.{TraceIndividualRequest, TraceIndividualResponse}
import play.api.http.Status
import play.api.libs.json.Json
import testsupport.stubs.NpsHeaders.npsHeaders

object TraceIndividualStub {

  def traceIndividual(request: TraceIndividualRequest, response: TraceIndividualResponse): StubMapping = {
    WireMockHelpers.Post.stubForPost(
      url             = url,
      responseBody    = Json.prettyPrint(Json.toJson(response)),
      responseStatus  = Status.OK,
      requestBodyJson = Some(Json.prettyPrint(Json.toJson(request))),
      requiredHeaders = npsHeaders
    )
  }

  def traceIndividualVariedResponseStub(
      request: TraceIndividualRequest,
      body:    String                 = "simulated bad request error",
      status:  Int                    = Status.BAD_REQUEST
  ): StubMapping =
    WireMockHelpers.Post.stubForPost(
      url             = url,
      responseBody    = body,
      responseStatus  = status,
      requestBodyJson = Some(Json.prettyPrint(Json.toJson(request))),
      requiredHeaders = npsHeaders
    )

  def verifyTraceIndividual(correlationId: CorrelationId): Unit =
    verify(
      exactly(1),
      postRequestedFor(urlPathEqualTo(url)).withHeader("correlationid", matching(correlationId.value.toString))
    )

  def verifyNoneTraceIndividual(): Unit = verify(exactly(0), postRequestedFor(urlPathEqualTo(url)))

  private val url: String = s"/p800-refunds-backend/nps/trace-individual"

}
