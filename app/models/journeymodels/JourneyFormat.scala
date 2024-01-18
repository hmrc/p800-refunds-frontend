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

package models.journeymodels

import play.api.libs.json.{Format, Json, OFormat, OWrites}
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats

import java.time.Instant

private[journeymodels] object JourneyFormat {

  val format: OFormat[Journey] = {

    implicit val instantFormat: Format[Instant] = MongoJavatimeFormats.instantFormat

    @SuppressWarnings(Array("org.wartremover.warts.Any"))
    val defaultFormat: OFormat[Journey] = Json.format[Journey]

    //we need to write some extra fields on the top of the structure so it's
    //possible to index on them and use them in queries
    val customWrites = OWrites[Journey](j =>
      defaultFormat.writes(j) ++ Json.obj(
        "createdAt" -> MongoJavatimeFormats.instantFormat.writes(j.createdAt),
        "lastUpdated" -> MongoJavatimeFormats.instantFormat.writes(j.lastUpdated)
      ))
    OFormat(
      defaultFormat,
      customWrites
    )
  }
}
