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

package models.ecospend.consent

import akka.http.scaladsl.model.Uri
import models.UriFormats.uriJsonFormat
import models.ecospend.BankId
import play.api.libs.json._

import java.time.LocalDateTime

final case class BankConsentRequest(
    bankId:           BankId,
    redirectUrl:      Uri,
    merchantId:       Option[String],
    merchantUserId:   Option[String],
    consentEndDate:   LocalDateTime,
    permissions:      List[ConsentPermission],
    referrerChannel:  ConsentReferrerChannel,
    additionalParams: Option[String],
    creationReason:   ConsentCreationReason
)

@SuppressWarnings(Array("org.wartremover.warts.Any"))
object BankConsentRequest {
  implicit val format: OFormat[BankConsentRequest] = {
    implicit val config: JsonConfiguration = JsonConfiguration(JsonNaming.SnakeCase)

    Json.format[BankConsentRequest]
  }
}

