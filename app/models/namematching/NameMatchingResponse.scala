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

sealed trait NameMatchingResponse

case object BasicSuccessfulNameMatch extends NameMatchingResponse
case object InitialsSuccessfulNameMatch extends NameMatchingResponse
case object LevenshteinSuccessfulNameMatch extends NameMatchingResponse
case object FailedNameMatch extends NameMatchingResponse

final case class ComparisonResult(didNamesMatch: Boolean, npsNameWithInitials: String, ecospendNameWithInitials: String)
