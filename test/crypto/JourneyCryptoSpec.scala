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

package crypto

import testsupport.ItSpec

class JourneyCryptoSpec extends ItSpec {

  "JourneyCrypto does symmetric encryption" in {
    val journeyCrypto = app.injector.instanceOf[JourneyCrypto]
    val journey = tdAll.BankTransfer.journeyClaimedOverpayment
    val encrypted = journeyCrypto.encryptJourney(journey)
    val decrypted = journeyCrypto.decryptJourney(encrypted)
    decrypted shouldBe journey
    encrypted should not be journey
  }
}
