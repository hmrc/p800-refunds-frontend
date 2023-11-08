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

import testdata.TdAll
import testsupport.ItSpec

class YourChequeWillBePostedToYouPageSpec extends ItSpec {

  "/your-cheque-will-be-posted-to-you renders your cheque will be posted page" in {
    pages.yourChequeWillBePostedToYouPage.open()
    pages.yourChequeWillBePostedToYouPage.assertPageIsDisplayed()
  }

  "clicking Submit refund request cta button redirects to cheque request received" in {
    pages.yourChequeWillBePostedToYouPage.open()
    pages.yourChequeWillBePostedToYouPage.assertPageIsDisplayed()
    pages.yourChequeWillBePostedToYouPage.clickSubmitRefundRequest()
    // will there be an api call made at this point? todo add wiremock check for that call, if not remove this comment.
    pages.chequeRequestReceivedPage.assertPageIsDisplayed()
  }

  "back button sends user to do you want your refund via bank transfer page" in {
    addJourneyIdToSession(TdAll.journeyId)
    upsertJourneyToDatabase(TdAll.journeyCheckYourReferenceValid)
    pages.yourChequeWillBePostedToYouPage.open()
    pages.yourChequeWillBePostedToYouPage.assertPageIsDisplayed()
    pages.yourChequeWillBePostedToYouPage.clickBackButton()
    pages.doYouWantYourRefundViaBankTransferPage.assertPageIsDisplayed()
  }
}
