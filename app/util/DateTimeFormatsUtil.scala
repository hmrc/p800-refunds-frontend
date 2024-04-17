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

import java.time.{Instant, ZoneId}
import java.time.format.DateTimeFormatter

object DateTimeFormatsUtil {

  //e.g. 1 September 2017
  val gdsDateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMMM Y")

  //e.g. 1 December 2024, 5:26pm
  def lockoutUnlockDateFormatter(unlockDate: Instant): String = {
    val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy, h:mma").withZone(ZoneId.systemDefault())
    val unlockDateFormatted = dateFormatter.format(unlockDate)
    val (date, amPm) = unlockDateFormatted.format(unlockDate) splitAt (unlockDateFormatted.length - 2)

    s"$date${amPm.toLowerCase}"
  }
}
