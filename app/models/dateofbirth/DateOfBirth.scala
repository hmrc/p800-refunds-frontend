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

package models.dateofbirth

import play.api.libs.json.{Json, OFormat}
import util.SafeEquals.EqualsOps

import java.time.LocalDate
import java.util.{Calendar, Locale}
import scala.jdk.CollectionConverters.MapHasAsScala
import scala.util.{Failure, Success, Try}

final case class DateOfBirth(dayOfMonth: DayOfMonth, month: Month, year: Year)

object DateOfBirth {
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val format: OFormat[DateOfBirth] = Json.format[DateOfBirth]

  /**
   * It creates a list of month strings tupled with int value, i.e. ("January", 0), ("Jan",0)
   */
  val monthStringAndIntValue: List[(String, Int)] = {
    val calendar = Calendar.getInstance(Locale.UK)
    val shortFormat = calendar.getDisplayNames(Calendar.MONTH, Calendar.SHORT_FORMAT, Locale.UK).asScala.toList
    val longFormat = calendar.getDisplayNames(Calendar.MONTH, Calendar.LONG_FORMAT, Locale.UK).asScala.toList
    (shortFormat ++ longFormat).map((monthDisplayNames: (String, Integer)) => monthDisplayNames._1 -> monthDisplayNames._2.intValue())
  }

  def asLocalDate(dateOfBirth: DateOfBirth): LocalDate = {
    Try {
      val month: Int = monthStringAndIntValue
        .find(_._1.toUpperCase() === dateOfBirth.month.value.toUpperCase()) //if the month is Jan or January (etc) get associated 0 based index of the month
        .map(_._2 + 1) //increase base so January is 1 (not 0)
        .getOrElse(dateOfBirth.month.value.toInt) //if the month is string represetnging integer, cast it to int

      LocalDate.of(
        dateOfBirth.year.value.toInt,
        month,
        dateOfBirth.dayOfMonth.value.toInt
      )
    } match {
      case Success(date) => date
      case Failure(ex)   => throw new RuntimeException(s"Could not parse LocalDate from date of birth: [${dateOfBirth.toString}]", ex)
    }
  }
}
