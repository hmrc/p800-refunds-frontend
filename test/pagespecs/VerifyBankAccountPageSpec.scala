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
import testsupport.stubs.{EcospendStub, P800RefundsExternalApiStub}

class VerifyBankAccountPageSpec extends ItSpec {

  override def beforeEach(): Unit = {
    super.beforeEach()
    addJourneyIdToSession(tdAll.journeyId)
    upsertJourneyToDatabase(tdAll.BankTransfer.journeyPermissionGiven)
  }

  "/verifying-bank-account renders the 'We are verifying your bank account' page" in {
    EcospendStub.AuthStubs.stubEcospendAuth2xxSucceeded
    EcospendStub.AccountStub.stubAccountSummary2xxSucceeded(tdAll.consentId)
    P800RefundsExternalApiStub.isValid(tdAll.consentId, EventValue.NotReceived)

    pages.verifyBankAccountPage.open()
    pages.verifyBankAccountPage.assertPageIsDisplayed()
    P800RefundsExternalApiStub.verifyIsValid(tdAll.consentId)
  }

  "clicking 'refresh this page' refreshes the page - showing the same page if bank is not verified yet" in {
    EcospendStub.AuthStubs.stubEcospendAuth2xxSucceeded
    EcospendStub.AccountStub.stubAccountSummary2xxSucceeded(tdAll.consentId)
    P800RefundsExternalApiStub.isValid(tdAll.consentId, EventValue.NotReceived)

    pages.verifyBankAccountPage.open()
    pages.verifyBankAccountPage.assertPageIsDisplayed()
    pages.verifyBankAccountPage.clickRefreshThisPageLink()
    pages.verifyBankAccountPage.assertPageIsDisplayed()
    P800RefundsExternalApiStub.verifyIsValid(tdAll.consentId, 2)
  }

  "redirect to bank transfer 'Request received' page when verification call returns Successful" in {
    EcospendStub.AuthStubs.stubEcospendAuth2xxSucceeded
    EcospendStub.AccountStub.stubAccountSummary2xxSucceeded(tdAll.consentId)
    P800RefundsExternalApiStub.isValid(tdAll.consentId, EventValue.NotReceived)

    pages.verifyBankAccountPage.open()
    pages.verifyBankAccountPage.assertPageIsDisplayed()

    P800RefundsExternalApiStub.isValid(tdAll.consentId, EventValue.Valid)
    pages.verifyBankAccountPage.clickRefreshThisPageLink()
    pages.requestReceivedBankTransferPage.assertPageIsDisplayedForBankTransfer()
  }

  "redirect to 'Request not submitted' page when verification call returns NotValid" in {
    EcospendStub.ValidateStubs.stubValidateNotValidatedYet
    EcospendStub.AuthStubs.stubEcospendAuth2xxSucceeded
    EcospendStub.AccountStub.stubAccountSummary2xxSucceeded(tdAll.consentId)
    P800RefundsExternalApiStub.isValid(tdAll.consentId, EventValue.NotReceived)

    pages.verifyBankAccountPage.open()
    pages.verifyBankAccountPage.assertPageIsDisplayed()
    P800RefundsExternalApiStub.isValid(tdAll.consentId, EventValue.NotValid)

    pages.verifyBankAccountPage.clickRefreshThisPageLink()
    pages.refundRequestNotSubmittedPage.assertPageIsDisplayed()
    P800RefundsExternalApiStub.verifyIsValid(tdAll.consentId, 2)
  }

  "refreshing the page does not re-send the account summary request" in {
    EcospendStub.AuthStubs.stubEcospendAuth2xxSucceeded
    EcospendStub.ValidateStubs.stubValidateNotValidatedYet
    EcospendStub.AuthStubs.stubEcospendAuth2xxSucceeded
    EcospendStub.AccountStub.stubAccountSummary2xxSucceeded(tdAll.consentId)
    P800RefundsExternalApiStub.isValid(tdAll.consentId, EventValue.NotReceived)

    pages.verifyBankAccountPage.open()
    pages.verifyBankAccountPage.assertPageIsDisplayed()
    EcospendStub.AccountStub.accountSummaryValidate(numberOfRequests = 1, tdAll.consentId)

    pages.verifyBankAccountPage.clickRefreshThisPageLink()
    pages.verifyBankAccountPage.assertPageIsDisplayed()
    EcospendStub.AccountStub.accountSummaryValidate(numberOfRequests = 1, tdAll.consentId)
  }

}
