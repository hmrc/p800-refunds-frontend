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

class ChooseAnotherWayToReceiveYourRefundPageForBankTransferJourneySpec extends ItSpec {

  override def beforeEach(): Unit = {
    super.beforeEach()
    addJourneyIdToSession(tdAll.journeyId)
  }

  "bank transfer from give-your-consent page" - {
    "render page" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.journeySelectedBank)
      pages.chooseAnotherWayToReceiveYourRefundPage.open()
      pages.chooseAnotherWayToReceiveYourRefundPage.assertPageIsDisplayedPtaOrCheque()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeySelectedBank
    }
    "select cheque" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.journeySelectedBank)
      pages.chooseAnotherWayToReceiveYourRefundPage.open()
      pages.chooseAnotherWayToReceiveYourRefundPage.assertPageIsDisplayedPtaOrCheque()
      pages.chooseAnotherWayToReceiveYourRefundPage.PtaOrCheque.selectCheque()
      pages.chooseAnotherWayToReceiveYourRefundPage.clickSubmit()
      pages.completeYourRefundRequestPage.assertPageIsDisplayed()
      val expectedJourney = tdAll.Cheque.journeyIdentityVerified.copy(
        dateOfBirth     = tdAll.BankTransfer.journeySelectedBank.dateOfBirth, //Date of Birth is also copied
        bankDescription = tdAll.BankTransfer.journeySelectedBank.bankDescription, //also bankDescription is copied
      )
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike expectedJourney
    }
    "select bank transfer via PTA" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.journeySelectedBank)
      pages.chooseAnotherWayToReceiveYourRefundPage.open()
      pages.chooseAnotherWayToReceiveYourRefundPage.assertPageIsDisplayedPtaOrCheque()
      pages.chooseAnotherWayToReceiveYourRefundPage.PtaOrCheque.selectBankTransferViaPta()
      pages.chooseAnotherWayToReceiveYourRefundPage.clickSubmit()
      pages.ptaSignInPage.assertPageIsDisplayed()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeySelectedBank
    }
    "empty selection" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.journeySelectedBank)
      pages.chooseAnotherWayToReceiveYourRefundPage.open()
      pages.chooseAnotherWayToReceiveYourRefundPage.assertPageIsDisplayedPtaOrCheque()
      pages.chooseAnotherWayToReceiveYourRefundPage.clickSubmit()
      pages.chooseAnotherWayToReceiveYourRefundPtaOrChequePage.assertPageIsDisplayedPtaOrCheque(
        ContentExpectation(
          PageUtil.Xpath.errorSummary,
          """There is a problem
            |Select the way you want to receive your refund
            |""".stripMargin
        )
      )
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeySelectedBank
    }
  }

  "bank transfer from your-refund-was-not-submitted page" - {
    "render page" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.journeyClaimOverpaymentFailedButIsChoosingAnotherWay)
      pages.chooseAnotherWayToReceiveYourRefundPage.open()
      pages.chooseAnotherWayToReceiveYourRefundPage.assertPageIsDisplayedPtaOrCheque()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeyClaimOverpaymentFailedButIsChoosingAnotherWay
    }
    "select cheque" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.journeyClaimOverpaymentFailedButIsChoosingAnotherWay)
      pages.chooseAnotherWayToReceiveYourRefundPage.open()
      pages.chooseAnotherWayToReceiveYourRefundPage.assertPageIsDisplayedPtaOrCheque()
      pages.chooseAnotherWayToReceiveYourRefundPage.PtaOrCheque.selectCheque()
      pages.chooseAnotherWayToReceiveYourRefundPage.clickSubmit()
      pages.completeYourRefundRequestPage.assertPageIsDisplayed()

      val expectedJourney = tdAll.BankTransfer.journeyClaimOverpaymentFailedButIsChoosingAnotherWay.copy(
        journeyType = Some(JourneyType.Cheque)
      )
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike expectedJourney
    }
    "select bank transfer via PTA" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.journeyClaimOverpaymentFailedButIsChoosingAnotherWay)
      pages.chooseAnotherWayToReceiveYourRefundPage.open()
      pages.chooseAnotherWayToReceiveYourRefundPage.assertPageIsDisplayedPtaOrCheque()
      pages.chooseAnotherWayToReceiveYourRefundPage.PtaOrCheque.selectBankTransferViaPta()
      pages.chooseAnotherWayToReceiveYourRefundPage.clickSubmit()
      pages.ptaSignInPage.assertPageIsDisplayed()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeyClaimOverpaymentFailedButIsChoosingAnotherWay
    }
    "empty selection" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.journeyClaimOverpaymentFailedButIsChoosingAnotherWay)
      pages.chooseAnotherWayToReceiveYourRefundPage.open()
      pages.chooseAnotherWayToReceiveYourRefundPage.assertPageIsDisplayedPtaOrCheque()
      pages.chooseAnotherWayToReceiveYourRefundPage.clickSubmit()
      pages.chooseAnotherWayToReceiveYourRefundPtaOrChequePage.assertPageIsDisplayedPtaOrCheque(
        ContentExpectation(
          PageUtil.Xpath.errorSummary,
          """There is a problem
            |Select the way you want to receive your refund
            |""".stripMargin
        )
      )
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeyClaimOverpaymentFailedButIsChoosingAnotherWay
    }
  }

}
