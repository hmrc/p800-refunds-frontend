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

class WeHaveConfirmedYourIdentityPageSpec extends ItSpec {

  "page renders correctly" in {
    pages.weHaveConfirmedYourIdentityPage.open()
    pages.weHaveConfirmedYourIdentityPage.assertPageIsDisplayed()
  }

  "clicking submit navigates to What Is The Name Of Your Bank Account page" in {
    pages.weHaveConfirmedYourIdentityPage.open()
    pages.weHaveConfirmedYourIdentityPage.assertPageIsDisplayed()
    pages.weHaveConfirmedYourIdentityPage.clickSubmit()
    pages.whatIsTheNameOfYourBankAccountPage.assertPageIsDisplayed()
  }

  "clicking back button navigates to Check Your Answers page" in {
    pages.weHaveConfirmedYourIdentityPage.open()
    pages.weHaveConfirmedYourIdentityPage.assertPageIsDisplayed()
    pages.weHaveConfirmedYourIdentityPage.clickBackButton()
    pages.checkYourAnswersPage.assertPageIsDisplayed()
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    addJourneyIdToSession(td.journeyId)
    upsertJourneyToDatabase(td.journeyCheckYourAnswers)
  }

  private lazy val td: TdAll = new TdAll {}

}
