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
    RawNpsName(Some("Jennifer"), None, "Married"),
    "Jennifer Maiden-Name",
    "jennifer married",
    "jennifer maiden name"
  )

  val failedComprehensiveAudit: NameMatchingAudit = NameMatchingAudit(
    NameMatchOutcome(isSuccessful = false, "comprehensive failed match"),
    RawNpsName(Some("K"), Some("J"), "Turner"),
    "Jennifer Kate Turner",
    "k j turner",
    "j k turner",
    Some(2)
  )

  val successfulBasicAudit: NameMatchingAudit = NameMatchingAudit(
    NameMatchOutcome(isSuccessful = true, "basic name match"),
    RawNpsName(Some("T"), None, "Patel"),
    "T Patel",
    "t patel",
    "t patel"
  )

  val successfulFirstMiddleAudit: NameMatchingAudit = NameMatchingAudit(
    NameMatchOutcome(isSuccessful = true, "First and middle name match"),
    RawNpsName(Some("Paul"), Some("James"), "Rubens"),
    "Paul James John Rubens",
    "paul james rubens",
    "paul james rubens"
  )

  val successfulLevenshteinAudit: NameMatchingAudit = NameMatchingAudit(
    NameMatchOutcome(isSuccessful = true, "Levenshtein name match"),
    RawNpsName(Some("Paula"), Some("James"), "Rubens"),
    "Paul James Rubens",
    "paula james rubens",
    "paul james rubens",
    Some(1)
  )
}
