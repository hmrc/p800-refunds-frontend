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

class WeCannotConfirmYourIdentityPageSpec extends ItSpec {

  override def beforeEach(): Unit = {
    super.beforeEach()
    addJourneyIdToSession(tdAll.journeyId)
  }

  "render 'We cannot confirm your identity' page" - {
    "for /bank-transfer/cannot-confirm-your-identity-try-again" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.journeyReferenceDidntMatchNino)
      test(JourneyType.BankTransfer)
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeyReferenceDidntMatchNino
    }
    "for /cheque/cannot-confirm-your-identity-try-again" in {
      upsertJourneyToDatabase(tdAll.Cheque.journeyIdentityNotVerified)
      test(JourneyType.Cheque)
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.Cheque.journeyIdentityNotVerified
    }

      def test(journeyType: JourneyType): Unit = {
        val page = journeyType match {
          case JourneyType.Cheque       => pages.weCannotConfirmYourIdentityChequePage
          case JourneyType.BankTransfer => pages.weCannotConfirmYourIdentityBankTransferPage
        }
        page.open()
        page.assertPageIsDisplayed(journeyType)
      }
  }

  "clicking 'Back' sends user to check your answers" - {

    "bank transfer" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.journeyReferenceDidntMatchNino)
      test(JourneyType.BankTransfer)
      pages.checkYourAnswersBankTransferPage.assertPageIsDisplayedForBankTransfer(
        tdAll.p800Reference,
        tdAll.dateOfBirthFormatted,
        tdAll.nationalInsuranceNumber
      )
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeyReferenceDidntMatchNino
    }
    "cheque" in {
      upsertJourneyToDatabase(tdAll.Cheque.journeyIdentityNotVerified)
      test(JourneyType.Cheque)
      pages.checkYourAnswersChequePage.assertPageIsDisplayedForCheque(
        tdAll.p800Reference,
        tdAll.nationalInsuranceNumber
      )
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.Cheque.journeyIdentityNotVerified
    }

      def test(journeyType: JourneyType): Unit = {
        val page = journeyType match {
          case JourneyType.Cheque       => pages.weCannotConfirmYourIdentityChequePage
          case JourneyType.BankTransfer => pages.weCannotConfirmYourIdentityBankTransferPage
        }
        page.open()
        page.assertPageIsDisplayed(journeyType)
        page.clickBackButton()
      }

  }

  "clicking 'Try again' sends user to 'Check your answers page'" - {
    "bank transfer transfer" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.journeyReferenceDidntMatchNino)
      test(JourneyType.BankTransfer)
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeyReferenceDidntMatchNino
    }
    "cheque" in {
      upsertJourneyToDatabase(tdAll.Cheque.journeyIdentityNotVerified)
      test(JourneyType.Cheque)
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.Cheque.journeyIdentityNotVerified
    }

      def test(journeyType: JourneyType): Unit = {
        val weCannotConfirmYourIdentityPage = journeyType match {
          case JourneyType.Cheque       => pages.weCannotConfirmYourIdentityChequePage
          case JourneyType.BankTransfer => pages.weCannotConfirmYourIdentityBankTransferPage
        }
        weCannotConfirmYourIdentityPage.open()
        weCannotConfirmYourIdentityPage.assertPageIsDisplayed(journeyType)
        weCannotConfirmYourIdentityPage.clickTryAgain()
        journeyType match {
          case JourneyType.Cheque =>
            pages.checkYourAnswersChequePage.assertPageIsDisplayedForCheque(
              p800Reference           = tdAll.p800Reference,
              nationalInsuranceNumber = tdAll.nationalInsuranceNumber
            )
          case JourneyType.BankTransfer =>
            pages.checkYourAnswersBankTransferPage.assertPageIsDisplayedForBankTransfer(
              p800Reference           = tdAll.p800Reference,
              dateOfBirth             = tdAll.dateOfBirthFormatted,
              nationalInsuranceNumber = tdAll.nationalInsuranceNumber
            )
        }
      }
  }

  "clicking 'Choose another method' sends user to 'Choose another way to receive your refund page'" - {
    "bank transfer" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.journeyReferenceDidntMatchNino)
      test(JourneyType.BankTransfer)
      pages.chooseAnotherWayToReceiveYourRefundBankTransferPage.assertPageIsDisplayedPtaOrCheque()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeyReferenceDidntMatchNino
    }
    "cheque" in {
      upsertJourneyToDatabase(tdAll.Cheque.journeyIdentityNotVerified)
      test(JourneyType.Cheque)
      pages.chooseAnotherWayToReceiveYourRefundChequePage.assertPageIsDisplayedPtaOrBankTransfer()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.Cheque.journeyIdentityNotVerified
    }

      def test(journeyType: JourneyType): Unit = {
        val page = journeyType match {
          case JourneyType.Cheque       => pages.weCannotConfirmYourIdentityChequePage
          case JourneyType.BankTransfer => pages.weCannotConfirmYourIdentityBankTransferPage
        }
        page.open()
        page.assertPageIsDisplayed(journeyType)
        page.clickChooseAnotherWay()
      }
  }
}
