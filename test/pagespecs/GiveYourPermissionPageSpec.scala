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

class GiveYourPermissionPageSpec extends ItSpec {

  override def beforeEach(): Unit = {
    super.beforeEach()
    addJourneyIdToSession(tdAll.journeyId)
    upsertJourneyToDatabase(tdAll.BankTransfer.journeySelectedBank)
  }

  "/give-your-permission renders the give your permission page" in {
    pages.giveYourPermissionPage.open()
    pages.giveYourPermissionPage.assertPageIsDisplayed()
    getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeySelectedBank
  }

  // TODO: ticket says url will be determined in another ticket - I think this is right though.
  "clicking 'Change my bank' redirects to 'What is the name of your bank' page" in {
    EcospendStub.stubEcospendAuth2xxSucceeded
    EcospendStub.stubEcospendGetBanks2xx
    pages.giveYourPermissionPage.open()
    pages.giveYourPermissionPage.assertPageIsDisplayed()
    pages.giveYourPermissionPage.clickChangeBank()
    pages.enterTheNameOfYourBankAccountPage.assertPageIsDisplayed()
    getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeySelectedBank
  }

  "clicking 'Approve this refund' redirects to 'Verifying bank account' via 'Bank Stub Page'" in {
    EcospendStub.stubEcospendAuth2xxSucceeded
    EcospendStub.ValidateStubs.stubValidateNotValidatedYet
    EcospendStub.stubEcospendAuth2xxSucceeded
    EcospendStub.ConsentStubs.stubConsent2xxSucceeded(tdAll.bankId)
    EcospendStub.stubEcospendAuth2xxSucceeded
    EcospendStub.AccountStub.stubAccountSummary2xxSucceeded(tdAll.consentId)

    pages.giveYourPermissionPage.open()
    pages.giveYourPermissionPage.assertPageIsDisplayed()
    pages.giveYourPermissionPage.clickApproveThisRefund()
    pages.bankStubPage.assertPageIsDisplayed()
    pages.bankStubPage.selectAuthorised()
    pages.bankStubPage.clickSubmit()
    pages.verifyBankAccountPage.assertPageIsDisplayed()

    getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeyAccountSummary
  }

  "clicking 'Choose another way to get my money' redirects to 'Choose another way to get my refund' page" in {
    pages.giveYourPermissionPage.open()
    pages.giveYourPermissionPage.assertPageIsDisplayed()
    pages.giveYourPermissionPage.clickChooseAnotherWayToGetMyMoney()
    pages.chooseAnotherWayToReceiveYourRefundBankTransferPage.assertPageIsDisplayedPtaOrCheque()
    getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeySelectedBank
  }

  "clicking 'Back' redirects user to 'Select a bank account' page" in {
    EcospendStub.stubEcospendAuth2xxSucceeded
    EcospendStub.stubEcospendGetBanks2xx
    pages.giveYourPermissionPage.open()
    pages.giveYourPermissionPage.assertPageIsDisplayed()
    pages.giveYourPermissionPage.clickBackButton()
    pages.enterTheNameOfYourBankAccountPage.assertPageIsDisplayed()
    getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeySelectedBank
  }
}
