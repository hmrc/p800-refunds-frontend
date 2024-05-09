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

import models.audit.NameMatchingAudit
import models.namematching._
import play.api.mvc.RequestHeader
import testdata.TdAudit._
import testdata.TdRequest
import testsupport.UnitSpec

class NameMatchingServiceSpec extends UnitSpec with TdRequest {
  implicit val requestHeader: RequestHeader = fakeRequest
  implicit def stringToOption(name: String): Option[String] = if (name === "") None else Some(name)

  // format: OFF
  val testScenarios = Seq(
    ("Jennifer"     , ""            , "Married"       , "Jennifer Maiden-Name"      , FailedSurnameMatch), //Different married vs maiden names
    ("K"            , "J"           , "Turner"        , "Jennifer Kate Turner"      , FailedComprehensiveNameMatch), //Wrong initials
    ("Tom"          , ""            , "Griffith"      , "Thomas Griffith"           , FailedComprehensiveNameMatch), //Tom is not Thomas
    ("A"            , ""            , "Shone"         , "T Patel"                   , FailedSurnameMatch), //Different Surname
    ("Paul"         , "James"       , "Rubens"        , "Paul James Robins"         , FailedSurnameMatch), //Different Surname but same first/middle name
    ("James"        , "Paul"        , "Rubens"        , "Paul James John Rubens"    , FailedComprehensiveNameMatch), //First and Middle name in wrong order
    ("Tina"         , ""            , "Patel"         , "Tara Patel"                , FailedComprehensiveNameMatch), //First names don't match even if same initials
    ("Paulie"       , "James"       , "Rubens"        , "Paul James Rubens"         , FailedComprehensiveNameMatch), //Levenshtein distance of two
    ("T"            , ""            , "Patel"         , "T Patel"                   , BasicSuccessfulNameMatch), //First names match since both are initials
    ("O'Riley"      , ""            , "Masterson"     , "ORiley Masterson"          , BasicSuccessfulNameMatch), //Apostrophes are not a problem
    ("Sõnekâëí"     , "  Francis  " , "Akpawe   "     , "Sonekaei Francis Akpawe"   , BasicSuccessfulNameMatch), //Removes diacritics and spaces
    ("Paul"         , "James"       , "Rubens"        , "Paul James Rubens"         , BasicSuccessfulNameMatch), //Exact name match
    ("Paul"         , "James"       , "Rubens-Smith"  , "Paul James Rubens Smith"   , BasicSuccessfulNameMatch), //Spaces vs Hyphens in the surname
    ("Paul"         , "James"       , "Rubens Smith"  , "Paul James Rubens-Smith"   , BasicSuccessfulNameMatch), //Spaces vs Hyphens in the surname (swapped for ecospend)
    ("Paul"         , "James"       , "Odd-Hyphen"    , "Paul James Odd‒‑—–-Hyphen" , BasicSuccessfulNameMatch), //Using different hyphen types
    ("Paul Janes"   , ""            , "Rubens Smith"  , "Paul-Janes Rubens-Smith"   , BasicSuccessfulNameMatch), //Hyphens in first names and last names
    ("P"            , ""            , "Rubens"        , "Paul James Rubens"         , FirstAndMiddleNameSuccessfulNameMatch), //Initials check for the first name and no middle name
    ("P"            , "J"           , "Rubens"        , "Paul James Rubens"         , FirstAndMiddleNameSuccessfulNameMatch), //Initials check for the first and middle name
    ("P"            , "J."          , "Rubens"        , "Paul James Rubens"         , FirstAndMiddleNameSuccessfulNameMatch), //Initials with a fullstop
    ("P"            , "James"       , "Rubens"        , "Paul James Rubens"         , FirstAndMiddleNameSuccessfulNameMatch), //Mix of initials for first name and a full middle name
    ("Paul"         , "J"           , "Rubens"        , "Paul James Rubens"         , FirstAndMiddleNameSuccessfulNameMatch), //Mix of initials for middle name and a full first name
    ("Paul"         , "James John"  , "Rubens"        , "Paul James Rubens"         , FirstAndMiddleNameSuccessfulNameMatch), //Multiple middle names in NPS but not Ecospend
    ("Paul"         , "James"       , "Rubens"        , "Paul James John Rubens"    , FirstAndMiddleNameSuccessfulNameMatch), //Multiple middle names in Ecospendd but not NPS
    ("Paula"        , "James"       , "Rubens"        , "Paul James Rubens"         , LevenshteinSuccessfulNameMatch) //Levenshtein distance of one
  )

