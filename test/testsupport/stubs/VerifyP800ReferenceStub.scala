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
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import models.{CorrelationId, Nino, P800Reference}
import nps.models.ValidateReferenceResult
import play.api.http.Status
import play.api.libs.json.Json
import testsupport.stubs.NpsHeaders.npsHeaders

object VerifyP800ReferenceStub {

  private def requestBody(nino: Nino, p800Reference: P800Reference): String =
    //language=JSON
    s"""{
         | "nino": "${nino.value}",
         | "p800Reference": ${p800Reference.value.toString}
         |}
         |"""
      .stripMargin

  def p800ReferenceChecked(nino: Nino, p800Reference: P800Reference, response: ValidateReferenceResult): StubMapping = {
    WireMockHelpers.Post.stubForPost(
      url             = url,
      responseBody    = Json.toJson(response).toString(),
      responseStatus  = Status.OK,
      requestBodyJson = Some(requestBody(nino, p800Reference)),
      requiredHeaders = npsHeaders
    )
  }

  def refundAlreadyTaken(nino: Nino, p800Reference: P800Reference): StubMapping =
    WireMockHelpers.Post.stubForPost(
      url             = url,
      responseBody    = Json.toJson(ValidateReferenceResult.RefundAlreadyTaken)(ValidateReferenceResult.format.writes(_)).toString(),
      responseStatus  = Status.OK,
      requestBodyJson = Some(requestBody(nino, p800Reference)),
      requiredHeaders = npsHeaders
    )

  def checkReferenceReferenceDidntMatchNino(nino: Nino, p800Reference: P800Reference): StubMapping =
    WireMockHelpers.Post.stubForPost(
      url             = url,
      responseBody    = Json.toJson(ValidateReferenceResult.ReferenceDidntMatchNino)(ValidateReferenceResult.format.writes(_)).toString(),
      responseStatus  = Status.OK,
      requestBodyJson = Some(requestBody(nino, p800Reference)),
      requiredHeaders = npsHeaders
    )

  def verify(correlationId: CorrelationId): Unit =
    WireMock.verify(
      exactly(1),
      postRequestedFor(urlPathEqualTo(url))
        .withHeader("correlationid", matching(correlationId.value.toString))
    )

  private val url = "/p800-refunds-backend/nps/validate-p800-reference"

}
