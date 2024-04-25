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
import testsupport.ItSpec
import testsupport.stubs.NpsIssuePayableOrderStub

class IsYourAddressUpToDatePageSpec extends ItSpec {

  override def beforeEach(): Unit = {
    super.beforeEach()
    addJourneyIdToSession(tdAll.journeyId)
    upsertJourneyToDatabase(tdAll.Cheque.AfterReferenceCheck.journeyReferenceChecked)
  }

  val journey: Journey = tdAll.Cheque.AfterReferenceCheck.journeyReferenceChecked

  "/cheque/is-your-address-up-to-date" - {

    "render 'Is your address up to date' page" in {
      pages.isYourAddressUpToDate.open()
      pages.isYourAddressUpToDate.assertPageIsDisplayed()
    }

    "selecting 'Yes' and submitting redirects to" - {
      "'Cheque Request received' page when API calls succeed" in {
        NpsIssuePayableOrderStub.`issuePayableOrder 200`(
          journey.nino.value,
          tdAll.p800Reference,
          IssuePayableOrderRequest(
            customerAccountNumber   = tdAll.p800ReferenceChecked.customerAccountNumber,
            associatedPayableNumber = tdAll.p800ReferenceChecked.associatedPayableNumber,
            currentOptimisticLock   = tdAll.p800ReferenceChecked.currentOptimisticLock
          )
        )

        pages.isYourAddressUpToDate.open()
        pages.isYourAddressUpToDate.assertPageIsDisplayed()
        pages.isYourAddressUpToDate.selectYes()
        pages.isYourAddressUpToDate.clickSubmit()

        pages.requestReceivedChequePage.assertPageIsDisplayedForCheque()
        NpsIssuePayableOrderStub.verify(journey.nino.value, tdAll.p800Reference, tdAll.correlationId)
        getJourneyFromDatabase(journey.journeyId) shouldBeLike tdAll.Cheque.journeyClaimedOverpayment
      }

      "'Technical difficulties' when API call fails we don't update the journey state" in {
        NpsIssuePayableOrderStub.`issuePayableOrder 5xx refundAlreadyTaken`(
          journey.nino.value,
          tdAll.p800Reference,
          IssuePayableOrderRequest(
            customerAccountNumber   = tdAll.p800ReferenceChecked.customerAccountNumber,
            associatedPayableNumber = tdAll.p800ReferenceChecked.associatedPayableNumber,
            currentOptimisticLock   = tdAll.p800ReferenceChecked.currentOptimisticLock
          )
        )

        pages.isYourAddressUpToDate.open()
        pages.isYourAddressUpToDate.assertPageIsDisplayed()
        pages.isYourAddressUpToDate.selectYes()
        pages.isYourAddressUpToDate.clickSubmit()

        pages.isYourAddressUpToDate.assertPageIsDisplayedWithTechnicalDifficultiesError()
        NpsIssuePayableOrderStub.verify(journey.nino.value, tdAll.p800Reference, tdAll.correlationId)
        getJourneyFromDatabase(journey.journeyId) shouldBeLike journey withClue "journey was not updated"
      }
    }

    "selecting 'No, I need to update it' and submitting redirects to 'Update your address' page" in {
      pages.isYourAddressUpToDate.open()
      pages.isYourAddressUpToDate.assertPageIsDisplayed()
      pages.isYourAddressUpToDate.selectNo()
      pages.isYourAddressUpToDate.clickSubmit()

      pages.updateYourAddressPage.assertPageIsDisplayed()
      NpsIssuePayableOrderStub.verifyNone(journey.nino.value, journey.p800Reference.value.sanitiseReference)
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.Cheque.AfterReferenceCheck.journeyReferenceChecked
    }

    "submitting without selected an option shows an error message" in {
      pages.isYourAddressUpToDate.open()
      pages.isYourAddressUpToDate.assertPageIsDisplayed()
      pages.isYourAddressUpToDate.clickSubmit()
      pages.isYourAddressUpToDate.assertPageIsDisplayedWithError()

      NpsIssuePayableOrderStub.verifyNone(journey.nino.value, journey.p800Reference.value.sanitiseReference)
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.Cheque.AfterReferenceCheck.journeyReferenceChecked
    }

  }

}
