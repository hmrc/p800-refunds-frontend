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

package models

import models.journeymodels.JourneyId
import play.api.libs.json.{Format, Json}
import play.api.mvc.{PathBindable, QueryStringBindable}

/**
 * Prediction from journeyId which we can use in urls and in logging to log in kibana what happened during particular journey
 */
final case class TraceId(value: String)

object TraceId {
  def apply(journeyId: JourneyId): TraceId = TraceId(eightDigitAbsoluteMod(journeyId.value.hashCode))

  def eightDigitAbsoluteMod(i: Int): String = {
    val absoluteMod = Math.abs(i % 100000000)
    f"$absoluteMod%08d"
  }

  implicit val format: Format[TraceId] = Json.valueFormat
  implicit val traceIdPathBinder: PathBindable[TraceId] = util.ValueClassBinder.valueClassBinder(_.value)
  implicit val traceIdQueryStringBinder: QueryStringBindable[TraceId] = util.ValueClassBinder.queryStringValueBinder(_.value)
}

