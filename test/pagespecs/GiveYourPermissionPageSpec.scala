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

import models.p800externalapi.EventValue
import testsupport.ItSpec
import testsupport.stubs.{EcospendStub, P800RefundsExternalApiStub, NpsClaimOverpaymentStub}

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
    EcospendStub.AuthStubs.stubEcospendAuth2xxSucceeded
    EcospendStub.BanksStubs.stubEcospendGetBanks2xx
    pages.giveYourPermissionPage.open()
    pages.giveYourPermissionPage.assertPageIsDisplayed()
    pages.giveYourPermissionPage.clickChangeBank()
    pages.enterTheNameOfYourBankAccountPage.assertPageIsDisplayed()
    getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeySelectedBank
  }

  "clicking 'Approve this refund' redirects to 'Verifying bank account' via 'Bank Stub Page'" in {
    EcospendStub.AuthStubs.stubEcospendAuth2xxSucceeded
    EcospendStub.ValidateStubs.stubValidateNotValidatedYet
    EcospendStub.AuthStubs.stubEcospendAuth2xxSucceeded
    EcospendStub.ConsentStubs.stubConsent2xxSucceeded(tdAll.bankId)
    EcospendStub.AuthStubs.stubEcospendAuth2xxSucceeded
    EcospendStub.AccountStub.stubAccountSummary2xxSucceeded(tdAll.consentId)
    P800RefundsExternalApiStub.isValid(tdAll.consentId, EventValue.NotReceived)
    NpsClaimOverpaymentStub.claimOverpayment(
      nino          = tdAll.nino,
      p800Reference = tdAll.p800ReferenceSanitised,
      request       = tdAll.claimOverpaymentRequest,
      response      = tdAll.claimOverpaymentResponse,
    )

    pages.giveYourPermissionPage.open()
    pages.giveYourPermissionPage.assertPageIsDisplayed()
    pages.giveYourPermissionPage.clickApproveThisRefund()
    pages.bankStubPage.assertPageIsDisplayed()
    pages.bankStubPage.selectAuthorised()
    pages.bankStubPage.clickSubmit()
    pages.verifyBankAccountPage.assertPageIsDisplayed()

    NpsClaimOverpaymentStub.verifyClaimOverpayment(tdAll.nino, tdAll.p800ReferenceSanitised)
    getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeyReceivedNotificationFromEcospendNotReceived
  }

  "clicking 'Choose another way to get my money' redirects to 'Choose another way to get my refund' page" in {
    pages.giveYourPermissionPage.open()
    pages.giveYourPermissionPage.assertPageIsDisplayed()
    pages.giveYourPermissionPage.clickChooseAnotherWayToGetMyMoney()
    pages.chooseAnotherWayToReceiveYourRefundPage.assertPageIsDisplayed()
    getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeySelectedBank
  }
}
