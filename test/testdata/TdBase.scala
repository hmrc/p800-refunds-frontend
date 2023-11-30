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

package testdata

import models.{P800Reference, FullName, NationalInsuranceNumber}
import models.dateofbirth.{DateOfBirth, DayOfMonth, Month, Year}

import java.time.{Instant, LocalDateTime, ZoneOffset}
import java.time.format.DateTimeFormatter

trait TdBase {

  lazy val dateString: String = "2059-11-25"
  lazy val timeString: String = s"${dateString}T16:33:51.880"
  lazy val localDateTime: LocalDateTime = {
    //the frozen time has to be in future otherwise the journeys will disappear from mongodb because of expiry index
    LocalDateTime.parse(timeString, DateTimeFormatter.ISO_DATE_TIME)
  }
  lazy val instant: Instant = localDateTime.toInstant(ZoneOffset.UTC)
  lazy val newInstant: Instant = instant.plusSeconds(20) //used when a new journey is created from existing one

  lazy val p800Reference: P800Reference = P800Reference("P800REFNO1")

  lazy val fullName: FullName = FullName("Test Name.-")

  lazy val gdsDateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMMM Y")

  lazy val dayOfMonth: DayOfMonth = DayOfMonth("1")
  lazy val month: Month = Month("1")
  lazy val year: Year = Year("2000")
  lazy val dateOfBirth: DateOfBirth = DateOfBirth(dayOfMonth, month, year)
  lazy val dateOfBirthFormatted: String = "01 January 2000"

  lazy val nationalInsuranceNumber: NationalInsuranceNumber = NationalInsuranceNumber("MA000003A")
}
