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

package util

import testsupport.UnitSpec

class NameParsingUtilSpec extends UnitSpec {

  val titlesList: Seq[String] = Seq("Mr", "Mrs", "Miss", "Ms", "Master", "Madame", "Dr", "Sir")

  "removeTitleFromName should return the same name when no titles are provided" in {
    val result = NameParsingUtil.removeTitleFromName(Seq.empty, "Mr John Doe")
    result should be ("Mr John Doe")
  }

  "removeTitleFromName should return the same name even if the name includes a title's letters" in {
    val result = NameParsingUtil.removeTitleFromName(titlesList, "Mrian Revanish")
    result should be ("Mrian Revanish")
  }

  "removeTitleFromName should remove the title from the name when a title is provided" in {
    val result = NameParsingUtil.removeTitleFromName(titlesList, "Mr John Andrew Doe")
    result should be ("John Andrew Doe")
  }

  "removeTitleFromName should remove the title even if it includes a fullstop" in {
    val result = NameParsingUtil.removeTitleFromName(titlesList, "Mr. John Andrew Doe")
    result should be ("John Andrew Doe")
  }

  "removeTitleFromName should NOT remove the title from the name when the title is not at the start" in {
    val result = NameParsingUtil.removeTitleFromName(titlesList, "John Andrew Doe Mr")
    result should be ("John Andrew Doe Mr")
  }

  "removeTitleFromName should remove only the first title from the name when multiple titles are provided" in {
    val result = NameParsingUtil.removeTitleFromName(titlesList, "Dr Master John Doe")
    result should be ("Master John Doe")
  }

  "removeTitleFromName should remove all titles from the name when multiple names are provided" in {
    val result = NameParsingUtil.removeTitleFromName(titlesList, "Mr John Doe, Mrs. Jane Doe")
    result should be ("John Doe, Jane Doe")
  }

  "Single Account, one forename initial: 'Rubens P' should become 'P Rubens" in {
    NameParsingUtil.bankIdBasedAccountNameParsing("Rubens P") shouldBe Seq("P Rubens")
  }

  "Single Account, multiple initials: 'Rubens PJ' should become 'P J Rubens" in {
    NameParsingUtil.bankIdBasedAccountNameParsing("Rubens PJ") shouldBe Seq("P J Rubens")
  }

  "Single Account, double barrelled surname: 'Smith-Rubens PJ' should become 'P J Smith-Rubens" in {
    NameParsingUtil.bankIdBasedAccountNameParsing("Smith-Rubens PJ") shouldBe Seq("P J Smith-Rubens")
  }

  Seq(
    // Joint account - Single initial
    ("Rubens P & J", Seq("P Rubens", "J Rubens")),
    ("Rubens P & Smith J", Seq("P Rubens", "J Smith")),
    ("Smith-Rubens P & J", Seq("P Smith-Rubens", "J Smith-Rubens")),

    // Joint account - Double Initial
    ("Rubens JI & ER", Seq("J I Rubens", "E R Rubens")),
    ("Rubens JI & Smith ER", Seq("J I Rubens", "E R Smith")),
    ("Smith-Rubens JI & ER", Seq("J I Smith-Rubens", "E R Smith-Rubens")),

    // Joint account - Spaced out double initials
    ("Rubens J I & E R", Seq("J I Rubens", "E R Rubens")),
    ("Rubens J I & Smith E R", Seq("J I Rubens", "E R Smith")),
    ("Smith-Rubens J I & E R", Seq("J I Smith-Rubens", "E R Smith-Rubens")),

    // Joint account - Extra spaces
    ("  Rubens   P   &    J  ", Seq("P Rubens", "J Rubens")),
    ("  Rubens    P & Smith J  ", Seq("P Rubens", "J Smith")),
    ("  Smith-Rubens    P   &   J  ", Seq("P Smith-Rubens", "J Smith-Rubens")),
    ("  Rubens J     I  &    E    R  ", Seq("J I Rubens", "E R Rubens")),
    ("  Rubens    J  I  &  Smith    E     R  ", Seq("J I Rubens", "E R Smith")),
    ("  Smith-Rubens    J    I   &   E    R  ", Seq("J I Smith-Rubens", "E R Smith-Rubens"))
  ).foreach {
      case (input, expected) =>
        val printableExpected = expected.map(x => s"'${x}'").mkString(" & ")

        s"Joint Account, same surname for: '${input}' should become ${printableExpected}" in {
          NameParsingUtil.bankIdBasedAccountNameParsing(input) shouldBe expected
        }
    }

  "bankIdBasedAccountNameParsing should return empty sequence for empty account name" in {
    val result = NameParsingUtil.bankIdBasedAccountNameParsing("")
    result shouldBe Seq(" ")
  }
}
