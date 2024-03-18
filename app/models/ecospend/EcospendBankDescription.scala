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

package models.ecospend

import org.apache.pekko.http.scaladsl.model.Uri
import models.UriFormats.uriJsonFormat
import play.api.libs.json._

final case class EcospendBankDescription(
    bankId:         BankId,
    name:           BankName,
    friendlyName:   BankFriendlyName,
    isSandbox:      Boolean,
    logo:           Uri,
    icon:           Uri,
    standard:       Option[String],
    countryIsoCode: Option[String],
    division:       Option[String],
    group:          Option[BankGroup],
    order:          Int,
    serviceStatus:  Boolean,
    abilities:      EcospendBankAbilities
) {
  def toFrontendBankDescription: BankDescription = BankDescription(bankId, name, friendlyName, logo, icon)
}

@SuppressWarnings(Array("org.wartremover.warts.Any"))
object EcospendBankDescription {
  implicit val format: OFormat[EcospendBankDescription] = {
    implicit val config: JsonConfiguration = JsonConfiguration(JsonNaming.SnakeCase)

    Json.format[EcospendBankDescription]
  }
}

