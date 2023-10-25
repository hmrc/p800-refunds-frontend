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

package forms

import language.{ErrorMessages, Language}
import play.api.data.Form
import play.api.data.Forms.{boolean, mapping, optional}

final case class DoYouWantToSignInForm(signIn: Boolean)

object DoYouWantToSignInForm {
  def form(implicit language: Language): Form[DoYouWantToSignInForm] = Form(
    mapping = mapping(
      "sign-in" -> optional(boolean)
        .verifying(ErrorMessages.`Select yes if you want to sign in to your tax account`.show, _.isDefined)
        .transform[Boolean](_.getOrElse(throw new Exception("Unexpected None value for Option, this should never happen!")), Some(_))
    )(DoYouWantToSignInForm.apply)(DoYouWantToSignInForm.unapply)
  )
}
