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
import testsupport.stubs.EcospendStub

class BankTransferRequestReceivedPageSpec extends ItSpec {

  override def beforeEach(): Unit = {
    super.beforeEach()
    addJourneyIdToSession(tdAll.journeyId)
    upsertJourneyToDatabase(tdAll.journeyApprovedRefund)
  }

  "/bank/request-received renders the bank transfer request received page" in {
    pages.bankTransferRequestReceivedPage.open()
    pages.bankTransferRequestReceivedPage.assertPageIsDisplayed()
  }

  "user is kept in the final page if clicked browser's back button" in {
    upsertJourneyToDatabase(tdAll.journeyRefundConsentGiven)
    EcospendStub.stubEcospendAuth2xxSucceeded
    EcospendStub.ValidateStubs.stubValidateNotValidatedYet
    //setup the history in the browser:
    pages.verifyBankAccountPage.open()
    pages.verifyBankAccountPage.assertPageIsDisplayed()
    EcospendStub.ValidateStubs.stubValidatePaymentSuccessful()
    pages.verifyBankAccountPage.clickRefreshThisPageLink()
    pages.bankTransferRequestReceivedPage.assertPageIsDisplayed()
    pages.bankTransferRequestReceivedPage.clickBackButtonInBrowser()
    pages.bankTransferRequestReceivedPage.assertPageIsDisplayed()
  }

}
