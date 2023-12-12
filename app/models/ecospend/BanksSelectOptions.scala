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

package models.ecospend

final case class BanksSelectOptions(bankId: Option[BankId], bankName: Option[BankName])

object BanksSelectOptions {
  def noBankOption: BanksSelectOptions =
    BanksSelectOptions(None, Some(BankName("Choose your bank")))

  def apply(bankId: BankId, bankName: BankName): BanksSelectOptions =
    BanksSelectOptions(Some(bankId), Some(bankName))

  implicit val banksSelectOptionsOrdering: Ordering[BanksSelectOptions] =
    Ordering.by(_.bankName.getOrElse(BankName("")).value)
}

