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

package models

import play.api.libs.json.{Format, Json}

final case class P800Reference(value: Long) extends AnyVal {
  // NPS accepts p800 refs between 1 and 2147483646, we let a user enter a higher number, but it would always fail the NPS call
  def withinNpsBounds: Boolean = value > 0 && value <= 2147483646
}

object P800Reference {
  implicit val format: Format[P800Reference] = Json.valueFormat[P800Reference]
}

