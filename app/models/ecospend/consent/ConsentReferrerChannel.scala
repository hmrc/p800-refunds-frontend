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

import enumeratum.values._

sealed abstract class ConsentReferrerChannel(val value: Int) extends IntEnumEntry

case object ConsentReferrerChannel extends IntEnum[ConsentReferrerChannel] with IntPlayJsonValueEnum[ConsentReferrerChannel] {
  val values = findValues

  // Channel information of the user paying via Paylink. ch = 1: Sms ch = 2: Email ch = 3: Web
  case object Sms extends ConsentReferrerChannel(value = 1)
  case object Email extends ConsentReferrerChannel(value = 2)
  case object Web extends ConsentReferrerChannel(value = 3)

}

