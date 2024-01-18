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

package pagespecs.pages

import org.openqa.selenium.WebDriver

/**
 * This page is used only for testing showing errors on choose-another-way page
 * (the url changes)
 */
class ChooseAnotherWayToReceiveYourRefundPtaOrBankTransferPage(baseUrl: String)(implicit webDriver: WebDriver) extends ChooseAnotherWayToReceiveYourRefundPage(
  baseUrl,
  path = "/get-an-income-tax-refund/choose-another-way-to-receive-your-refund/bank-transfer-via-pta-or-bank-transfer-logged-out"
)
