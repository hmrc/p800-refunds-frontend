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

class EnterP800ReferencePageSpec extends ItSpec {

  override def beforeEach(): Unit = {
    super.beforeEach()

    addJourneyIdToSession(TdAll.journeyId)
    addJourneyToDatabase(TdAll.journeyDoYouWantToSignInNo)
  }

  "Entering valid p800 reference and clicking Continue redirects to /check-your-reference" in {
    pages.enterP800ReferencePage.open()
    pages.enterP800ReferencePage.assertPageIsDisplayed()
    pages.enterP800ReferencePage.enterP800Reference("VALIDP800REF")
    pages.enterP800ReferencePage.clickSubmit()
    pages.checkYourReferencePage.assertPageIsDisplayed()
  }

  "Clicking Continue with empty text input shows error" in {
    pages.enterP800ReferencePage.open()
    pages.enterP800ReferencePage.assertPageIsDisplayed()
    pages.enterP800ReferencePage.clickSubmit()
    pages.enterP800ReferencePage.assertPageShowsErrorRequired()
  }

  "Clicking Continue with invalid reference shows error" in {
    pages.enterP800ReferencePage.open()
    pages.enterP800ReferencePage.assertPageIsDisplayed()
    pages.enterP800ReferencePage.enterP800Reference("this is a really long and invalid reference")
    pages.enterP800ReferencePage.clickSubmit()
    pages.enterP800ReferencePage.assertPageShowsErrorReferenceFormat()
  }

  "Clicking 'Sign in or create a personal tax account' link opens correctly" in {
    pages.enterP800ReferencePage.open()
    pages.enterP800ReferencePage.assertPageIsDisplayed()
    pages.enterP800ReferencePage.clickPtaSignInLink()
    pages.ptaSignInPage.assertPageIsDisplayed()
  }

  "Clicking 'Call or write to the income tax helpline' link opens correctly" in {
    pages.enterP800ReferencePage.open()
    pages.enterP800ReferencePage.assertPageIsDisplayed()
    val newTabHandle = pages.enterP800ReferencePage.clickIncomeTaxGeneralEnquiriesLink()
    switchToTab(newTabHandle)
    pages.generalIncomeTaxEnquiriesPage.assertPageIsDisplayed()
  }

  "Clicking on back button redirects back to 'Do you want to sign in?' page" in {
    pages.enterP800ReferencePage.open()
    pages.enterP800ReferencePage.assertPageIsDisplayed()
    pages.enterP800ReferencePage.clickBackButton()
    pages.doYouWantToSignInPage.assertPageIsDisplayed()
  }
}
