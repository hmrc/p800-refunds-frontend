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

sealed trait ConsentPermission extends EnumEntry

object ConsentPermission extends Enum[ConsentPermission] with PlayJsonEnum[ConsentPermission] {

  val values = findValues

  case object Account extends ConsentPermission
  case object Balance extends ConsentPermission
  case object Transactions extends ConsentPermission
  case object DirectDebits extends ConsentPermission
  case object StandingOrders extends ConsentPermission
  case object Parties extends ConsentPermission
  case object ScheduledPayments extends ConsentPermission
  case object Statements extends ConsentPermission
  case object Offers extends ConsentPermission
}

