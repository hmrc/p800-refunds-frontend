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

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.{exactly, matching}
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import models.{Nino, P800Reference}
import nps.models.ReferenceCheckResult
import play.api.http.Status
import play.api.libs.json.Json
import WireMock._
import com.github.tomakehurst.wiremock.matching.StringValuePattern

object NpsReferenceCheckStub {

  def checkReference(nino: Nino, p800Reference: P800Reference, response: ReferenceCheckResult.P800ReferenceChecked): StubMapping = {
    WireMockHelpers.stubForGetWithResponseBody(
      url             = url(nino, p800Reference),
      responseBody    = Json.prettyPrint(Json.toJson(response)),
      responseStatus  = Status.OK,
      requiredHeaders = npsHeaders
    )
  }

  def checkReferenceRefundAlreadyTaken(nino: Nino, p800Reference: P800Reference): StubMapping =
    WireMockHelpers.stubForGetWithResponseBody(
      url             = url(nino, p800Reference),
      responseBody    =
        """
          |{
          | "failures" : [
          |   {"reason" : "Reference ", "code": "TODO-refund-already-taken"}
          | ]
          |}
          |""".stripMargin,
      responseStatus  = Status.UNPROCESSABLE_ENTITY,
      requiredHeaders = npsHeaders
    )

  def checkReferenceReferenceDidntMatchNino(nino: Nino, p800Reference: P800Reference): StubMapping =
    WireMockHelpers.stubForGetWithResponseBody(
      url             = url(nino, p800Reference),
      responseBody    = "",
      responseStatus  = Status.NOT_FOUND,
      requiredHeaders = npsHeaders
    )

  def verifyCheckReference(nino: Nino, p800Reference: P800Reference): Unit =
    verify(exactly(1), getRequestedFor(urlPathEqualTo(url(nino, p800Reference))))

  private def url(nino: Nino, p800Reference: P800Reference) = s"/nps-json-service/nps/v1/api/reconciliation/p800/${nino.value}/${p800Reference.value}"

  private val npsHeaders: Seq[(String, StringValuePattern)] = Seq(
    ("CorrelationId", matching(".*")),
    ("gov-uk-originator-id", matching("DA2_MRA_DIGITAL")),
    ("Authorization", matching("Basic .*"))
  )

}
