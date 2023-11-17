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
import org.scalatest.prop.TableDrivenPropertyChecks._
import pagespecs.pagesupport.Page
import testdata.TdAll
import testsupport.ItSpec

class WeNeedYouToConfirmYourIdentityPageSpec extends ItSpec {

  override def beforeEach(): Unit = {
    super.beforeEach()
    addJourneyIdToSession(TdAll.journeyId)
    upsertJourneyToDatabase(TdAll.journeyDoYouWantYourRefundViaBankTransferYes)
  }

  "/confirm-your-identity renders the we need to confirm your identity page" in {
    pages.weNeedYouToConfirmYourIdentityPage.open()
    pages.weNeedYouToConfirmYourIdentityPage.assertPageIsDisplayed()
  }

  "'Continue' button sends user to 'What is your full name' page" in {
    pages.weNeedYouToConfirmYourIdentityPage.open()
    pages.weNeedYouToConfirmYourIdentityPage.assertPageIsDisplayed()
    pages.weNeedYouToConfirmYourIdentityPage.clickSubmit()
    pages.whatIsYourFullNamePage.assertPageIsDisplayed()
  }

  "'Back' button sends user to 'Do you want your refund by bank transfer' page" in {
    pages.weNeedYouToConfirmYourIdentityPage.open()
    pages.weNeedYouToConfirmYourIdentityPage.assertPageIsDisplayed()
    pages.weNeedYouToConfirmYourIdentityPage.clickBackButton()
    pages.doYouWantYourRefundViaBankTransferPage.assertPageIsDisplayed()
  }

  forAll(Table(
    ("journeyState", "expectedPage"),
    (TdAll.journeyDoYouWantYourRefundViaBankTransferNo, pages.yourChequeWillBePostedToYouPage),
    (TdAll.journeyStarted, pages.doYouWantToSignInPage),
    (TdAll.journeyDoYouWantToSignInNo, pages.enterP800ReferencePage),
    (TdAll.journeyWhatIsYourP800Reference, pages.checkYourReferencePage),
    (TdAll.journeyCheckYourReferenceValid, pages.doYouWantYourRefundViaBankTransferPage),
    (TdAll.journeyYourChequeWillBePostedToYou, pages.chequeRequestReceivedPage),
    //todo jake add another scenario in for JAfterDoYouWantYourRefundViaBankTransferYes when we have it
  )) { (journeyState: Journey, expectedPage: Page) =>
    s"JourneyState: [${journeyState.name}] should redirect accordingly when state is before this page" in {
      upsertJourneyToDatabase(journeyState)
      pages.weNeedYouToConfirmYourIdentityPage.open()
      expectedPage.assertPageIsDisplayed()
    }
  }

}