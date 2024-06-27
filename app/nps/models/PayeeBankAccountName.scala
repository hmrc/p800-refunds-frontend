/*
 * Copyright 2024 HM Revenue & Customs
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

package nps.models

import play.api.libs.json.{Format, Json}
import util.SafeEquals.EqualsOps

final case class PayeeBankAccountName(value: String) {
  def sanitisePayeeBankAccountName: PayeeBankAccountName = {
    val sanitisedValue: String =
      value
        .filter(x => x.isLetter || x === ' ')
        .trim
        .take(50)

    require(
      sanitisedValue.length > 0 && sanitisedValue.length <= 50,
      s"Unable to sanitize PayeeBankAccountName [value: ${value}, sanitisedValue: ${sanitisedValue}]"
    )

    PayeeBankAccountName(sanitisedValue)
  }
}

@SuppressWarnings(Array("org.wartremover.warts.Any"))
object PayeeBankAccountName {
  implicit val format: Format[PayeeBankAccountName] = Json.valueFormat[PayeeBankAccountName]
}
