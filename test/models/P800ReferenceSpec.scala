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

package models

import testsupport.UnitSpec

class P800ReferenceSpec extends UnitSpec {

  "withinNpsBounds" - {
    "return true when number is between 1 (inclusive) and 2147483646 (inclusive)" in {
      Seq(
        (1, "min bounds"),
        (2147483646, "max bounds"),
        (10000000, "some number in-between")
      ).foreach {
          case (input, clue) =>
            P800Reference(input).withinNpsBounds shouldBe true withClue clue
        }
    }
    "return false when number is outside range of 1-2147483646 (both inclusive)" in {
      Seq(
        (0, "zero"),
        (-1, "negative"),
        (2147483647, "larger than 2147483646"),
        (-2147483646, "negative 2147483646"),
      ).foreach {
          case (input, clue) =>
            P800Reference(input).withinNpsBounds shouldBe false withClue clue
        }
    }
  }

}
