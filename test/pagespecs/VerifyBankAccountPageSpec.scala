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

import edh.GetBankDetailsRiskResultResponse
import models.p800externalapi.EventValue
import testsupport.ItSpec
import testsupport.stubs.{CaseManagementStub, DateCalculatorStub, EcospendStub, EdhStub, NpsClaimOverpaymentStub, NpsSuspendOverpaymentStub, P800RefundsExternalApiStub}

class VerifyBankAccountPageSpec extends ItSpec {

  override def beforeEach(): Unit = {
    super.beforeEach()
    addJourneyIdToSession(tdAll.journeyId)
    upsertJourneyToDatabase(tdAll.BankTransfer.journeyBankConsent)
  }

  //HINT: Those tests work only because EventValue is NotReceived.
  //The page will keep refreshing due to javascript code in it, until the event is Valid or NotValid

  "/verifying-bank-account renders the 'We are verifying your bank account' page" in {
    EcospendStub.AuthStubs.stubEcospendAuth2xxSucceeded
    EcospendStub.AccountStub.stubAccountSummary2xxSucceeded(tdAll.consentId)
    P800RefundsExternalApiStub.isValid(tdAll.consentId, EventValue.NotReceived)
    NpsClaimOverpaymentStub.claimOverpayment(
      nino          = tdAll.nino,
      p800Reference = tdAll.p800ReferenceSanitised,
      request       = tdAll.claimOverpaymentRequest,
      response      = tdAll.claimOverpaymentResponse
    )
    EdhStub.getBankDetailsRiskResult(tdAll.getBankDetailsRiskResultRequest, tdAll.getBankDetailsRiskResultResponse)
    pages.verifyBankAccountPage.open()
    pages.verifyBankAccountPage.assertPageIsDisplayed()
    EdhStub.verifyGetBankDetailsRiskResult(tdAll.claimId, tdAll.correlationId)
    P800RefundsExternalApiStub.verifyIsValid(tdAll.consentId)
    NpsClaimOverpaymentStub.verifyNoneClaimOverpayment(tdAll.nino, tdAll.p800ReferenceSanitised)
  }

  "clicking 'refresh this page' refreshes the page - showing the same page if bank is not verified yet" in {
    EcospendStub.AuthStubs.stubEcospendAuth2xxSucceeded
    EcospendStub.AccountStub.stubAccountSummary2xxSucceeded(tdAll.consentId)
    P800RefundsExternalApiStub.isValid(tdAll.consentId, EventValue.NotReceived)
    NpsClaimOverpaymentStub.claimOverpayment(
      nino          = tdAll.nino,
      p800Reference = tdAll.p800ReferenceSanitised,
      request       = tdAll.claimOverpaymentRequest,
      response      = tdAll.claimOverpaymentResponse
    )
    EdhStub.getBankDetailsRiskResult(tdAll.getBankDetailsRiskResultRequest, tdAll.getBankDetailsRiskResultResponse)

    pages.verifyBankAccountPage.open()
    pages.verifyBankAccountPage.assertPageIsDisplayed()
    pages.verifyBankAccountPage.clickRefreshThisPageLink()
    pages.verifyBankAccountPage.assertPageIsDisplayed()
    EdhStub.verifyGetBankDetailsRiskResult(tdAll.claimId, tdAll.correlationId)
    P800RefundsExternalApiStub.verifyIsValid(tdAll.consentId, 2)
    NpsClaimOverpaymentStub.verifyNoneClaimOverpayment(tdAll.nino, tdAll.p800ReferenceSanitised)
  }

