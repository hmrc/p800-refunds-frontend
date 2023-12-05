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
import testsupport.stubs.WireMockHelpers._

object ReferenceValidationStub {

  val url = "/validate-reference"

  def validateReference2xxValid: StubMapping = stubForPostWithResponseBody(url, """{ "isValid": true }""")
  def validateReference2xxInvalid: StubMapping = stubForPostWithResponseBody(url, """{ "isValid": false }""")
}
