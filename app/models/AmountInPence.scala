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

package models

import cats.Eq
import play.api.libs.json._

import java.text.NumberFormat
import java.util.Locale

final case class AmountInPence(value: Long) {
  def formatInPounds: String = NumberFormat.getCurrencyInstance(Locale.UK).format(inPounds)

  //removes trailing decimal and zeros, i.e. if £x.00
  def gdsFormatInPounds: String = formatInPounds.replace(".00", "")

  def formatInDecimal: String = "%,1.2f".format(inPounds)

  def inPounds: BigDecimal = AmountInPence.toPounds(this)

  def >(other: AmountInPence): Boolean = value > other.value
  def +(other: AmountInPence): AmountInPence = AmountInPence(value + other.value)
  def -(other: AmountInPence): AmountInPence = AmountInPence(value - other.value)
}

object AmountInPence {
  def apply(str: String): AmountInPence = {
    str match {
      case s if s.isEmpty => AmountInPence(0)
      case s              => AmountInPence((BigDecimal(s) * 100).toLongExact)
    }
  }
  def apply(bigDecimal: BigDecimal): AmountInPence = AmountInPence((bigDecimal * 100).toLongExact)

  val zero: AmountInPence = AmountInPence(0)

  implicit val eq: Eq[AmountInPence] = Eq.fromUniversalEquals

  implicit val format: Format[AmountInPence] = Format(
    Reads{
      case JsNumber(n) if n.isWhole => JsSuccess(AmountInPence(n.toLong))
      case JsNumber(_)              => JsError("Expected positive integer but got non-integral number")
      case other                    => JsError(s"Expected positive integer but got type ${other.getClass.getSimpleName}")
    },
    Writes(a => JsNumber(BigDecimal(a.value)))
  )

  private def toPounds(amountInPence: AmountInPence): BigDecimal = {
    val pd = BigDecimal(amountInPence.value) / 100
    if (pd.isValidInt) pd else pd.setScale(2)
  }
}
