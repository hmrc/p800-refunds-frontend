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

class YouCannotConfirmYourIdentityYetSpec extends ItSpec {

  override def beforeEach(): Unit = {
    super.beforeEach()
    clearDownFailedAttemptDatabase()
    addJourneyIdToSession(tdAll.journeyId)
    upsertJourneyToDatabase(tdAll.Cheque.journeyLockedOutFromFailedAttempts)
  }

  "navigating to /you-cannot-confirm-your-security-details-yet" in {
    upsertFailedAttemptToDatabase(tdAll.attemptInfo(failedAttempts = 3))
    pages.youCannotConfirmYourIdentityYetSpec.open()
    pages.youCannotConfirmYourIdentityYetSpec.assertPageIsDisplayed()
    getFailedAttemptCount() shouldBe Some(3)
  }

  "navigating to /you-cannot-confirm-your-security-details-yet and Pressing the back button, should stay on the same page" in {
    upsertFailedAttemptToDatabase(tdAll.attemptInfo(failedAttempts = 3))
    pages.youCannotConfirmYourIdentityYetSpec.open()
    pages.youCannotConfirmYourIdentityYetSpec.assertPageIsDisplayed()
    pages.youCannotConfirmYourIdentityYetSpec.clickBackButton()
    pages.youCannotConfirmYourIdentityYetSpec.assertPageIsDisplayed()
  }

  "navigating to /you-cannot-confirm-your-security-details-yet without any AttemptInfo should display the Error Page" in {
    pages.youCannotConfirmYourIdentityYetSpec.open()
    getJourneyFromDatabase(tdAll.journeyId) shouldBe tdAll.Cheque.journeyLockedOutFromFailedAttempts
    pages.youCannotConfirmYourIdentityYetSpec.assertPageIsDisplayedWithTechnicalDifficultiesError()
  }

}
