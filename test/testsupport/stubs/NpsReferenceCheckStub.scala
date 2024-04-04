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
import models.{CorrelationId, Nino, P800Reference}
import nps.models.ReferenceCheckResult
import play.api.libs.json.Json
import play.api.http.Status
import testsupport.stubs.NpsHeaders.npsHeaders

object NpsReferenceCheckStub {

  def checkReference(nino: Nino, p800Reference: P800Reference, response: ReferenceCheckResult): StubMapping = {
    WireMockHelpers.Get.stubForGetWithResponseBody(
      url             = url(nino, p800Reference),
      responseBody    = Json.toJson(response).toString(),
      responseStatus  = Status.OK,
      requiredHeaders = npsHeaders
    )
  }

  def checkReferenceRefundAlreadyTaken(nino: Nino, p800Reference: P800Reference): StubMapping =
    WireMockHelpers.Get.stubForGetWithResponseBody(
      url             = url(nino, p800Reference),
      responseBody    = Json.toJson(ReferenceCheckResult.RefundAlreadyTaken)(ReferenceCheckResult.format.writes(_)).toString(),
      responseStatus  = Status.OK,
      requiredHeaders = npsHeaders
    )

  def checkReferenceReferenceDidntMatchNino(nino: Nino, p800Reference: P800Reference): StubMapping =
    WireMockHelpers.Get.stubForGetWithResponseBody(
      url             = url(nino, p800Reference),
      responseBody    = Json.toJson(ReferenceCheckResult.ReferenceDidntMatchNino)(ReferenceCheckResult.format.writes(_)).toString(),
      responseStatus  = Status.OK,
      requiredHeaders = npsHeaders
    )

  def verifyCheckReference(nino: Nino, p800Reference: P800Reference, correlationId: CorrelationId): Unit =
    verify(
      exactly(1),
      getRequestedFor(urlPathEqualTo(url(nino, p800Reference)))
        .withHeader("correlationid", matching(correlationId.value.toString))
    )

  private def url(nino: Nino, p800Reference: P800Reference) = s"/p800-refunds-backend/nps-json-service/nps/v1/api/reconciliation/p800/${nino.value}/${p800Reference.value}"

}
