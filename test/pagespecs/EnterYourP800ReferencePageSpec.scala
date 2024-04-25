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

import models.UserEnteredP800Reference
import models.journeymodels.JourneyType
import testsupport.ItSpec

class EnterYourP800ReferencePageSpec extends ItSpec {

  override def beforeEach(): Unit = {
    super.beforeEach()
    addJourneyIdToSession(tdAll.journeyId)
  }

  "Entering valid p800 reference and clicking Continue redirects to WhatIsYourNationalInsuranceNumberPage" - {

    val testScenarios: List[String] = List(
      "1",
      "123",
      "123456789",
      "1234567890",
      "1234567891",
      "01234567891",
      "001234567891",
      "0001234567891",
      "12-34-56-78-90",
      "12,34,56,78,90",
      "12 34 56 78 90",
      "2,14-16 29"
    )

    testScenarios.foreach { reference =>
      s"[$reference] bank transfer" in {
        upsertJourneyToDatabase(tdAll.BankTransfer.journeySelectedType)
        test(JourneyType.BankTransfer, reference)
        getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeyEnteredP800Reference.copy(p800Reference = Some(UserEnteredP800Reference(reference)))
      }
      s"[$reference] cheque" in {
        upsertJourneyToDatabase(tdAll.Cheque.journeySelectedType)
        test(JourneyType.Cheque, reference)
        getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.Cheque.journeyEnteredP800Reference.copy(p800Reference = Some(UserEnteredP800Reference(reference)))
      }
    }

      def test(journeyType: JourneyType, referenceInput: String): Unit = {
        val (startPage, endPage) = journeyType match {
          case JourneyType.BankTransfer => pages.whatIsYourP800ReferenceBankTransferPage -> pages.enterYourNationalInsuranceNumberBankTransferPage
          case JourneyType.Cheque       => pages.whatIsYourP800ReferenceChequePage -> pages.enterYourNationalInsuranceNumberChequePage
        }
        startPage.open()
        startPage.assertPageIsDisplayed(journeyType)
        startPage.enterP800Reference(referenceInput)
        startPage.clickSubmit()
        endPage.assertPageIsDisplayed(journeyType)
      }
  }

  "P800 reference validation" - {

      def test(journeyType: JourneyType, referenceInput: Option[String], expectedErrorContent: String): Unit = {
        val page = journeyType match {
          case JourneyType.BankTransfer => pages.whatIsYourP800ReferenceBankTransferPage
          case JourneyType.Cheque       => pages.whatIsYourP800ReferenceChequePage
        }
        page.open()
        page.assertPageIsDisplayed(journeyType)
        referenceInput.fold(())(r => page.enterP800Reference(r))
        page.clickSubmit()
        page.assertPageShowsError(journeyType, expectedErrorContent)
      }

    "Submitting with an empty text input shows error" - {
      "bank transfer" in {
        upsertJourneyToDatabase(tdAll.BankTransfer.journeySelectedType)
        test(JourneyType.BankTransfer, None, pages.whatIsYourP800ReferenceBankTransferPage.missingInputErrorContent)
        getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeySelectedType
      }
      "cheque" in {
        upsertJourneyToDatabase(tdAll.Cheque.journeySelectedType)
        test(JourneyType.Cheque, None, pages.whatIsYourP800ReferenceChequePage.missingInputErrorContent)
        getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.Cheque.journeySelectedType
      }
    }

    "Submitting with reference that is too long" - {
      val referenceThatIsTooLong = Some("12345678912")
      "bank transfer" in {
        upsertJourneyToDatabase(tdAll.BankTransfer.journeySelectedType)
        test(JourneyType.BankTransfer, referenceThatIsTooLong, pages.whatIsYourP800ReferenceBankTransferPage.invalidInputErrorContent)
        getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeySelectedType
      }
      "cheque" in {
        upsertJourneyToDatabase(tdAll.Cheque.journeySelectedType)
        test(JourneyType.Cheque, referenceThatIsTooLong, pages.whatIsYourP800ReferenceChequePage.invalidInputErrorContent)
        getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.Cheque.journeySelectedType
      }
    }

    "Submitting with reference that contains invalid characters" - {
      val testScenarios: List[String] = List(
        "P800123456",
        "123456A",
        "123456!",
        "123456?",
        "123456O",
        "123456$",
        "123456."
      )
      testScenarios.foreach { reference =>
        val referenceContainingInvalidCharacter = Some(reference)
        s"[$reference] bank transfer" in {
          upsertJourneyToDatabase(tdAll.BankTransfer.journeySelectedType)
          test(JourneyType.BankTransfer, referenceContainingInvalidCharacter, pages.whatIsYourP800ReferenceBankTransferPage.invalidInputErrorContent)
          getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeySelectedType
        }
        s"[$reference] cheque" in {
          upsertJourneyToDatabase(tdAll.Cheque.journeySelectedType)
          test(JourneyType.Cheque, referenceContainingInvalidCharacter, pages.whatIsYourP800ReferenceChequePage.invalidInputErrorContent)
          getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.Cheque.journeySelectedType
        }
      }
    }
  }

}
