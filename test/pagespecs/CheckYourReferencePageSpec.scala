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

import testsupport.ItSpec
import testsupport.stubs.ReferenceValidationConnectorStub

class CheckYourReferencePageSpec extends ItSpec {

  override def beforeEach(): Unit = {
    super.beforeEach()
    addJourneyIdToSession(tdAll.journeyId)
    upsertJourneyToDatabase(tdAll.journeyWhatIsYourP800Reference)
  }

  "Selecting 'Yes' with a valid reference redirects to 'Do you want your refund via bank transfer?' page" in {
    ReferenceValidationConnectorStub.validateReference2xxValid

    pages.checkYourReferencePage.open()
    pages.checkYourReferencePage.assertPageIsDisplayed()
    pages.checkYourReferencePage.selectYes()
    pages.checkYourReferencePage.clickSubmit()
    pages.doYouWantYourRefundViaBankTransferPage.assertPageIsDisplayed()
  }

  "Selecting 'Yes' with an invalid reference redirects to 'We cannot confirm your reference' page" in {
    ReferenceValidationConnectorStub.validateReference2xxInvalid

    pages.checkYourReferencePage.open()
    pages.checkYourReferencePage.assertPageIsDisplayed()
    pages.checkYourReferencePage.selectYes()
    pages.checkYourReferencePage.clickSubmit()
    pages.cannotConfirmReferencePage.assertPageIsDisplayed()
  }

  "Selecting 'No, I need to change it' redirects to 'What is your P800 Reference' page" in {
    pages.checkYourReferencePage.open()
    pages.checkYourReferencePage.assertPageIsDisplayed()
    pages.checkYourReferencePage.selectNo()
    pages.checkYourReferencePage.clickSubmit()
    pages.enterP800ReferencePage.assertPageIsDisplayed()
  }

  "Clicking 'Back' redirects back to 'What is your P800 Reference' page" in {
    pages.checkYourReferencePage.open()
    pages.checkYourReferencePage.assertPageIsDisplayed()
    pages.checkYourReferencePage.clickBackButton()
    pages.enterP800ReferencePage.assertPageIsDisplayed()
  }

  "Selecting nothing and clicking continue shows error" in {
    pages.checkYourReferencePage.open()
    pages.checkYourReferencePage.assertPageIsDisplayed()
    pages.checkYourReferencePage.clickSubmit()
    pages.checkYourReferencePage.assertPageShowsWithErrors()
  }

}
