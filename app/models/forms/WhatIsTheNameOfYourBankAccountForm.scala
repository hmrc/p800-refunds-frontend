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

import language.{Language, Messages}
import models.ecospend.BankId
import play.api.data.format.Formatter
import play.api.data.{Form, FormError, Forms}

object WhatIsTheNameOfYourBankAccountForm {
  def form(implicit language: Language): Form[BankId] = {
    val bankIdFormatter = new Formatter[BankId]() {
      override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], BankId] =
        data.get(key) match {
          case Some(value) if value.nonEmpty => Right(BankId(value))
          case _                             => Left(Seq(FormError(key, Messages.WhatIsTheNameOfYourBankAccount.`Select a bank from the list`.show)))
        }

      override def unbind(key: String, value: BankId): Map[String, String] = Map(key -> value.value)
    }

    Form(
      "selectedBankId" -> Forms.of(bankIdFormatter)
    )
  }
}
