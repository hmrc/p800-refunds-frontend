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

class TimeoutSpec extends ItSpec {

  override def beforeEach(): Unit = {
    super.beforeEach()
    addJourneyIdToSession(tdAll.journeyId)
    upsertJourneyToDatabase(tdAll.BankTransfer.journeyBankAccountConsentSuccessfulNameMatch)
  }

  "navigating to /timeout should render the Timeout page when" - {
    "the user chooses to delete their data" in {
      val timeoutPage = pages.timeoutPage(true)
      timeoutPage.open()
      timeoutPage.assertPageIsDisplayed()
    }
    "the page naturally times out" in {
      val timeoutPage = pages.timeoutPage(false)
      timeoutPage.open()
      timeoutPage.assertPageIsDisplayed()
    }
  }
}
