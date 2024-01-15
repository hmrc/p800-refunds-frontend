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

  "page renders correctly" - {
    "bank transfer" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.journeyEnteredDateOfBirth)
      test()
      pages.checkYourAnswersPage.assertPageIsDisplayedForBankTransfer(
        tdAll.p800Reference,
        tdAll.dateOfBirthFormatted,
        tdAll.nationalInsuranceNumber
      )
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeyEnteredDateOfBirth
    }
    "cheque" in {
      upsertJourneyToDatabase(tdAll.Cheque.journeyEnteredNino)
      test()
      pages.checkYourAnswersPage.assertPageIsDisplayedForCheque(
        tdAll.p800Reference,
        tdAll.nationalInsuranceNumber
      )
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.Cheque.journeyEnteredNino
    }

      def test() = {
        pages.checkYourAnswersPage.open()
      }
  }

  "changing reference" - {
    "bank transfer" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.journeyEnteredDateOfBirth)
      test()
      pages.whatIsYourP800ReferencePage.assertPageIsDisplayed()
      val newReference = P800Reference("newreference")
      pages.whatIsYourP800ReferencePage.enterP800Reference(newReference.value)
      pages.whatIsYourP800ReferencePage.clickSubmit()
      pages.checkYourAnswersPage.assertPageIsDisplayedForBankTransfer(
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
      test()
      val newReference = P800Reference("newreference")
      pages.whatIsYourP800ReferencePage.enterP800Reference(newReference.value)
      pages.whatIsYourP800ReferencePage.clickSubmit()
      pages.checkYourAnswersPage.assertPageIsDisplayedForCheque(
        newReference,
        tdAll.nationalInsuranceNumber
      )
      val expectedJourney = tdAll.Cheque.journeyEnteredNino.copy(
        p800Reference = Some(newReference)
      )
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike expectedJourney
    }

      def test() = {
        pages.checkYourAnswersPage.open()
        pages.checkYourAnswersPage.clickChangeReference()
      }
  }

  "changing date of birth" - {
    "bank transfer" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.journeyEnteredDateOfBirth)
      pages.checkYourAnswersPage.open()
      pages.checkYourAnswersPage.clickChangeDateOfBirth()
      pages.whatIsYourDateOfBirthPage.assertPageIsDisplayed()
      val newDob = DateOfBirth(DayOfMonth("19"), Month("Feb"), Year("2001"))
      pages.whatIsYourDateOfBirthPage.enterYear(newDob.year.value)
      pages.whatIsYourDateOfBirthPage.enterMonth(newDob.month.value)
      pages.whatIsYourDateOfBirthPage.enterDayOfMonth(newDob.dayOfMonth.value)

      pages.whatIsYourDateOfBirthPage.clickSubmit()
      pages.checkYourAnswersPage.assertPageIsDisplayedForBankTransfer(
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
      test()
      pages.whatIsYourNationalInsuranceNumberPage.assertPageIsDisplayed()
      val newNino = NationalInsuranceNumber("AB123123C")
      pages.whatIsYourNationalInsuranceNumberPage.enterNationalInsuranceNumber(newNino)
      pages.whatIsYourNationalInsuranceNumberPage.clickSubmit()
      pages.checkYourAnswersPage.assertPageIsDisplayedForBankTransfer(
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
      test()
      val newNino = NationalInsuranceNumber("AB123123C")
      pages.whatIsYourNationalInsuranceNumberPage.enterNationalInsuranceNumber(newNino)
      pages.whatIsYourNationalInsuranceNumberPage.clickSubmit()
      pages.checkYourAnswersPage.assertPageIsDisplayedForCheque(
        tdAll.p800Reference,
        newNino
      )
      val expectedJourney = tdAll.Cheque.journeyEnteredNino.copy(
        nationalInsuranceNumber = Some(newNino)
      )
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike expectedJourney
    }

      def test() = {
        pages.checkYourAnswersPage.open()
        pages.checkYourAnswersPage.clickChangeNationalInsuranceNumber()
      }
  }

  "clicking submit redirects to 'Your identity has been confirmed' if response from NPS indicates identity verification is successful" - {
    "bank transfer" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.journeyEnteredDateOfBirth)
      //TODO TraceIndividual API
      pages.checkYourAnswersPage.open()
      IdentityVerificationStub.stubIdentityVerification2xx(tdAll.BankTransfer.journeyIdentityVerified.identityVerificationResponse.value)
      pages.checkYourAnswersPage.clickSubmit()
      pages.weHaveConfirmedYourIdentityPage.assertPageIsDisplayed()
      IdentityVerificationStub.verifyIdentityVerification()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeyIdentityVerified
    }
    "cheque" in {
      upsertJourneyToDatabase(tdAll.Cheque.journeyEnteredNino)
      pages.checkYourAnswersPage.open()
      IdentityVerificationStub.stubIdentityVerification2xx(tdAll.Cheque.journeyIdentityVerified.identityVerificationResponse.value)
      pages.checkYourAnswersPage.clickSubmit()
      pages.weHaveConfirmedYourIdentityPage.assertPageIsDisplayed()
      IdentityVerificationStub.verifyIdentityVerification()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.Cheque.journeyIdentityVerified
    }
  }

  "clicking submit redirects to 'We cannot confirm your identity' if response from NPS indicates identity verification failed" - {
    "bank transfer" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.journeyEnteredDateOfBirth)
      //TODO TraceIndividual API
      pages.checkYourAnswersPage.open()
      IdentityVerificationStub.stubIdentityVerification2xx(tdAll.BankTransfer.journeyIdentityNotVerified.identityVerificationResponse.value)
      pages.checkYourAnswersPage.clickSubmit()
      pages.weCannotConfirmYourIdentityPage.assertPageIsDisplayed()
      IdentityVerificationStub.verifyIdentityVerification()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeyIdentityNotVerified
    }
    "cheque" in {
      upsertJourneyToDatabase(tdAll.Cheque.journeyEnteredNino)
      pages.checkYourAnswersPage.open()
      IdentityVerificationStub.stubIdentityVerification2xx(tdAll.Cheque.journeyIdentityNotVerified.identityVerificationResponse.value)
      pages.checkYourAnswersPage.clickSubmit()
      pages.weCannotConfirmYourIdentityPage.assertPageIsDisplayed()
      IdentityVerificationStub.verifyIdentityVerification()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.Cheque.journeyIdentityNotVerified
    }
  }

  "clicking back button navigates to What Is Your National Insurance Number page" - {
    "bank transfer" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.journeyEnteredDateOfBirth)
      test()
      pages.whatIsYourDateOfBirthPage.assertPageIsDisplayed()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeyEnteredDateOfBirth
    }
    "cheque" in {
      upsertJourneyToDatabase(tdAll.Cheque.journeyEnteredNino)
      test()
      pages.whatIsYourNationalInsuranceNumberPage.assertPageIsDisplayed()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.Cheque.journeyEnteredNino
    }
      def test(): Unit = {
        pages.checkYourAnswersPage.open()
        pages.checkYourAnswersPage.clickBackButton()
      }
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    addJourneyIdToSession(tdAll.journeyId)
  }
}
