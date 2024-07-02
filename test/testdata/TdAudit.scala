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

import models.audit.{NameMatchingAudit, NameMatchOutcome, RawNpsName, ChosenBank}
import models.ecospend.{BankId, BankName, BankFriendlyName}

object TdAudit {

  val failedSurnameAudit: NameMatchingAudit = NameMatchingAudit(
    outcome             = NameMatchOutcome(isSuccessful = false, "surname failed match"),
    rawNpsName          = RawNpsName(Some("Jennifer"), None, Some("Married")),
    rawBankName         = Some("Jennifer Maiden-Name"),
    transformedNpsName  = Some("jennifer married"),
    transformedBankName = Some("jennifer maiden name"),
    chosenBank          = ChosenBank(
      bankId       = BankId("obie-barclays-personal"),
      name         = BankName("Barclays Personal"),
      friendlyName = BankFriendlyName("Barclays Personal")
    )
  )

  val failedFirstAndMiddleNameAudit: NameMatchingAudit = NameMatchingAudit(
    outcome             = NameMatchOutcome(isSuccessful = false, "first and middle name failed match"),
    rawNpsName          = RawNpsName(Some("K"), None, Some("Turner")),
    rawBankName         = Some("Jennifer Turner"),
    transformedNpsName  = Some("k turner"),
    transformedBankName = Some("j turner"),
    chosenBank          = ChosenBank(
      bankId       = BankId("obie-barclays-personal"),
      name         = BankName("Barclays Personal"),
      friendlyName = BankFriendlyName("Barclays Personal")
    )
  )

  val failedComprehensiveAudit: NameMatchingAudit = NameMatchingAudit(
    outcome             = NameMatchOutcome(isSuccessful = false, "comprehensive failed match"),
    rawNpsName          = RawNpsName(Some("Kate"), Some("Jenny"), Some("Turner")),
    rawBankName         = Some("Jennifer Kate Turner"),
    transformedNpsName  = Some("kate jenny turner"),
    transformedBankName = Some("jennifer kate turner"),
    chosenBank          = ChosenBank(
      bankId       = BankId("obie-barclays-personal"),
      name         = BankName("Barclays Personal"),
      friendlyName = BankFriendlyName("Barclays Personal")
    ),
    levenshteinDistance = Some(12)
  )

  val successfulBasicAudit: NameMatchingAudit = NameMatchingAudit(
    outcome             = NameMatchOutcome(isSuccessful = true, "basic name match"),
    rawNpsName          = RawNpsName(Some("T"), None, Some("Patel")),
    rawBankName         = Some("T Patel"),
    transformedNpsName  = Some("t patel"),
    transformedBankName = Some("t patel"),
    chosenBank          = ChosenBank(
      bankId       = BankId("obie-barclays-personal"),
      name         = BankName("Barclays Personal"),
      friendlyName = BankFriendlyName("Barclays Personal")
    )
  )

  val successfulFirstMiddleAudit: NameMatchingAudit = NameMatchingAudit(
    outcome             = NameMatchOutcome(isSuccessful = true, "first and middle name match"),
    rawNpsName          = RawNpsName(Some("Paul"), Some("James"), Some("Rubens")),
    rawBankName         = Some("Paul James John Rubens"),
    transformedNpsName  = Some("paul james rubens"),
    transformedBankName = Some("paul james rubens"),
    chosenBank          = ChosenBank(
      bankId       = BankId("obie-barclays-personal"),
      name         = BankName("Barclays Personal"),
      friendlyName = BankFriendlyName("Barclays Personal")
    )
  )

  val successfulLevenshteinAudit: NameMatchingAudit = NameMatchingAudit(
    outcome             = NameMatchOutcome(isSuccessful = true, "levenshtein name match"),
    rawNpsName          = RawNpsName(Some("Paula"), Some("James"), Some("Rubens")),
    rawBankName         = Some("Paul James Rubens"),
    transformedNpsName  = Some("paula james rubens"),
    transformedBankName = Some("paul james rubens"),
    chosenBank          = ChosenBank(
      bankId       = BankId("obie-barclays-personal"),
      name         = BankName("Barclays Personal"),
      friendlyName = BankFriendlyName("Barclays Personal")
    ),
    levenshteinDistance = Some(1)
  )
}
