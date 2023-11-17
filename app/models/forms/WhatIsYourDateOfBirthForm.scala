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

import models.dateofbirth.{DayOfMonth, Month, Year}
import play.api.data.Forms.{mapping, nonEmptyText}
import play.api.data.validation.{Constraint, Invalid, Valid}
import play.api.data.{Form, Mapping}
import util.SafeEquals.EqualsOps

import scala.util.{Failure, Success, Try}

final case class WhatIsYourDateOfBirthForm(dayOfMonth: DayOfMonth, month: Month, year: Year)

object WhatIsYourDateOfBirthForm {

  def form: Form[WhatIsYourDateOfBirthForm] = Form(
    mapping(
      "dayOfMonth" -> dayOfMonthMapping,
      "month" -> monthMapping,
      "year" -> yearMapping
    )(WhatIsYourDateOfBirthForm.apply)(WhatIsYourDateOfBirthForm.unapply)
  )

  val dayOfMonthConstraint: Constraint[DayOfMonth] = Constraint { dayOfMonth =>
    Try(dayOfMonth.value.toInt) match {
      case Failure(_) => Invalid("not a number")
      case Success(value) =>
        if (value > 31) Invalid("too big")
        else if (value < 1) Invalid("too small")
        else Valid
    }
  }

  val dayOfMonthMapping: Mapping[DayOfMonth] = {
    nonEmptyText
      .transform[DayOfMonth]((day: String) => DayOfMonth(day), _.value)
      .verifying(dayOfMonthConstraint)
  }

  val monthConstraint: Constraint[Month] = Constraint { month =>
    if (month.value === "invalid") Invalid("someerror")
    else Valid
  }

  val monthMapping: Mapping[Month] = {
    nonEmptyText
      .transform[Month](month => Month(month), _.value)
      .verifying(monthConstraint)
  }

  val yearConstraint: Constraint[Year] = Constraint { year =>
    Try(year.value.toInt) match {
      case Failure(_) => Invalid("not a number")
      case Success(value) =>
        if (value < 31) Invalid("someerror")
        else Valid
    }
  }

  val yearMapping: Mapping[Year] = {
    nonEmptyText
      .transform[Year]((year: String) => Year(year), _.value)
      .verifying(yearConstraint)
  }

}
