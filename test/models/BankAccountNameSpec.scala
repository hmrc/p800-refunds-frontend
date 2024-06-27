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

import edh.BankAccountName
import testsupport.UnitSpec

class BankAccountNameSpec extends UnitSpec {

  "sanitiseBankAccountName" - {
    "Succesfully remove non alphabetic characters and limit to 34 characters" in {
      val input: BankAccountName = BankAccountName("NatWest Online & Mobile Banking.-, more chars than can fit")
      val expected: BankAccountName = BankAccountName("NatWest Online  Mobile Banking mor")

      input.sanitiseBankAccountName shouldBe expected withClue "Sanitised BankAccountName not equal to expected"
      input.sanitiseBankAccountName.value.length should be > 0
      input.sanitiseBankAccountName.value.length should be <= 34
    }

    "Reject input non alphabetic characters and limit to 34 characters" in {
      val input: BankAccountName = BankAccountName("NatWest Online & Mobile Banking.-,")
      val expected: BankAccountName = BankAccountName("NatWest Online  Mobile Banking")

      input.sanitiseBankAccountName shouldBe expected withClue "Sanitised BankAccountName not equal to expected"
      input.sanitiseBankAccountName.value.length should be > 0
      input.sanitiseBankAccountName.value.length should be <= 34
    }

    "Raise exception for input unable to be sanitised" in {
      val input: BankAccountName = BankAccountName("")

      an[IllegalArgumentException] should be thrownBy {
        input.sanitiseBankAccountName
      }
    }
  }

}
