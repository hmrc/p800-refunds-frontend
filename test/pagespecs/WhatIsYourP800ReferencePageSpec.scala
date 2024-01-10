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

class WhatIsYourP800ReferencePageSpec extends ItSpec {

  override def beforeEach(): Unit = {
    super.beforeEach()
    addJourneyIdToSession(tdAll.journeyId)
  }

  "Entering valid p800 reference and clicking Continue redirects to WhatIsYourNationalInsuranceNumberPage" - {
    "bank transfer" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.journeySelectedType)
      test()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeyEnteredP800Reference
    }
    "cheque" in {
      upsertJourneyToDatabase(tdAll.Cheque.journeySelectedType)
      test()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.Cheque.journeyEnteredP800Reference
    }

      def test(): Unit = {
        pages.whatIsYourP800ReferencePage.open()
        pages.whatIsYourP800ReferencePage.assertPageIsDisplayed()
        pages.whatIsYourP800ReferencePage.enterP800Reference(tdAll.p800Reference.value)
        pages.whatIsYourP800ReferencePage.clickSubmit()
        pages.whatIsYourNationalInsuranceNumberPage.assertPageIsDisplayed()
      }
  }

  "Clicking Continue with empty text input shows error" - {
    "bank transfer" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.journeySelectedType)
      test()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeySelectedType
    }
    "cheque" in {
      upsertJourneyToDatabase(tdAll.Cheque.journeySelectedType)
      test()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.Cheque.journeySelectedType
    }

      def test(): Unit = {
        pages.whatIsYourP800ReferencePage.open()
        pages.whatIsYourP800ReferencePage.assertPageIsDisplayed()
        pages.whatIsYourP800ReferencePage.clickSubmit()
        pages.whatIsYourP800ReferencePage.assertPageShowsErrorRequired()
      }
  }

  "Clicking Continue with invalid reference shows error" - {

    "bank transfer" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.journeySelectedType)
      test()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeySelectedType
    }
    "cheque" in {
      upsertJourneyToDatabase(tdAll.Cheque.journeySelectedType)
      test()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.Cheque.journeySelectedType
    }
      def test(): Unit = {
        pages.whatIsYourP800ReferencePage.open()
        pages.whatIsYourP800ReferencePage.assertPageIsDisplayed()
        pages.whatIsYourP800ReferencePage.enterP800Reference("this is a really long and invalid reference")
        pages.whatIsYourP800ReferencePage.clickSubmit()
        pages.whatIsYourP800ReferencePage.assertPageShowsErrorReferenceFormat()
      }
  }

  "Clicking 'Sign in or create a personal tax account' link opens correctly" - {
    "bank transfer" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.journeySelectedType)
      test()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeySelectedType
    }
    "cheque" in {
      upsertJourneyToDatabase(tdAll.Cheque.journeySelectedType)
      test()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.Cheque.journeySelectedType
    }
      def test(): Unit = {
        pages.whatIsYourP800ReferencePage.open()
        pages.whatIsYourP800ReferencePage.assertPageIsDisplayed()
        pages.whatIsYourP800ReferencePage.clickPtaSignInLink()
        pages.ptaSignInPage.assertPageIsDisplayed()
      }
  }

  "Clicking on back button redirects back to 'Do you want to sign in?' page" - {
    "bank transfer" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.journeySelectedType)
      test()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeySelectedType
    }
    "cheque" in {
      upsertJourneyToDatabase(tdAll.Cheque.journeySelectedType)
      test()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.Cheque.journeySelectedType
    }
      def test() = {
        pages.whatIsYourP800ReferencePage.open()
        pages.whatIsYourP800ReferencePage.assertPageIsDisplayed()
        pages.whatIsYourP800ReferencePage.clickBackButton()
        pages.doYouWantToSignInPage.assertPageIsDisplayed()
      }
  }
}
