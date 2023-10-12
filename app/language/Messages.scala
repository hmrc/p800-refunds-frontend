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

package language

object Messages {

  val `Example message.`: Message = Message(
    english = "Example message",
    welsh   = "Example welsh message"
  )

  val `Another example message`: Message = Message(
    english = "Another example message",
    welsh   = "Another welsh example message"
  )

  val `Final example message`: Message = Message(
    english = "Final example message",
    welsh   = "Final example message in welsh"
  )

  object CommonMessages {
    val back: Message = Message(
      english = "Back",
      welsh   = "Yn ôl"
    )

    val error: Message = Message(
      english = "Error: ",
      welsh   = "Gwall: "
    )
  }

  object ServicePhase {

    val serviceName: Message = Message(
      english = "Claim an income tax refund"
    )

    val beta: Message = Message(
      english = "beta",
      welsh   = "beta"
    )

    def bannerText(link: String): Message = Message(
      english = s"""This is a new service – your <a class="govuk-link" href="$link">feedback</a> will help us to improve it.""",
      welsh   = s"""Mae hwn yn wasanaeth newydd – bydd eich <a class="govuk-link" href="$link">adborth</a> yn ein helpu i’w wella."""
    )
  }

}
