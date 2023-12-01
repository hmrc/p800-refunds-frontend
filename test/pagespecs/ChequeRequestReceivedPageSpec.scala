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

package pagespecs

import testsupport.ItSpec

class ChequeRequestReceivedPageSpec extends ItSpec {

  override def beforeEach(): Unit = {
    super.beforeEach()
    addJourneyIdToSession(tdAll.journeyId)
    upsertJourneyToDatabase(tdAll.journeyYourChequeWillBePostedToYou)
  }

  "/request-received renders the cheque request received page" in {
    pages.chequeRequestReceivedPage.open()
    pages.chequeRequestReceivedPage.assertPageIsDisplayed()
  }

  "user is kept in the final page if clicked browser's back button" in {
    //setup the history in the browser:
    upsertJourneyToDatabase(tdAll.journeyDoYouWantYourRefundViaBankTransferNo)
    pages.yourChequeWillBePostedToYouPage.open()
    pages.yourChequeWillBePostedToYouPage.assertPageIsDisplayed()
    pages.yourChequeWillBePostedToYouPage.clickSubmitRefundRequest()

    pages.chequeRequestReceivedPage.open()
    pages.chequeRequestReceivedPage.assertPageIsDisplayed()
    pages.chequeRequestReceivedPage.clickBackButtonInBrowser()
    pages.chequeRequestReceivedPage.assertPageIsDisplayed()
  }

  "clicking service name navigates to the gov-uk route in page" in {
    pages.chequeRequestReceivedPage.open()
    pages.chequeRequestReceivedPage.assertPageIsDisplayed()
    pages.chequeRequestReceivedPage.clickServiceName()
    pages.govUkRouteInPage.assertPageIsDisplayed()
  }
}
