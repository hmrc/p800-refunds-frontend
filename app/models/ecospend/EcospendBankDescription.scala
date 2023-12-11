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

import akka.http.scaladsl.model.Uri
import models.UriFormats.uriJsonFormat
import play.api.libs.functional.syntax._
import play.api.libs.json._

final case class EcospendBankDescription(
    bankId:         BankId,
    name:           String,
    friendlyName:   String,
    isSandbox:      Boolean,
    logoUrl:        Uri,
    standard:       Option[String],
    countryIsoCode: Option[String],
    division:       Option[String],
    group:          String,
    order:          Int,
    abilities:      EcospendBankAbilities,
    serviceStatus:  Boolean,
    iconUrl:        Uri
) {
  def toFrontendBankDescription: BankDescription = BankDescription(bankId, name, friendlyName, logoUrl, group, iconUrl, abilities.domesticScheduled)
}

@SuppressWarnings(Array("org.wartremover.warts.Any"))
object EcospendBankDescription {

  private val reads: Reads[EcospendBankDescription] = (
    (__ \ "bank_id").read[BankId].orElse(Reads(_ => JsError("Could not parse bank_id value"))) and
    (__ \ "name").read[String].orElse(Reads(_ => JsError("Could not parse name value"))) and
    (__ \ "friendly_name").read[String].orElse(Reads(_ => JsError("Could not parse friendly_name value"))) and
    (__ \ "is_sandbox").read[Boolean].orElse(Reads(_ => JsError("Could not parse is_sandbox value"))) and
    (__ \ "logo").read[String].orElse(Reads(_ => JsError("Could not parse logo value"))) and
    (__ \ "standard").readNullable[String].orElse(Reads(_ => JsError("Could not parse standard value"))) and
    (__ \ "country_iso_code").readNullable[String].orElse(Reads(_ => JsError("Could not parse country_iso_code value"))) and
    (__ \ "division").readNullable[String].orElse(Reads(_ => JsError("Could not parse division value"))) and
    (__ \ "group").read[String].orElse(Reads(_ => JsError("Could not parse group value"))) and
    (__ \ "order").read[Int].orElse(Reads(_ => JsError("Could not parse order value"))) and
    (__ \ "abilities").read[EcospendBankAbilities].orElse(Reads(_ => JsError("Could not parse abilities value"))) and
    (__ \ "service_status").read[Boolean].orElse(Reads(_ => JsError("Could not parse service_status value"))) and
    (__ \ "icon").read[String].orElse(Reads(_ => JsError("Could not parse icon value")))
  ) ((bankId, name, friendlyName, isSandbox, logo, standard, countryIsoCode, division, group, order, abilities, serviceStatus, icon) =>
      EcospendBankDescription(bankId, name, friendlyName, isSandbox, logo, standard, countryIsoCode, division, group, order, abilities, serviceStatus, icon))

  private val writes: OWrites[EcospendBankDescription] = (ecospendBankDescription: EcospendBankDescription) => Json.obj(
    "bank_id" -> ecospendBankDescription.bankId,
    "name" -> ecospendBankDescription.name,
    "friendly_name" -> ecospendBankDescription.friendlyName,
    "is_sandbox" -> ecospendBankDescription.isSandbox,
    "logo" -> ecospendBankDescription.logoUrl,
    "standard" -> ecospendBankDescription.standard,
    "country_iso_code" -> ecospendBankDescription.countryIsoCode,
    "division" -> ecospendBankDescription.division,
    "group" -> ecospendBankDescription.group,
    "order" -> ecospendBankDescription.order,
    "abilities" -> ecospendBankDescription.abilities,
    "service_status" -> ecospendBankDescription.serviceStatus,
    "icon" -> ecospendBankDescription.iconUrl,
  )

  implicit val format: OFormat[EcospendBankDescription] = OFormat(reads, writes)
}

