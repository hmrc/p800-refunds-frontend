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

class RequestNotSubmittedPageSpec extends ItSpec {

  override def beforeEach(): Unit = {
    super.beforeEach()

    addJourneyIdToSession(tdAll.journeyId)
    //TODO    upsertJourneyToDatabase(tdAll.journeyNotApprovedRefund)
  }

  "/request-not-submitted renders 'Request not submitted' page" in {
    pages.requestNotSubmittedPage.open()
    pages.requestNotSubmittedPage.assertPageIsDisplayed()
  }

  "Clicking 'Choose another way to you my money' redirects to 'Choose another way to recive your refund' page" in {
    pages.requestNotSubmittedPage.open()
    pages.requestNotSubmittedPage.assertPageIsDisplayed()
    pages.requestNotSubmittedPage.clickTryAgain()
    pages.chooseAnotherWayToReceiveYourRefundPage.assertPageIsDisplayed()
  }

  "Clicking browser back button should remain on 'Request not submitted' page" in {
    // setup the history in the browser:
    //TODO    upsertJourneyToDatabase(tdAll.journeyRefundConsentGiven)

    EcospendStub.stubEcospendAuth2xxSucceeded
    EcospendStub.ValidateStubs.stubValidateNotValidatedYet
    pages.verifyBankAccountPage.open()
    pages.verifyBankAccountPage.assertPageIsDisplayed()
    EcospendStub.ValidateStubs.stubValidatePaymentUnSuccessful()
    pages.verifyBankAccountPage.clickRefreshThisPageLink()

    // then assert we stay on the page after the back button is clicked
    pages.requestNotSubmittedPage.assertPageIsDisplayed()
    pages.requestNotSubmittedPage.clickBackButtonInBrowser()
    pages.requestNotSubmittedPage.assertPageIsDisplayed()

    EcospendStub.ValidateStubs.verifyValidate(2)
  }

}
