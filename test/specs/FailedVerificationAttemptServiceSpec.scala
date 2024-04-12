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

package specs

import models.attemptmodels.IpAddress
import play.api.mvc.RequestHeader
import play.api.test.FakeRequest
import services.FailedVerificationAttemptService
import testsupport.ItSpec

class FailedVerificationAttemptServiceSpec extends ItSpec {

  val failedVerificationAttemptService: FailedVerificationAttemptService = app.injector.instanceOf[FailedVerificationAttemptService]

  "get ip address from True-Client-IP header value" in {
    implicit val requestHeader: RequestHeader = FakeRequest()
      .withHeaders("True-Client-IP" -> "10.10.10.10")

    val expected = "10.10.10.10"
    val result: IpAddress = failedVerificationAttemptService.trueClientIpAddress

    result.value shouldBe expected
  }

  "fall back on remoteAddress if True-Client-IP is not set" in {
    implicit val requestHeader: RequestHeader = FakeRequest()

    val expected = "127.0.0.1"
    val result: IpAddress = failedVerificationAttemptService.trueClientIpAddress

    result.value shouldBe expected
  }
}
