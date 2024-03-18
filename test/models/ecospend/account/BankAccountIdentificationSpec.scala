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

import testsupport.UnitSpec

class BankAccountIdentificationSpec extends UnitSpec {

  "split into sort code and bank account number" in {
    BankAccountIdentification("12345688888888").sortCode shouldBe "123456"
    BankAccountIdentification("12345688888888").bankAccountNumber shouldBe "88888888"
  }

}
