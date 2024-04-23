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

package services

import edh.Postcode
import models.namematching.{BasicSuccessfulNameMatch, ComparisonResult, FailedNameMatch, InitialsSuccessfulNameMatch, LevenshteinSuccessfulNameMatch}
import nps.models.TraceIndividualResponse
import testsupport.UnitSpec

class NameMatchingServiceSpec extends UnitSpec {

  val testNameMatchingService = new NameMatchingService

  def buildTrace(firstName: Option[String], middleName: Option[String], surname: String): TraceIndividualResponse = {
    TraceIndividualResponse(None, firstName, middleName, surname, "addressl1", "addressl2", Postcode("AA1 1AA"))
  }

  val testScenarios = Seq(
    (buildTrace(Some("Jennifer"), None, "Married"), "Jennifer Maiden-Name", FailedNameMatch),
    (buildTrace(Some("K"), Some("J"), "Turner"), "Jennifer Kate Turner", FailedNameMatch),
    (buildTrace(Some("Tom"), None, "Griffith"), "Thomas Griffith", FailedNameMatch),
    (buildTrace(Some("A"), None, "Shone"), "T Patel", FailedNameMatch),
    (buildTrace(Some("Paul"), Some("James"), "Rubens"), "Paul James Robins", FailedNameMatch),
    (buildTrace(Some("Tina"), None, "Patel"), "Tara Patel", FailedNameMatch),
    (buildTrace(Some("T"), None, "Patel"), "T Patel", BasicSuccessfulNameMatch),
    (buildTrace(Some("P"), None, "Rubens"), "Paul James Rubens", InitialsSuccessfulNameMatch),
    (buildTrace(Some("P"), Some("J"), "Rubens"), "Paul James Rubens", InitialsSuccessfulNameMatch),
    (buildTrace(Some("P"), Some("James"), "Rubens"), "Paul James Rubens", InitialsSuccessfulNameMatch),
    (buildTrace(Some("Paul"), Some("J"), "Rubens"), "Paul James Rubens", InitialsSuccessfulNameMatch),
    (buildTrace(Some("Paula"), Some("James"), "Rubens"), "Paul James Rubens", LevenshteinSuccessfulNameMatch),
    (buildTrace(Some("Paul"), Some("James"), "Rubens"), "Paul James Rubens", BasicSuccessfulNameMatch),
    (buildTrace(Some("Paul"), Some("James"), "Rubens Smith"), "Paul James Rubens-Smith", BasicSuccessfulNameMatch),
    (buildTrace(Some("Paul"), Some("James John"), "Rubens"), "Paul James Rubens", InitialsSuccessfulNameMatch),
    (buildTrace(Some("Paul"), Some("James"), "Rubens"), "Paul James John Rubens", InitialsSuccessfulNameMatch),
    (buildTrace(Some("Sõnekâëí"), Some("   Francis    "), "Akpawe   "), "Sonekaei Francis Akpawe", BasicSuccessfulNameMatch)
  )

  testScenarios.foreach{ tuple =>
    val (npsName, ecoName, expectedOutput) = tuple
    val npsFullName = testNameMatchingService.buildNpsName(npsName)
    s"fuzzy matching should return ${expectedOutput.toString} for NpsName:$npsFullName & EcospendName:$ecoName" in {
      val result = testNameMatchingService.fuzzyNameMatching(npsName, ecoName)
      result shouldBe expectedOutput
    }
  }

  "remove diacritics should do return a string without special accents" in {
    val result = testNameMatchingService.removeDiacritics("ÀÁÂÃÄÈÉÊËÍÌÎÏÙÚÛÜÒÓÔÕÖÑÇªº§³²¹àáâãäèéêëíìîïùúûüòóôõöñç")
    result shouldBe "AAAAAEEEEIIIIUUUUOOOOONCaaaaaeeeeiiiiuuuuooooonc"
  }

  "sanitiseName should remove hyphens and return the string in lowercase" in {
    val result = testNameMatchingService.sanitiseName("Ichio-James Jingle-Son")
    result shouldBe "ichiojames jingleson"
  }

