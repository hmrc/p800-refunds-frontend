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

import play.api.libs.functional.syntax._
import play.api.libs.json._

final case class EcospendBankAbilities(
    domestic:                   Boolean,
    domesticScheduled:          Boolean,
    domesticStandingOrder:      Boolean,
    international:              Boolean,
    internationalScheduled:     Boolean,
    internationalStandingOrder: Boolean
)

@SuppressWarnings(Array("org.wartremover.warts.Any"))
object EcospendBankAbilities {

  private val reads: Reads[EcospendBankAbilities] = (
    (__ \ "domestic_payment").read[Boolean].orElse(Reads(_ => JsError("Could not parse domestic_payment value"))) and
    (__ \ "domestic_scheduled_payment").read[Boolean].orElse(Reads(_ => JsError("Could not parse domestic_scheduled_payment value"))) and
    (__ \ "domestic_standing_order").read[Boolean].orElse(Reads(_ => JsError("Could not parse domestic_standing_order value"))) and
    (__ \ "international_payment").read[Boolean].orElse(Reads(_ => JsError("Could not parse international_payment value"))) and
    (__ \ "international_scheduled_payment").read[Boolean].orElse(Reads(_ => JsError("Could not parse international_scheduled_payment value"))) and
    (__ \ "international_standing_order").read[Boolean].orElse(Reads(_ => JsError("Could not parse international_standing_order value")))
  ) ((domestic, domesticScheduled, domesticStanding, international, internationalScheduled, internationalStanding) =>
      EcospendBankAbilities(domestic, domesticScheduled, domesticStanding, international, internationalScheduled, internationalStanding))

  private val writes: OWrites[EcospendBankAbilities] = (ecospendBankDescription: EcospendBankAbilities) => Json.obj(
    "domestic_payment" -> ecospendBankDescription.domestic,
    "domestic_scheduled_payment" -> ecospendBankDescription.domesticScheduled,
    "domestic_standing_order" -> ecospendBankDescription.domesticStandingOrder,
    "international_payment" -> ecospendBankDescription.international,
    "international_scheduled_payment" -> ecospendBankDescription.internationalScheduled,
    "international_standing_order" -> ecospendBankDescription.internationalStandingOrder
  )

  implicit val format: OFormat[EcospendBankAbilities] = OFormat(reads, writes)
}

