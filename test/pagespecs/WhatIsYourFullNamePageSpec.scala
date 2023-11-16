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

import testdata.TdAll
import testsupport.ItSpec

class WhatIsYourFullNamePageSpec extends ItSpec {

  override def beforeEach(): Unit = {
    super.beforeEach()

    addJourneyIdToSession(TdAll.journeyId)
    upsertJourneyToDatabase(TdAll.journeyDoYouWantYourRefundViaBankTransferYes)
  }

  "/what-is-your-full-name renders the 'What is your full name?' page" in {
    pages.whatIsYourFullNamePage.open()
    pages.whatIsYourFullNamePage.assertPageIsDisplayed()
  }

  "Entering a valid full name and clicking 'Continue' redirects to 'What is your date of birth?' page" in {
    pages.whatIsYourFullNamePage.open()
    pages.whatIsYourFullNamePage.assertPageIsDisplayed()
    pages.whatIsYourFullNamePage.enterFullName(TdAll.fullName.value)
    pages.whatIsYourFullNamePage.clickSubmit()
    pages.whatIsYourDateOfBirthPage.assertPageIsDisplayed()
  }

  "Clicking 'Continue' with empty text input shows error" in {
    pages.whatIsYourFullNamePage.open()
    pages.whatIsYourFullNamePage.assertPageIsDisplayed()
    pages.whatIsYourFullNamePage.clickSubmit()
    pages.whatIsYourFullNamePage.assertPageShowsErrorTooShort()
  }

  "Clicking 'Continue' with a full name shorter than 2 characters shows error" in {
    pages.whatIsYourFullNamePage.open()
    pages.whatIsYourFullNamePage.assertPageIsDisplayed()
    pages.whatIsYourFullNamePage.enterFullName("A")
    pages.whatIsYourFullNamePage.clickSubmit()
    pages.whatIsYourFullNamePage.assertPageShowsErrorTooShort()
  }

  "Clicking 'Continue' with a full name 2 exactly characters redirects to 'What is your date of birth?' page" in {
    pages.whatIsYourFullNamePage.open()
    pages.whatIsYourFullNamePage.assertPageIsDisplayed()
    pages.whatIsYourFullNamePage.enterFullName("AB")
    pages.whatIsYourFullNamePage.clickSubmit()
    pages.whatIsYourDateOfBirthPage.assertPageIsDisplayed()
  }

  "Clicking 'Continue' with an full name longer than 160 characters shows error" in {
    pages.whatIsYourFullNamePage.open()
    pages.whatIsYourFullNamePage.assertPageIsDisplayed()
    pages.whatIsYourFullNamePage.enterFullName("A" * 161)
    pages.whatIsYourFullNamePage.clickSubmit()
    pages.whatIsYourFullNamePage.assertPageShowsErrorTooLong()
  }

  "Clicking 'Continue' with a full name 160 exactly characters redirects to 'What is your date of birth?' page" in {
    pages.whatIsYourFullNamePage.open()
    pages.whatIsYourFullNamePage.assertPageIsDisplayed()
    pages.whatIsYourFullNamePage.enterFullName("A" * 160)
    pages.whatIsYourFullNamePage.clickSubmit()
    pages.whatIsYourDateOfBirthPage.assertPageIsDisplayed()
  }

  "Clicking 'Continue' with a full name with 1 invalid character shows error" in {
    pages.whatIsYourFullNamePage.open()
    pages.whatIsYourFullNamePage.assertPageIsDisplayed()
    pages.whatIsYourFullNamePage.enterFullName("Paweł")
    pages.whatIsYourFullNamePage.clickSubmit()
    pages.whatIsYourFullNamePage.assertPageShowsInvalidCharacterError("ł")
  }

  "Clicking 'Continue' with a full name with 1 invalid symbol character shows error" in {
    pages.whatIsYourFullNamePage.open()
    pages.whatIsYourFullNamePage.assertPageIsDisplayed()
    pages.whatIsYourFullNamePage.enterFullName("Nam£")
    pages.whatIsYourFullNamePage.clickSubmit()
    pages.whatIsYourFullNamePage.assertPageShowsInvalidCharacterError("£")
  }

  "Clicking 'Continue' with a full name with 2 invalid characters shows error" in {
    pages.whatIsYourFullNamePage.open()
    pages.whatIsYourFullNamePage.assertPageIsDisplayed()
    pages.whatIsYourFullNamePage.enterFullName("N%am£")
    pages.whatIsYourFullNamePage.clickSubmit()
    pages.whatIsYourFullNamePage.assertPageShowsInvalidCharacterError("% or £")
  }

  "Clicking 'Continue' with a full name with many invalid characters shows error" in {
    pages.whatIsYourFullNamePage.open()
    pages.whatIsYourFullNamePage.assertPageIsDisplayed()
    pages.whatIsYourFullNamePage.enterFullName("N%am£!@#")
    pages.whatIsYourFullNamePage.clickSubmit()
    pages.whatIsYourFullNamePage.assertPageShowsTooManyInvalidCharacterError()
  }

  "Clicking 'Continue' with a full name with many numeric characters shows error" in {
    pages.whatIsYourFullNamePage.open()
    pages.whatIsYourFullNamePage.assertPageIsDisplayed()
    pages.whatIsYourFullNamePage.enterFullName("12345")
    pages.whatIsYourFullNamePage.clickSubmit()
    pages.whatIsYourFullNamePage.assertPageShowsTooManyInvalidCharacterError()
  }

  "Clicking 'Back' redirects to 'We need you to confirm your identity' page" in {
    pages.whatIsYourFullNamePage.open()
    pages.whatIsYourFullNamePage.assertPageIsDisplayed()
    pages.whatIsYourFullNamePage.clickBackButton()
    pages.weNeedYouToConfirmYourIdentityPage.assertPageIsDisplayed()
  }
}
