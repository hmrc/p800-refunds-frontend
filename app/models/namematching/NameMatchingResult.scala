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

package models.namematching

import play.api.libs.json.{Json, Format}

/**
 * Represents the result of a name maching check for storage in the Journey.
 *
 * true is a name matching success.
 * false is a name matching failure.
 */
final case class NameMatchingResult(value: Boolean) extends AnyVal

object NameMatchingResult {
  implicit val format: Format[NameMatchingResult] = Json.valueFormat[NameMatchingResult]
}
