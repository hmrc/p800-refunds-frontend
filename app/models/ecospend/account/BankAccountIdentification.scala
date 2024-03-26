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

package models.ecospend.account

import edh.{BankAccountNumber, BankSortCode}
import nps.models.{PayeeBankAccountNumber, PayeeBankSortCode}
import play.api.libs.json.{Format, Json}

final case class BankAccountIdentification(value: String) extends AnyVal {
  private def asSortCodeAndAccountNumber: (String, String) = value.splitAt(6)

  private def sortCode: String = asSortCodeAndAccountNumber._1
  //TODO: The length of an account number can vary depending on the bank, but it's typically between 6 to 10 digits long.
  private def bankAccountNumber: String = asSortCodeAndAccountNumber._2

  def asBankSortCode: BankSortCode = BankSortCode(sortCode)
  def asPayeeBankSortCode: PayeeBankSortCode = PayeeBankSortCode(sortCode)

  def asPayeeBankAccountNumber: PayeeBankAccountNumber = PayeeBankAccountNumber(bankAccountNumber)
  def asBankAccountNumber: BankAccountNumber = BankAccountNumber(bankAccountNumber)
}

object BankAccountIdentification {
  implicit val format: Format[BankAccountIdentification] = Json.valueFormat[BankAccountIdentification]
}
