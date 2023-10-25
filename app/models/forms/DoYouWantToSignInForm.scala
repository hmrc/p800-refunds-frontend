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

package models.forms

import language.{Language, Messages}
import models.forms.enumsforforms.DoYouWantToSignInFormValue
import play.api.data.{Form, Forms}
import play.api.data.Forms.mapping
import util.EnumFormatter

object DoYouWantToSignInForm {
  def form(implicit language: Language): Form[DoYouWantToSignInFormValue] = {
    val doYouWantToSignInMapping = Forms.of(EnumFormatter.format(
      `enum`                  = DoYouWantToSignInFormValue,
      errorMessageIfMissing   = Messages.DoYouWantToSignInMessages.`Select yes if you want to sign in to your tax account`.show,
      errorMessageIfEnumError = Messages.DoYouWantToSignInMessages.`Select yes if you want to sign in to your tax account`.show
    ))

    Form(
      mapping = mapping(
        "sign-in" -> doYouWantToSignInMapping
      )(identity)(Some(_))
    )
  }
}
