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

import SafeEquals._
import scala.util.Try

object WelshDateUtil {
  private val months: Map[String, String] = Map(
    "January" -> "Ionawr",
    "February" -> "Chwefror",
    "March" -> "Mawrth",
    "April" -> "Ebrill",
    "May" -> "Mai",
    "June" -> "Mehefin",
    "July" -> "Gorffennaf",
    "August" -> "Awst",
    "September" -> "Medi",
    "October" -> "Hydref",
    "November" -> "Tachwedd",
    "December" -> "Rhagfyr"
  )

  private val shortMonths: Map[String, String] = Map(
    "Jan" -> "Ion",
    "Feb" -> "Chwef",
    "Mar" -> "Maw",
    "Apr" -> "Ebr",
    "May" -> "Mai",
    "Jun" -> "Meh",
    "Jul" -> "Gorff",
    "Aug" -> "Awst",
    "Sep" -> "Medi",
    "Oct" -> "Hyd",
    "Nov" -> "Tach",
    "Dec" -> "Rhag"
  )

  implicit class StringOps(date: String) {
    // Try and update any reference of long month notation (i.e. January), if that fails try short month (i.e. Jan), if that fails just use English as fallback
    def welshMonth: String = {
        def updateString(d: String, m: Map[String, String]) = {
          val monthToChange: Map[String, String] = m.filter(_._1 === d.filter(_.isLetter))
          d.replace(monthToChange.headOption.fold(d)(_._1), monthToChange(d.filter(_.isLetter)))
        }
      val longMonth: Try[String] = Try(updateString(date, months))
      longMonth.getOrElse(Try(updateString(date, shortMonths)).getOrElse(date))
    }
  }
}
