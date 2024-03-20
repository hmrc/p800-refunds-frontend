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

package nps.models

import nps.{NpsErrorCodes, ClaimOverpaymentConnector}
import testsupport.UnitSpec
import uk.gov.hmrc.http.{HttpResponse, UpstreamErrorResponse}

class ClaimOverpaymentConnectorSpec extends UnitSpec {

  "read 422 with specific code as RefundAlreadyTaken" in {
    val codeForRefundAlreadyTaken = """63480"""
    codeForRefundAlreadyTaken shouldBe NpsErrorCodes.`Overpayment has already been claimed`
    val response: HttpResponse = HttpResponse(422, s"""{"failures":[{"reason":"Overpayment has already been claimed","code":"$codeForRefundAlreadyTaken"}]}""")
    ClaimOverpaymentConnector.reads.read("PUT", "url", response) shouldBe ClaimOverpaymentResult.RefundAlreadyTaken
  }

  "read 422 with specific code as Suspended" in {
    val codeForRefundSuspended = """63481"""
    codeForRefundSuspended shouldBe NpsErrorCodes.`Overpayment is suspended`
    val response: HttpResponse = HttpResponse(422, s"""{"failures":[{"reason":"Overpayment is suspended","code":"$codeForRefundSuspended"}]}""")
    ClaimOverpaymentConnector.reads.read("PUT", "url", response) shouldBe ClaimOverpaymentResult.RefundSuspended
  }

  "read 422 with other codes as Upstream4xxResponse" in {
    val response: HttpResponse = HttpResponse(422, s"""{"failures":[{"reason":"Account is not Live","code":"63477"}]}""")
    val thrown = intercept[UpstreamErrorResponse](
      ClaimOverpaymentConnector.reads.read("PUT", "url", response)
    )
    thrown.getMessage() shouldBe """PUT of 'url' returned 422. Response body: '{"failures":[{"reason":"Account is not Live","code":"63477"}]}'"""
    thrown.statusCode shouldBe 422
    thrown.reportAs shouldBe 500
  }

  "read 400 response as UpstreamErrorResponse" in {
    val response: HttpResponse = HttpResponse(400, """{"failures":[{"reason":"constraint violation","code":"400.1"}]}""")
    val thrown = intercept[UpstreamErrorResponse](
      ClaimOverpaymentConnector.reads.read("PUT", "url", response)
    )
    thrown.getMessage() shouldBe """PUT of 'url' returned 400. Response body: '{"failures":[{"reason":"constraint violation","code":"400.1"}]}'"""
    thrown.statusCode shouldBe 400
    thrown.reportAs shouldBe 500
  }

  "read 500 response as UpstreamErrorResponse" in {
    val response: HttpResponse = HttpResponse(500, "")
    val thrown = intercept[UpstreamErrorResponse](
      ClaimOverpaymentConnector.reads.read("PUT", "url", response)
    )
    thrown.getMessage() shouldBe """PUT of 'url' returned 500. Response body: ''"""
    thrown.statusCode shouldBe 500
    thrown.reportAs shouldBe 502
  }
}
