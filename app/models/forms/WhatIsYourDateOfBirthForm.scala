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
import scala.util.{Failure, Success, Try}

final case class WhatIsYourDateOfBirthForm(date: DateOfBirth)

object WhatIsYourDateOfBirthForm {

  def form(implicit language: Language): Form[WhatIsYourDateOfBirthForm] = Form[WhatIsYourDateOfBirthForm](
    mapping(
      "date" -> mapping(
        "day" -> Forms.of(dayFormatter),
        "month" -> Forms.of(monthFormatter),
        "year" -> Forms.of(yearFormatter)
      )(DateOfBirth.apply)(DateOfBirth.unapply).verifying(dateConstraint)
    )(WhatIsYourDateOfBirthForm.apply)(WhatIsYourDateOfBirthForm.unapply)
  )

  private val dateOfBirthDayKey = "date.day"
  private val dateOfBirthMonthKey = "date.month"
  private val dateOfBirthYearKey = "date.year"

  private def monthStringIsValidMonth(month: String): Boolean = DateOfBirth.monthStringAndIntValue.collectFirst {
    case (m: String, _: Int) if m.toLowerCase === month.toLowerCase => true
  }.getOrElse(false)

  private def createFormError(key: String, message: Message)(implicit language: Language): Left[Seq[FormError], Nothing] = Left(Seq(FormError(key, message.show)))

  private def checkIfFormValuesAreEmpty[A](
      key:                              String,
      valueFromFormToKeepIfNotAllEmpty: A,
      dayFromForm:                      String,
      monthFromForm:                    String,
      yearFromForm:                     String
  )(implicit language: Language): Either[Seq[FormError], A] = {
    val errorMessageOrFormInput: Either[Message, A] = (dayFromForm.isEmpty, monthFromForm.isEmpty, yearFromForm.isEmpty) match {
      case (true, true, true)    => Left(Messages.WhatIsYourDateOfBirth.Errors.`Enter your date of birth`)
      case (false, true, true)   => Left(Messages.WhatIsYourDateOfBirth.Errors.`Date of birth must include a ...`(Message("month and year", "mis a blwyddyn")))
      case (true, false, false)  => Left(Messages.WhatIsYourDateOfBirth.Errors.`Date of birth must include a ...`(Message("day", "diwrnod")))
      case (true, false, true)   => Left(Messages.WhatIsYourDateOfBirth.Errors.`Date of birth must include a ...`(Message("day and year", "diwrnod a blwyddyn")))
      case (true, true, false)   => Left(Messages.WhatIsYourDateOfBirth.Errors.`Date of birth must include a ...`(Message("day and month", "diwrnod a mis")))
      case (false, true, false)  => Left(Messages.WhatIsYourDateOfBirth.Errors.`Date of birth must include a ...`(Message("month", "mis")))
      case (false, false, true)  => Left(Messages.WhatIsYourDateOfBirth.Errors.`Date of birth must include a ...`(Message("year", "blwyddyn")))
      case (false, false, false) => Right(valueFromFormToKeepIfNotAllEmpty)
    }
    errorMessageOrFormInput.fold((errorMessage: Message) => createFormError(key, errorMessage), Right(_))
  }

