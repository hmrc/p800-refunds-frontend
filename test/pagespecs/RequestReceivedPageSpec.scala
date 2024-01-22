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

class RequestReceivedPageSpec extends ItSpec {

  override def beforeEach(): Unit = {
    super.beforeEach()
    addJourneyIdToSession(tdAll.journeyId)
  }

  "/request-received should render the relevant page for" - {
    "bank transfer" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.journeyClaimedOverpayment)
      pages.requestReceivedBankTransferPage.open()
      pages.requestReceivedBankTransferPage.assertPageIsDisplayedForBankTransfer()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeyClaimedOverpayment
    }

    "cheque" in {
      upsertJourneyToDatabase(tdAll.Cheque.journeyClaimedOverpayment)
      pages.requestReceivedChequePage.open()
      pages.requestReceivedChequePage.assertPageIsDisplayedForCheque()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.Cheque.journeyClaimedOverpayment
    }
  }

  //TODO: unignore this when we have the callbacks/fetching of the bank verification statuses from ecospend along with other API calls, rewrite in style above
  "[bank transfer ]user is kept in the final page if clicked browser's back button" ignore {
    upsertJourneyToDatabase(tdAll.BankTransfer.journeyPermissionGiven)
    getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeyPermissionGiven
    EcospendStub.stubEcospendAuth2xxSucceeded
    EcospendStub.ValidateStubs.stubValidateNotValidatedYet
    //setup the history in the browser:
    pages.verifyBankAccountPage.open()
    pages.verifyBankAccountPage.assertPageIsDisplayed()
    EcospendStub.ValidateStubs.stubValidatePaymentSuccessful()
    pages.verifyBankAccountPage.clickRefreshThisPageLink()
    pages.requestReceivedBankTransferPage.assertPageIsDisplayedForBankTransfer()
    pages.requestReceivedBankTransferPage.clickBackButtonInBrowser()
    pages.requestReceivedBankTransferPage.assertPageIsDisplayedForBankTransfer()
    getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeyClaimedOverpayment
  }

  //TODO: unignore this when we have the other API calls, rewrite in style above
  "[cheque] user is kept in the final page if clicked browser's back button" ignore {
    //setup the history in the browser:
    //TODO    upsertJourneyToDatabase(tdAll.journeyDoYouWantYourRefundViaBankTransferNo)
    pages.completeYourRefundRequestPage.open()
    pages.completeYourRefundRequestPage.assertPageIsDisplayed()
    pages.completeYourRefundRequestPage.clickSubmitRefundRequest()

    pages.requestReceivedChequePage.open()
    pages.requestReceivedChequePage.assertPageIsDisplayedForCheque()
    pages.requestReceivedChequePage.clickBackButtonInBrowser()
    pages.requestReceivedChequePage.assertPageIsDisplayed()
  }

}
