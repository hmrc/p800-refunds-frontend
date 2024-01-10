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

class ChooseAnotherWayToReceiveYourRefundPageSpec extends ItSpec {

  override def beforeEach(): Unit = {
    super.beforeEach()
    addJourneyIdToSession(tdAll.journeyId)
    //TODO    upsertJourneyToDatabase(tdAll.journeyIdentityNotVerified)
  }

  "/choose-another-option renders the choose another option page" in {
    pages.chooseAnotherWayToReceiveYourRefundPage.open()
    pages.chooseAnotherWayToReceiveYourRefundPage.assertPageIsDisplayed()
  }

  "Selecting 'Bank transfer via your personal tax account' option sends user to PTA /tax-you-paid" in {
    pages.chooseAnotherWayToReceiveYourRefundPage.open()
    pages.chooseAnotherWayToReceiveYourRefundPage.assertPageIsDisplayed()
    pages.chooseAnotherWayToReceiveYourRefundPage.clickBankTransferOption()
    pages.chooseAnotherWayToReceiveYourRefundPage.clickSubmit()
    //todo assert /tax-your-paid
  }
  "Selecting 'Cheque' option sends user to 'Your cheque will be posted to you' page" in {
    pages.chooseAnotherWayToReceiveYourRefundPage.open()
    pages.chooseAnotherWayToReceiveYourRefundPage.assertPageIsDisplayed()
    pages.chooseAnotherWayToReceiveYourRefundPage.clickChequeOption()
    pages.chooseAnotherWayToReceiveYourRefundPage.clickSubmit()
    pages.completeYourRefundRequestPage.assertPageIsDisplayed()
  }
  //todo un ignore this test when we know where the back button is supposed to go.
  "Clicking 'Back' sends user to 'What is the name of your bank account' page" ignore {
    pages.chooseAnotherWayToReceiveYourRefundPage.open()
    pages.chooseAnotherWayToReceiveYourRefundPage.assertPageIsDisplayed()
    pages.chooseAnotherWayToReceiveYourRefundPage.clickBackButton()
    pages.whatIsTheNameOfYourBankAccountPage.assertPageIsDisplayed()
  }

  "Submitting with no option selected shows error message" in {
    pages.chooseAnotherWayToReceiveYourRefundPage.open()
    pages.chooseAnotherWayToReceiveYourRefundPage.assertPageIsDisplayed()
    pages.chooseAnotherWayToReceiveYourRefundPage.clickSubmit()
    pages.chooseAnotherWayToReceiveYourRefundPage.assertPageShowsWithErrors()
  }
}
