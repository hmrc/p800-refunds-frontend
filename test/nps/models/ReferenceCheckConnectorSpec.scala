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

import nps.{NpsErrorCodes, ReferenceCheckConnector}
import testsupport.UnitSpec
import uk.gov.hmrc.http.{HttpResponse, UpstreamErrorResponse}

class ReferenceCheckConnectorSpec extends UnitSpec {

  "read 422 with specific code as RefundAlreadyTaken" in {
    val codeForRefundAlreadyTaken = """63480"""
    codeForRefundAlreadyTaken shouldBe NpsErrorCodes.`Overpayment has already been claimed`
    val response: HttpResponse = HttpResponse(422, s"""{"failures":[{"reason":"Overpayment has already been claimed","code":"$codeForRefundAlreadyTaken"}]}""")
    ReferenceCheckConnector.reads.read("GET", "url", response) shouldBe ReferenceCheckResult.RefundAlreadyTaken
  }

  "read 422 with other codes as Upstream4xxResponse" in {
    val response: HttpResponse = HttpResponse(422, s"""{"failures":[{"reason":"Account is not Live","code":"63477"}]}""")
    val thrown = intercept[UpstreamErrorResponse](
      ReferenceCheckConnector.reads.read("GET", "url", response)
    )
    thrown.getMessage() shouldBe """GET of 'url' returned 422. Response body: '{"failures":[{"reason":"Account is not Live","code":"63477"}]}'"""
    thrown.statusCode shouldBe 422
    thrown.reportAs shouldBe 500
  }
}
