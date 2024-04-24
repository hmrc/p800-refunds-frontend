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

import models.journeymodels.Journey
import testsupport.ItSpec
import testsupport.stubs.VerifyP800ReferenceStub

class ThereIsAProblemSpec extends ItSpec {

  override def beforeEach(): Unit = {
    super.beforeEach()
    addJourneyIdToSession(tdAll.journeyId)
  }

  "navigating to /there-is-a-problem should render the 'There is a problem' page correctly for" - {
    "bank transfer" in {
      val journey = tdAll.BankTransfer.AfterReferenceCheck.journeyRefundAlreadyTaken
      upsertJourneyToDatabase(journey)
      pages.thereIsAProblemPage.open()
      pages.thereIsAProblemPage.assertPageIsDisplayed()
    }
    "cheque" in {
      val journey = tdAll.Cheque.AfterReferenceCheck.journeyRefundAlreadyTaken
      upsertJourneyToDatabase(journey)
      pages.thereIsAProblemPage.open()
      pages.thereIsAProblemPage.assertPageIsDisplayed()
    }
  }

  "If locked out the user should not be able to click back - instead be redirected to the 'There is a problem' page" in {
    val j: Journey = tdAll.BankTransfer.journeyEnteredDateOfBirth
    VerifyP800ReferenceStub.refundAlreadyTaken(j.nino.value, tdAll.p800ReferenceSanitised)
    upsertJourneyToDatabase(j)
    pages.checkYourAnswersBankTransferPage.open()
    pages.checkYourAnswersBankTransferPage.clickSubmit()
    pages.thereIsAProblemPage.assertPageIsDisplayed()
    pages.thereIsAProblemPage.clickBackButtonInBrowser()
    pages.thereIsAProblemPage.assertPageIsDisplayed()
  }
}
