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
import models.UserEnteredP800Reference
import play.api.data.Forms.mapping
import play.api.data.format.Formatter
import play.api.data.{Form, FormError, Forms}
import util.SafeEquals.EqualsOps

import scala.util.matching.Regex

object EnterP800ReferenceForm {

  val allowedSpecialCharacters: Set[Char] = Set(' ', '-', ',')
  private val referenceMinLength: Int = 1
  private val referenceMaxLength: Int = 10
  private val allowedCharactersRegex: Regex = "^[0-9,-]+$".r

  private def trimLeadingZeros(str: String): String = str.dropWhile(_ === '0')
  private def isWithinBounds(str: String): Boolean = str.length <= referenceMaxLength && str.length >= referenceMinLength

  def form(implicit langauge: Language): Form[UserEnteredP800Reference] = {
    val p800ReferenceMapping = Forms.of(new Formatter[UserEnteredP800Reference]() {
      override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], UserEnteredP800Reference] = {

        data.get(key).fold[Either[Seq[FormError], UserEnteredP800Reference]] {
          Left(Seq(FormError(key, Messages.EnterP800ReferenceMessages.`Enter your P800 reference`.show)))
        } { referenceEntered: String =>

          val referenceContainsNotAllowedCharacter: Boolean = !referenceEntered.replaceAll(" ", "").matches(allowedCharactersRegex.regex)
          val attemptAtSanitising: String = trimLeadingZeros(referenceEntered.filter(!allowedSpecialCharacters.contains(_))).filter(_.isDigit)

          if (referenceEntered.trim.isEmpty) {
            Left(Seq(FormError(key, Messages.EnterP800ReferenceMessages.`Enter your P800 reference`.show)))
          } else if (referenceContainsNotAllowedCharacter) {
            Left(Seq(FormError(key, Messages.EnterP800ReferenceMessages.`Enter your P800 reference in the correct format`.show)))
          } else if (!isWithinBounds(attemptAtSanitising)) {
            Left(Seq(FormError(key, Messages.EnterP800ReferenceMessages.`Enter your P800 reference in the correct format`.show)))
          } else Right(UserEnteredP800Reference(referenceEntered))
        }
      }

      override def unbind(key: String, value: UserEnteredP800Reference): Map[String, String] = Map(key -> value.value)
    })

    Form(
      mapping = mapping(
        "reference" -> p800ReferenceMapping
      )(identity)(Some(_))
    )
  }
}
