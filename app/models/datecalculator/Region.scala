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

package models.datecalculator

import enumeratum._

sealed trait Region extends EnumEntry

object Region extends Enum[Region] with PlayJsonEnum[Region] {

  val values: IndexedSeq[Region] = findValues
  //TODO: we need to check if we should just use this, or cater for different regions also (i.e. "SC" (Scotland), "NI" (Northern Ireland))
  //England and Wales.
  case object EW extends Region
}
