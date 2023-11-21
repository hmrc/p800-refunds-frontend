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

import language.{Language, Messages, Message}
import models.FullName
import play.api.data.Forms.mapping
import play.api.data.format.Formatter
import play.api.data.{Form, FormError, Forms}
import java.util.regex.Pattern

object WhatIsYourFullNameForm {
  val fullNameRegexPattern: Pattern = Pattern.compile("^[A-Za-z .'-]{1,160}$")

  def validFullName(value: String): Boolean =
    fullNameRegexPattern.matcher(value).matches()

  def invalidFullNameError(input: String): Message = {
    val symbols: Array[Char] = input.trim
      .replaceAll("[a-zA-Z .'-]*", "")
      .toCharArray

    symbols match {
      case Array(x)    => Messages.WhatIsYourFullName.`Name must not include X`(x.toString)
      case Array(x, y) => Messages.WhatIsYourFullName.`Name must not include X`(s"${x.toString} or ${y.toString}")
      case _           => Messages.WhatIsYourFullName.`Full name must only include letters a to z, and special characters such as hyphens, spaces and apostrophes`
    }
  }

  def form(implicit language: Language): Form[FullName] = {
    val fullNameMapping = Forms.of(new Formatter[FullName]() {
      override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], FullName] =
        data.get(key) match {
          case Some(value) if value.trim.length < 2     => Left(Seq(FormError(key, Messages.WhatIsYourFullName.`Full name must be 2 characters or more`.show)))
          case Some(value) if value.trim.length > 160   => Left(Seq(FormError(key, Messages.WhatIsYourFullName.`Full name must be 160 characters or less`.show)))
          case Some(value) if validFullName(value.trim) => Right(FullName(value))
          case Some(value)                              => Left(Seq(FormError(key, invalidFullNameError(value.trim).show)))
          case None                                     => Left(Seq(FormError(key, Messages.WhatIsYourFullName.`Full name must be 2 characters or more`.show)))
        }

      override def unbind(key: String, value: FullName): Map[String, String] = Map(key -> value.value)
    })

    Form(
      mapping = mapping(
        "fullName" -> fullNameMapping
      )(identity)(Some(_))
    )
  }
}
