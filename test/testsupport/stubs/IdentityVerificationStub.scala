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

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import models.{IdentityVerificationRequest, IdentityVerificationResponse}
import play.api.http.Status
import play.api.libs.json.Json

object IdentityVerificationStub {

  val url = "/verify-identity"

  def stubIdentityVerification2xx(response: IdentityVerificationResponse): StubMapping =
    WireMockHelpers.stubForPostWithResponseBody(
      url,
      Json.prettyPrint(Json.toJson(response))
    )

  //todo once we have specs, update these and use in tests.
  def stubIdentityVerification5xxBadGateway: StubMapping = WireMockHelpers.stubForPostNoResponseBody(url, Status.BAD_GATEWAY)
  def stubIdentityVerification5xxServiceUnavailable: StubMapping = WireMockHelpers.stubForPostNoResponseBody(url, Status.SERVICE_UNAVAILABLE)
  def stubIdentityVerification5xxGatewayTimeout: StubMapping = WireMockHelpers.stubForPostNoResponseBody(url, Status.GATEWAY_TIMEOUT)

  def verifyIdentityVerification(): Unit = WireMockHelpers.verifyExactlyWithBodyParse(url, 1)(IdentityVerificationRequest.format)
  def verifyNoneIdentityVerification(): Unit = WireMockHelpers.verifyNone(url)

}
