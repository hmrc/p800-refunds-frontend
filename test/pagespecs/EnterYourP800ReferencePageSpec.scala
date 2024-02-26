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

class EnterYourP800ReferencePageSpec extends ItSpec {

  override def beforeEach(): Unit = {
    super.beforeEach()
    addJourneyIdToSession(tdAll.journeyId)
  }

  "Entering valid p800 reference and clicking Continue redirects to WhatIsYourNationalInsuranceNumberPage" - {
    "bank transfer" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.journeySelectedType)
      test(JourneyType.BankTransfer)
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeyEnteredP800Reference
    }
    "cheque" in {
      upsertJourneyToDatabase(tdAll.Cheque.journeySelectedType)
      test(JourneyType.Cheque)
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.Cheque.journeyEnteredP800Reference
    }

      def test(journeyType: JourneyType): Unit = {
        val (startPage, endPage) = journeyType match {
          case JourneyType.BankTransfer => pages.whatIsYourP800ReferenceBankTransferPage -> pages.enterYourNationalInsuranceNumberBankTransferPage
          case JourneyType.Cheque       => pages.whatIsYourP800ReferenceChequePage -> pages.enterYourNationalInsuranceNumberChequePage
        }
        startPage.open()
        startPage.assertPageIsDisplayed(journeyType)
        startPage.enterP800Reference(tdAll.p800Reference.value)
        startPage.clickSubmit()
        endPage.assertPageIsDisplayed(journeyType)
      }
  }

  "Clicking Continue with empty text input shows error" - {
    "bank transfer" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.journeySelectedType)
      test(JourneyType.BankTransfer)
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeySelectedType
    }
    "cheque" in {
      upsertJourneyToDatabase(tdAll.Cheque.journeySelectedType)
      test(JourneyType.Cheque)
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.Cheque.journeySelectedType
    }

      def test(journeyType: JourneyType): Unit = {
        val page = journeyType match {
          case JourneyType.BankTransfer => pages.whatIsYourP800ReferenceBankTransferPage
          case JourneyType.Cheque       => pages.whatIsYourP800ReferenceChequePage
        }
        page.open()
        page.assertPageIsDisplayed(journeyType)
        page.clickSubmit()
        page.assertPageShowsErrorRequired(journeyType)
      }
  }

  "Clicking Continue with invalid reference shows error" - {

    "bank transfer" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.journeySelectedType)
      test(JourneyType.BankTransfer)
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeySelectedType
    }
    "cheque" in {
      upsertJourneyToDatabase(tdAll.Cheque.journeySelectedType)
      test(JourneyType.Cheque)
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.Cheque.journeySelectedType
    }
      def test(journeyType: JourneyType): Unit = {
        val page = journeyType match {
          case JourneyType.BankTransfer => pages.whatIsYourP800ReferenceBankTransferPage
          case JourneyType.Cheque       => pages.whatIsYourP800ReferenceChequePage
        }
        page.open()
        page.assertPageIsDisplayed(journeyType)
        page.enterP800Reference("this is a really long and invalid reference")
        page.clickSubmit()
        page.assertPageShowsErrorReferenceFormat(journeyType)
      }
  }

  "Clicking 'Sign in or create a personal tax account' link opens correctly" - {
    "bank transfer" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.journeySelectedType)
      test(JourneyType.BankTransfer)
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeySelectedType
    }
    "cheque" in {
      upsertJourneyToDatabase(tdAll.Cheque.journeySelectedType)
      test(JourneyType.Cheque)
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.Cheque.journeySelectedType
    }
      def test(journeyType: JourneyType): Unit = {
        val page = journeyType match {
          case JourneyType.BankTransfer => pages.whatIsYourP800ReferenceBankTransferPage
          case JourneyType.Cheque       => pages.whatIsYourP800ReferenceChequePage
        }
        page.open()
        page.assertPageIsDisplayed(journeyType)
        page.clickPtaSignInLink()
        pages.ptaSignInPage.assertPageIsDisplayed()
      }
  }
}