  "redirect to bank transfer 'Request received' page when verification call returns Successful" in {
    EcospendStub.AuthStubs.stubEcospendAuth2xxSucceeded
    EcospendStub.AccountStub.stubAccountSummary2xxSucceeded(tdAll.consentId)
    P800RefundsExternalApiStub.isValid(tdAll.consentId, EventValue.NotReceived)
    EdhStub.getBankDetailsRiskResult(tdAll.getBankDetailsRiskResultRequest, tdAll.getBankDetailsRiskResultResponse)
    DateCalculatorStub.addWorkingDays()
    NpsClaimOverpaymentStub.claimOverpayment(
      nino          = tdAll.nino,
      p800Reference = tdAll.p800ReferenceSanitised,
      request       = tdAll.claimOverpaymentRequest,
      response      = tdAll.claimOverpaymentResponse
    )

    pages.verifyBankAccountPage.open()
    pages.verifyBankAccountPage.assertPageIsDisplayed()
    EdhStub.verifyGetBankDetailsRiskResult(tdAll.claimId, tdAll.correlationId)
    NpsClaimOverpaymentStub.verifyNoneClaimOverpayment(tdAll.nino, tdAll.p800ReferenceSanitised)
    P800RefundsExternalApiStub.isValid(tdAll.consentId, EventValue.Valid)
    NpsClaimOverpaymentStub.claimOverpayment(
      nino          = tdAll.nino,
      p800Reference = tdAll.p800ReferenceSanitised,
      request       = tdAll.claimOverpaymentRequest,
      response      = tdAll.claimOverpaymentResponse
    )
    pages.verifyBankAccountPage.clickRefreshThisPageLink()
    pages.requestReceivedBankTransferPage.assertPageIsDisplayedForBankTransfer()
    DateCalculatorStub.verifyAddWorkingDays()
    NpsClaimOverpaymentStub.verifyClaimOverpayment(tdAll.nino, tdAll.p800ReferenceSanitised, tdAll.correlationId)
  }

  "Show technical difficulties error page when claim overpayment call returns 'Suspended'" in {
    EcospendStub.AuthStubs.stubEcospendAuth2xxSucceeded
    EcospendStub.AccountStub.stubAccountSummary2xxSucceeded(tdAll.consentId)
    P800RefundsExternalApiStub.isValid(tdAll.consentId, EventValue.Valid)
    EdhStub.getBankDetailsRiskResult(tdAll.getBankDetailsRiskResultRequest, tdAll.getBankDetailsRiskResultResponse)
    NpsClaimOverpaymentStub.claimOverpaymentRefundSuspended(
      nino          = tdAll.nino,
      p800Reference = tdAll.p800ReferenceSanitised
    )

    pages.verifyBankAccountPage.open()
    pages.verifyBankAccountPage.assertPageIsDisplayedWithTechnicalDifficultiesError()
    EdhStub.verifyGetBankDetailsRiskResult(tdAll.claimId, tdAll.correlationId)
    NpsClaimOverpaymentStub.verifyClaimOverpayment(tdAll.nino, tdAll.p800ReferenceSanitised, tdAll.correlationId)
  }

  "Show technical difficulties error page when claim overpayment call returns 'Already Taken'" in {
    EcospendStub.AuthStubs.stubEcospendAuth2xxSucceeded
    EcospendStub.AccountStub.stubAccountSummary2xxSucceeded(tdAll.consentId)
    P800RefundsExternalApiStub.isValid(tdAll.consentId, EventValue.Valid)
    EdhStub.getBankDetailsRiskResult(tdAll.getBankDetailsRiskResultRequest, tdAll.getBankDetailsRiskResultResponse)
    NpsClaimOverpaymentStub.claimOverpaymentRefundAlreadyTaken(
      nino          = tdAll.nino,
      p800Reference = tdAll.p800ReferenceSanitised
    )
    pages.verifyBankAccountPage.open()
    pages.verifyBankAccountPage.assertPageIsDisplayedWithTechnicalDifficultiesError()
    EdhStub.verifyGetBankDetailsRiskResult(tdAll.claimId, tdAll.correlationId)
    NpsClaimOverpaymentStub.verifyClaimOverpayment(tdAll.nino, tdAll.p800ReferenceSanitised, tdAll.correlationId)
  }

