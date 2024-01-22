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

class NoMoreAttemptsLeftToConfirmYourIdentityPageSpec extends ItSpec {

  override def beforeEach(): Unit = {
    super.beforeEach()
    addJourneyIdToSession(tdAll.journeyId)
  }

  "/no-more-attempts-left-to-confirm-your-identity renders correct content" - {
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
        pages.noMoreAttemptsLeftToConfirmYourIdentityPage.open()
        pages.noMoreAttemptsLeftToConfirmYourIdentityPage.assertPageIsDisplayed()
      }
  }

  "clicking ' sign in to you HMRC online account ' sends user to Pta Sign In page" - {
    "bank transfer" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.journeyIdentityNotVerified)
      test()
      pages.noMoreAttemptsLeftToConfirmYourIdentityPage.clickSignInToYourHmrcAccount()
      pages.ptaSignInPage.assertPageIsDisplayed()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeyIdentityNotVerified
    }
    "cheque" in {
      upsertJourneyToDatabase(tdAll.Cheque.journeyIdentityNotVerified)
      test()
      pages.noMoreAttemptsLeftToConfirmYourIdentityPage.clickSignInToYourHmrcAccount()
      pages.ptaSignInPage.assertPageIsDisplayed()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.Cheque.journeyIdentityNotVerified
    }

      def test(): Unit = {
        pages.noMoreAttemptsLeftToConfirmYourIdentityPage.open()
        pages.noMoreAttemptsLeftToConfirmYourIdentityPage.assertPageIsDisplayed()
      }
  }
}