  "sanitiseName should remove white spaces and return the string in lowercase" in {
    val result = testNameMatchingService.sanitiseName("  Timothy   Delamaine  Panam    ")
    result shouldBe "timothy delamaine panam"
  }

  "isWithinLevenshteinDistance should return LevenshteinSuccessfulNameMatch for a distance of 0 if the names are the same" in {
    val result = testNameMatchingService.isWithinLevenshteinDistance("Antony Ode Kino", "Antony Ode Kino")
    result shouldBe LevenshteinSuccessfulNameMatch
  }

  "isWithinLevenshteinDistance should return LevenshteinSuccessfulNameMatch for a distance of 1" in {
    val result = testNameMatchingService.isWithinLevenshteinDistance("Antony Ode Kina", "Antony Ode Kino")
    result shouldBe LevenshteinSuccessfulNameMatch
  }

  "isWithinLevenshteinDistance should return FailedNameMatch for a distance 2" in {
    val result = testNameMatchingService.isWithinLevenshteinDistance("Hello", "Hela")
    result shouldBe FailedNameMatch
  }

  "isWithinLevenshteinDistance should return FailedNameMatch when same letter appears multiple times" in {
    val result = testNameMatchingService.isWithinLevenshteinDistance("OooooO", "o")
    result shouldBe FailedNameMatch
  }

  "compareFirstAndMiddleNames should return 'didNamesMatch = false' if the first names are different but share the same initial" in {
    val result = testNameMatchingService.compareFirstAndMiddleNames(Array("Jane", "Kasveko"), Array("Josie", "Kasveko"))
    val expectedOutcome = ComparisonResult(
      didNamesMatch            = false,
      npsNameWithInitials      = "Jane Kasveko",
      ecospendNameWithInitials = "Josie Kasveko"
    )

    result shouldBe expectedOutcome
  }

  "compareFirstAndMiddleNames should return 'didNamesMatch = false' for different middle names with the same initials" in {
    val result = testNameMatchingService.compareFirstAndMiddleNames(
      Array("Lowe", "Jane", "Kasveko"),
      Array("Lowe", "Josie", "Kasveko")
    )

    val expectedOutcome = ComparisonResult(
      didNamesMatch            = false,
      npsNameWithInitials      = "Lowe Jane Kasveko",
      ecospendNameWithInitials = "Lowe Josie Kasveko"
    )

    result shouldBe expectedOutcome
  }

  "compareFirstAndMiddleNames should return 'didNamesMatch = true' where one name is just an initial that matches the corresponding middle name" in {
    val result = testNameMatchingService.compareFirstAndMiddleNames(
      Array("Lowe", "J", "Kasveko"),
      Array("Lowe", "Josie", "Kasveko")
    )

    val expectedOutcome = ComparisonResult(
      didNamesMatch            = true,
      npsNameWithInitials      = "Lowe J Kasveko",
      ecospendNameWithInitials = "Lowe J Kasveko"
    )

    result shouldBe expectedOutcome
  }

  "compareFirstAndMiddleNames should return 'didNamesMatch = true' if the names match and there is only an Initial " in {
    val result = testNameMatchingService.compareFirstAndMiddleNames(Array("J"), Array("J"))
    val expectedOutcome = ComparisonResult(
      didNamesMatch            = true,
      npsNameWithInitials      = "J",
      ecospendNameWithInitials = "J"
    )

    result shouldBe expectedOutcome
  }

  "compareFirstAndMiddleNames should convert multiple names to initials and ignore excess names" in {
    val result = testNameMatchingService.compareFirstAndMiddleNames(
      Array("A", "Kinte", "J", "Kasveko", "IGNORED"),
      Array("Odie", "Kinte", "Josie", "Kasveko")
    )

    val expectedOutcome = ComparisonResult(
      didNamesMatch            = false,
      npsNameWithInitials      = "A Kinte J Kasveko",
      ecospendNameWithInitials = "O Kinte J Kasveko"
    )

    result shouldBe expectedOutcome
  }
}
