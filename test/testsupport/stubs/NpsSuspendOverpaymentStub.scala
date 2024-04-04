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
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import models.{CorrelationId, Nino, P800Reference}
import nps.models.{SuspendOverpaymentRequest, SuspendOverpaymentResponse}
import play.api.http.Status
import play.api.libs.json.Json
import testsupport.stubs.NpsHeaders.npsHeaders

object NpsSuspendOverpaymentStub {

  def suspendOverpayment(nino: Nino, p800Reference: P800Reference, request: SuspendOverpaymentRequest, response: SuspendOverpaymentResponse): StubMapping = {
    WireMockHelpers.Put.stubForPut(
      url             = url(nino, p800Reference),
      responseBody    = Json.prettyPrint(Json.toJson(response)),
      responseStatus  = Status.OK,
      requestBodyJson = Some(Json.prettyPrint(Json.toJson(request))),
      requiredHeaders = npsHeaders
    )
  }

  def suspendOverpaymentInternalServerError(nino: Nino, p800Reference: P800Reference): StubMapping =
    WireMockHelpers.Put.stubForPut(
      url             = url(nino, p800Reference),
      responseBody    = "",
      responseStatus  = Status.INTERNAL_SERVER_ERROR,
      requiredHeaders = npsHeaders
    )

  def verifySuspendOverpayment(nino: Nino, p800Reference: P800Reference, correlationId: CorrelationId): Unit =
    verify(
      exactly(1),
      putRequestedFor(urlPathEqualTo(url(nino, p800Reference)))
        .withHeader("correlationid", matching(correlationId.value.toString))
    )

  def verifyNoneSuspendOverpayment(nino: Nino, p800Reference: P800Reference): Unit =
    verify(exactly(0), putRequestedFor(urlPathEqualTo(url(nino, p800Reference))))

  private def url(nino: Nino, p800Reference: P800Reference) = s"/nps-json-service/nps/v1/api/accounting/suspend-overpayment/${nino.value}/${p800Reference.value}"
}