  def dayFormatter(implicit language: Language): Formatter[DayOfMonth] = new Formatter[DayOfMonth] {
    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], DayOfMonth] = {
      val (dayFromForm, monthFromForm, yearFromForm) = (
        data.getOrElse(key, ""),
        data.getOrElse(dateOfBirthMonthKey, ""),
        data.getOrElse(dateOfBirthYearKey, "")
      )

      data.get(key) match {
        case Some(value) if value.isEmpty => checkIfFormValuesAreEmpty[DayOfMonth](key, DayOfMonth(value), dayFromForm, monthFromForm, yearFromForm)
        case Some(value) => Try(value.toInt) match {
          case Failure(_) => createFormError(key, Messages.WhatIsYourDateOfBirth.Errors.`You must enter a real date`)
          case Success(value) =>
            if (value < 1 || value > 31) createFormError(key, Messages.WhatIsYourDateOfBirth.Errors.`You must enter a real date`)
            else Right(DayOfMonth(value.toString))
        }
        case None => checkIfFormValuesAreEmpty[DayOfMonth](key, DayOfMonth(""), dayFromForm, monthFromForm, yearFromForm)
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
        case Some(value) if value.isEmpty => checkIfFormValuesAreEmpty[Month](key, Month(value), dayFromForm, monthFromForm, yearFromForm)
        case Some(value) => Try(value.toInt) match {
          case Failure(_) =>
            if (monthStringIsValidMonth(value)) Right(Month(value))
            else createFormError(key, Messages.WhatIsYourDateOfBirth.Errors.`You must enter a real date`)
          case Success(value) =>
            if (value < 1 || value > 12) createFormError(key, Messages.WhatIsYourDateOfBirth.Errors.`You must enter a real date`)
            else Right(Month(value.toString))
        }
        case None => checkIfFormValuesAreEmpty[Month](key, Month(""), dayFromForm, monthFromForm, yearFromForm)
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
        case Some(value) if value.isEmpty => checkIfFormValuesAreEmpty[Year](key, Year(value), dayFromForm, monthFromForm, yearFromForm)
        case Some(value) => Try(value.toInt) match {
          case Failure(_) =>
            createFormError(key, Messages.WhatIsYourDateOfBirth.Errors.`Enter a year which contains 4 numbers`)
          case Success(value) =>
            val yearAsFourIntegers: String = """[0-9]{4}""".r.regex
            if (!value.toString.matches(yearAsFourIntegers)) createFormError(key, Messages.WhatIsYourDateOfBirth.Errors.`Enter a year which contains 4 numbers`)
            else Right(Year(value.toString))
        }
        case None => checkIfFormValuesAreEmpty[Year](key, Year(""), dayFromForm, monthFromForm, yearFromForm)
      }
    }

    override def unbind(key: String, value: Year): Map[String, String] = Map(key -> value.value)
  }

  def dateConstraint(implicit language: Language): Constraint[DateOfBirth] = Constraint { date: DateOfBirth =>

    val monthIntoInt: Option[Int] = Try(date.month.value.toInt).toOption match {
      case Some(value) => Some(value)
      case None => DateOfBirth.monthStringAndIntValue.collectFirst {
        case (m: String, monthNumber) if m.toLowerCase === date.month.value.toLowerCase => monthNumber + 1
      }
    }

    monthIntoInt match {
      case Some(validMonth) =>
        Try(java.time.LocalDate.of(date.year.value.toInt, validMonth, date.dayOfMonth.value.toInt)).fold(
          _ => Invalid(Messages.WhatIsYourDateOfBirth.Errors.`You must enter a real date`.show),
          validDate => {

            val dateNow: LocalDate = LocalDate.now()
            val dateFor16Years: LocalDate = dateNow.minusYears(16L)
            val dateFor110Years: LocalDate = dateNow.minusYears(110L)

            if (validDate.isAfter(dateNow))
              Invalid(Messages.WhatIsYourDateOfBirth.Errors.`Date of birth must be in the past`.show)

            else if (validDate.isAfter(dateFor16Years))
              Invalid(Messages.WhatIsYourDateOfBirth.Errors.`Date of birth must be on or before`(dateFor16Years.format(DateTimeFormatsUtil.gdsDateTimeFormatter)).show)

            else if (validDate.isBefore(dateFor110Years))
              Invalid(Messages.WhatIsYourDateOfBirth.Errors.`Date of birth must be on or after`(dateFor110Years.format(DateTimeFormatsUtil.gdsDateTimeFormatter)).show)

            else Valid
          }
        )
      case None => Invalid(Messages.WhatIsYourDateOfBirth.Errors.`You must enter a real date`.show)
    }
  }

}
