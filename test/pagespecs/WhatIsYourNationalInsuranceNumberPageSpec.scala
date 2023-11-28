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
import testdata.TdAll

class WhatIsYourNationalInsuranceNumberPageSpec extends ItSpec {

  override def beforeEach(): Unit = {
    super.beforeEach()

    addJourneyIdToSession(TdAll.journeyId)
    upsertJourneyToDatabase(TdAll.journeyWhatIsYourDateOfBirth)
  }

  "/what-is-your-national-insurance-number renders the what is your national insurance number page" in {
    pages.whatIsYourNationalInsuranceNumberPage.open()
    pages.whatIsYourNationalInsuranceNumberPage.assertPageIsDisplayed()
  }

  Seq(TdAll.nationalInsuranceNumber.value, "aa000000a", "AA000000A.", "MA 00 00 03 A").foreach { nino =>
    s"Entering a valid NINO ($nino) and clicking 'Continue' redirects user to 'Check your answers' page" in {
      pages.whatIsYourNationalInsuranceNumberPage.open()
      pages.whatIsYourNationalInsuranceNumberPage.assertPageIsDisplayed()
      pages.whatIsYourNationalInsuranceNumberPage.enterNationalInsuranceNumber(NationalInsuranceNumber(nino))
      pages.whatIsYourNationalInsuranceNumberPage.clickSubmit()
      pages.checkYourAnswersPage.assertPageIsDisplayed()
    }
  }

  "Clicking 'Continue' with empty text input shows error" in {
    pages.whatIsYourNationalInsuranceNumberPage.open()
    pages.whatIsYourNationalInsuranceNumberPage.assertPageIsDisplayed()
    pages.whatIsYourNationalInsuranceNumberPage.clickSubmit()
    pages.whatIsYourNationalInsuranceNumberPage.assertPageShowsErrorEmptyInput()
  }

  Seq("Not a NINO", "1234", "QQ 12 34 56 C", "MA0%0£03A").foreach { nino =>
    s"Clicking 'Continue' with invalid NINO ($nino) shows error" in {
      pages.whatIsYourNationalInsuranceNumberPage.open()
      pages.whatIsYourNationalInsuranceNumberPage.assertPageIsDisplayed()
      pages.whatIsYourNationalInsuranceNumberPage.enterNationalInsuranceNumber(NationalInsuranceNumber(nino))
      pages.whatIsYourNationalInsuranceNumberPage.clickSubmit()
      pages.whatIsYourNationalInsuranceNumberPage.assertPageShowsErrorInvalid()
    }
  }

  "Clicking 'Back' redirects to 'What is your Date of birth?' page" in {
    pages.whatIsYourNationalInsuranceNumberPage.open()
    pages.whatIsYourNationalInsuranceNumberPage.assertPageIsDisplayed()
    pages.whatIsYourNationalInsuranceNumberPage.clickBackButton()
    pages.whatIsYourDateOfBirthPage.assertPageIsDisplayed()
  }

  "Prepopulate the form if the user has already entered it" in {
    upsertJourneyToDatabase(TdAll.journeyWhatIsYourNationalInsuranceNumber)
    pages.whatIsYourNationalInsuranceNumberPage.open()
    pages.whatIsYourNationalInsuranceNumberPage.assertDataPrepopulated("MA000003A")
  }
}
