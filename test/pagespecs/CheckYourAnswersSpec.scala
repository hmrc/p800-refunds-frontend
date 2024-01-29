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

import models.dateofbirth.{DateOfBirth, DayOfMonth, Month, Year}
import models.{NationalInsuranceNumber, P800Reference}
import testsupport.ItSpec
import testsupport.stubs.IdentityVerificationStub

class CheckYourAnswersSpec extends ItSpec {

  "demo" in {
    import java.text.Normalizer
    import java.text.Normalizer.Form

    val stringWithSpecialChars = "Prinsesstårta"

      def removeDiacritics(inputStr: String): String = {
        val nfdNormalizedString = Normalizer.normalize(inputStr, Form.NFD)
        nfdNormalizedString.filterNot(ch => Character.getType(ch) == Character.NON_SPACING_MARK)
      }

    println(removeDiacritics(stringWithSpecialChars))
  }

  "page renders correctly" - {
    "bank transfer" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.journeyEnteredDateOfBirth)
      pages.checkYourAnswersBankTransferPage.open()
      pages.checkYourAnswersBankTransferPage.assertPageIsDisplayedForBankTransfer(
        tdAll.p800Reference,
        tdAll.dateOfBirthFormatted,
        tdAll.nationalInsuranceNumber
      )
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeyEnteredDateOfBirth
    }
    "cheque" in {
      upsertJourneyToDatabase(tdAll.Cheque.journeyEnteredNino)
      pages.checkYourAnswersChequePage.open()
      pages.checkYourAnswersChequePage.assertPageIsDisplayedForCheque(
        tdAll.p800Reference,
        tdAll.nationalInsuranceNumber
      )
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.Cheque.journeyEnteredNino
    }
  }

  "changing reference" - {
    "bank transfer" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.journeyEnteredDateOfBirth)
      pages.checkYourAnswersBankTransferPage.open()
      pages.checkYourAnswersBankTransferPage.assertPageIsDisplayedForBankTransfer(
        tdAll.p800Reference,
        tdAll.dateOfBirthFormatted,
        tdAll.nationalInsuranceNumber
      )
      pages.checkYourAnswersBankTransferPage.clickChangeReference()
      pages.whatIsYourP800ReferenceBankTransferPage.assertPageIsDisplayed()
      val newReference = P800Reference("newreference")
      pages.whatIsYourP800ReferenceBankTransferPage.enterP800Reference(newReference.value)
      pages.whatIsYourP800ReferenceBankTransferPage.clickSubmit()
      pages.checkYourAnswersBankTransferPage.assertPageIsDisplayedForBankTransfer(
        newReference,
        tdAll.dateOfBirthFormatted,
        tdAll.nationalInsuranceNumber
      )
      val expectedJourney = tdAll.BankTransfer.journeyEnteredDateOfBirth.copy(
        p800Reference = Some(newReference)
      )
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike expectedJourney
    }
    "cheque" in {
      upsertJourneyToDatabase(tdAll.Cheque.journeyEnteredNino)
      pages.checkYourAnswersChequePage.open()
      pages.checkYourAnswersChequePage.clickChangeReference()
      pages.whatIsYourP800ReferenceChequePage.assertPageIsDisplayed()
      val newReference = P800Reference("newreference")
      pages.whatIsYourP800ReferenceChequePage.enterP800Reference(newReference.value)
      pages.whatIsYourP800ReferenceChequePage.clickSubmit()
      pages.checkYourAnswersChequePage.assertPageIsDisplayedForCheque(
        newReference,
        tdAll.nationalInsuranceNumber
      )
      val expectedJourney = tdAll.Cheque.journeyEnteredNino.copy(
        p800Reference = Some(newReference)
      )
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike expectedJourney
    }
  }

  "changing date of birth" - {
    "bank transfer" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.journeyEnteredDateOfBirth)
      pages.checkYourAnswersBankTransferPage.open()
      pages.checkYourAnswersBankTransferPage.clickChangeDateOfBirth()
      pages.whatIsYourDateOfBirthPage.assertPageIsDisplayed()
      val newDob = DateOfBirth(DayOfMonth("19"), Month("Feb"), Year("2001"))
      pages.whatIsYourDateOfBirthPage.enterYear(newDob.year.value)
      pages.whatIsYourDateOfBirthPage.enterMonth(newDob.month.value)
      pages.whatIsYourDateOfBirthPage.enterDayOfMonth(newDob.dayOfMonth.value)

      pages.whatIsYourDateOfBirthPage.clickSubmit()
      pages.checkYourAnswersBankTransferPage.assertPageIsDisplayedForBankTransfer(
        tdAll.p800Reference,
        "19 February 2001",
        tdAll.nationalInsuranceNumber
      )
      val expectedJourney = tdAll.BankTransfer.journeyEnteredDateOfBirth.copy(
        dateOfBirth = Some(newDob)
      )
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike expectedJourney
    }
  }

  "changing national insurance number" - {
    "bank transfer" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.journeyEnteredDateOfBirth)
      pages.checkYourAnswersBankTransferPage.open()
      pages.checkYourAnswersBankTransferPage.clickChangeNationalInsuranceNumber()
      pages.whatIsYourNationalInsuranceNumberBankTransferPage.assertPageIsDisplayed()
      val newNino = NationalInsuranceNumber("AB123123C")
      pages.whatIsYourNationalInsuranceNumberBankTransferPage.enterNationalInsuranceNumber(newNino)
      pages.whatIsYourNationalInsuranceNumberBankTransferPage.clickSubmit()
      pages.checkYourAnswersBankTransferPage.assertPageIsDisplayedForBankTransfer(
        tdAll.p800Reference,
        tdAll.dateOfBirthFormatted,
        newNino
      )
      val expectedJourney = tdAll.BankTransfer.journeyEnteredDateOfBirth.copy(
        nationalInsuranceNumber = Some(newNino)
      )
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike expectedJourney
    }
    "cheque" in {
      upsertJourneyToDatabase(tdAll.Cheque.journeyEnteredNino)
      pages.checkYourAnswersChequePage.open()
      pages.checkYourAnswersChequePage.clickChangeNationalInsuranceNumber()
      val newNino = NationalInsuranceNumber("AB123123C")
      pages.whatIsYourNationalInsuranceNumberChequePage.enterNationalInsuranceNumber(newNino)
      pages.whatIsYourNationalInsuranceNumberChequePage.clickSubmit()
      pages.checkYourAnswersChequePage.assertPageIsDisplayedForCheque(
        tdAll.p800Reference,
        newNino
      )
      val expectedJourney = tdAll.Cheque.journeyEnteredNino.copy(
        nationalInsuranceNumber = Some(newNino)
      )
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike expectedJourney
    }
  }

  "clicking submit redirects to 'Your identity has been confirmed' if response from NPS indicates identity verification is successful" - {
    "bank transfer" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.journeyEnteredDateOfBirth)
      //TODO TraceIndividual API
      pages.checkYourAnswersBankTransferPage.open()
      IdentityVerificationStub.stubIdentityVerification2xx(tdAll.BankTransfer.journeyIdentityVerified.identityVerificationResponse.value)
      pages.checkYourAnswersBankTransferPage.clickSubmit()
      pages.weHaveConfirmedYourIdentityBankTransferPage.assertPageIsDisplayed()
      IdentityVerificationStub.verifyIdentityVerification()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeyIdentityVerified
    }
    "cheque" in {
      upsertJourneyToDatabase(tdAll.Cheque.journeyEnteredNino)
      pages.checkYourAnswersChequePage.open()
      IdentityVerificationStub.stubIdentityVerification2xx(tdAll.Cheque.journeyIdentityVerified.identityVerificationResponse.value)
      pages.checkYourAnswersChequePage.clickSubmit()
      pages.weHaveConfirmedYourIdentityChequePage.assertPageIsDisplayed()
      IdentityVerificationStub.verifyIdentityVerification()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.Cheque.journeyIdentityVerified
    }
  }

  "clicking submit redirects to 'We cannot confirm your identity' if response from NPS indicates identity verification failed" - {
    "bank transfer" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.journeyEnteredDateOfBirth)
      //TODO TraceIndividual API
      pages.checkYourAnswersBankTransferPage.open()
      IdentityVerificationStub.stubIdentityVerification2xx(tdAll.BankTransfer.journeyIdentityNotVerified.identityVerificationResponse.value)
      pages.checkYourAnswersBankTransferPage.clickSubmit()
      pages.weCannotConfirmYourIdentityPage.assertPageIsDisplayed()
      IdentityVerificationStub.verifyIdentityVerification()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeyIdentityNotVerified
    }
    "cheque" in {
      upsertJourneyToDatabase(tdAll.Cheque.journeyEnteredNino)
      pages.checkYourAnswersChequePage.open()
      IdentityVerificationStub.stubIdentityVerification2xx(tdAll.Cheque.journeyIdentityNotVerified.identityVerificationResponse.value)
      pages.checkYourAnswersChequePage.clickSubmit()
      pages.weCannotConfirmYourIdentityPage.assertPageIsDisplayed()
      IdentityVerificationStub.verifyIdentityVerification()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.Cheque.journeyIdentityNotVerified
    }
  }

  "clicking back button navigates to What Is Your National Insurance Number page" - {
    "bank transfer" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.journeyEnteredDateOfBirth)
      pages.checkYourAnswersBankTransferPage.open()
      pages.checkYourAnswersBankTransferPage.clickBackButton()
      pages.whatIsYourDateOfBirthPage.assertPageIsDisplayed()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeyEnteredDateOfBirth
    }
    "cheque" in {
      upsertJourneyToDatabase(tdAll.Cheque.journeyEnteredNino)
      pages.checkYourAnswersChequePage.open()
      pages.checkYourAnswersChequePage.clickBackButton()
      pages.whatIsYourNationalInsuranceNumberChequePage.assertPageIsDisplayed()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.Cheque.journeyEnteredNino
    }
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    addJourneyIdToSession(tdAll.journeyId)
  }
}
