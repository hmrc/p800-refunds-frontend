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

import models.{Nino, P800Reference}
import models.dateofbirth.{DateOfBirth, DayOfMonth, Month, Year}
import models.journeymodels.{Journey, JourneyInternal, JourneyType}
import testsupport.ItSpec
import testsupport.stubs.NpsReferenceCheckStub

class CheckYourAnswersSpec extends ItSpec {

  "page renders correctly" - {
    "bank transfer" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.journeyEnteredDateOfBirth)
      pages.checkYourAnswersBankTransferPage.open()
      pages.checkYourAnswersBankTransferPage.assertPageIsDisplayedForBankTransfer(
        tdAll.p800Reference,
        tdAll.dateOfBirthFormatted,
        tdAll.nino
      )
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeyEnteredDateOfBirth
    }
    "cheque" in {
      upsertJourneyToDatabase(tdAll.Cheque.journeyEnteredNino)
      pages.checkYourAnswersChequePage.open()
      pages.checkYourAnswersChequePage.assertPageIsDisplayedForCheque(
        tdAll.p800Reference,
        tdAll.nino
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
        tdAll.nino
      )
      pages.checkYourAnswersBankTransferPage.clickChangeReference()
      pages.whatIsYourP800ReferenceBankTransferPage.assertPageIsDisplayed(journeyType = JourneyType.BankTransfer)
      val newReference = P800Reference("newreference")
      pages.whatIsYourP800ReferenceBankTransferPage.enterP800Reference(newReference.value)
      pages.whatIsYourP800ReferenceBankTransferPage.clickSubmit()
      pages.checkYourAnswersBankTransferPage.assertPageIsDisplayedForBankTransfer(
        newReference,
        tdAll.dateOfBirthFormatted,
        tdAll.nino
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
      pages.whatIsYourP800ReferenceChequePage.assertPageIsDisplayed(journeyType = JourneyType.Cheque)
      val newReference = P800Reference("newreference")
      pages.whatIsYourP800ReferenceChequePage.enterP800Reference(newReference.value)
      pages.whatIsYourP800ReferenceChequePage.clickSubmit()
      pages.checkYourAnswersChequePage.assertPageIsDisplayedForCheque(
        newReference,
        tdAll.nino
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
      pages.enterYourDateOfBirthPage.assertPageIsDisplayed()
      val newDob = DateOfBirth(DayOfMonth("19"), Month("Feb"), Year("2001"))
      pages.enterYourDateOfBirthPage.enterYear(newDob.year.value)
      pages.enterYourDateOfBirthPage.enterMonth(newDob.month.value)
      pages.enterYourDateOfBirthPage.enterDayOfMonth(newDob.dayOfMonth.value)

      pages.enterYourDateOfBirthPage.clickSubmit()
      pages.checkYourAnswersBankTransferPage.assertPageIsDisplayedForBankTransfer(
        tdAll.p800Reference,
        "19 February 2001",
        tdAll.nino
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
      pages.enterYourNationalInsuranceNumberBankTransferPage.assertPageIsDisplayed(journeyType = JourneyType.BankTransfer)
      val newNino = Nino("AB123123C")
      pages.enterYourNationalInsuranceNumberBankTransferPage.enterNationalInsuranceNumber(newNino)
      pages.enterYourNationalInsuranceNumberBankTransferPage.clickSubmit()
      pages.checkYourAnswersBankTransferPage.assertPageIsDisplayedForBankTransfer(
        tdAll.p800Reference,
        tdAll.dateOfBirthFormatted,
        newNino
      )
      val expectedJourney = tdAll.BankTransfer.journeyEnteredDateOfBirth.copy(
        nino = Some(newNino)
      )
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike expectedJourney
    }
    "cheque" in {
      upsertJourneyToDatabase(tdAll.Cheque.journeyEnteredNino)
      pages.checkYourAnswersChequePage.open()
      pages.checkYourAnswersChequePage.clickChangeNationalInsuranceNumber()
      val newNino = Nino("AB123123C")
      pages.enterYourNationalInsuranceNumberChequePage.enterNationalInsuranceNumber(newNino)
      pages.enterYourNationalInsuranceNumberChequePage.clickSubmit()
      pages.checkYourAnswersChequePage.assertPageIsDisplayedForCheque(
        tdAll.p800Reference,
        newNino
      )
      val expectedJourney = tdAll.Cheque.journeyEnteredNino.copy(
        nino = Some(newNino)
      )
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike expectedJourney
    }
  }

  "clicking submit redirects to 'Your identity is confirmed' if response from NPS indicates identity verification is successful" - {
    "bank transfer" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.journeyEnteredDateOfBirth)
      //TODO TraceIndividual API
      pages.checkYourAnswersBankTransferPage.open()
      val j = tdAll.BankTransfer.AfterReferenceCheck.journeyReferenceChecked
      NpsReferenceCheckStub.checkReference(j.nino.value, j.p800Reference.value, new Journey(j).getP800ReferenceChecked(request = tdAll.fakeRequest))
      pages.checkYourAnswersBankTransferPage.clickSubmit()
      pages.yourIdentityIsConfirmedBankTransferPage.assertPageIsDisplayed(JourneyType.BankTransfer)
      NpsReferenceCheckStub.verifyCheckReference(j.nino.value, j.p800Reference.value)
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike j
    }
    "cheque" in {
      upsertJourneyToDatabase(tdAll.Cheque.journeyEnteredNino)
      pages.checkYourAnswersChequePage.open()
      val j = tdAll.Cheque.AfterReferenceCheck.journeyReferenceChecked
      NpsReferenceCheckStub.checkReference(j.nino.value, j.p800Reference.value, new Journey(j).getP800ReferenceChecked(request = tdAll.fakeRequest))
      pages.checkYourAnswersChequePage.clickSubmit()
      pages.yourIdentityIsConfirmedChequePage.assertPageIsDisplayed(JourneyType.Cheque)
      NpsReferenceCheckStub.verifyCheckReference(j.nino.value, j.p800Reference.value)
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike j
    }
  }

  "clicking submit redirects to 'We cannot confirm your identity' if response from NPS indicates identity verification failed" - {

    "bank transfer - failed attempts repo is empty" in {
      val j = tdAll.BankTransfer.journeyEnteredDateOfBirth
      upsertJourneyToDatabase(j)

      NpsReferenceCheckStub.checkReferenceReferenceDidntMatchNino(j.nino.value, j.p800Reference.value)

      //TODO TraceIndividual API

      pages.checkYourAnswersBankTransferPage.open()
      pages.checkYourAnswersBankTransferPage.assertPageIsDisplayedForBankTransfer(
        tdAll.p800Reference,
        tdAll.dateOfBirthFormatted,
        tdAll.nino
      )
      pages.checkYourAnswersBankTransferPage.clickSubmit()
      pages.weCannotConfirmYourIdentityBankTransferPage.assertPageIsDisplayed(JourneyType.BankTransfer)
      NpsReferenceCheckStub.verifyCheckReference(j.nino.value, j.p800Reference.value)
      //TODO: Verify failed attempts increased
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.AfterReferenceCheck.journeyReferenceDidntMatchNino
    }

    "cheque - failed attempts repo is empty" in {
      val j = tdAll.Cheque.journeyEnteredNino
      upsertJourneyToDatabase(j)
      NpsReferenceCheckStub.checkReferenceReferenceDidntMatchNino(j.nino.value, j.p800Reference.value)

      pages.checkYourAnswersChequePage.open()
      pages.checkYourAnswersChequePage.clickSubmit()
      pages.weCannotConfirmYourIdentityChequePage.assertPageIsDisplayed(JourneyType.Cheque)

      NpsReferenceCheckStub.verifyCheckReference(j.nino.value, j.p800Reference.value)
      //TODO: Verify failed attempts increased
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.Cheque.AfterReferenceCheck.journeyReferenceDidntMatchNino
    }

    "bank transfer - user already failed 1 time" in {
      val j = tdAll.BankTransfer.AfterReferenceCheck.journeyReferenceDidntMatchNino
      NpsReferenceCheckStub.checkReferenceReferenceDidntMatchNino(j.nino.value, j.p800Reference.value)

      upsertJourneyToDatabase(j)
      upsertFailedAttemptToDatabase(tdAll.attemptInfo(failedAttempts = 1))

      pages.checkYourAnswersBankTransferPage.open()
      pages.checkYourAnswersBankTransferPage.clickSubmit()
      pages.weCannotConfirmYourIdentityBankTransferPage.assertPageIsDisplayed(JourneyType.BankTransfer)

      NpsReferenceCheckStub.verifyCheckReference(j.nino.value, j.p800Reference.value)
      //TODO: Verify failed attempts increased
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike j
    }

    "cheque - user already failed 1 time" in {
      val j = tdAll.Cheque.AfterReferenceCheck.journeyReferenceDidntMatchNino
      NpsReferenceCheckStub.checkReferenceReferenceDidntMatchNino(j.nino.value, j.p800Reference.value)

      upsertJourneyToDatabase(j)
      upsertFailedAttemptToDatabase(tdAll.attemptInfo(failedAttempts = 1))

      pages.checkYourAnswersChequePage.open()
      pages.checkYourAnswersChequePage.clickSubmit()
      pages.weCannotConfirmYourIdentityChequePage.assertPageIsDisplayed(JourneyType.Cheque)

      NpsReferenceCheckStub.verifyCheckReference(j.nino.value, j.p800Reference.value)
      //TODO: Verify failed attempts increased
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike j
    }
  }

  "clicking submit, resulting in failing verification too many times, redirects to 'No more attempts left' page" - {
    //todo test for when user goes from 2 failed attempts to 3, then journey should be hasFinished.LockedOut

    "bank transfer - when user has 2 existing failed attempts, 3rd attempt also fails" in {
      val j: JourneyInternal = tdAll.BankTransfer.AfterReferenceCheck.journeyReferenceDidntMatchNino
      NpsReferenceCheckStub.checkReferenceReferenceDidntMatchNino(j.nino.value, j.p800Reference.value)

      upsertJourneyToDatabase(j)
      upsertFailedAttemptToDatabase(tdAll.attemptInfo(2))
      test(JourneyType.BankTransfer)
      pages.noMoreAttemptsLeftToConfirmYourIdentityBankTransferPage.assertPageIsDisplayed(JourneyType.BankTransfer)
      NpsReferenceCheckStub.verifyCheckReference(j.nino.value, j.p800Reference.value)
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeyLockedOutFromFailedAttempts
    }

    "cheque - when user has 2 existing failed attempts, 3rd attempt also fails" in {
      val j: JourneyInternal = tdAll.Cheque.AfterReferenceCheck.journeyReferenceDidntMatchNino
      NpsReferenceCheckStub.checkReferenceReferenceDidntMatchNino(j.nino.value, j.p800Reference.value)

      upsertJourneyToDatabase(j)
      upsertFailedAttemptToDatabase(tdAll.attemptInfo(2))
      test(JourneyType.Cheque)
      pages.noMoreAttemptsLeftToConfirmYourIdentityChequePage.assertPageIsDisplayed(JourneyType.Cheque)
      NpsReferenceCheckStub.verifyCheckReference(j.nino.value, j.p800Reference.value)
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.Cheque.journeyLockedOutFromFailedAttempts
    }

      def test(journeyType: JourneyType): Unit = {
        val startPage = journeyType match {
          case JourneyType.Cheque       => pages.checkYourAnswersChequePage
          case JourneyType.BankTransfer => pages.checkYourAnswersBankTransferPage
        }
        startPage.open()
        startPage.clickSubmit()
      }
  }

  "clicking back button navigates to What Is Your National Insurance Number page" - {
    "bank transfer" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.journeyEnteredDateOfBirth)
      pages.checkYourAnswersBankTransferPage.open()
      pages.checkYourAnswersBankTransferPage.clickBackButton()
      pages.enterYourDateOfBirthPage.assertPageIsDisplayed()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeyEnteredDateOfBirth
    }
    "cheque" in {
      upsertJourneyToDatabase(tdAll.Cheque.journeyEnteredNino)
      pages.checkYourAnswersChequePage.open()
      pages.checkYourAnswersChequePage.clickBackButton()
      pages.enterYourNationalInsuranceNumberChequePage.assertPageIsDisplayed(JourneyType.Cheque)
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.Cheque.journeyEnteredNino
    }
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    addJourneyIdToSession(tdAll.journeyId)
  }
}
