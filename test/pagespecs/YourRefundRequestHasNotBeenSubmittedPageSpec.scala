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

class YourRefundRequestHasNotBeenSubmittedPageSpec extends ItSpec {

  override def beforeEach(): Unit = {
    super.beforeEach()
    addJourneyIdToSession(tdAll.journeyId)
  }

  "render page" - {
    "bank transfer" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.journeyClaimOverpaymentFailed)
      pages.yourRequestHasNotBeenSubmittedPage.open()
      pages.yourRequestHasNotBeenSubmittedPage.assertPageIsDisplayed()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeyClaimOverpaymentFailed
    }
    "cheque" ignore {
      //no such case
    }
  }

  "Clicking 'Choose another way to you my money' redirects to 'Choose another way to receive your refund' page" - {
    "bank transfer" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.journeyClaimOverpaymentFailed)
      pages.yourRequestHasNotBeenSubmittedPage.open()
      pages.yourRequestHasNotBeenSubmittedPage.assertPageIsDisplayed()
      pages.yourRequestHasNotBeenSubmittedPage.clickTryAgain()
      pages.chooseAnotherWayToReceiveYourRefundPage.assertPageIsDisplayedPtaOrCheque()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeyClaimOverpaymentFailedButIsChoosingAnotherWay
    }
  }

  "Clicking browser back button should remain on 'Request not submitted' page" - {

    "bank transfer" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.journeyClaimOverpaymentFailed)
      pages.yourRequestHasNotBeenSubmittedPage.open()
      pages.yourRequestHasNotBeenSubmittedPage.assertPageIsDisplayed()
      //any page before final page is not displayed, the redirect to the final page is sent
      pages.checkYourAnswersPage.open()
      pages.yourRequestHasNotBeenSubmittedPage.assertPageIsDisplayed()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeyClaimOverpaymentFailed
    }
  }

}