  "Show technical difficulties error page when claim overpayments call returns 500 error" in {
    EcospendStub.AuthStubs.stubEcospendAuth2xxSucceeded
    EcospendStub.AccountStub.stubAccountSummary2xxSucceeded(tdAll.consentId)
    P800RefundsExternalApiStub.isValid(tdAll.consentId, EventValue.Valid)
    EdhStub.getBankDetailsRiskResult(tdAll.getBankDetailsRiskResultRequest, tdAll.getBankDetailsRiskResultResponse)
    NpsClaimOverpaymentStub.claimOverpaymentInternalServerError(
      nino          = tdAll.nino,
      p800Reference = tdAll.p800ReferenceSanitised
    )

    pages.verifyBankAccountPage.open()
    pages.verifyBankAccountPage.assertPageIsDisplayedWithTechnicalDifficultiesError()
    EdhStub.verifyGetBankDetailsRiskResult(tdAll.claimId, tdAll.correlationId)
    NpsClaimOverpaymentStub.verifyClaimOverpayment(tdAll.nino, tdAll.p800ReferenceSanitised, tdAll.correlationId)
  }

  "redirect to 'Request not submitted' page when verification call returns NotValid" in {
    EcospendStub.AuthStubs.stubEcospendAuth2xxSucceeded
    EcospendStub.AccountStub.stubAccountSummary2xxSucceeded(tdAll.consentId)
    P800RefundsExternalApiStub.isValid(tdAll.consentId, EventValue.NotReceived)
    EdhStub.getBankDetailsRiskResult(tdAll.getBankDetailsRiskResultRequest, tdAll.getBankDetailsRiskResultResponse)
    NpsClaimOverpaymentStub.claimOverpayment(
      nino          = tdAll.nino,
      p800Reference = tdAll.p800ReferenceSanitised,
      request       = tdAll.claimOverpaymentRequest,
      response      = tdAll.claimOverpaymentResponse
    )

    pages.verifyBankAccountPage.open()
    pages.verifyBankAccountPage.assertPageIsDisplayed()
    EdhStub.verifyGetBankDetailsRiskResult(tdAll.claimId, tdAll.correlationId)

    P800RefundsExternalApiStub.isValid(tdAll.consentId, EventValue.NotValid)
    NpsClaimOverpaymentStub.claimOverpayment(
      nino          = tdAll.nino,
      p800Reference = tdAll.p800ReferenceSanitised,
      request       = tdAll.claimOverpaymentRequest,
      response      = tdAll.claimOverpaymentResponse
    )

    pages.verifyBankAccountPage.clickRefreshThisPageLink()
    pages.refundRequestNotSubmittedPage.assertPageIsDisplayed()
    P800RefundsExternalApiStub.verifyIsValid(tdAll.consentId, 2)
  }

  "refreshing the page does not re-send the account summary request, nor edh request" in {
    EcospendStub.AuthStubs.stubEcospendAuth2xxSucceeded
    EcospendStub.AuthStubs.stubEcospendAuth2xxSucceeded
    EcospendStub.AccountStub.stubAccountSummary2xxSucceeded(tdAll.consentId)
    P800RefundsExternalApiStub.isValid(tdAll.consentId, EventValue.NotReceived)
    EdhStub.getBankDetailsRiskResult(tdAll.getBankDetailsRiskResultRequest, tdAll.getBankDetailsRiskResultResponse)

    pages.verifyBankAccountPage.open()
    pages.verifyBankAccountPage.assertPageIsDisplayed()
    EcospendStub.AccountStub.accountSummaryValidate(numberOfRequests = 1, tdAll.consentId)

    pages.verifyBankAccountPage.clickRefreshThisPageLink()
    pages.verifyBankAccountPage.assertPageIsDisplayed()
    EcospendStub.AccountStub.accountSummaryValidate(numberOfRequests = 1, tdAll.consentId)
    EdhStub.verifyGetBankDetailsRiskResult(tdAll.claimId, tdAll.correlationId, numberOfRequests = 1)
  }

