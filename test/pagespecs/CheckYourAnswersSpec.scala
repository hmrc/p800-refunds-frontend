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
import testsupport.stubs.IdentityVerificationStub

class CheckYourAnswersSpec extends ItSpec {

  "page renders correctly" in {
    pages.checkYourAnswersPage.open()
    //TODO    pages.checkYourAnswersPage.assertPageIsDisplayed(
    //TODO tdAll.fullName,
    //TODO tdAll.dateOfBirthFormatted,
    //TODO tdAll.nationalInsuranceNumber
    //TODO )
  }

  "changing name" in {
    pages.checkYourAnswersPage.open()
    pages.checkYourAnswersPage.assertPageIsDisplayed()
    pages.checkYourAnswersPage.clickChangeReference()
    //TODO pages.whatIsYourFullNamePage.assertPageIsDisplayed()
    //TODO     val newName: FullName = FullName("NewName")
    //TODO pages.whatIsYourFullNamePage.enterFullName(newName)
    //TODO pages.whatIsYourFullNamePage.clickSubmit()
    //TODO     pages.checkYourAnswersPage.assertPageIsDisplayed(
    //TODO newName,
    //TODO tdAll.dateOfBirthFormatted,
    //TODO tdAll.nationalInsuranceNumber
    //TODO ) withClue "user is navigated back to check your answers page"
  }

  "changing date of birth" in {
    pages.checkYourAnswersPage.open()
    pages.checkYourAnswersPage.assertPageIsDisplayed()
    pages.checkYourAnswersPage.clickChangeDateOfBirth()
    pages.whatIsYourDateOfBirthPage.assertPageIsDisplayed()
    pages.whatIsYourDateOfBirthPage.enterDayOfMonth("11")
    pages.whatIsYourDateOfBirthPage.enterMonth("12")
    pages.whatIsYourDateOfBirthPage.enterYear("1971")
    pages.whatIsYourDateOfBirthPage.clickSubmit()
    //TODO     pages.checkYourAnswersPage.assertPageIsDisplayed(
    //TODO       tdAll.fullName,
    //TODO "11 December 1971",
    //TODO tdAll.nationalInsuranceNumber
    //TODO     ) withClue "user is navigated back to check your answers page"
  }

  "changing national insurance number" in {
    pages.checkYourAnswersPage.open()
    pages.checkYourAnswersPage.assertPageIsDisplayed()
    pages.checkYourAnswersPage.clickChangeNationalInsuranceNumber()
    pages.whatIsYourNationalInsuranceNumberPage.assertPageIsDisplayed()
    val newNationalInsuranceNumber = NationalInsuranceNumber("AB123123C")
    pages.whatIsYourNationalInsuranceNumberPage.enterNationalInsuranceNumber(newNationalInsuranceNumber)
    pages.whatIsYourNationalInsuranceNumberPage.clickSubmit()
    //TODO pages.checkYourAnswersPage.assertPageIsDisplayed(
    //TODO tdAll.fullName,
    //TODO tdAll.dateOfBirthFormatted,
    //TODO newNationalInsuranceNumber
    //TODO ) withClue "user is navigated back to check your answers page"
  }

  "clicking submit redirects to 'Your identity has been confirmed' if response from NPS indicates identity verification is successful" in {
    IdentityVerificationStub.stubIdentityVerification2xxSucceeded
    upsertJourneyToDatabase(tdAll.BankTransfer.journeyEnteredDateOfBirth)
    pages.checkYourAnswersPage.open()
    pages.checkYourAnswersPage.assertPageIsDisplayed()
    pages.checkYourAnswersPage.clickSubmit()
    pages.weHaveConfirmedYourIdentityPage.assertPageIsDisplayed()
    IdentityVerificationStub.verifyIdentityVerification()
  }

  "clicking submit redirects to 'We cannot confirm your identity' if response from NPS indicates identity verification failed" in {
    IdentityVerificationStub.stubIdentityVerification2xxFailed
    pages.checkYourAnswersPage.open()
    pages.checkYourAnswersPage.assertPageIsDisplayed()
    pages.checkYourAnswersPage.clickSubmit()
    pages.weCannotConfirmYourIdentityPage.assertPageIsDisplayed()
    IdentityVerificationStub.verifyIdentityVerification()
  }

  "clicking back button navigates to What Is Your National Insurance Number page" in {
    pages.checkYourAnswersPage.open()
    pages.checkYourAnswersPage.assertPageIsDisplayed()
    pages.checkYourAnswersPage.clickBackButton()
    pages.whatIsYourNationalInsuranceNumberPage.assertPageIsDisplayed()
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    addJourneyIdToSession(tdAll.journeyId)
    upsertJourneyToDatabase(tdAll.BankTransfer.journeyEnteredDateOfBirth)
  }
}
