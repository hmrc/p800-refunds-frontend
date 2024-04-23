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

import action.JourneyRequest
import models.CorrelationId
import models.journeymodels.{HasFinished, IsChanging, Journey, JourneyId}
import models.namematching._
import play.api.mvc.AnyContentAsEmpty
import testdata.TdRequest
import testsupport.UnitSpec

import java.time.Instant
import java.util.UUID

class NameMatchingServiceSpec extends UnitSpec with TdRequest {

  lazy val journey: Journey = Journey(
    _id                           = JourneyId("64886ed616fe8b501cbf0088"),
    createdAt                     = Instant.now(),
    correlationId                 = CorrelationId(UUID.randomUUID()),
    hasFinished                   = HasFinished.No,
    journeyType                   = None,
    p800Reference                 = None,
    nino                          = None,
    isChanging                    = IsChanging.No,
    dateOfBirth                   = None,
    referenceCheckResult          = None,
    traceIndividualResponse       = None,
    bankDescription               = None,
    bankConsentResponse           = None,
    bankAccountSummary            = None,
    isValidEventValue             = None,
    bankDetailsRiskResultResponse = None
  )

  val nameMatchingService = new NameMatchingService
  implicit val requestHeader: JourneyRequest[AnyContentAsEmpty.type] = new JourneyRequest[AnyContentAsEmpty.type](journey, fakeRequest)

  val testScenarios = Seq(
    (Some("Jennifer"), None, "Married", "Jennifer Maiden-Name", FailedNameMatch),
    (Some("K"), Some("J"), "Turner", "Jennifer Kate Turner", FailedNameMatch),
    (Some("Tom"), None, "Griffith", "Thomas Griffith", FailedNameMatch),
    (Some("A"), None, "Shone", "T Patel", FailedNameMatch),
    (Some("Paul"), Some("James"), "Rubens-Smith", "Paul James Rubens Smith", FailedNameMatch),
    (Some("Paul"), Some("James"), "Rubens", "Paul James Robins", FailedNameMatch),
    (Some("Tina"), None, "Patel", "Tara Patel", FailedNameMatch),
    (Some("T"), None, "Patel", "T Patel", BasicSuccessfulNameMatch),
    (Some("P"), None, "Rubens", "Paul James Rubens", InitialsSuccessfulNameMatch),
    (Some("P"), Some("J"), "Rubens", "Paul James Rubens", InitialsSuccessfulNameMatch),
    (Some("P"), Some("James"), "Rubens", "Paul James Rubens", InitialsSuccessfulNameMatch),
    (Some("Paul"), Some("J"), "Rubens", "Paul James Rubens", InitialsSuccessfulNameMatch),
    (Some("Paula"), Some("James"), "Rubens", "Paul James Rubens", LevenshteinSuccessfulNameMatch),
    (Some("Paul"), Some("James"), "Rubens", "Paul James Rubens", BasicSuccessfulNameMatch),
    (Some("Paul"), Some("James"), "Rubens Smith", "Paul James Rubens-Smith", BasicSuccessfulNameMatch),
    (Some("Paul Janes"), None, "Rubens Smith", "Paul-Janes Rubens-Smith", BasicSuccessfulNameMatch),
    (Some("Paul"), Some("James John"), "Rubens", "Paul James Rubens", InitialsSuccessfulNameMatch),
    (Some("Paul"), Some("James"), "Rubens", "Paul James John Rubens", InitialsSuccessfulNameMatch),
    (Some("Sõnekâëí"), Some("   Francis    "), "Akpawe   ", "Sonekaei Francis Akpawe", BasicSuccessfulNameMatch)
  )

  testScenarios.foreach{ tuple =>
    val (npsFirstName, npsSecondName, npsSurname, ecoName, expectedOutput) = tuple
    val npsFullName = s"${npsFirstName.getOrElse("")} ${npsSecondName.getOrElse("")} $npsSurname"

    s"fuzzy matching should return ${expectedOutput.toString} for NpsName:$npsFullName & EcospendName:$ecoName" in {
      val result = nameMatchingService.fuzzyNameMatching(npsFirstName, npsSecondName, npsSurname, ecoName)
      result shouldBe expectedOutput
    }
  }

  "remove diacritics should do return a string without special accents" in {
    val result = nameMatchingService.removeDiacritics("ÀÁÂÃÄÈÉÊËÍÌÎÏÙÚÛÜÒÓÔÕÖÑÇªº§³²¹àáâãäèéêëíìîïùúûüòóôõöñç")
    result shouldBe "AAAAAEEEEIIIIUUUUOOOOONCaaaaaeeeeiiiiuuuuooooonc"
  }

  "sanitiseName should remove hyphens and return the string in lowercase" in {
    val result = nameMatchingService.sanitiseFullName("Ichio-James Jingle-Son")
    result shouldBe "ichiojames jingleson"
  }

  "sanitiseName should remove white spaces and return the string in lowercase" in {
    val result = nameMatchingService.sanitiseFullName("  Timothy   Delamaine  Panam    ")
    result shouldBe "timothy delamaine panam"
  }

  "sanitiseName should replace tabs with spaces" in {
    val result = nameMatchingService.sanitiseFullName("John\tSmith")
    result shouldBe "john smith"
  }

  "isWithinLevenshteinDistance should return LevenshteinSuccessfulNameMatch for a distance of 0 if the names are the same" in {
    val result = nameMatchingService.isWithinLevenshteinDistance("Antony Ode Kino", "Antony Ode Kino")
    result shouldBe LevenshteinSuccessfulNameMatch
  }

  "isWithinLevenshteinDistance should return LevenshteinSuccessfulNameMatch for a distance of 1" in {
    val result = nameMatchingService.isWithinLevenshteinDistance("Antony Ode Kina", "Antony Ode Kino")
    result shouldBe LevenshteinSuccessfulNameMatch
  }

  "isWithinLevenshteinDistance should return FailedNameMatch for a distance 2" in {
    val result = nameMatchingService.isWithinLevenshteinDistance("Hello", "Hela")
    result shouldBe FailedNameMatch
  }

  "isWithinLevenshteinDistance should return FailedNameMatch when same letter appears multiple times" in {
    val result = nameMatchingService.isWithinLevenshteinDistance("oooo", "o")
    result shouldBe FailedNameMatch
  }

  "compareFirstAndMiddleNames should return 'didNamesMatch = false' if the first names are different but share the same initial" in {
    val result = nameMatchingService.compareFirstAndMiddleNames(Array("Jane", "Kasveko"), Array("Josie", "Kasveko"))
    val expectedOutcome = ComparisonResult(
      didNamesMatch            = false,
      npsNameWithInitials      = "Jane Kasveko",
      ecospendNameWithInitials = "Josie Kasveko"
    )

    result shouldBe expectedOutcome
  }

  "compareFirstAndMiddleNames should return 'didNamesMatch = false' for different middle names with the same initials" in {
    val result = nameMatchingService.compareFirstAndMiddleNames(
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
    val result = nameMatchingService.compareFirstAndMiddleNames(
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
    val result = nameMatchingService.compareFirstAndMiddleNames(Array("J"), Array("J"))
    val expectedOutcome = ComparisonResult(
      didNamesMatch            = true,
      npsNameWithInitials      = "J",
      ecospendNameWithInitials = "J"
    )

    result shouldBe expectedOutcome
  }

  "compareFirstAndMiddleNames should convert multiple names to initials and ignore excess names" in {
    val result = nameMatchingService.compareFirstAndMiddleNames(
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
