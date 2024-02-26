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

class CompleteYourRefundRequestPageSpec extends ItSpec {

  override def beforeEach(): Unit = {
    super.beforeEach()
    addJourneyIdToSession(tdAll.journeyId)
    upsertJourneyToDatabase(tdAll.Cheque.AfterReferenceCheck.journeyReferenceChecked)
  }

  "render page" in {
    pages.completeYourRefundRequestPage.open()
    pages.completeYourRefundRequestPage.assertPageIsDisplayed()
    getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.Cheque.AfterReferenceCheck.journeyReferenceChecked
  }

  //TODO unignore when api call added and we update the status to hasFinished.Yes
  "clicking Submit refund request cta button redirects to cheque request received" ignore {
    pages.completeYourRefundRequestPage.open()
    pages.completeYourRefundRequestPage.assertPageIsDisplayed()
    pages.completeYourRefundRequestPage.clickSubmitRefundRequest()
    // will there be an api call made at this point? todo add wiremock check for that call, if not remove this comment.
    pages.requestReceivedChequePage.assertPageIsDisplayedForCheque()
    getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.Cheque.journeyClaimedOverpayment
  }

}
