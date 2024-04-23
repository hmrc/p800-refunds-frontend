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

sealed trait NameMatchingResponse {
  val isSuccess: Boolean
}

sealed class SuccessfulMatch extends NameMatchingResponse {
  val isSuccess = true
}

case object BasicSuccessfulNameMatch extends SuccessfulMatch
case object InitialsSuccessfulNameMatch extends SuccessfulMatch
case object LevenshteinSuccessfulNameMatch extends SuccessfulMatch

case object FailedNameMatch extends NameMatchingResponse {
  override val isSuccess: Boolean = false
}

final case class ComparisonResult(didNamesMatch: Boolean, npsNameWithInitials: String, ecospendNameWithInitials: String)
