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

import java.time.{Clock, LocalDateTime}

final case class EcospendAccessToken(token: String, expiry: LocalDateTime)

@SuppressWarnings(Array("org.wartremover.warts.Any"))
object EcospendAccessToken {
  implicit def ecospendReads(implicit clock: Clock): Reads[EcospendAccessToken] = (
    (__ \ "access_token").read[String].orElse(Reads(_ => JsError("Could not parse access_token value"))) and
    (__ \ "expires_in").read[Int].orElse(Reads(_ => JsError("Could not parse expires_in value"))) and
    (__ \ "token_type").read[String].orElse(Reads(_ => JsError("Could not parse token_type value"))) and
    (__ \ "scope").read[String].orElse(Reads(_ => JsError("Could not parse scope value")))
  )((token, expiry, _, _) => EcospendAccessToken(token, LocalDateTime.now(clock).plusNanos(expiry)))
}

