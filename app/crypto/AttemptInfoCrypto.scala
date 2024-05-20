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

import com.google.inject.{Inject, Singleton}
import com.softwaremill.quicklens._
import models.attemptmodels.{AttemptInfo, IpAddress}
import uk.gov.hmrc.crypto.{AesCrypto, Crypted, PlainText}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

@Singleton
class AttemptInfoCrypto(encryptionKeyInBase64: String) {

  def encrypt(ipAddress: IpAddress): IpAddress = mapPii(ipAddress)(encrypt0)
  def decryptAttemptInfo(ipAddress: IpAddress): IpAddress = mapPii(ipAddress)(decrypt0)

  def encrypt(attemptInfo: AttemptInfo): AttemptInfo = mapPii(attemptInfo)(encrypt0)
  def decrypt(attemptInfo: AttemptInfo): AttemptInfo = mapPii(attemptInfo)(decrypt0)

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  private def mapPii(attemptInfo: AttemptInfo)(f: String => String): AttemptInfo = attemptInfo
    .modify(_.ipAddress.value).using(f)

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  private def mapPii(ipAddress: IpAddress)(f: String => String): IpAddress = ipAddress
    .modify(_.value).using(f)

  @Inject
  def this(servicesConfig: ServicesConfig) = this(servicesConfig.getString("crypto.encryption-key"))

  private val aes = new AesCrypto { override val encryptionKey: String = encryptionKeyInBase64 }
  private def encrypt0(s: String): String = aes.encrypt(PlainText(s)).value
  private def decrypt0(s: String): String = aes.decrypt(Crypted(s)).value
}
