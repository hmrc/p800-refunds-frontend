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
import testsupport.stubs.EcospendStub

class GiveYourConsentPageSpec extends ItSpec {

  override def beforeEach(): Unit = {
    super.beforeEach()
    addJourneyIdToSession(tdAll.journeyId)
    //TODO    upsertJourneyToDatabase(tdAll.journeyWhatIsTheNameOfYourBankAccount)
  }

  "/give-your-consent renders the give your consent page" in {
    pages.giveYourConsentPage.open()
    pages.giveYourConsentPage.assertPageIsDisplayed()
  }

  //todo ticket says url will be determined in another ticket - I think this is right though.
  "clicking 'Change my bank' redirects to 'What is the name of your bank' page" in {
    EcospendStub.stubEcospendAuth2xxSucceeded
    EcospendStub.stubEcospendGetBanks2xx
    pages.giveYourConsentPage.open()
    pages.giveYourConsentPage.assertPageIsDisplayed()
    pages.giveYourConsentPage.clickChangeBank()
    pages.whatIsTheNameOfYourBankAccountPage.assertPageIsDisplayed()
  }

  "clicking 'Approve this refund' redirects to 'Verifying bank account'" in {
    EcospendStub.stubEcospendAuth2xxSucceeded
    EcospendStub.ValidateStubs.stubValidateNotValidatedYet
    pages.giveYourConsentPage.open()
    pages.giveYourConsentPage.assertPageIsDisplayed()
    pages.giveYourConsentPage.clickApproveThisRefund()
    pages.verifyBankAccountPage.assertPageIsDisplayed()
  }

  "clicking 'Choose another way to get my money' redirects to 'Choose another way to get my refund' page" in {
    pages.giveYourConsentPage.open()
    pages.giveYourConsentPage.assertPageIsDisplayed()
    pages.giveYourConsentPage.clickChooseAnotherWayToGetMyMoney()
    pages.chooseAnotherWayToReceiveYourRefundPage.assertPageIsDisplayed()
  }

  "clicking 'Back' redirects user to 'Select a bank account' page" in {
    EcospendStub.stubEcospendAuth2xxSucceeded
    EcospendStub.stubEcospendGetBanks2xx
    pages.giveYourConsentPage.open()
    pages.giveYourConsentPage.assertPageIsDisplayed()
    pages.giveYourConsentPage.clickBackButton()
    pages.whatIsTheNameOfYourBankAccountPage.assertPageIsDisplayed()
  }
}
