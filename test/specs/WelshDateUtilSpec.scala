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

package specs

import testsupport.UnitSpec
import util.WelshDateUtil

class WelshDateUtilSpec extends UnitSpec {

  "StringOps.welshDate" - {
    "return welsh month correctly" in {
      val testCases: List[(String, String)] = List(
        "January" -> "Ionawr",
        "February" -> "Chwefror",
        "March" -> "Mawrth",
        "April" -> "Ebrill",
        "May" -> "Mai",
        "June" -> "Mehefin",
        "July" -> "Gorffennaf",
        "August" -> "Awst",
        "September" -> "Medi",
        "October" -> "Hydref",
        "November" -> "Tachwedd",
        "December" -> "Rhagfyr",
        "Jan" -> "Ion",
        "Feb" -> "Chwef",
        "Mar" -> "Maw",
        "Apr" -> "Ebr",
        "May" -> "Mai",
        "Jun" -> "Meh",
        "Jul" -> "Gorff",
        "Aug" -> "Awst",
        "Sep" -> "Medi",
        "Oct" -> "Hyd",
        "Nov" -> "Tach",
        "Dec" -> "Rhag",
        "4 January 2024" -> "4 Ionawr 2024",
        "4 January 2024" -> "4 Ionawr 2024",
        "4 January 2024, 5:35am" -> "4 Ionawr 2024, 5:35am",
        "4 January 2024, 5:35pm" -> "4 Ionawr 2024, 5:35pm",
      )
      testCases.foreach {
        case (input: String, expectedOutput: String) =>
          WelshDateUtil.StringOps(input).welshMonth shouldBe expectedOutput
      }
    }

    "return the english if it can't find a welsh month" in {
      WelshDateUtil.StringOps("Januaree").welshMonth shouldBe "Januaree"
    }
  }

}
