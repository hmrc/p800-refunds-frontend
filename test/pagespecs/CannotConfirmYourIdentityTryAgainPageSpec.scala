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

class CannotConfirmYourIdentityTryAgainPageSpec extends ItSpec {

  override def beforeEach(): Unit = {
    super.beforeEach()
    addJourneyIdToSession(tdAll.journeyId)
  }

  "render 'We cannot confirm your identity' page" - {
    "for /bank-transfer/cannot-confirm-your-identity-try-again" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.AfterReferenceCheck.journeyReferenceDidntMatchNino)
      test(JourneyType.BankTransfer)
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.AfterReferenceCheck.journeyReferenceDidntMatchNino
    }
    "for /cheque/cannot-confirm-your-identity-try-again" in {
      upsertJourneyToDatabase(tdAll.Cheque.AfterReferenceCheck.journeyReferenceDidntMatchNino)
      test(JourneyType.Cheque)
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.Cheque.AfterReferenceCheck.journeyReferenceDidntMatchNino
    }

      def test(journeyType: JourneyType): Unit = {
        val page = journeyType match {
          case JourneyType.Cheque       => pages.cannotConfirmYourIdentityTryAgainChequePage
          case JourneyType.BankTransfer => pages.cannotConfirmYourIdentityTryAgainBankTransferPage
        }
        page.open()
        page.assertPageIsDisplayed(journeyType)
      }
  }

  "clicking 'Try again' sends user to 'Check your answers page'" - {
    "bank transfer transfer" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.AfterReferenceCheck.journeyReferenceDidntMatchNino)
      test(JourneyType.BankTransfer)
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.AfterReferenceCheck.journeyReferenceDidntMatchNino
    }
    "cheque" in {
      upsertJourneyToDatabase(tdAll.Cheque.AfterReferenceCheck.journeyReferenceDidntMatchNino)
      test(JourneyType.Cheque)
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.Cheque.AfterReferenceCheck.journeyReferenceDidntMatchNino
    }

      def test(journeyType: JourneyType): Unit = {
        val weCannotConfirmYourIdentityPage = journeyType match {
          case JourneyType.Cheque       => pages.cannotConfirmYourIdentityTryAgainChequePage
          case JourneyType.BankTransfer => pages.cannotConfirmYourIdentityTryAgainBankTransferPage
        }
        weCannotConfirmYourIdentityPage.open()
        weCannotConfirmYourIdentityPage.assertPageIsDisplayed(journeyType)
        weCannotConfirmYourIdentityPage.clickTryAgain()
        journeyType match {
          case JourneyType.Cheque =>
            pages.checkYourAnswersChequePage.assertPageIsDisplayedForCheque(
              p800Reference           = tdAll.userEnteredP800Reference,
              nationalInsuranceNumber = tdAll.nino
            )
          case JourneyType.BankTransfer =>
            pages.checkYourAnswersBankTransferPage.assertPageIsDisplayedForBankTransfer(
              p800Reference           = tdAll.userEnteredP800Reference,
              dateOfBirth             = tdAll.dateOfBirthFormatted,
              nationalInsuranceNumber = tdAll.nino
            )
        }
      }
  }

  "clicking 'Choose another method' sends user to" - {
    "'Choose another way to get your refund' for bank transfer" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.AfterReferenceCheck.journeyReferenceDidntMatchNino)
      test(JourneyType.BankTransfer)
      pages.chooseAnotherWayToReceiveYourRefundPage.assertPageIsDisplayed()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.AfterReferenceCheck.journeyReferenceDidntMatchNino
    }
    "'Claim your refund by bank transfer' for cheque" in {
      upsertJourneyToDatabase(tdAll.Cheque.AfterReferenceCheck.journeyReferenceDidntMatchNino)
      test(JourneyType.Cheque)
      pages.claimYourRefundByBankTransferPage.assertPageIsDisplayed()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.Cheque.AfterReferenceCheck.journeyReferenceDidntMatchNino
    }

      def test(journeyType: JourneyType): Unit = {
        val page = journeyType match {
          case JourneyType.Cheque       => pages.cannotConfirmYourIdentityTryAgainChequePage
          case JourneyType.BankTransfer => pages.cannotConfirmYourIdentityTryAgainBankTransferPage
        }
        page.open()
        page.assertPageIsDisplayed(journeyType)
        page.clickChooseAnotherWay()
      }
  }
}
