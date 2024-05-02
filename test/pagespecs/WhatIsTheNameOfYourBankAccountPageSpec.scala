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
    pages.enterNameOfYourBankAccountPage.open()
    pages.enterNameOfYourBankAccountPage.assertPageIsDisplayed()

    EcospendStub.AuthStubs.verifyEcospendAccessToken()
    EcospendStub.BanksStubs.verifyEcospendGetBanks()
  }

  "Clicking 'Continue' after selecing a bank name redirects to 'Give your permission' page" in {
    pages.enterNameOfYourBankAccountPage.open()
    pages.enterNameOfYourBankAccountPage.assertPageIsDisplayed()
    pages.enterNameOfYourBankAccountPage.selectBankAccount(tdAll.bankId)
    pages.enterNameOfYourBankAccountPage.clickSubmit()
    pages.giveYourPermissionPage.assertPageIsDisplayed()

    EcospendStub.AuthStubs.verifyEcospendAccessToken(numberOfRequests = 2)
    EcospendStub.BanksStubs.verifyEcospendGetBanks(numberOfRequests = 2)
  }

  "Clicking 'My account is not listed' redirects to 'Choose another option' page" in {
    pages.enterNameOfYourBankAccountPage.open()
    pages.enterNameOfYourBankAccountPage.assertPageIsDisplayed()
    pages.enterNameOfYourBankAccountPage.clickMyAccountIsNotListed()
    pages.chooseAnotherWayToReceiveYourRefundPage.assertPageIsDisplayed()

    EcospendStub.AuthStubs.verifyEcospendAccessToken()
    EcospendStub.BanksStubs.verifyEcospendGetBanks()
  }

  "Clicking 'Continue' without selecting a bank shows error" in {
    pages.enterNameOfYourBankAccountPage.open()
    pages.enterNameOfYourBankAccountPage.assertPageIsDisplayed()
    pages.enterNameOfYourBankAccountPage.clickSubmit()
    pages.enterNameOfYourBankAccountPage.assertPageShowsError() // should fail?

    EcospendStub.AuthStubs.verifyEcospendAccessToken(numberOfRequests = 2)
    EcospendStub.BanksStubs.verifyEcospendGetBanks(numberOfRequests = 2)
  }
}
