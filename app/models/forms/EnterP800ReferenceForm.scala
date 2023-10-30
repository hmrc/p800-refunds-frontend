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
import models.P800Reference
import play.api.data.Forms.mapping
import play.api.data.format.Formatter
import play.api.data.{Form, FormError, Forms}

object EnterP800ReferenceForm {
  def form(implicit langauge: Language): Form[P800Reference] = {
    val p800ReferenceMapping = Forms.of(new Formatter[P800Reference]() {
      override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], P800Reference] =
        data.get(key) match {
          case Some(value) if value.trim.length < 1  => Left(Seq(FormError(key, Messages.EnterP800ReferenceMessages.`Enter your P800 reference`.show)))
          case Some(value) if value.trim.length > 16 => Left(Seq(FormError(key, Messages.EnterP800ReferenceMessages.`Enter your P800 reference in the correct format`.show)))
          case Some(value)                           => Right(P800Reference(value))
          case None                                  => Left(Seq(FormError(key, Messages.EnterP800ReferenceMessages.`Enter your P800 reference`.show)))
        }

      override def unbind(key: String, value: P800Reference): Map[String, String] = Map(key -> value.value)
    })

    Form(
      mapping = mapping(
        "reference" -> p800ReferenceMapping
      )(identity)(Some(_))
    )
  }
}
