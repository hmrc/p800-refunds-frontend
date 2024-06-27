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

import nps.models.PayeeBankAccountName
import testsupport.UnitSpec

class PayeeBankAccountNameSpec extends UnitSpec {

  "sanitisePayeeBankAccountName" - {
    "Succesfully remove non alphabetic characters and limit to 50 characters" in {
      val input: PayeeBankAccountName = PayeeBankAccountName("NatWest Online & Mobile Banking.-, more chars than can fit")
      val expected: PayeeBankAccountName = PayeeBankAccountName("NatWest Online  Mobile Banking more chars than can")

      input.sanitisePayeeBankAccountName shouldBe expected withClue "Sanitised PayeeBankAccountName not equal to expected"
      input.sanitisePayeeBankAccountName.value.length should be > 0
      input.sanitisePayeeBankAccountName.value.length should be <= 50
    }

    "Reject input non alphabetic characters and limit to 50 characters" in {
      val input: PayeeBankAccountName = PayeeBankAccountName("NatWest Online & Mobile Banking.-,")
      val expected: PayeeBankAccountName = PayeeBankAccountName("NatWest Online  Mobile Banking")

      input.sanitisePayeeBankAccountName shouldBe expected withClue "Sanitised PayeeBankAccountName not equal to expected"
      input.sanitisePayeeBankAccountName.value.length should be > 0
      input.sanitisePayeeBankAccountName.value.length should be <= 50
    }

    "Raise exception for input unable to be sanitised" in {
      val input: PayeeBankAccountName = PayeeBankAccountName("")

      an[IllegalArgumentException] should be thrownBy {
        input.sanitisePayeeBankAccountName
      }
    }
  }

}
