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

class YourIdentityIsConfirmedPageSpec extends ItSpec {

  override def beforeEach(): Unit = {
    super.beforeEach()
    addJourneyIdToSession(tdAll.journeyId)
  }

  "page renders correctly" - {
    "bank transfer" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.journeyAfterTracedIndividual)
      pages.yourIdentityIsConfirmedBankTransferPage.open()
      pages.yourIdentityIsConfirmedBankTransferPage.assertPageIsDisplayed()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeyAfterTracedIndividual
    }
    "cheque" in {
      upsertJourneyToDatabase(tdAll.Cheque.AfterReferenceCheck.journeyReferenceChecked)
      pages.yourIdentityIsConfirmedChequePage.open()
      pages.yourIdentityIsConfirmedChequePage.assertPageIsDisplayed()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.Cheque.AfterReferenceCheck.journeyReferenceChecked
    }
  }

  "clicking submit navigates to " - {
    "'What Is The Name Of Your Bank Account page' for bank transfer" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.journeyAfterTracedIndividual)
      EcospendStub.AuthStubs.stubEcospendAuth2xxSucceeded
      EcospendStub.BanksStubs.stubEcospendGetBanks2xx
      pages.yourIdentityIsConfirmedBankTransferPage.open()
      pages.yourIdentityIsConfirmedBankTransferPage.assertPageIsDisplayed()
      pages.yourIdentityIsConfirmedBankTransferPage.clickSubmit()
      pages.enterTheNameOfYourBankAccountPage.assertPageIsDisplayed()
      EcospendStub.AuthStubs.verifyEcospendAccessToken()
      EcospendStub.BanksStubs.verifyEcospendGetBanks()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeyAfterTracedIndividual
    }
    "'Is your address up to date' page for cheque" in {
      upsertJourneyToDatabase(tdAll.Cheque.AfterReferenceCheck.journeyReferenceChecked)
      pages.yourIdentityIsConfirmedChequePage.open()
      pages.yourIdentityIsConfirmedChequePage.assertPageIsDisplayed()
      pages.yourIdentityIsConfirmedChequePage.clickSubmit()
      pages.isYourAddressUpToDate.assertPageIsDisplayed()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.Cheque.AfterReferenceCheck.journeyReferenceChecked
    }
  }

}