  val auditTestScenarios = Seq(
    ("Jennifer"     , ""            , "Married"       , "Jennifer Maiden-Name"      , failedSurnameAudit),
    ("K"            , "J"           , "Turner"        , "Jennifer Kate Turner"      , failedComprehensiveAudit),
    ("T"            , ""            , "Patel"         , "T Patel"                   , successfulBasicAudit),
    ("Paul"         , "James"       , "Rubens"        , "Paul James John Rubens"    , successfulFirstMiddleAudit),
    ("Paula"        , "James"       , "Rubens"        , "Paul James Rubens"         , successfulLevenshteinAudit)
  )
  // format: ON

  testScenarios.foreach{ tuple =>
    val (npsFirstName, npsSecondName, npsSurname, ecoName, expectedOutput) = tuple
    val npsFullName = s"$npsFirstName $npsSecondName $npsSurname"

    s"fuzzy matching should return ${expectedOutput.toString} for NpsName:$npsFullName & EcospendName:$ecoName" in {
      val (matchingResult, _) = NameMatchingService.fuzzyNameMatching(npsFirstName, npsSecondName, npsSurname, ecoName)
      matchingResult shouldBe expectedOutput
    }
  }

  auditTestScenarios.foreach{ tuple =>
    val (npsFirstName, npsSecondName, npsSurname, ecoName, expectedOutput: NameMatchingAudit) = tuple
    val npsFullName = s"$npsFirstName $npsSecondName $npsSurname"

    s"The AuditEvent should be for a ${expectedOutput.outcome.category} for NpsName:$npsFullName & EcospendName:$ecoName" in {
      val (_, matchingAuditEvent) = NameMatchingService.fuzzyNameMatching(npsFirstName, npsSecondName, npsSurname, ecoName)
      matchingAuditEvent shouldBe expectedOutput
    }
  }

  "sanitiseFullName should return the name in sanitised name in lower case" in {
    val result = NameMatchingService.sanitiseFullName(" JÍngle-Song Blonde ")
    result shouldBe "jingle song blonde"
  }

  "remove diacritics should do return a string without special accents" in {
    val result = NameMatchingService.removeDiacritics("ÀÁÂÃÄÈÉÊËÍÌÎÏÙÚÛÜÒÓÔÕÖÑÇªº§³²¹àáâãäèéêëíìîïùúûüòóôõöñç")
    result shouldBe "AAAAAEEEEIIIIUUUUOOOOONCaaaaaeeeeiiiiuuuuooooonc"
  }

  "processSpacesApostrophesAndHyphens should turn hyphens of any amount into a space" in {
    val result = NameMatchingService.processSpacesApostrophesAndHyphens("‒‑—–-")
    result shouldBe " "
  }

  "processSpacesApostrophesAndHyphens should remove apostrophes" in {
    val result = NameMatchingService.processSpacesApostrophesAndHyphens("Ol' Mc'Donald")
    result shouldBe "Ol McDonald"
  }

  "processSpacesApostrophesAndHyphens should remove white spaces at the end and collapse white spaces in the middle" in {
    val result = NameMatchingService.processSpacesApostrophesAndHyphens("  Timothy   Delamaine  Panam    ")
    result shouldBe "Timothy Delamaine Panam"
  }

  "processSpacesApostrophesAndHyphens should replace tabs with spaces" in {
    val result = NameMatchingService.processSpacesApostrophesAndHyphens("John \tSmith")
    result shouldBe "John Smith"
  }

