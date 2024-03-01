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

package models.attemptmodels

import play.api.libs.json.{Format, Json}
import play.api.mvc.RequestHeader
import requests.RequestSupport.hc

final case class TrueClientIp private (value: String) extends AnyVal

object TrueClientIp {
  implicit val format: Format[TrueClientIp] = Json.valueFormat

  //  private so it's not possible to create it by hand, use dedicated constructor for it
  private def apply(value: String): TrueClientIp = new TrueClientIp(value)

  def trueClientIp()(implicit requestHeader: RequestHeader): TrueClientIp = hc.trueClientIp.map(apply).getOrElse(
    throw new RuntimeException("The request comes from unknown destination. Missing 'trueClientIp' header")
  )

}
