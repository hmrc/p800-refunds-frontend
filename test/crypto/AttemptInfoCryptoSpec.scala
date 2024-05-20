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

import models.attemptmodels.{AttemptInfo, IpAddress}
import testsupport.ItSpec

class AttemptInfoCryptoSpec extends ItSpec {

  "AttemptInfoCrypto symmetrically encrypt attemptInfo" in {
    val attemptInfoCrypto = app.injector.instanceOf[AttemptInfoCrypto]
    val attemptInfo = tdAll.attemptInfo(3)
    val encrypted: AttemptInfo = attemptInfoCrypto.encrypt(attemptInfo)
    val decrypted = attemptInfoCrypto.decrypt(encrypted)
    decrypted shouldBe attemptInfo
    encrypted should not be decrypted
  }

  "AttemptInfoCrypto symmetrically encrypt IpAddress" in {
    val attemptInfoCrypto = app.injector.instanceOf[AttemptInfoCrypto]
    val attemptInfo = IpAddress("1.2.3.4")
    val encrypted: IpAddress = attemptInfoCrypto.encrypt(attemptInfo)
    val decrypted: IpAddress = attemptInfoCrypto.decryptAttemptInfo(encrypted)
    decrypted shouldBe attemptInfo
    encrypted should not be decrypted
  }

  "AttemptInfoCrypto deterministically encrypt ip address" in {
    val attemptInfoCrypto = app.injector.instanceOf[AttemptInfoCrypto]
    val attemptInfo = IpAddress("1.2.3.4")
    val encrypted: IpAddress = attemptInfoCrypto.encrypt(attemptInfo)
    val encrypted2: IpAddress = attemptInfoCrypto.encrypt(attemptInfo)
    encrypted shouldBe encrypted2 withClue "Encrypting the same value twice results in the same encrypted string; otherwise, it won't be possible to search for this field in MongoDB"
  }
}
