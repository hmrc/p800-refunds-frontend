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

class VerifyBankAccountPageSpec extends ItSpec {

  override def beforeEach(): Unit = {
    super.beforeEach()
    addJourneyIdToSession(tdAll.journeyId)
    upsertJourneyToDatabase(tdAll.journeyRefundConsentGiven)
  }

  "/verifying-bank-account renders the 'We are verifying your bank account' page" in {
    EcospendStub.stubEcospendAuth2xxSucceeded
    EcospendStub.ValidateStubs.stubValidateNotValidatedYet
    pages.verifyBankAccountPage.open()
    pages.verifyBankAccountPage.assertPageIsDisplayed()
    EcospendStub.ValidateStubs.verifyValidate()
  }

  "clicking 'refresh this page' refreshes the page - showing the same page if bank is not verified yet" in {
    EcospendStub.stubEcospendAuth2xxSucceeded
    EcospendStub.ValidateStubs.stubValidateNotValidatedYet
    pages.verifyBankAccountPage.open()
    pages.verifyBankAccountPage.assertPageIsDisplayed()
    pages.verifyBankAccountPage.clickRefreshThisPageLink()
    pages.verifyBankAccountPage.assertPageIsDisplayed()
    EcospendStub.ValidateStubs.verifyValidate(2)
  }

  "redirect to bank transfer 'Request received' page when verification call returns Successful" in {
    EcospendStub.stubEcospendAuth2xxSucceeded
    EcospendStub.ValidateStubs.stubValidateNotValidatedYet
    pages.verifyBankAccountPage.open()
    pages.verifyBankAccountPage.assertPageIsDisplayed()
    EcospendStub.ValidateStubs.stubValidatePaymentSuccessful()
    pages.verifyBankAccountPage.clickRefreshThisPageLink()
    pages.bankTransferRequestReceivedPage.assertPageIsDisplayed()
    EcospendStub.ValidateStubs.verifyValidate(2)
  }

  "redirect to 'Request not submitted' page when verification call returns UnSuccessful" in {
    EcospendStub.stubEcospendAuth2xxSucceeded
    EcospendStub.ValidateStubs.stubValidateNotValidatedYet
    pages.verifyBankAccountPage.open()
    pages.verifyBankAccountPage.assertPageIsDisplayed()
    EcospendStub.ValidateStubs.stubValidatePaymentUnSuccessful()
    pages.verifyBankAccountPage.clickRefreshThisPageLink()
    pages.requestNotSubmittedPage.assertPageIsDisplayed()
    EcospendStub.ValidateStubs.verifyValidate(2)
  }

  "clicking 'Back' sends user to 'What is the name of your bank account' page" in {
    EcospendStub.stubEcospendAuth2xxSucceeded
    EcospendStub.ValidateStubs.stubValidateNotValidatedYet
    EcospendStub.stubEcospendGetBanks2xx
    pages.verifyBankAccountPage.open()
    pages.verifyBankAccountPage.assertPageIsDisplayed()
    pages.verifyBankAccountPage.clickBackButton()
    pages.whatIsTheNameOfYourBankAccountPage.assertPageIsDisplayed()
  }

}
