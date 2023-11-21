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

package models.forms

import language.{Language, Message, Messages}
import models.dateofbirth.{DateOfBirth, DayOfMonth, Month, Year}
import play.api.data.Forms.mapping
import play.api.data.format.Formatter
import play.api.data.validation.{Constraint, Invalid, Valid}
import play.api.data.{Form, FormError, Forms}
import util.DateTimeFormatsUtil
import util.SafeEquals.EqualsOps

import java.time.LocalDate
import java.util.{Calendar, Locale}
import scala.jdk.CollectionConverters.MapHasAsScala
import scala.util.{Failure, Success, Try}

final case class WhatIsYourDateOfBirthForm(date: DateOfBirth)

object WhatIsYourDateOfBirthForm {

  def form(implicit language: Language): Form[WhatIsYourDateOfBirthForm] = Form[WhatIsYourDateOfBirthForm](
    mapping(
      "date" -> mapping(
        "day" -> Forms.of(dayFormatter),
        "month" -> Forms.of(monthFormatter),
        "year" -> Forms.of(yearFormatter)
      //        "year" -> yearMapping
      )(DateOfBirth.apply)(DateOfBirth.unapply).verifying(dateConstraint)
    )(WhatIsYourDateOfBirthForm.apply)(WhatIsYourDateOfBirthForm.unapply)
  )

  private val dateOfBirthDayKey = "date.day"
  private val dateOfBirthMonthKey = "date.month"
  private val dateOfBirthYearKey = "date.year"

  val monthStringAndIntValue: List[(String, Int)] = {
    val calendar = Calendar.getInstance(Locale.UK)
    val shortFormat = calendar.getDisplayNames(Calendar.MONTH, Calendar.SHORT_FORMAT, Locale.UK).asScala.toList.map((x: (String, Integer)) => x._1 -> x._2.intValue())
    val longFormat = calendar.getDisplayNames(Calendar.MONTH, Calendar.LONG_FORMAT, Locale.UK).asScala.toList.map((x: (String, Integer)) => x._1 -> x._2.intValue())
    shortFormat ++ longFormat
  }

  private def monthStringIsValidMonth(month: String): Boolean = monthStringAndIntValue.collectFirst {
    case (m: String, _: Int) if m.toLowerCase === month.toLowerCase => true
  }.getOrElse(false)

  def createFormError(key: String, message: Message)(implicit language: Language): Left[Seq[FormError], Nothing] = Left(Seq(FormError(key, message.show)))

  //this is disgusting, refactor, make them booleans and use .isEmpty or something
  def maybeEndOfMessage[A](key: String, maybeThing: A, dayFromForm: String, monthFromForm: String, yearFromForm: String)(implicit language: Language): Either[Seq[FormError], A] = (dayFromForm, monthFromForm, yearFromForm) match {
    case ("", "", "") => createFormError(key, Messages.WhatIsYourDateOfBirth.Errors.`Enter your date of birth`)

    case (day, "", "") if day.nonEmpty =>
      createFormError(key, Messages.WhatIsYourDateOfBirth.Errors.`Date of birth must include a [include whichever fields are incomplete]`("month and year"))

    case ("", month, year) if month.nonEmpty && year.nonEmpty =>
      createFormError(key, Messages.WhatIsYourDateOfBirth.Errors.`Date of birth must include a [include whichever fields are incomplete]`("day"))

    case ("", month, "") if month.nonEmpty =>
      createFormError(key, Messages.WhatIsYourDateOfBirth.Errors.`Date of birth must include a [include whichever fields are incomplete]`("day and year"))

    case ("", "", year) if year.isEmpty =>
      createFormError(key, Messages.WhatIsYourDateOfBirth.Errors.`Date of birth must include a [include whichever fields are incomplete]`("day and month"))

    case (_, "", _)  => createFormError(key, Messages.WhatIsYourDateOfBirth.Errors.`Date of birth must include a [include whichever fields are incomplete]`("month"))

    case ("", _, "") => Right(maybeThing)
    case ("", _, _)  => Right(maybeThing)
    case (_, _, "")  => createFormError(key, Messages.WhatIsYourDateOfBirth.Errors.`Date of birth must include a [include whichever fields are incomplete]`("year"))
    case (_, _, _)   => Right(maybeThing)

  }

