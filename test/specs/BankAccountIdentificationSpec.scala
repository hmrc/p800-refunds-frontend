/*
 * Copyright 2023 HM Revenue & Customs
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

import models.ecospend.account.BankAccountIdentification
import testsupport.UnitSpec

class BankAccountIdentificationSpec extends UnitSpec {
  "Extract sort code" in {
    val accountIdentification = BankAccountIdentification("44556610002333")

    accountIdentification.sortCode shouldBe "445566"
  }

  "Extract account number" in {
    val accountIdentification = BankAccountIdentification("44556610002333")

    accountIdentification.accountNumber shouldBe "10002333"
  }
}

