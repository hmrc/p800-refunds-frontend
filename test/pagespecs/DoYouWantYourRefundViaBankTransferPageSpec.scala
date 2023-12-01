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
    upsertJourneyToDatabase(tdAll.journeyCheckYourReferenceValid)
  }

  "Selecting 'Yes' redirects to ConfirmYourIdentityPage" in {
    pages.doYouWantYourRefundViaBankTransferPage.open()
    pages.doYouWantYourRefundViaBankTransferPage.assertPageIsDisplayed()
    pages.doYouWantYourRefundViaBankTransferPage.selectYes()
    pages.doYouWantToSignInPage.clickSubmit()
    pages.weNeedYouToConfirmYourIdentityPage.assertPageIsDisplayed()
  }

  "Selecting 'No, continue without signing in' redirects to 'What is your P800 Reference' page" in {
    pages.doYouWantYourRefundViaBankTransferPage.open()
    pages.doYouWantYourRefundViaBankTransferPage.assertPageIsDisplayed()
    pages.doYouWantYourRefundViaBankTransferPage.selectNo()
    pages.doYouWantToSignInPage.clickSubmit()
    pages.yourChequeWillBePostedToYouPage.assertPageIsDisplayed()
  }

  //TODO: Pawel
  "Clicking 'Back' redirects back to start page" ignore {
    pages.doYouWantYourRefundViaBankTransferPage.open()
    pages.doYouWantYourRefundViaBankTransferPage.assertPageIsDisplayed()
    pages.doYouWantYourRefundViaBankTransferPage.clickBackButton()
    pages.checkYourReferencePage.assertPageIsDisplayed()
  }

  "Selecting nothing and clicking continue shows error" in {
    pages.doYouWantYourRefundViaBankTransferPage.open()
    pages.doYouWantYourRefundViaBankTransferPage.assertPageIsDisplayed()
    pages.doYouWantYourRefundViaBankTransferPage.clickSubmit()
    pages.doYouWantYourRefundViaBankTransferPage.assertPageShowsWithErrors()
  }

  "clicking service name navigates to the gov-uk route in page" in {
    pages.doYouWantYourRefundViaBankTransferPage.open()
    pages.doYouWantYourRefundViaBankTransferPage.assertPageIsDisplayed()
    pages.doYouWantYourRefundViaBankTransferPage.clickServiceName()
    pages.govUkRouteInPage.assertPageIsDisplayed()
  }
}