  "isWithinLevenshteinDistance should return LevenshteinSuccessfulNameMatch for a distance of 0 if the names are the same" in {
    val result = NameMatchingService.isWithinLevenshteinDistance("Antony Ode Kino", "Antony Ode Kino")
    result shouldBe (LevenshteinSuccessfulNameMatch, 0, true)
  }

  "isWithinLevenshteinDistance should return LevenshteinSuccessfulNameMatch for a distance of 1" in {
    val result = NameMatchingService.isWithinLevenshteinDistance("Antony Ode Kina", "Antony Ode Kino")
    result shouldBe (LevenshteinSuccessfulNameMatch, 1, true)
  }

  "isWithinLevenshteinDistance should return FailedComprehensiveNameMatch for a distance 2" in {
    val result = NameMatchingService.isWithinLevenshteinDistance("Hello", "Hela")
    result shouldBe (FailedComprehensiveNameMatch, 2, false)
  }

  "isWithinLevenshteinDistance should return FailedComprehensiveNameMatch when same letter appears multiple times" in {
    val result = NameMatchingService.isWithinLevenshteinDistance("oooo", "o")
    result shouldBe (FailedComprehensiveNameMatch, 3, false)
  }

  "compareFirstAndMiddleNames should return 'didNamesMatch = false' if the first names are different but share the same initial" in {
    val result = NameMatchingService.compareFirstAndMiddleNames(Seq("Jane", "Kasveko"), Seq("Josie", "Kasveko"))
    val expectedOutcome = ComparisonResult(
      didNamesMatch            = false,
      npsNameWithInitials      = "Jane Kasveko",
      ecospendNameWithInitials = "Josie Kasveko"
    )

    result shouldBe expectedOutcome
  }

  "compareFirstAndMiddleNames should return 'didNamesMatch = false' for different middle names with the same initials" in {
    val result = NameMatchingService.compareFirstAndMiddleNames(
      Seq("Lowe", "Jane", "Kasveko"),
      Seq("Lowe", "Josie", "Kasveko")
    )

    val expectedOutcome = ComparisonResult(
      didNamesMatch            = false,
      npsNameWithInitials      = "Lowe Jane Kasveko",
      ecospendNameWithInitials = "Lowe Josie Kasveko"
    )

    result shouldBe expectedOutcome
  }

  "compareFirstAndMiddleNames should return 'didNamesMatch = true' where one name is just an initial that matches the corresponding middle name" in {
    val result = NameMatchingService.compareFirstAndMiddleNames(
      Seq("Lowe", "J", "Kasveko"),
      Seq("Lowe", "Josie", "Kasveko")
    )

    val expectedOutcome = ComparisonResult(
      didNamesMatch            = true,
      npsNameWithInitials      = "Lowe J Kasveko",
      ecospendNameWithInitials = "Lowe J Kasveko"
    )

    result shouldBe expectedOutcome
  }

  "compareFirstAndMiddleNames should return 'didNamesMatch = true' if the names match and there is only an Initial " in {
    val result = NameMatchingService.compareFirstAndMiddleNames(Seq("J"), Seq("J"))
    val expectedOutcome = ComparisonResult(
      didNamesMatch            = true,
      npsNameWithInitials      = "J",
      ecospendNameWithInitials = "J"
    )

    result shouldBe expectedOutcome
  }

  "compareFirstAndMiddleNames should convert multiple names to initials and ignore excess names" in {
    val result = NameMatchingService.compareFirstAndMiddleNames(
      Seq("A", "Kinte", "J", "Kasveko", "IGNORED"),
      Seq("Odie", "Kinte", "Josie", "Kasveko")
    )

    val expectedOutcome = ComparisonResult(
      didNamesMatch            = false,
      npsNameWithInitials      = "A Kinte J Kasveko",
      ecospendNameWithInitials = "O Kinte J Kasveko"
    )

    result shouldBe expectedOutcome
  }
}
