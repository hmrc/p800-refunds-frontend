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

class NoMoreAttemptsLeftToConfirmYourIdentityPageSpec extends ItSpec {

  override def beforeEach(): Unit = {
    super.beforeEach()
    addJourneyIdToSession(tdAll.journeyId)
  }

  "/no-more-attempts-left-to-confirm-your-identity renders correct content" - {
    "bank transfer" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.journeyLockedOutFromFailedAttempts)
      test(JourneyType.BankTransfer)
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeyLockedOutFromFailedAttempts
    }
    "cheque" in {
      upsertJourneyToDatabase(tdAll.Cheque.journeyLockedOutFromFailedAttempts)
      test(JourneyType.Cheque)
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.Cheque.journeyLockedOutFromFailedAttempts
    }

      def test(journeyType: JourneyType): Unit = {
        val page = journeyType match {
          case JourneyType.Cheque       => pages.noMoreAttemptsLeftToConfirmYourIdentityChequePage
          case JourneyType.BankTransfer => pages.noMoreAttemptsLeftToConfirmYourIdentityBankTransferPage
        }
        page.open()
        page.assertPageIsDisplayed(journeyType)
      }
  }

  "clicking 'sign in to you HMRC online account' sends user to Pta Sign In page" - {
    "bank transfer" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.journeyLockedOutFromFailedAttempts)
      test(JourneyType.BankTransfer)
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeyLockedOutFromFailedAttempts
    }
    "cheque" in {
      upsertJourneyToDatabase(tdAll.Cheque.journeyLockedOutFromFailedAttempts)
      test(JourneyType.Cheque)
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.Cheque.journeyLockedOutFromFailedAttempts
    }

      def test(journeyType: JourneyType): Unit = {
        val page = journeyType match {
          case JourneyType.Cheque       => pages.noMoreAttemptsLeftToConfirmYourIdentityChequePage
          case JourneyType.BankTransfer => pages.noMoreAttemptsLeftToConfirmYourIdentityBankTransferPage
        }
        page.open()
        page.assertPageIsDisplayed(journeyType)
        page.clickSignInToYourHmrcAccount()
        pages.ptaSignInPage.assertPageIsDisplayed()
      }
  }
}
