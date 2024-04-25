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

import play.api.libs.json.{Json, OFormat}

final case class ValidateP800ReferenceFailures(
    failures: List[Failure]
)

object ValidateP800ReferenceFailures {
  implicit val format: OFormat[ValidateP800ReferenceFailures] = Json.format[ValidateP800ReferenceFailures]
}

final case class Failure(reason: String, code: String)

object Failure {

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val format: OFormat[Failure] = Json.format[Failure]
}

