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

package testsupport.wiremock

import com.github.tomakehurst.wiremock.stubbing.Scenario

object WireMockStates {
  def state(index: Int): String = if (index == 0) Scenario.STARTED else index.toString
  def nextState(index: Int): String = (index + 1).toString
  def endState(index: Int, size: Int): String = if (size == 1) Scenario.STARTED else (if (index + 1 >= size) index else index + 1).toString

  val p800RefundsUpsert: String = "p800-refunds-frontend-upsert"
  val p800RefundsFind: String = "p800-refunds-frontend-find"
}

