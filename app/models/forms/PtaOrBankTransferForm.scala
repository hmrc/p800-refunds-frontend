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
import models.forms.enumsforforms.PtaOrBankTransferFormValue
import play.api.data.Forms.mapping
import play.api.data.{Form, Forms}
import util.EnumFormatter

/**
 * Form for choosing Bank Transfer Via Personal Tax Account (Pta) or Bank Transfer Logged Out
 */
object PtaOrBankTransferForm {

  def form(implicit language: Language): Form[PtaOrBankTransferFormValue] = {
    val chooseAnotherWayToGetYourRefundMapping = Forms.of(EnumFormatter.format(
      `enum`                  = PtaOrBankTransferFormValue,
      errorMessageIfMissing   = Messages.ChooseAnotherWayToReceiveYourRefund.`Select if you want to receive a bank transfer via your personal tax account, or a bank transfer logged out`.show,
      errorMessageIfEnumError = Messages.ChooseAnotherWayToReceiveYourRefund.`Select if you want to receive a bank transfer via your personal tax account, or a bank transfer logged out`.show
    ))

    Form(
      mapping = mapping(
        "way-to-get-refund" -> chooseAnotherWayToGetYourRefundMapping
      )(identity)(Some(_))
    )
  }
}
