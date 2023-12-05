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

import models.{FullName, NationalInsuranceNumber}
import testsupport.ItSpec
import testsupport.stubs.IdentityVerificationStub

class CheckYourAnswersSpec extends ItSpec {

  "page renders correctly" in {
    pages.checkYourAnswersPage.open()
    pages.checkYourAnswersPage.assertPageIsDisplayed(
      tdAll.fullName,
      tdAll.dateOfBirthFormatted,
      tdAll.nationalInsuranceNumber
    )
  }

  "changing name" in {
    pages.checkYourAnswersPage.open()
    pages.checkYourAnswersPage.assertPageIsDisplayed()
    pages.checkYourAnswersPage.clickChangeName()
    pages.whatIsYourFullNamePage.assertPageIsDisplayed()
    val newName: FullName = FullName("NewName")
    pages.whatIsYourFullNamePage.enterFullName(newName)
    pages.whatIsYourFullNamePage.clickSubmit()
    pages.checkYourAnswersPage.assertPageIsDisplayed(
      newName,
      tdAll.dateOfBirthFormatted,
      tdAll.nationalInsuranceNumber
    ) withClue "user is navigated back to check your answers page"
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
    pages.checkYourAnswersPage.assertPageIsDisplayed(
      tdAll.fullName,
      "11 December 1971",
      tdAll.nationalInsuranceNumber
    ) withClue "user is navigated back to check your answers page"
  }

  "changing national insurance number" in {
    pages.checkYourAnswersPage.open()
    pages.checkYourAnswersPage.assertPageIsDisplayed()
    pages.checkYourAnswersPage.clickChangeNationalInsuranceNumber()
    pages.whatIsYourNationalInsuranceNumberPage.assertPageIsDisplayed()
    val newNationalInsuranceNumber = NationalInsuranceNumber("AB123123C")
    pages.whatIsYourNationalInsuranceNumberPage.enterNationalInsuranceNumber(newNationalInsuranceNumber)
    pages.whatIsYourNationalInsuranceNumberPage.clickSubmit()
    pages.checkYourAnswersPage.assertPageIsDisplayed(
      tdAll.fullName,
      tdAll.dateOfBirthFormatted,
      newNationalInsuranceNumber
    ) withClue "user is navigated back to check your answers page"
  }

  "clicking submit redirects to 'Your identity has been confirmed' if response from NPS indicates identity verification is successful" in {
    IdentityVerificationStub.stubIdentityVerification2xxSucceeded
    upsertJourneyToDatabase(tdAll.journeyCheckYourAnswers)
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
    upsertJourneyToDatabase(tdAll.journeyWhatIsYourNationalInsuranceNumber)
  }
}
