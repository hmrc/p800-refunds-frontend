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
import models.Nino
import play.api.data.Forms.mapping
import play.api.data.format.Formatter
import play.api.data.{Form, FormError, Forms}
import java.util.regex.Pattern

object WhatIsYourNationalInsuranceNumberForm {
  private val nationalInsuranceNumberPattern: Pattern =
    Pattern.compile("(?!BG)(?!GB)(?!NK)(?!KN)(?!TN)(?!NT)(?!ZZ)(?:[A-CEGHJ-PR-TW-Z][A-CEGHJ-NPR-TW-Z])(?:\\s*\\d\\s*){6}([A-D]|\\s)")

  private def validNationalInsuranceNumber(value: String): Boolean =
    nationalInsuranceNumberPattern.matcher(value).matches()

  private def cleanInput(input: Option[String]): Option[String] =
    input.map(_.replaceAll("[^0-9a-zA-Z]", "").toUpperCase)

  def form(implicit language: Language): Form[Nino] = {
    val nationalInsuranceNumberMapping = Forms.of(new Formatter[Nino] {
      override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Nino] =
        cleanInput(data.get(key)) match {
          case Some(value) if value.trim.isEmpty => Left(Seq(FormError(key, Messages.WhatIsYourNationalInsuranceNumber.`Enter your National Insurance number`.show)))
          case Some(value) if validNationalInsuranceNumber(value) => Right(Nino(value))
          case Some(_) => Left(Seq(FormError(key, Messages.WhatIsYourNationalInsuranceNumber.`Enter your National Insurance number in the correct format`.show)))
          case None => Left(Seq(FormError(key, Messages.WhatIsYourNationalInsuranceNumber.`Enter your National Insurance number in the correct format`.show)))
        }

      override def unbind(key: String, value: Nino): Map[String, String] = Map(key -> value.value)
    })

    Form(
      mapping = mapping(
        "nationalInsuranceNumber" -> nationalInsuranceNumberMapping
      )(identity)(Some(_))
    )
  }
}
