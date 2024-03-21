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
import models.datecalculator.DateCalculatorRequest

object DateCalculatorStub {
  val addWorkingDaysUrl: String = "/date-calculator/add-working-days"

  def addWorkingDays(): StubMapping = WireMockHelpers.Post.stubForPost(addWorkingDaysUrl, """{ "result": "2059-11-30" }""", 200)

  def addWorkingDaysBadRequest(): StubMapping = WireMockHelpers.Post.stubForPost(addWorkingDaysUrl, "", 400)

  def addWorkingDaysUnprocessableEntity(): StubMapping = WireMockHelpers.Post.stubForPost(addWorkingDaysUrl, "", 422)

  def addWorkingDaysServerError(): StubMapping = WireMockHelpers.Post.stubForPost(addWorkingDaysUrl, "", 500)

  def verifyAddWorkingDays(count: Int = 1): Unit = WireMockHelpers.Post.verifyExactlyWithBodyParse(addWorkingDaysUrl, count)(DateCalculatorRequest.format)

  def verifyNoneAddWorkingDays(): Unit = WireMockHelpers.Post.verifyNone(addWorkingDaysUrl)

}
