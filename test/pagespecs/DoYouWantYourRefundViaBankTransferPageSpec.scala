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

class DoYouWantYourRefundViaBankTransferPageSpec extends ItSpec {

  override def beforeEach(): Unit = {
    super.beforeEach()
    addJourneyIdToSession(tdAll.journeyId)
    upsertJourneyToDatabase(tdAll.journeyStarted)
  }

  "Selecting 'Yes' redirects to WeNeedYouToConfirmYourIdentityPage" in {
    pages.doYouWantYourRefundViaBankTransferPage.open()
    pages.doYouWantYourRefundViaBankTransferPage.assertPageIsDisplayed()
    pages.doYouWantYourRefundViaBankTransferPage.selectYes()
    pages.doYouWantYourRefundViaBankTransferPage.clickSubmit()
    pages.weNeedYouToConfirmYourIdentityBankTransferPage.assertPageIsDisplayedForBankTransferJourney()
    getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeySelectedType withClue "JourneyType becomes BankTransfer"
  }

  "Selecting 'No, also redirects to WeNeedYouToConfirmYourIdentityPage LOL" in {
    pages.doYouWantYourRefundViaBankTransferPage.open()
    pages.doYouWantYourRefundViaBankTransferPage.assertPageIsDisplayed()
    pages.doYouWantYourRefundViaBankTransferPage.selectNo()
    pages.doYouWantYourRefundViaBankTransferPage.clickSubmit()
    pages.weNeedYouToConfirmYourIdentityChequePage.assertPageIsDisplayedForChequeJourney()
    getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.Cheque.journeySelectedType withClue "however the journey type becomes Cheque"
  }

  "Selecting nothing and clicking continue shows error" in {
    pages.doYouWantYourRefundViaBankTransferPage.open()
    pages.doYouWantYourRefundViaBankTransferPage.assertPageIsDisplayed()
    pages.doYouWantYourRefundViaBankTransferPage.clickSubmit()
    pages.doYouWantYourRefundViaBankTransferPage.assertPageShowsWithErrors()
    getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.journeyStarted
  }
}
