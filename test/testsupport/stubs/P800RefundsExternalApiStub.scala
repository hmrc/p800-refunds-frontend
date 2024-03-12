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

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import models.p800externalapi.EventValue
import play.api.http.Status
import play.api.libs.json.Json

object P800RefundsExternalApiStub {

  def isValidUrl(renameMe: String): String = s"/is-valid/$renameMe"

  def isValid(renameMe: String, eventValue: EventValue): StubMapping = {
    WireMockHelpers.Get.stubForGetWithResponseBody(
      url            = isValidUrl(renameMe),
      responseBody   = Json.prettyPrint(Json.toJson(eventValue)),
      responseStatus = Status.OK
    )
  }

  def verifyIsValid(renameMe: String, count: Int = 1): Unit = WireMockHelpers.Get.verifyGetNoHeaders(isValidUrl(renameMe), count)
  def verifyNoneIsValid(renameMe: String): Unit = WireMockHelpers.Get.verifyNone(isValidUrl(renameMe))

}
