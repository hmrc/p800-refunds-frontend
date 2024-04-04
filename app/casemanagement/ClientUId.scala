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

package casemanagement

import play.api.libs.json.{Format, Json}

import scala.util.matching.Regex

final case class ClientUId(value: String) {
  def validate: Option[String] = if (ClientUId.regex.matches(value) && value.length <= 36) None else Some("Invalid 'ClientUId'")
}

object ClientUId {
  implicit val format: Format[ClientUId] = Json.valueFormat[ClientUId]
  val regex: Regex = """^[A-Za-z0-9\- ]*$""".r
}