  "Show technical difficulties error page when EDH endpoint fails" in {
    EcospendStub.AuthStubs.stubEcospendAuth2xxSucceeded
    EcospendStub.AccountStub.stubAccountSummary2xxSucceeded(tdAll.consentId)
    P800RefundsExternalApiStub.isValid(tdAll.consentId, EventValue.Valid)
    EdhStub.getBankDetailsRiskResult5xx(tdAll.getBankDetailsRiskResultRequest)

    pages.verifyBankAccountPage.open()
    pages.verifyBankAccountPage.assertPageIsDisplayedWithTechnicalDifficultiesError()
    EdhStub.verifyGetBankDetailsRiskResult(tdAll.claimId, tdAll.correlationId)
    NpsClaimOverpaymentStub.verifyNoneClaimOverpayment(tdAll.nino, tdAll.p800ReferenceSanitised)
  }

  "redirect to 'Request not submitted' page when EDH call results in nextAction=DoNotPay" in {
    EcospendStub.AuthStubs.stubEcospendAuth2xxSucceeded
    EcospendStub.AccountStub.stubAccountSummary2xxSucceeded(tdAll.consentId)
    P800RefundsExternalApiStub.isValid(tdAll.consentId, EventValue.Valid)

    val doNotPay: GetBankDetailsRiskResultResponse = tdAll.getBankDetailsRiskResultResponseDoNotPay
    EdhStub.getBankDetailsRiskResult(tdAll.getBankDetailsRiskResultRequest, doNotPay)
    CaseManagementStub.notifyCaseManagement2xx(tdAll.clientUId, tdAll.caseManagementRequest)
    NpsSuspendOverpaymentStub.suspendOverpayment(tdAll.nino, tdAll.p800ReferenceSanitised, tdAll.suspendOverpaymentRequest)

    pages.verifyBankAccountPage.open()
    pages.refundRequestNotSubmittedPage.assertPageIsDisplayed()

    EdhStub.verifyGetBankDetailsRiskResult(tdAll.claimId, tdAll.correlationId)
    CaseManagementStub.verifyNotifyCaseManagement(tdAll.clientUId, tdAll.correlationId)
    NpsSuspendOverpaymentStub.verifySuspendOverpayment(tdAll.nino, tdAll.p800ReferenceSanitised, tdAll.correlationId)
    P800RefundsExternalApiStub.verifyIsValid(tdAll.consentId)
    NpsClaimOverpaymentStub.verifyNoneClaimOverpayment(tdAll.nino, tdAll.p800ReferenceSanitised)

  }

  "Show technical difficulties page when 'Notify case management' call fails" in {
    EcospendStub.AuthStubs.stubEcospendAuth2xxSucceeded
    EcospendStub.AccountStub.stubAccountSummary2xxSucceeded(tdAll.consentId)
    P800RefundsExternalApiStub.isValid(tdAll.consentId, EventValue.Valid)

    val doNotPay: GetBankDetailsRiskResultResponse = tdAll.getBankDetailsRiskResultResponseDoNotPay
    EdhStub.getBankDetailsRiskResult(tdAll.getBankDetailsRiskResultRequest, doNotPay)
    CaseManagementStub.notifyCaseManagement4xx(tdAll.clientUId, tdAll.caseManagementRequest)

    pages.verifyBankAccountPage.open()
    pages.verifyBankAccountPage.assertPageIsDisplayedWithTechnicalDifficultiesError()

    EdhStub.verifyGetBankDetailsRiskResult(tdAll.claimId, tdAll.correlationId)
    CaseManagementStub.verifyNotifyCaseManagement(tdAll.clientUId, tdAll.correlationId)
    P800RefundsExternalApiStub.verifyIsValid(tdAll.consentId)
    NpsClaimOverpaymentStub.verifyNoneClaimOverpayment(tdAll.nino, tdAll.p800ReferenceSanitised)
  }

}
