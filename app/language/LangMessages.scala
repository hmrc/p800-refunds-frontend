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

object LangMessages {
  val `Claim an income tax refund`: Message = Message(
    english = "Claim an income tax refund"
  )

  val doYouWantToSignInH1: Message = Message(
    english = "Do you want to sign in?"
  )

  val doYouWantToSignInHint: Message = Message(
    english = "Sign in with your Government Gateway user ID. You'll have fewer details to enter this way."
  )

  val `Yes, sign in`: Message = Message(
    english = "Yes, sign in"
  )

  val `No, continue without signing in`: Message = Message(
    english = "No, continue without signing in"
  )

  val continue: Message = Message(
    english = "Continue",
    welsh   = "Yn eich blaen"
  )
}
