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

package models.audit

import play.api.libs.json.{Json, Writes, OWrites}

final case class UserLoginSelection(
    login:              Login,
    ipAddressLockedout: IpAddressLockedout
) extends AuditDetail {
  val auditType: String = "userLoginSelection"
}

object UserLoginSelection {
  implicit val writes: OWrites[UserLoginSelection] = Json.writes[UserLoginSelection]
}

final case class Login(value: Boolean) extends AnyVal

object Login {
  implicit val writes: Writes[Login] = Json.valueWrites[Login]
}

final case class IpAddressLockedout(value: Boolean) extends AnyVal

object IpAddressLockedout {
  implicit val writes: Writes[IpAddressLockedout] = Json.valueWrites[IpAddressLockedout]
}

