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

  "Single Account, one forename initial: 'Rubens P' should become 'P Rubens" in {
    NameParsingUtil.bankIdBasedAccountNameParsing("Rubens P") shouldBe Seq("P Rubens")
  }

  "Single Account, multiple initials: 'Rubens PJ' should become 'P J Rubens" in {
    NameParsingUtil.bankIdBasedAccountNameParsing("Rubens PJ") shouldBe Seq("P J Rubens")
  }

  "Single Account, double barrelled surname: 'Smith-Rubens PJ' should become 'P J Smith-Rubens" in {
    NameParsingUtil.bankIdBasedAccountNameParsing("Smith-Rubens PJ") shouldBe Seq("P J Smith-Rubens")
  }

  "Joint Account, same surname for: 'Rubens P & J' should become 'P Rubens' & 'J Rubens'" in {
    NameParsingUtil.bankIdBasedAccountNameParsing("Rubens P & J") shouldBe Seq("P Rubens", "J Rubens")
  }

  "Joint Account, different surname for: 'Rubens P & Smith J' should become 'P Rubens' & 'J Smith'" in {
    NameParsingUtil.bankIdBasedAccountNameParsing("Rubens P & Smith J") shouldBe Seq("P Rubens", "J Smith")
  }

  "Joint Account, double barrelled surname: 'Smith-Rubens P & J' should become 'P Smith-Rubens' & 'J Smith-Rubens'" in {
    NameParsingUtil.bankIdBasedAccountNameParsing("Smith-Rubens P & J") shouldBe Seq("P Smith-Rubens", "J Smith-Rubens")
  }

  "bankIdBasedAccountNameParsing should return empty sequence for empty account name" in {
    val result = NameParsingUtil.bankIdBasedAccountNameParsing("")
    result shouldBe Seq(" ")
  }
}
