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
import pagespecs.pagesupport.{ContentExpectation, PageUtil}
import testsupport.ItSpec

class ChooseAnotherWayToReceiveYourRefundPageForChequeJourneySpec extends ItSpec {

  override def beforeEach(): Unit = {
    super.beforeEach()
    addJourneyIdToSession(tdAll.journeyId)
  }

  "Cheque journey from we-cannot-confirm-your-identity page" - {
    "render page" in {
      upsertJourneyToDatabase(tdAll.Cheque.journeyIdentityNotVerified)
      pages.chooseAnotherWayToReceiveYourRefundPage.open()
      pages.chooseAnotherWayToReceiveYourRefundPage.assertPageIsDisplayedPtaOrBankTransfer()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.Cheque.journeyIdentityNotVerified
    }

    "select bank transfer via PTA" in {
      upsertJourneyToDatabase(tdAll.Cheque.journeyIdentityNotVerified)
      pages.chooseAnotherWayToReceiveYourRefundPage.open()
      pages.chooseAnotherWayToReceiveYourRefundPage.assertPageIsDisplayedPtaOrBankTransfer()
      pages.chooseAnotherWayToReceiveYourRefundPage.PtaOrBankTransfer.selectBankTransferViaPta()
      pages.chooseAnotherWayToReceiveYourRefundPage.clickSubmit()
      pages.ptaSignInPage.assertPageIsDisplayed()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.Cheque.journeyIdentityNotVerified
    }

    "select Bank Transfer Logged Out" in {
      upsertJourneyToDatabase(tdAll.Cheque.journeyIdentityNotVerified)
      pages.chooseAnotherWayToReceiveYourRefundPage.open()
      pages.chooseAnotherWayToReceiveYourRefundPage.assertPageIsDisplayedPtaOrBankTransfer()
      pages.chooseAnotherWayToReceiveYourRefundPage.PtaOrBankTransfer.selectBankTransferLoggedOut()
      pages.chooseAnotherWayToReceiveYourRefundPage.clickSubmit()
      pages.weNeedYouToConfirmYourIdentityBankTransferPage.assertPageIsDisplayed(JourneyType.BankTransfer)
      val expectedJourney = tdAll.Cheque.journeyIdentityNotVerified.copy(
        journeyType = Some(JourneyType.BankTransfer)
      )
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike expectedJourney
    }

    "empty selection" in {
      upsertJourneyToDatabase(tdAll.Cheque.journeyIdentityNotVerified)
      pages.chooseAnotherWayToReceiveYourRefundPage.open()
      pages.chooseAnotherWayToReceiveYourRefundPage.assertPageIsDisplayedPtaOrBankTransfer()
      pages.chooseAnotherWayToReceiveYourRefundPage.clickSubmit()
      pages.chooseAnotherWayToReceiveYourRefundPtaOrBankTransferPage.assertPageIsDisplayedPtaOrBankTransfer(
        ContentExpectation(
          PageUtil.Xpath.errorSummary,
          """There is a problem
            |Select the way you want to receive your refund
            |""".stripMargin
        )
      )
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.Cheque.journeyIdentityNotVerified
    }
  }

}
