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

class WeCannotConfirmYourIdentityPageSpec extends ItSpec {

  override def beforeEach(): Unit = {
    super.beforeEach()
    addJourneyIdToSession(tdAll.journeyId)
  }

  "/we-cannot-confirm-your-identity renders your 'We cannot confirm your identity' page" - {
    "bank transfer" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.journeyIdentityNotVerified)
      test()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeyIdentityNotVerified
    }
    "cheque" in {
      upsertJourneyToDatabase(tdAll.Cheque.journeyIdentityNotVerified)
      test()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.Cheque.journeyIdentityNotVerified
    }

      def test(): Unit = {
        pages.weCannotConfirmYourIdentityPage.open()
        pages.weCannotConfirmYourIdentityPage.assertPageIsDisplayed()
      }
  }

  "clicking 'Back' sends user to check your answers" - {

    "bank transfer" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.journeyIdentityNotVerified)
      test()
      pages.checkYourAnswersPage.assertPageIsDisplayedForBankTransfer(
        tdAll.p800Reference,
        tdAll.dateOfBirthFormatted,
        tdAll.nationalInsuranceNumber
      )
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeyIdentityNotVerified
    }
    "cheque" in {
      upsertJourneyToDatabase(tdAll.Cheque.journeyIdentityNotVerified)
      test()
      pages.checkYourAnswersPage.assertPageIsDisplayedForCheque(
        tdAll.p800Reference,
        tdAll.nationalInsuranceNumber
      )
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.Cheque.journeyIdentityNotVerified
    }

      def test(): Unit = {
        pages.weCannotConfirmYourIdentityPage.open()
        pages.weCannotConfirmYourIdentityPage.assertPageIsDisplayed()
        pages.weCannotConfirmYourIdentityPage.clickBackButton()
      }

  }

  "clicking 'Try again' sends user to 'We need you to confirm your identity page'" - {
    "bank transfer" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.journeyIdentityNotVerified)
      test()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeyIdentityNotVerified
    }
    "cheque" in {
      upsertJourneyToDatabase(tdAll.Cheque.journeyIdentityNotVerified)
      test()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.Cheque.journeyIdentityNotVerified
    }

      def test(): Unit = {
        pages.weCannotConfirmYourIdentityPage.open()
        pages.weCannotConfirmYourIdentityPage.assertPageIsDisplayed()
        pages.weCannotConfirmYourIdentityPage.clickTryAgain()
        pages.weNeedYouToConfirmYourIdentityPage.assertPageIsDisplayed()
      }
  }

  "clicking 'Choose another method' sends user to 'Choose another way to receive your refund page'" - {
    "bank transfer" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.journeyIdentityNotVerified)
      test()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeyIdentityNotVerified
    }
    "cheque" in {
      upsertJourneyToDatabase(tdAll.Cheque.journeyIdentityNotVerified)
      test()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.Cheque.journeyIdentityNotVerified
    }

      def test(): Unit = {
        pages.weCannotConfirmYourIdentityPage.open()
        pages.weCannotConfirmYourIdentityPage.assertPageIsDisplayed()
        pages.weCannotConfirmYourIdentityPage.clickChooseAnotherWay()
        pages.chooseAnotherWayToReceiveYourRefundPage.assertPageIsDisplayed()
      }
  }
}
