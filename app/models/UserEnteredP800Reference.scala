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

package models

import models.forms.EnterP800ReferenceForm
import util.SafeEquals.EqualsOps
import play.api.libs.json.{Format, Json}

final case class UserEnteredP800Reference(value: String) {

  def sanitiseReference: P800Reference = {
    val charactersThatAreAllowedFromForm: Set[Char] = EnterP800ReferenceForm.allowedSpecialCharacters
    P800Reference(
      value
        .trim
        .replaceAll(" ", "")
        .filter(!charactersThatAreAllowedFromForm.contains(_))
        .filter(_.isDigit)
        .dropWhile(_ === '0')
        .toInt
    )
  }
}

object UserEnteredP800Reference {
  implicit val formats: Format[UserEnteredP800Reference] = Json.valueFormat[UserEnteredP800Reference]
}

