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

class WeHaveConfirmedYourIdentityPageSpec extends ItSpec {

  override def beforeEach(): Unit = {
    super.beforeEach()
    addJourneyIdToSession(tdAll.journeyId)
  }

  "page renders correctly" - {
    "bank transfer" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.AfterReferenceCheck.journeyReferenceChecked)
      pages.yourIdentityIsConfirmedBankTransferPage.open()
      pages.yourIdentityIsConfirmedBankTransferPage.assertPageIsDisplayed(JourneyType.BankTransfer)
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.AfterReferenceCheck.journeyReferenceChecked
    }
    "cheque" in {
      upsertJourneyToDatabase(tdAll.Cheque.AfterReferenceCheck.journeyReferenceChecked)
      pages.yourIdentityIsConfirmedChequePage.open()
      pages.yourIdentityIsConfirmedChequePage.assertPageIsDisplayed(JourneyType.Cheque)
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.Cheque.AfterReferenceCheck.journeyReferenceChecked
    }
  }

  "clicking submit navigates to What Is The Name Of Your Bank Account page" - {
    "bank transfer" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.AfterReferenceCheck.journeyReferenceChecked)
      EcospendStub.AuthStubs.stubEcospendAuth2xxSucceeded
      EcospendStub.BanksStubs.stubEcospendGetBanks2xx
      pages.yourIdentityIsConfirmedBankTransferPage.open()
      pages.yourIdentityIsConfirmedBankTransferPage.assertPageIsDisplayed(JourneyType.BankTransfer)
      pages.yourIdentityIsConfirmedBankTransferPage.clickSubmit()
      pages.enterTheNameOfYourBankAccountPage.assertPageIsDisplayed()
      EcospendStub.AuthStubs.verifyEcospendAccessToken()
      EcospendStub.BanksStubs.verifyEcospendGetBanks()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.AfterReferenceCheck.journeyReferenceChecked
    }
    "cheque" in {
      upsertJourneyToDatabase(tdAll.Cheque.AfterReferenceCheck.journeyReferenceChecked)
      pages.yourIdentityIsConfirmedChequePage.open()
      pages.yourIdentityIsConfirmedChequePage.assertPageIsDisplayed(JourneyType.Cheque)
      pages.yourIdentityIsConfirmedChequePage.clickSubmit()
      pages.completeYourRefundRequestPage.assertPageIsDisplayed()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.Cheque.AfterReferenceCheck.journeyReferenceChecked
    }
  }

  "clicking back button navigates to Check Your Answers page" - {
    "bank transfer" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.AfterReferenceCheck.journeyReferenceChecked)
      pages.yourIdentityIsConfirmedBankTransferPage.open()
      pages.yourIdentityIsConfirmedBankTransferPage.assertPageIsDisplayed(JourneyType.BankTransfer)
      pages.yourIdentityIsConfirmedBankTransferPage.clickBackButton()
      pages.checkYourAnswersBankTransferPage.assertPageIsDisplayedForBankTransfer(
        tdAll.p800Reference,
        tdAll.dateOfBirthFormatted,
        tdAll.nino
      )
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.AfterReferenceCheck.journeyReferenceChecked
    }

    "cheque" in {
      upsertJourneyToDatabase(tdAll.Cheque.AfterReferenceCheck.journeyReferenceChecked)
      pages.yourIdentityIsConfirmedChequePage.open()
      pages.yourIdentityIsConfirmedChequePage.assertPageIsDisplayed(JourneyType.Cheque)
      pages.yourIdentityIsConfirmedChequePage.clickBackButton()
      pages.checkYourAnswersChequePage.assertPageIsDisplayedForCheque(
        tdAll.p800Reference,
        tdAll.nino
      )
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.Cheque.AfterReferenceCheck.journeyReferenceChecked
    }
  }

}
