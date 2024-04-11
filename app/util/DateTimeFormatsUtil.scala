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

package util

import util.SafeEquals.EqualsOps

import java.time.{Instant, ZoneId}
import java.time.format.DateTimeFormatter

object DateTimeFormatsUtil {

  //e.g. 1 September 2017
  val gdsDateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMMM Y")

  //e.g. 1 December 2024, 5:26pm
  val instantFullDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy, h:mma").withZone(ZoneId.systemDefault())

  //e.g. 5:26pm
  val instantTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("h:mma").withZone(ZoneId.systemDefault())

  def customDateFormatter(lockoutDate: Instant): String = {
    val zonedLockoutDate = lockoutDate.atZone(ZoneId.systemDefault())
    val zonedNow = Instant.now().atZone(ZoneId.systemDefault())

    val isToday = zonedLockoutDate.getDayOfWeek === zonedNow.getDayOfWeek
    val isTomorrow = zonedLockoutDate.getDayOfWeek === zonedNow.plusDays(1).getDayOfWeek
    val isMidday = zonedLockoutDate.getHour === 12
    val isMidnight = zonedLockoutDate.getHour === 0

    (isToday, isTomorrow, isMidday, isMidnight) match {
      case (true, false, true, false)  => "midday today"
      case (true, false, false, true)  => "midnight tonight"
      case (true, false, false, false) => s"${zonedLockoutDate.format(instantTimeFormatter)} today"
      case (false, true, true, false)  => "midday tomorrow"
      case (false, true, false, true)  => "midnight tomorrow"
      case (false, true, false, false) => s"${zonedLockoutDate.format(instantTimeFormatter)} tomorrow"
      case (_, _, _, _)                => instantFullDateFormatter.format(zonedLockoutDate)
    }
  }
}
