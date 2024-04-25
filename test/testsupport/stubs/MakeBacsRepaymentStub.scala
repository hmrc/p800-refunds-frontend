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
import nps.models.{MakeBacsRepaymentRequest, MakeBacsRepaymentResponse}
import play.api.http.Status
import play.api.libs.json.Json
import testsupport.stubs.NpsHeaders.npsHeaders

object MakeBacsRepaymentStub {

  def `makeBacsRepayment 200 OK`(nino: Nino, request: MakeBacsRepaymentRequest, response: MakeBacsRepaymentResponse): StubMapping = {
    WireMockHelpers.Post.stubForPost(
      url             = url(nino),
      responseBody    = Json.prettyPrint(Json.toJson(response)),
      responseStatus  = Status.OK,
      requestBodyJson = Some(Json.prettyPrint(Json.toJson(request))),
      requiredHeaders = npsHeaders
    )
  }

  //TODO: this actually doesn't happen. p800-refunds-backend responds with 5xx for such case
  def `refundAlreadyTaken 422`(nino: Nino): StubMapping =
    WireMockHelpers.Post.stubForPost(
      url             = url(nino),
      responseBody    =
        //language=JSON
        """
          {
           "failures" : [
             {"reason" : "Reference ", "code": "63480"}
           ]
          }
          """.stripMargin,
      responseStatus  = Status.UNPROCESSABLE_ENTITY,
      requiredHeaders = npsHeaders
    )

  //TODO: this actually doesn't happen. p800-refunds-backend responds with 5xx for such case
  def `refundSuspended 422`(nino: Nino): StubMapping =
    WireMockHelpers.Post.stubForPost(
      url             = url(nino),
      responseBody    =
        //language=JSON
        """
          {
           "failures" : [
             {"reason" : "Reference ", "code": "63480"}
           ]
          }
          """.stripMargin,
      responseStatus  = Status.UNPROCESSABLE_ENTITY,
      requiredHeaders = npsHeaders
    )

  def `internalServerError 500`(nino: Nino): StubMapping =
    WireMockHelpers.Post.stubForPost(
      url             = url(nino),
      responseBody    = "",
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

  private def url(nino: Nino) = s"/p800-refunds-backend/nps/make-bacs-repayment/${nino.value}"
}
