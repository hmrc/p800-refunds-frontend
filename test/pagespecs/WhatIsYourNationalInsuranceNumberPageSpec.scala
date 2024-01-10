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

import models.NationalInsuranceNumber
import testsupport.ItSpec

class WhatIsYourNationalInsuranceNumberPageSpec extends ItSpec {

  override def beforeEach(): Unit = {
    super.beforeEach()
    addJourneyIdToSession(tdAll.journeyId)
  }

  "whatIsYourNationalInsuranceNumberPage renders the what is your national insurance number page" - {
    "bank transfer" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.journeyEnteredP800Reference)
      test()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeyEnteredP800Reference
    }
    "cheque" in {
      upsertJourneyToDatabase(tdAll.Cheque.journeyEnteredP800Reference)
      test()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.Cheque.journeyEnteredP800Reference
    }
      def test() = {
        pages.whatIsYourNationalInsuranceNumberPage.open()
        pages.whatIsYourNationalInsuranceNumberPage.assertPageIsDisplayed()
      }
  }

  private def cleanNino(nino: String): String = nino.replaceAll("[^0-9a-zA-Z]", "").toUpperCase

  s"Entering a valid NINO and clicking 'Continue'" - {
    Seq(tdAll.nationalInsuranceNumber.value, "aa000000a", " AA000000A.", "MA 00 00 03 A").foreach { nino =>
      s"($nino) bank transfer - redirects user to whatIsYourDateOfBirthPage" in {
        upsertJourneyToDatabase(tdAll.BankTransfer.journeyEnteredP800Reference)
        test()
        pages.whatIsYourDateOfBirthPage.assertPageIsDisplayed()
        val expectedJourney = tdAll.BankTransfer.journeyEnteredNino.copy(nationalInsuranceNumber = Some(NationalInsuranceNumber(cleanNino(nino))))
        getJourneyFromDatabase(tdAll.journeyId) shouldBeLike expectedJourney
      }
      s"($nino) cheque - redirects user to checkYourAnswersPage" in {
        upsertJourneyToDatabase(tdAll.Cheque.journeyEnteredP800Reference)
        test()
        val expectedJourney = tdAll.Cheque.journeyEnteredNino.copy(nationalInsuranceNumber = Some(NationalInsuranceNumber(cleanNino(nino))))
        pages.checkYourAnswersPage.assertPageIsDisplayedForCheque(
          expectedJourney.p800Reference.value,
          expectedJourney.nationalInsuranceNumber.value
        )
        getJourneyFromDatabase(tdAll.journeyId) shouldBeLike expectedJourney
      }

        def test(): Unit = {
          pages.whatIsYourNationalInsuranceNumberPage.open()
          pages.whatIsYourNationalInsuranceNumberPage.assertPageIsDisplayed()
          pages.whatIsYourNationalInsuranceNumberPage.enterNationalInsuranceNumber(NationalInsuranceNumber(nino))
          pages.whatIsYourNationalInsuranceNumberPage.clickSubmit()
        }

    }
  }

  "Clicking 'Continue' with empty text input shows error" - {
    "bank transfer" in {
      val expectedJourney = tdAll.BankTransfer.journeyEnteredP800Reference
      upsertJourneyToDatabase(expectedJourney)
      test()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike expectedJourney
    }
    "cheque" in {
      val expectedJourney = tdAll.Cheque.journeyEnteredP800Reference
      upsertJourneyToDatabase(expectedJourney)
      test()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike expectedJourney
    }

      def test(): Unit = {
        pages.whatIsYourNationalInsuranceNumberPage.open()
        pages.whatIsYourNationalInsuranceNumberPage.assertPageIsDisplayed()
        pages.whatIsYourNationalInsuranceNumberPage.clickSubmit()
        pages.whatIsYourNationalInsuranceNumberPage.assertPageShowsErrorEmptyInput()
      }
  }

  s"Clicking 'Continue' with invalid NINO shows error" - {
    Seq("Not a NINO", "1234", "QQ 12 34 56 C", "MA0%0£03A").foreach { nino =>
      s"($nino) bank transfer" in {
        val expectedJourney = tdAll.BankTransfer.journeyEnteredP800Reference
        upsertJourneyToDatabase(expectedJourney)
        test()
        getJourneyFromDatabase(tdAll.journeyId) shouldBeLike expectedJourney
      }
      s"($nino) cheque" in {
        val expectedJourney = tdAll.Cheque.journeyEnteredP800Reference
        upsertJourneyToDatabase(expectedJourney)
        test()
        getJourneyFromDatabase(tdAll.journeyId) shouldBeLike expectedJourney
      }

        def test(): Unit = {
          pages.whatIsYourNationalInsuranceNumberPage.open()
          pages.whatIsYourNationalInsuranceNumberPage.assertPageIsDisplayed()
          pages.whatIsYourNationalInsuranceNumberPage.enterNationalInsuranceNumber(NationalInsuranceNumber(nino))
          pages.whatIsYourNationalInsuranceNumberPage.clickSubmit()
          pages.whatIsYourNationalInsuranceNumberPage.assertPageShowsErrorInvalid()
        }
    }
  }

  "Clicking 'Back' redirects to whatIsYourP800ReferencePage" - {
    s"bank transfer" in {
      val expectedJourney = tdAll.BankTransfer.journeyEnteredP800Reference
      upsertJourneyToDatabase(expectedJourney)
      test()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike expectedJourney
    }
    s"cheque" in {
      val expectedJourney = tdAll.Cheque.journeyEnteredP800Reference
      upsertJourneyToDatabase(expectedJourney)
      test()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike expectedJourney
    }

      def test(): Unit = {
        pages.whatIsYourNationalInsuranceNumberPage.open()
        pages.whatIsYourNationalInsuranceNumberPage.assertPageIsDisplayed()
        pages.whatIsYourNationalInsuranceNumberPage.clickBackButton()
        pages.whatIsYourP800ReferencePage.assertPageIsDisplayed()
      }

  }

  "Prepopulate the form if the user has already entered it" - {

    s"bank transfer" in {
      val expectedJourney = tdAll.BankTransfer.journeyEnteredNino
      upsertJourneyToDatabase(expectedJourney)
      test()
      pages.whatIsYourNationalInsuranceNumberPage.assertDataPrepopulated(expectedJourney.nationalInsuranceNumber.value)
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike expectedJourney
    }
    s"cheque" in {
      val expectedJourney = tdAll.Cheque.journeyEnteredNino
      upsertJourneyToDatabase(expectedJourney)
      test()
      pages.whatIsYourNationalInsuranceNumberPage.assertDataPrepopulated(expectedJourney.nationalInsuranceNumber.value)
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike expectedJourney
    }

      def test(): Unit = {
        pages.whatIsYourNationalInsuranceNumberPage.open()
        pages.whatIsYourNationalInsuranceNumberPage.assertPageIsDisplayed()

      }

  }

}
