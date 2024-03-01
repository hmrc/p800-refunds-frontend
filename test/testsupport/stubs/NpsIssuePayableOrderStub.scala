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
import models.{Nino, P800Reference}
import nps.models.IssuePayableOrderRequest
import play.api.http.Status
import play.api.libs.json.{JsObject, Json}
import testsupport.stubs.NpsHeaders.npsHeaders

object NpsIssuePayableOrderStub {

  def issuePayableOrder(nino: Nino, p800Reference: P800Reference, request: IssuePayableOrderRequest, response: JsObject): StubMapping = {
    WireMockHelpers.stubForPut(
      url             = url(nino, p800Reference),
      responseBody    = Json.prettyPrint(response),
      responseStatus  = Status.OK,
      requestBodyJson = Some(Json.prettyPrint(Json.toJson(request))),
      requiredHeaders = npsHeaders
    )
  }

  def issuePayableOrderRefundAlreadyTaken(nino: Nino, p800Reference: P800Reference, request: IssuePayableOrderRequest): StubMapping = {
    WireMockHelpers.stubForPut(
      url             = url(nino, p800Reference),
      responseBody    =
        //language=JSON
        """
        {
         "failures" : [
           {"reason" : "Overpayment has already been claimed", "code": "63480"}
         ]
        }
        """.stripMargin,
      responseStatus  = Status.UNPROCESSABLE_ENTITY,
      requestBodyJson = Some(Json.prettyPrint(Json.toJson(request))),
      requiredHeaders = npsHeaders
    )
  }

  def verifyIssuePayableOrder(nino: Nino, p800Reference: P800Reference): Unit =
    verify(exactly(1), putRequestedFor(urlPathEqualTo(url(nino, p800Reference))))

  private def url(nino: Nino, p800Reference: P800Reference) = s"/nps-json-service/nps/v1/api/accounting/issue-payable-order/${nino.value}/${p800Reference.value}"
}
