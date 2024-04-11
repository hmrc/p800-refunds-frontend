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

package util

import testsupport.UnitSpec

import java.time.format.DateTimeFormatter
import java.time.{Instant, LocalDate, ZoneId, ZonedDateTime}

class DateTimeFormatUtilSpec extends UnitSpec {

  val testLocalDateFormat: DateTimeFormatter = DateTimeFormatsUtil.gdsDateTimeFormatter
  val zonedToday: ZonedDateTime = Instant.now().atZone(ZoneId.systemDefault())
  val zonedTomorrow: ZonedDateTime = Instant.now().atZone(ZoneId.systemDefault()).plusDays(1)

  "The LocalDate formatter should only return a date in the format '11 June 24'" in {
    val testInstantDate: LocalDate = LocalDate.parse("2019-03-29")
    val result = testLocalDateFormat.format(testInstantDate)

    result shouldBe "29 March 2019"
  }

  "The Instant formatter should return the date and time" in {
    val testInstantDate = Instant.parse("2042-01-12T11:24:24.00Z")
    val result = DateTimeFormatsUtil.customDateFormatter(testInstantDate)

    result shouldBe "12 January 2042, 11:24am"
  }

  "The Instant formatter should return the time on a 12 hour clock" in {
    val testInstantDate = Instant.parse("2024-12-01T17:26:24.00Z")
    val result = DateTimeFormatsUtil.customDateFormatter(testInstantDate)

    result shouldBe "1 December 2024, 5:26pm"
  }

  "The Instant formatter should return 'midday today'" in {
    val testInstantDate = zonedToday.withHour(12).toInstant
    val result = DateTimeFormatsUtil.customDateFormatter(testInstantDate)

    result shouldBe "midday today"
  }

  "The Instant formatter should return 'midnight tonight'" in {
    val testInstantDate = zonedToday.withHour(0).toInstant
    val result = DateTimeFormatsUtil.customDateFormatter(testInstantDate)

    result shouldBe "midnight tonight"
  }

  "The Instant formatter should return '5:45am today'" in {
    val testInstantDate = zonedToday.withHour(5).withMinute(45).toInstant
    val result = DateTimeFormatsUtil.customDateFormatter(testInstantDate)

    result shouldBe "5:45am today"
  }

  "The Instant formatter should return 'midday tomorrow'" in {
    val testInstantDate = zonedTomorrow.withHour(12).toInstant
    val result = DateTimeFormatsUtil.customDateFormatter(testInstantDate)

    result shouldBe "midday tomorrow"
  }

  "The Instant formatter should return 'midnight tomorrow'" in {
    val testInstantDate = zonedTomorrow.withHour(0).toInstant
    val result = DateTimeFormatsUtil.customDateFormatter(testInstantDate)

    result shouldBe "midnight tomorrow"
  }

  "The Instant formatter should return '5:45am tomorrow'" in {
    val testInstantDate = zonedTomorrow.withHour(17).withMinute(45).toInstant
    val result = DateTimeFormatsUtil.customDateFormatter(testInstantDate)

    result shouldBe "5:45pm tomorrow"
  }
}
