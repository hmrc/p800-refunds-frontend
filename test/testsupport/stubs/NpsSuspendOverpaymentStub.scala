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

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import models.{CorrelationId, Nino}
import nps.models.SuspendOverpaymentRequest
import play.api.http.Status
import play.api.libs.json.Json
import testsupport.stubs.NpsHeaders.npsHeaders

object NpsSuspendOverpaymentStub {

  def suspendOverpayment(nino: Nino, request: SuspendOverpaymentRequest): StubMapping = {
    WireMockHelpers.Post.stubForPost(
      url             = url(nino),
      responseBody    = """{"identifier":"AB999999C","currentOptimisticLock":15}""",
      responseStatus  = Status.OK,
      requestBodyJson = Some(Json.prettyPrint(Json.toJson(request))),
      requiredHeaders = npsHeaders
    )
  }

  def suspendOverpaymentInternalServerError(nino: Nino): StubMapping =
    WireMockHelpers.Post.stubForPost(
      url             = url(nino),
      responseBody    =
        //whatever actually because it will be only logged when exception is caught by error handler
        """POST of url ... returned 422. Response body: '{
         "failures" : [
           {"reason" : "Overpayment has already been claimed", "code": "63480"}
         ]
        }'""",
      responseStatus  = Status.INTERNAL_SERVER_ERROR,
      requiredHeaders = npsHeaders
    )

  def verify(nino: Nino, correlationId: CorrelationId): Unit =
    WireMock.verify(
      exactly(1),
      postRequestedFor(urlPathEqualTo(url(nino)))
        .withHeader("correlationid", matching(correlationId.value.toString))
    )

  def verifyNone(nino: Nino): Unit =
    WireMock.verify(exactly(0), postRequestedFor(urlPathEqualTo(url(nino))))

  private def url(nino: Nino) = s"/p800-refunds-backend/nps/suspend-overpayment/${nino.value}"
}
