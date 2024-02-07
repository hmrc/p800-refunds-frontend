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

package models.forms.testonly

import play.api.data.{Form, Forms}
import play.api.data.Forms.mapping
import util.EnumFormatter

import enumeratum.Enum
import scala.collection.immutable.IndexedSeq

object BankStubForm {
  val form: Form[BankStubFormValue] = {
    val bankStubMapping = Forms.of(EnumFormatter.format(
      `enum`                  = BankStubFormValue,
      errorMessageIfMissing   = "Select an option",
      errorMessageIfEnumError = "Select an option"
    ))

    Form(
      mapping = mapping(
        "bank-result" -> bankStubMapping
      )(identity)(Some(_))
    )
  }
}

sealed trait BankStubFormValue extends enumeratum.EnumEntry

object BankStubFormValue extends Enum[BankStubFormValue] {
  case object Authorised extends BankStubFormValue
  case object Canceled extends BankStubFormValue
  case object Failed extends BankStubFormValue
  override def values: IndexedSeq[BankStubFormValue] = findValues
}
