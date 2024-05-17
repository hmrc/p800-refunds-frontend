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

package testdata

import models.audit.{NameMatchingAudit, NameMatchOutcome, RawNpsName}

object TdAudit {

  val failedSurnameAudit: NameMatchingAudit = NameMatchingAudit(
    NameMatchOutcome(isSuccessful = false, "surname failed match"),
    RawNpsName(Some("Jennifer"), None, Some("Married")),
    Some("Jennifer Maiden-Name"),
    Some("jennifer married"),
    Some("jennifer maiden name")
  )

  val failedFirstAndMiddleNameAudit: NameMatchingAudit = NameMatchingAudit(
    NameMatchOutcome(isSuccessful = false, "first and middle name failed match"),
    RawNpsName(Some("K"), None, Some("Turner")),
    Some("Jennifer Turner"),
    Some("k turner"),
    Some("j turner")
  )

  val failedComprehensiveAudit: NameMatchingAudit = NameMatchingAudit(
    NameMatchOutcome(isSuccessful = false, "comprehensive failed match"),
    RawNpsName(Some("Kate"), Some("Jenny"), Some("Turner")),
    Some("Jennifer Kate Turner"),
    Some("kate jenny turner"),
    Some("jennifer kate turner"),
    Some(12)
  )

  val successfulBasicAudit: NameMatchingAudit = NameMatchingAudit(
    NameMatchOutcome(isSuccessful = true, "basic name match"),
    RawNpsName(Some("T"), None, Some("Patel")),
    Some("T Patel"),
    Some("t patel"),
    Some("t patel")
  )

  val successfulFirstMiddleAudit: NameMatchingAudit = NameMatchingAudit(
    NameMatchOutcome(isSuccessful = true, "first and middle name match"),
    RawNpsName(Some("Paul"), Some("James"), Some("Rubens")),
    Some("Paul James John Rubens"),
    Some("paul james rubens"),
    Some("paul james rubens")
  )

  val successfulLevenshteinAudit: NameMatchingAudit = NameMatchingAudit(
    NameMatchOutcome(isSuccessful = true, "levenshtein name match"),
    RawNpsName(Some("Paula"), Some("James"), Some("Rubens")),
    Some("Paul James Rubens"),
    Some("paula james rubens"),
    Some("paul james rubens"),
    Some(1)
  )
}
