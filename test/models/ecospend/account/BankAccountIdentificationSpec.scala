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

package models.ecospend.account

import edh.{BankAccountNumber, BankSortCode}
import nps.models.{PayeeBankAccountNumber, PayeeBankSortCode}
import testsupport.UnitSpec

class BankAccountIdentificationSpec extends UnitSpec {

  //TODO: The length of an account number can vary depending on the bank,
  // but it's typically between 6 to 10 digits long.

  "split into sort code and bank account number" in {
    BankAccountIdentification("12345688888888").asPayeeBankSortCode shouldBe PayeeBankSortCode("123456")
    BankAccountIdentification("12345688888888").asBankSortCode shouldBe BankSortCode("123456")
    BankAccountIdentification("12345688888888").asBankAccountNumber shouldBe BankAccountNumber("88888888")
    BankAccountIdentification("12345688888888").asPayeeBankAccountNumber shouldBe PayeeBankAccountNumber("88888888")
  }

  "The length of an account number can vary depending on the bank, but it's typically between 6 to 10 digits long." in {
    List(
      //sortCode, accountNumber, clue
      ("000000", "123456", "6 digits in account number"),
      ("000000", "1234567", "7 digits in account number"),
      ("000000", "1234568", "8 digits in account number"),
      ("000000", "1234569", "9 digits in account number"),
      ("000000", "12345690", "10 digits in account number")
    ).foreach {
        case (expectedSortCode, expectedAccountNumber, clue) =>
          val bankAccountIdentification = BankAccountIdentification(expectedSortCode + expectedAccountNumber)
          val sortCode: String = bankAccountIdentification.asPayeeBankSortCode.value
          val accountNumber: String = bankAccountIdentification.asPayeeBankAccountNumber.value

          sortCode shouldBe expectedSortCode withClue clue
          accountNumber shouldBe expectedAccountNumber withClue clue
      }
  }

}
