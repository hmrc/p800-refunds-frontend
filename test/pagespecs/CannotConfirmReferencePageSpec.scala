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

class CannotConfirmReferencePageSpec extends ItSpec {

  "/we-cannot-confirm-your-reference renders page correctly" in {
    pages.cannotConfirmReferencePage.open()
    pages.cannotConfirmReferencePage.assertPageIsDisplayed()
  }

  "clicking 'Try again' should redirect to /enter-P800-reference" in {
    addJourneyIdToSession(TdAll.journeyId)
    upsertJourneyToDatabase(TdAll.journeyWhatIsYourP800Reference)
    pages.cannotConfirmReferencePage.open()
    pages.cannotConfirmReferencePage.clickTryAgain()
    pages.enterP800ReferencePage.assertPageIsDisplayed()
  }

  "clicking 'Back' should redirect to /check-your-reference" in {
    addJourneyIdToSession(TdAll.journeyId)
    upsertJourneyToDatabase(TdAll.journeyWhatIsYourP800Reference)
    pages.cannotConfirmReferencePage.open()
    pages.cannotConfirmReferencePage.clickBackButton()
    pages.checkYourReferencePage.assertPageIsDisplayed()
  }

  "clicking service name navigates to the gov-uk route in page" in {
    pages.cannotConfirmReferencePage.open()
    pages.cannotConfirmReferencePage.assertPageIsDisplayed()
    pages.cannotConfirmReferencePage.clickServiceName()
    pages.govUkRouteInPage.assertPageIsDisplayed()
  }
}
