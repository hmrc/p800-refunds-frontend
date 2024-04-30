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

class ClaimYourRefundByBankTransferSpec extends ItSpec {
  override def beforeEach(): Unit = {
    super.beforeEach()
    addJourneyIdToSession(tdAll.journeyId)
  }

  "/cheque/claim-your-refund-by-bank-transfer" - {

    "render page" in {
      upsertJourneyToDatabase(tdAll.Cheque.AfterReferenceCheck.journeyReferenceDidntMatchNino)
      pages.claimYourRefundByBankTransferPage.open()
      pages.claimYourRefundByBankTransferPage.assertPageIsDisplayed()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.Cheque.AfterReferenceCheck.journeyReferenceDidntMatchNino
    }

    "select 'Yes' to sign in via PTA" in {
      upsertJourneyToDatabase(tdAll.Cheque.AfterReferenceCheck.journeyReferenceDidntMatchNino)
      pages.claimYourRefundByBankTransferPage.open()
      pages.claimYourRefundByBankTransferPage.assertPageIsDisplayed()
      pages.claimYourRefundByBankTransferPage.selectYes()
      pages.claimYourRefundByBankTransferPage.clickSubmit()
      pages.ptaSignInPage.assertPageIsDisplayed()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.Cheque.AfterReferenceCheck.journeyReferenceDidntMatchNino
    }

    "select 'No' to do Bank Transfer Logged Out" in {
      upsertJourneyToDatabase(tdAll.Cheque.AfterReferenceCheck.journeyReferenceDidntMatchNino)
      pages.claimYourRefundByBankTransferPage.open()
      pages.claimYourRefundByBankTransferPage.assertPageIsDisplayed()
      pages.claimYourRefundByBankTransferPage.selectNo()
      pages.claimYourRefundByBankTransferPage.clickSubmit()
      pages.weNeedYouToConfirmYourIdentityBankTransferPage.assertPageIsDisplayed()
      val expectedJourney = tdAll.Cheque.AfterReferenceCheck.journeyReferenceDidntMatchNino.copy(
        journeyType = Some(JourneyType.BankTransfer)
      )
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike expectedJourney
    }

    "empty selection shows error message" in {
      upsertJourneyToDatabase(tdAll.Cheque.AfterReferenceCheck.journeyReferenceDidntMatchNino)
      pages.claimYourRefundByBankTransferPage.open()
      pages.claimYourRefundByBankTransferPage.assertPageIsDisplayed()
      pages.claimYourRefundByBankTransferPage.clickSubmit()
      pages.claimYourRefundByBankTransferPage.assertPageDisplayedWithErrorMessage()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.Cheque.AfterReferenceCheck.journeyReferenceDidntMatchNino
    }
  }
}
