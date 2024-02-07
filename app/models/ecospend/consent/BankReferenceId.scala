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

package models.ecospend.consent

import play.api.libs.json.{Format, Json}
import play.api.mvc.QueryStringBindable
import util.ValueClassBinder.queryStringValueBinder

final case class BankReferenceId(value: String) extends AnyVal

object BankReferenceId {
  implicit val format: Format[BankReferenceId] = Json.valueFormat[BankReferenceId]
  implicit val queryStringBindable: QueryStringBindable[BankReferenceId] = queryStringValueBinder(_.value)
}
