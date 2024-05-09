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
  val auditString: String
}

sealed class SuccessfulMatch extends NameMatchingResponse {
  val isSuccess = true
  override val auditString: String = "" //will get overwritten
}

sealed class FailedMatch extends NameMatchingResponse {
  val isSuccess = false
  override val auditString: String = "" //will get overwritten

}

case object BasicSuccessfulNameMatch extends SuccessfulMatch {
  override val auditString = "basic name match"

}
case object FirstAndMiddleNameSuccessfulNameMatch extends SuccessfulMatch {
  override val auditString = "First and middle name match"
}

case object LevenshteinSuccessfulNameMatch extends SuccessfulMatch {
  override val auditString = "Levenshtein name match"
}

case object FailedSurnameMatch extends FailedMatch {
  override val auditString = "surname failed match"
}

case object FailedComprehensiveNameMatch extends FailedMatch {
  override val auditString = "comprehensive failed match"
}

final case class ComparisonResult(didNamesMatch: Boolean, npsNameWithInitials: String, ecospendNameWithInitials: String)
