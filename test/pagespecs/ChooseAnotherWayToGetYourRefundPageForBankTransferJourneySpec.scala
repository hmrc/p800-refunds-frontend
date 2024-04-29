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

class ChooseAnotherWayToGetYourRefundPageForBankTransferJourneySpec extends ItSpec {

  override def beforeEach(): Unit = {
    super.beforeEach()
    addJourneyIdToSession(tdAll.journeyId)
  }

  "bank transfer from give-your-consent page" - {
    "render page" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.journeySelectedBank)
      pages.chooseAnotherWayToGetYourRefundPage.open()
      pages.chooseAnotherWayToGetYourRefundPage.assertPageIsDisplayed()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeySelectedBank
    }
    "select cheque" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.journeySelectedBank)
      pages.chooseAnotherWayToGetYourRefundPage.open()
      pages.chooseAnotherWayToGetYourRefundPage.assertPageIsDisplayed()
      pages.chooseAnotherWayToGetYourRefundPage.PtaOrCheque.selectCheque()
      pages.chooseAnotherWayToGetYourRefundPage.clickSubmit()
      pages.isYourAddressUpToDate.assertPageIsDisplayed()
      val expectedJourney = tdAll.Cheque.AfterReferenceCheck.journeyReferenceChecked.copy(
        dateOfBirth             = tdAll.BankTransfer.journeySelectedBank.dateOfBirth, //Date of Birth is also copied
        traceIndividualResponse = tdAll.BankTransfer.journeySelectedBank.traceIndividualResponse, //Date of Birth is also copied
        bankDescription         = tdAll.BankTransfer.journeySelectedBank.bankDescription, //also bankDescription is copied
      )
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike expectedJourney
    }
    "select bank transfer via PTA" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.journeySelectedBank)
      pages.chooseAnotherWayToGetYourRefundPage.open()
      pages.chooseAnotherWayToGetYourRefundPage.assertPageIsDisplayed()
      pages.chooseAnotherWayToGetYourRefundPage.PtaOrCheque.selectBankTransferViaPta()
      pages.chooseAnotherWayToGetYourRefundPage.clickSubmit()
      pages.ptaSignInPage.assertPageIsDisplayed()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeySelectedBank
    }
    "empty selection" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.journeySelectedBank)
      pages.chooseAnotherWayToGetYourRefundPage.open()
      pages.chooseAnotherWayToGetYourRefundPage.assertPageIsDisplayed()
      pages.chooseAnotherWayToGetYourRefundPage.clickSubmit()
      pages.chooseAnotherWayToGetYourRefundPage.assertPageIsDisplayed(
        ContentExpectation(
          PageUtil.Xpath.errorSummary,
          """There is a problem
            |Select if you want to receive a bank transfer or a cheque
            |""".stripMargin
        )
      )
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeySelectedBank
    }
  }

  "bank transfer from your-refund-was-not-submitted page" - {
    "render page" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.journeyClaimOverpaymentFailedButIsChoosingAnotherWay)
      pages.chooseAnotherWayToGetYourRefundPage.open()
      pages.chooseAnotherWayToGetYourRefundPage.assertPageIsDisplayed()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeyClaimOverpaymentFailedButIsChoosingAnotherWay
    }
    "select cheque" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.journeyClaimOverpaymentFailedButIsChoosingAnotherWay)
      pages.chooseAnotherWayToGetYourRefundPage.open()
      pages.chooseAnotherWayToGetYourRefundPage.assertPageIsDisplayed()
      pages.chooseAnotherWayToGetYourRefundPage.PtaOrCheque.selectCheque()
      pages.chooseAnotherWayToGetYourRefundPage.clickSubmit()
      pages.isYourAddressUpToDate.assertPageIsDisplayed()

      val expectedJourney = tdAll.BankTransfer.journeyClaimOverpaymentFailedButIsChoosingAnotherWay.copy(
        journeyType = Some(JourneyType.Cheque)
      )
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike expectedJourney
    }
    "select bank transfer via PTA" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.journeyClaimOverpaymentFailedButIsChoosingAnotherWay)
      pages.chooseAnotherWayToGetYourRefundPage.open()
      pages.chooseAnotherWayToGetYourRefundPage.assertPageIsDisplayed()
      pages.chooseAnotherWayToGetYourRefundPage.PtaOrCheque.selectBankTransferViaPta()
      pages.chooseAnotherWayToGetYourRefundPage.clickSubmit()
      pages.ptaSignInPage.assertPageIsDisplayed()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeyClaimOverpaymentFailedButIsChoosingAnotherWay
    }
    "empty selection" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.journeyClaimOverpaymentFailedButIsChoosingAnotherWay)
      pages.chooseAnotherWayToGetYourRefundPage.open()
      pages.chooseAnotherWayToGetYourRefundPage.assertPageIsDisplayed()
      pages.chooseAnotherWayToGetYourRefundPage.clickSubmit()
      pages.chooseAnotherWayToGetYourRefundPage.assertPageIsDisplayed(
        ContentExpectation(
          PageUtil.Xpath.errorSummary,
          """There is a problem
            |Select if you want to receive a bank transfer or a cheque
            |""".stripMargin
        )
      )
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeyClaimOverpaymentFailedButIsChoosingAnotherWay
    }
  }

  "bank transfer from we-cannot-confirm-confirm-your-identity page" - {
    "render page" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.AfterReferenceCheck.journeyReferenceDidntMatchNino)
      pages.chooseAnotherWayToGetYourRefundPage.open()
      pages.chooseAnotherWayToGetYourRefundPage.assertPageIsDisplayed()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.AfterReferenceCheck.journeyReferenceDidntMatchNino
    }
    "select cheque" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.AfterReferenceCheck.journeyReferenceDidntMatchNino)
      pages.chooseAnotherWayToGetYourRefundPage.open()
      pages.chooseAnotherWayToGetYourRefundPage.assertPageIsDisplayed()
      pages.chooseAnotherWayToGetYourRefundPage.PtaOrCheque.selectCheque()
      pages.chooseAnotherWayToGetYourRefundPage.clickSubmit()
      pages.weNeedYouToConfirmYourIdentityChequePage.assertPageIsDisplayed(JourneyType.Cheque)

      val expectedJourney = tdAll.BankTransfer.AfterReferenceCheck.journeyReferenceDidntMatchNino.copy(
        journeyType = Some(JourneyType.Cheque)
      )
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike expectedJourney
    }
    "select bank transfer via PTA" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.AfterReferenceCheck.journeyReferenceDidntMatchNino)
      pages.chooseAnotherWayToGetYourRefundPage.open()
      pages.chooseAnotherWayToGetYourRefundPage.assertPageIsDisplayed()
      pages.chooseAnotherWayToGetYourRefundPage.PtaOrCheque.selectBankTransferViaPta()
      pages.chooseAnotherWayToGetYourRefundPage.clickSubmit()
      pages.ptaSignInPage.assertPageIsDisplayed()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.AfterReferenceCheck.journeyReferenceDidntMatchNino
    }
    "empty selection" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.AfterReferenceCheck.journeyReferenceDidntMatchNino)
      pages.chooseAnotherWayToGetYourRefundPage.open()
      pages.chooseAnotherWayToGetYourRefundPage.assertPageIsDisplayed()
      pages.chooseAnotherWayToGetYourRefundPage.clickSubmit()
      pages.chooseAnotherWayToGetYourRefundPage.assertPageIsDisplayed(
        ContentExpectation(
          PageUtil.Xpath.errorSummary,
          """There is a problem
            |Select if you want to receive a bank transfer or a cheque
            |""".stripMargin
        )
      )
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.AfterReferenceCheck.journeyReferenceDidntMatchNino
    }
  }

}
