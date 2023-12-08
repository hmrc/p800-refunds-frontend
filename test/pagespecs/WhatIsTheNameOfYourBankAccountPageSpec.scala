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
    upsertJourneyToDatabase(tdAll.journeyIdentityVerified)

    EcospendStub.stubEcospendAuth2xxSucceeded
    EcospendStub.stubEcospendGetBanks2xx

    ()
  }

  "/what-is-the-name-of-your-bank-account renders the what is the name of your bank account page" in {
    pages.whatIsTheNameOfYourBankAccountPage.open()
    pages.whatIsTheNameOfYourBankAccountPage.assertPageIsDisplayed()

    EcospendStub.verifyEcospendAccessToken()
    EcospendStub.verifyEcospendGetBanks()
  }

  "Clicking 'Continue' after selecing a back redirects to 'Give your consent' page" in {
    pages.whatIsTheNameOfYourBankAccountPage.open()
    pages.whatIsTheNameOfYourBankAccountPage.assertPageIsDisplayed()
    pages.whatIsTheNameOfYourBankAccountPage.selectBankAccount(tdAll.bankId)
    pages.whatIsTheNameOfYourBankAccountPage.clickSubmit()
    pages.giveYourConsentPage.assertPageIsDisplayed()

    EcospendStub.verifyEcospendAccessToken()
    EcospendStub.verifyEcospendGetBanks()
  }

  "Clicking 'My account is not listed' redirects to 'Choose another option' page" in {
    pages.whatIsTheNameOfYourBankAccountPage.open()
    pages.whatIsTheNameOfYourBankAccountPage.assertPageIsDisplayed()
    pages.whatIsTheNameOfYourBankAccountPage.clickMyAccountIsNotListed()
    pages.chooseAnotherWayToReceiveYourRefundPage.assertPageIsDisplayed()

    EcospendStub.verifyEcospendAccessToken()
    EcospendStub.verifyEcospendGetBanks()
  }

  "Clicking 'Continue' without selecting a bank shows error" in {
    pages.whatIsTheNameOfYourBankAccountPage.open()
    pages.whatIsTheNameOfYourBankAccountPage.assertPageIsDisplayed()
    pages.whatIsTheNameOfYourBankAccountPage.clickSubmit()
    pages.whatIsTheNameOfYourBankAccountPage.assertPageShowsError() // should fail?

    EcospendStub.verifyEcospendAccessToken(numberOfRequests = 2)
    EcospendStub.verifyEcospendGetBanks(numberOfRequests = 2)
  }

  "Clicking 'Back' redirects to /we-have-confirmed-your-identity" in {
    pages.whatIsTheNameOfYourBankAccountPage.open()
    pages.whatIsTheNameOfYourBankAccountPage.assertPageIsDisplayed()
    pages.whatIsTheNameOfYourBankAccountPage.clickBackButton()
    pages.weHaveConfirmedYourIdentityPage.assertPageIsDisplayed()

    EcospendStub.verifyEcospendAccessToken()
    EcospendStub.verifyEcospendGetBanks()
  }
}
