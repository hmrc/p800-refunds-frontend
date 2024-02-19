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

import enumeratum._

sealed trait BankAccountSubType extends EnumEntry
object BankAccountSubType extends Enum[BankAccountSubType] with PlayJsonEnum[BankAccountSubType] {
  val values = findValues

  case object ChargeCard extends BankAccountSubType
  case object CreditCard extends BankAccountSubType
  case object CurrentAccount extends BankAccountSubType
  case object EMoney extends BankAccountSubType
  case object Loan extends BankAccountSubType
  case object Mortgage extends BankAccountSubType
  case object PrePaidCard extends BankAccountSubType
  case object Savings extends BankAccountSubType
  case object Primary extends BankAccountSubType
  case object Additional extends BankAccountSubType
  case object FixedTermDeposit extends BankAccountSubType
}

