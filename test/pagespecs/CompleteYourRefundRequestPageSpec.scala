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
import nps.models.IssuePayableOrderRequest
import play.api.libs.json.Json
import testsupport.ItSpec
import testsupport.stubs.NpsIssuePayableOrderStub

class CompleteYourRefundRequestPageSpec extends ItSpec {

  override def beforeEach(): Unit = {
    val journey: Journey = tdAll.Cheque.AfterReferenceCheck.journeyReferenceChecked

    super.beforeEach()
    addJourneyIdToSession(journey.journeyId)
    upsertJourneyToDatabase(journey)
  }

  "render page" in {
    val journey: Journey = tdAll.Cheque.AfterReferenceCheck.journeyReferenceChecked

    pages.completeYourRefundRequestPage.open()
    pages.completeYourRefundRequestPage.assertPageIsDisplayed()
    getJourneyFromDatabase(journey.journeyId) shouldBeLike tdAll.Cheque.AfterReferenceCheck.journeyReferenceChecked
  }

  "clicking Submit refund request cta button redirects to cheque request received" in {
    pages.completeYourRefundRequestPage.open()
    pages.completeYourRefundRequestPage.assertPageIsDisplayed()
    val journey: Journey = tdAll.Cheque.AfterReferenceCheck.journeyReferenceChecked

    NpsIssuePayableOrderStub.issuePayableOrder(
      journey.nino.value,
      journey.p800Reference.value,
      IssuePayableOrderRequest(
        customerAccountNumber   = tdAll.p800ReferenceChecked.customerAccountNumber,
        associatedPayableNumber = tdAll.p800ReferenceChecked.associatedPayableNumber,
        currentOptimisticLock   = tdAll.p800ReferenceChecked.currentOptimisticLock
      ),
      Json.obj("whatever" -> "we don't use it")
    )
    pages.completeYourRefundRequestPage.clickSubmitRefundRequest()
    pages.requestReceivedChequePage.assertPageIsDisplayedForCheque()
    NpsIssuePayableOrderStub.verifyIssuePayableOrder(journey.nino.value, journey.p800Reference.value)
    getJourneyFromDatabase(journey.journeyId) shouldBeLike tdAll.Cheque.journeyClaimedOverpayment
  }

  "when API call fails we don't update the journey state" in {
    pages.completeYourRefundRequestPage.open()
    pages.completeYourRefundRequestPage.assertPageIsDisplayed()
    val journey: Journey = tdAll.Cheque.AfterReferenceCheck.journeyReferenceChecked

    NpsIssuePayableOrderStub.issuePayableOrderRefundAlreadyTaken(
      journey.nino.value,
      journey.p800Reference.value,
      IssuePayableOrderRequest(
        customerAccountNumber   = tdAll.p800ReferenceChecked.customerAccountNumber,
        associatedPayableNumber = tdAll.p800ReferenceChecked.associatedPayableNumber,
        currentOptimisticLock   = tdAll.p800ReferenceChecked.currentOptimisticLock
      )
    )
    pages.completeYourRefundRequestPage.clickSubmitRefundRequest()
    pages.completeYourRefundRequestPage.assertPageIsDisplayedWithTechnicalDifficultiesError()
    NpsIssuePayableOrderStub.verifyIssuePayableOrder(journey.nino.value, journey.p800Reference.value)
    getJourneyFromDatabase(journey.journeyId) shouldBeLike journey withClue "journey was not updated"
  }

}
