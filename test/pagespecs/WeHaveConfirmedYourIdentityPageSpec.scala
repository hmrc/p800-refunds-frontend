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

class WeHaveConfirmedYourIdentityPageSpec extends ItSpec {

  override def beforeEach(): Unit = {
    super.beforeEach()
    addJourneyIdToSession(tdAll.journeyId)
  }

  "page renders correctly" - {
    "bank transfer" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.journeyIdentityVerified)
      pages.weHaveConfirmedYourIdentityBankTransferPage.open()
      pages.weHaveConfirmedYourIdentityBankTransferPage.assertPageIsDisplayed()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeyIdentityVerified
    }
    "cheque" in {
      upsertJourneyToDatabase(tdAll.Cheque.journeyIdentityVerified)
      pages.weHaveConfirmedYourIdentityChequePage.open()
      pages.weHaveConfirmedYourIdentityChequePage.assertPageIsDisplayed()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.Cheque.journeyIdentityVerified
    }
  }

  "clicking submit navigates to What Is The Name Of Your Bank Account page" - {
    "bank transfer" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.journeyIdentityVerified)
      EcospendStub.stubEcospendAuth2xxSucceeded
      EcospendStub.stubEcospendGetBanks2xx
      pages.weHaveConfirmedYourIdentityBankTransferPage.open()
      pages.weHaveConfirmedYourIdentityBankTransferPage.assertPageIsDisplayed()
      pages.weHaveConfirmedYourIdentityBankTransferPage.clickSubmit()
      pages.whatIsTheNameOfYourBankAccountPage.assertPageIsDisplayed()
      EcospendStub.verifyEcospendAccessToken()
      EcospendStub.verifyEcospendGetBanks()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeyIdentityVerified
    }
    "cheque" in {
      upsertJourneyToDatabase(tdAll.Cheque.journeyIdentityVerified)
      pages.weHaveConfirmedYourIdentityChequePage.open()
      pages.weHaveConfirmedYourIdentityChequePage.assertPageIsDisplayed()
      pages.weHaveConfirmedYourIdentityChequePage.clickSubmit()
      pages.completeYourRefundRequestPage.assertPageIsDisplayed()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.Cheque.journeyIdentityVerified
    }
  }

  "clicking back button navigates to Check Your Answers page" - {
    "bank transfer" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.journeyIdentityVerified)
      pages.weHaveConfirmedYourIdentityBankTransferPage.open()
      pages.weHaveConfirmedYourIdentityBankTransferPage.assertPageIsDisplayed()
      pages.weHaveConfirmedYourIdentityBankTransferPage.clickBackButton()
      pages.checkYourAnswersBankTransferPage.assertPageIsDisplayedForBankTransfer(
        tdAll.p800Reference,
        tdAll.dateOfBirthFormatted,
        tdAll.nationalInsuranceNumber
      )
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeyIdentityVerified
    }

    "cheque" in {
      upsertJourneyToDatabase(tdAll.Cheque.journeyIdentityVerified)
      pages.weHaveConfirmedYourIdentityChequePage.open()
      pages.weHaveConfirmedYourIdentityChequePage.assertPageIsDisplayed()
      pages.weHaveConfirmedYourIdentityChequePage.clickBackButton()
      pages.checkYourAnswersChequePage.assertPageIsDisplayedForCheque(
        tdAll.p800Reference,
        tdAll.nationalInsuranceNumber
      )
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.Cheque.journeyIdentityVerified
    }
  }

}
