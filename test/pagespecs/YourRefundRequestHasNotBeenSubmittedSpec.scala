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

import models.p800externalapi.EventValue
import testsupport.ItSpec
import testsupport.stubs.{EcospendStub, EdhStub, MakeBacsRepaymentStub, P800RefundsExternalApiStub}

class YourRefundRequestHasNotBeenSubmittedSpec extends ItSpec {

  override def beforeEach(): Unit = {
    super.beforeEach()
    addJourneyIdToSession(tdAll.journeyId)
    upsertJourneyToDatabase(tdAll.BankTransfer.journeyBankAccountConsentSuccessfulNameMatch)
  }

  "navigating to /your-refund-request-has-not-been-submitted should render the page" in {
    pages.yourRefundRequestHasNotBeenSubmittedSpec.open()
    pages.yourRefundRequestHasNotBeenSubmittedSpec.assertPageIsDisplayed()
  }

  "when user clicks Try again link is redirected to We are verifying your bank account page" in {
    EcospendStub.AuthStubs.stubEcospendAuth2xxSucceeded
    EcospendStub.AccountStub.stubAccountSummary2xxSucceeded(tdAll.consentId)
    P800RefundsExternalApiStub.isValid(tdAll.consentId, EventValue.NotReceived)
    MakeBacsRepaymentStub.`makeBacsRepayment 200 OK`(
      nino     = tdAll.nino,
      request  = tdAll.claimOverpaymentRequest,
      response = tdAll.claimOverpaymentResponse
    )
    EdhStub.getBankDetailsRiskResult(tdAll.getBankDetailsRiskResultRequest, tdAll.getBankDetailsRiskResultResponse)

    pages.yourRefundRequestHasNotBeenSubmittedSpec.open()
    pages.yourRefundRequestHasNotBeenSubmittedSpec.assertPageIsDisplayed()
    pages.yourRefundRequestHasNotBeenSubmittedSpec.clickTryAgain()

    pages.verifyingBankAccountPage.assertPageIsDisplayed()
  }

  "when user clicks Choose another way to receive your refund link is redirected to relevant page" in {
    pages.yourRefundRequestHasNotBeenSubmittedSpec.open()
    pages.yourRefundRequestHasNotBeenSubmittedSpec.assertPageIsDisplayed()
    pages.yourRefundRequestHasNotBeenSubmittedSpec.clickChooseAnother()

    pages.chooseAnotherWayToReceiveYourRefundPage.assertPageIsDisplayed()
  }

  "when user clicks Return to P800 guidance link is redirected to gov.uk P800 guidance page" in {
    pages.yourRefundRequestHasNotBeenSubmittedSpec.open()
    pages.yourRefundRequestHasNotBeenSubmittedSpec.assertPageIsDisplayed()
    val expectedNextPageUrl = pages.yourRefundRequestHasNotBeenSubmittedSpec.getGuidanceUrl

    pages.yourRefundRequestHasNotBeenSubmittedSpec.clickRefundGuidance()

    webDriver.getCurrentUrl shouldBe expectedNextPageUrl
  }

  "when user clicks Back link is redirected to We are verifying your bank account page" in {
    EcospendStub.AuthStubs.stubEcospendAuth2xxSucceeded
    EcospendStub.AccountStub.stubAccountSummary2xxSucceeded(tdAll.consentId)
    P800RefundsExternalApiStub.isValid(tdAll.consentId, EventValue.NotReceived)
    MakeBacsRepaymentStub.`makeBacsRepayment 200 OK`(
      nino     = tdAll.nino,
      request  = tdAll.claimOverpaymentRequest,
      response = tdAll.claimOverpaymentResponse
    )
    EdhStub.getBankDetailsRiskResult(tdAll.getBankDetailsRiskResultRequest, tdAll.getBankDetailsRiskResultResponse)

    pages.yourRefundRequestHasNotBeenSubmittedSpec.open()
    pages.yourRefundRequestHasNotBeenSubmittedSpec.assertPageIsDisplayed()
    pages.yourRefundRequestHasNotBeenSubmittedSpec.clickBackButton()

    pages.verifyingBankAccountPage.assertPageIsDisplayed()
  }
}