  def dayFormatter(implicit language: Language): Formatter[DayOfMonth] = new Formatter[DayOfMonth] {
    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], DayOfMonth] = {
      val (dayFromForm, monthFromForm, yearFromForm) = (
        data.getOrElse(key, ""),
        data.getOrElse(dateOfBirthMonthKey, ""),
        data.getOrElse(dateOfBirthYearKey, "")
      )

      data.get(key) match {
        //this might be wrong
        case Some(value) if value.isEmpty => maybeEndOfMessage[DayOfMonth](key, DayOfMonth(value), dayFromForm, monthFromForm, yearFromForm)

        case Some(value) => Try(value.toInt) match {
          case Failure(_) => createFormError(key, Messages.WhatIsYourDateOfBirth.Errors.`You must enter a real date`)
          case Success(value) =>
            if (value < 1 || value > 31) createFormError(key, Messages.WhatIsYourDateOfBirth.Errors.`You must enter a real date`)
            else Right(DayOfMonth(value.toString))
        }

        //this might be wrong
        case None => maybeEndOfMessage[DayOfMonth](key, DayOfMonth(""), dayFromForm, monthFromForm, yearFromForm)
      }
    }

    override def unbind(key: String, value: DayOfMonth): Map[String, String] = Map(key -> value.value)
  }

  def monthFormatter(implicit language: Language): Formatter[Month] = new Formatter[Month] {
    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Month] = {
      val (dayFromForm, monthFromForm, yearFromForm) = (
        data.getOrElse(dateOfBirthDayKey, ""),
        data.getOrElse(key, ""),
        data.getOrElse(dateOfBirthYearKey, "")
      )

      data.get(key) match {
        //this might be wrong
        case Some(value) if value.isEmpty => maybeEndOfMessage[Month](key, Month(value), dayFromForm, monthFromForm, yearFromForm)

        case Some(value) => Try(value.toInt) match {
          case Failure(_) =>
            if (monthStringIsValidMonth(value)) Right(Month(value))
            else createFormError(key, Messages.WhatIsYourDateOfBirth.Errors.`You must enter a real date`)
          case Success(value) =>
            if (value < 1 || value > 12) createFormError(key, Messages.WhatIsYourDateOfBirth.Errors.`You must enter a real date`)
            else Right(Month(value.toString))
        }

        //this might be wrong
        case None => maybeEndOfMessage[Month](key, Month(""), dayFromForm, monthFromForm, yearFromForm)
      }
    }

    override def unbind(key: String, value: Month): Map[String, String] = Map(key -> value.value)
  }

  def yearFormatter(implicit language: Language): Formatter[Year] = new Formatter[Year] {
    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Year] = {
      val (dayFromForm, monthFromForm, yearFromForm) = (
        data.getOrElse(dateOfBirthDayKey, ""),
        data.getOrElse(dateOfBirthMonthKey, ""),
        data.getOrElse(key, "")
      )

      data.get(key) match {
        //this might be wrong
        case Some(value) if value.isEmpty => maybeEndOfMessage[Year](key, Year(value), dayFromForm, monthFromForm, yearFromForm)

        case Some(value) => Try(value.toInt) match {
          case Failure(_) => createFormError(key, Messages.WhatIsYourDateOfBirth.Errors.`Enter a year which contains 4 numbers`)
          case Success(value) =>
            //todo this is very rough atm, update
            val isCorrectFormat = """[0-9]{4}""".r.regex
            if (!(value.toString matches isCorrectFormat)) createFormError(key, Messages.WhatIsYourDateOfBirth.Errors.`Enter a year which contains 4 numbers`)
            else Right(Year(value.toString))
        }

        //this might be wrong
        case None => maybeEndOfMessage[Year](key, Year(""), dayFromForm, monthFromForm, yearFromForm)
      }
    }

    override def unbind(key: String, value: Year): Map[String, String] = Map(key -> value.value)
  }

  def dateConstraint(implicit language: Language): Constraint[DateOfBirth] = Constraint { date: DateOfBirth =>

      def maybeMonthAsNumber: Option[Int] = Try(date.month.value.toInt).toOption

    val monthIntoInt: Option[Int] = maybeMonthAsNumber match {
      case Some(value) => Some(value)
      case None => monthStringAndIntValue.collectFirst {
        case (m: String, monthNumber) if m.toLowerCase === date.month.value.toLowerCase => monthNumber + 1
      }
    }

    monthIntoInt match {
      case Some(validMonth) =>
        Try(java.time.LocalDate.of(date.year.value.toInt, validMonth, date.dayOfMonth.value.toInt)).fold(
          _ => Invalid(Messages.WhatIsYourDateOfBirth.Errors.`You must enter a real date`.show),
          validDate => {
            if (validDate.isAfter(LocalDate.now()))
              Invalid(Messages.WhatIsYourDateOfBirth.Errors.`Date of birth must be in the past`.show)

            else if (validDate.isBefore(LocalDate.now().minusYears(110L)))
              Invalid(Messages.WhatIsYourDateOfBirth.Errors.`Date of birth must be on or after`(LocalDate.now().minusYears(110L).format(DateTimeFormatsUtil.gdsDateTimeFormatter)).show)

            else if (validDate.isAfter(LocalDate.now().minusYears(16L)))
              Invalid(Messages.WhatIsYourDateOfBirth.Errors.`Date of birth must be on or before`(LocalDate.now().minusYears(16L).format(DateTimeFormatsUtil.gdsDateTimeFormatter)).show)

            else Valid
          }
        )
      case None => Invalid(Messages.WhatIsYourDateOfBirth.Errors.`You must enter a real date`.show)
    }
  }

}
