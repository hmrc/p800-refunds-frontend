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

import models.journeymodels.JourneyType
import testsupport.ItSpec
import testsupport.stubs.EcospendStub

class WhatIsTheNameOfYourBankAccountPageSpec extends ItSpec {

  override def beforeEach(): Unit = {
    super.beforeEach()

    addJourneyIdToSession(tdAll.journeyId)
    upsertJourneyToDatabase(tdAll.BankTransfer.AfterReferenceCheck.journeyReferenceChecked)

    EcospendStub.AuthStubs.stubEcospendAuth2xxSucceeded
    EcospendStub.BanksStubs.stubEcospendGetBanks2xx

    ()
  }

  "/what-is-the-name-of-your-bank-account renders the what is the name of your bank account page" in {
    pages.enterTheNameOfYourBankAccountPage.open()
    pages.enterTheNameOfYourBankAccountPage.assertPageIsDisplayed()

    EcospendStub.AuthStubs.verifyEcospendAccessToken()
    EcospendStub.BanksStubs.verifyEcospendGetBanks()
  }

  "Clicking 'Continue' after selecing a bank name redirects to 'Give your permission' page" in {
    pages.enterTheNameOfYourBankAccountPage.open()
    pages.enterTheNameOfYourBankAccountPage.assertPageIsDisplayed()
    pages.enterTheNameOfYourBankAccountPage.selectBankAccount(tdAll.bankId)
    pages.enterTheNameOfYourBankAccountPage.clickSubmit()
    pages.giveYourPermissionPage.assertPageIsDisplayed()

    EcospendStub.AuthStubs.verifyEcospendAccessToken(numberOfRequests = 2)
    EcospendStub.BanksStubs.verifyEcospendGetBanks(numberOfRequests = 2)
  }

  "Clicking 'My account is not listed' redirects to 'Choose another option' page" in {
    pages.enterTheNameOfYourBankAccountPage.open()
    pages.enterTheNameOfYourBankAccountPage.assertPageIsDisplayed()
    pages.enterTheNameOfYourBankAccountPage.clickMyAccountIsNotListed()
    pages.chooseAnotherWayToReceiveYourRefundBankTransferPage.assertPageIsDisplayedPtaOrCheque()

    EcospendStub.AuthStubs.verifyEcospendAccessToken()
    EcospendStub.BanksStubs.verifyEcospendGetBanks()
  }

  "Clicking 'Continue' without selecting a bank shows error" in {
    pages.enterTheNameOfYourBankAccountPage.open()
    pages.enterTheNameOfYourBankAccountPage.assertPageIsDisplayed()
    pages.enterTheNameOfYourBankAccountPage.clickSubmit()
    pages.enterTheNameOfYourBankAccountPage.assertPageShowsError() // should fail?

    EcospendStub.AuthStubs.verifyEcospendAccessToken(numberOfRequests = 2)
    EcospendStub.BanksStubs.verifyEcospendGetBanks(numberOfRequests = 2)
  }

  "Clicking 'Back' redirects to /we-have-confirmed-your-identity" in {
    pages.enterTheNameOfYourBankAccountPage.open()
    pages.enterTheNameOfYourBankAccountPage.assertPageIsDisplayed()
    pages.enterTheNameOfYourBankAccountPage.clickBackButton()
    pages.yourIdentityIsConfirmedBankTransferPage.assertPageIsDisplayed(JourneyType.BankTransfer)

    EcospendStub.AuthStubs.verifyEcospendAccessToken()
    EcospendStub.BanksStubs.verifyEcospendGetBanks()
  }
}
