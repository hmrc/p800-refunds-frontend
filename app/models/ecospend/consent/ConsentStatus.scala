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

import enumeratum._
import play.api.mvc.QueryStringBindable
import util.ValueClassBinder.queryStringValueBinder

sealed trait ConsentStatus extends EnumEntry

object ConsentStatus extends Enum[ConsentStatus] with PlayJsonEnum[ConsentStatus] {
  val values = findValues

  implicit val queryStringBindable: QueryStringBindable[ConsentStatus] = queryStringValueBinder(_.toString)

  case object Initial extends ConsentStatus
  case object AwaitingAuthorization extends ConsentStatus
  case object Authorised extends ConsentStatus
  case object Active extends ConsentStatus
  case object Canceled extends ConsentStatus
  case object Failed extends ConsentStatus
  case object Abandoned extends ConsentStatus
  case object Revoked extends ConsentStatus
  case object Expired extends ConsentStatus
  case object RevocationPending extends ConsentStatus
}
