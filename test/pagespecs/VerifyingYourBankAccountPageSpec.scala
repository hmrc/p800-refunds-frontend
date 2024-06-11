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

import edh._
import models.ecospend.consent.ConsentStatus
import models.p800externalapi.EventValue
import play.api.libs.json.{JsObject, Json}
import testsupport.ItSpec
import testsupport.stubs._

class VerifyingYourBankAccountPageSpec extends ItSpec {

  override def beforeEach(): Unit = {
    super.beforeEach()
    addJourneyIdToSession(tdAll.journeyId)
    upsertJourneyToDatabase(tdAll.BankTransfer.journeyBankAccountConsentSuccessfulNameMatch)
  }

  //HINT: Those tests work only because EventValue is NotReceived.
  //The page will keep refreshing due to javascript code in it, until the event is Valid or NotValid

  "/verify-bank-account renders the 'Refund Request not Submitted' page when the Consent Status is Failed" in {
    val verifyingBankAccountConsentFailed = pages.verifyingBankAccountPageConsent(ConsentStatus.Failed)
    EcospendStub.AuthStubs.stubEcospendAuth2xxSucceeded
    EcospendStub.AccountStub.stubAccountSummary2xxSucceeded(tdAll.consentId)
    P800RefundsExternalApiStub.isValid(tdAll.consentId, EventValue.NotReceived)
    MakeBacsRepaymentStub.`makeBacsRepayment 200 OK`(
      nino     = tdAll.nino,
      request  = tdAll.claimOverpaymentRequest,
      response = tdAll.claimOverpaymentResponse
    )
    EdhStub.getBankDetailsRiskResult(tdAll.getBankDetailsRiskResultRequest, tdAll.getBankDetailsRiskResultResponse)
    verifyingBankAccountConsentFailed.open()
    pages.refundRequestNotSubmittedPage.assertPageIsDisplayed()
    EdhStub.verifyGetBankDetailsRiskResult(tdAll.claimId, tdAll.correlationId)
    P800RefundsExternalApiStub.verifyIsValid(tdAll.consentId)
    MakeBacsRepaymentStub.verifyNone(tdAll.nino)
    AuditConnectorStub.verifyNoAuditEvent(AuditConnectorStub.bankClaimAttemptMadeAuditType)
  }

  "/verify-bank-account renders the 'Refund Request not Submitted' page when the Consent Status is Canceled" in {
    val verifyingBankAccountConsentCanceled = pages.verifyingBankAccountPageConsent(ConsentStatus.Canceled)
    EcospendStub.AuthStubs.stubEcospendAuth2xxSucceeded
    EcospendStub.AccountStub.stubAccountSummary2xxSucceeded(tdAll.consentId)
    P800RefundsExternalApiStub.isValid(tdAll.consentId, EventValue.NotReceived)
    MakeBacsRepaymentStub.`makeBacsRepayment 200 OK`(
      nino     = tdAll.nino,
      request  = tdAll.claimOverpaymentRequest,
      response = tdAll.claimOverpaymentResponse
    )
    EdhStub.getBankDetailsRiskResult(tdAll.getBankDetailsRiskResultRequest, tdAll.getBankDetailsRiskResultResponse)
    verifyingBankAccountConsentCanceled.open()
    pages.refundRequestNotSubmittedPage.assertPageIsDisplayed()
    EdhStub.verifyGetBankDetailsRiskResult(tdAll.claimId, tdAll.correlationId)
    P800RefundsExternalApiStub.verifyIsValid(tdAll.consentId)
    MakeBacsRepaymentStub.verifyNone(tdAll.nino)
    AuditConnectorStub.verifyNoAuditEvent(AuditConnectorStub.bankClaimAttemptMadeAuditType)
  }

  "/verify-bank-account renders the 'Refund Request not Submitted' page when the Name Matching Fails" in {
    EcospendStub.AuthStubs.stubEcospendAuth2xxSucceeded
    EcospendStub.AccountStub.stubAccountSummary2xxSucceeded(tdAll.consentId, "fail name")
    P800RefundsExternalApiStub.isValid(tdAll.consentId, EventValue.NotReceived)
    MakeBacsRepaymentStub.`makeBacsRepayment 200 OK`(
      nino     = tdAll.nino,
      request  = tdAll.claimOverpaymentRequest,
      response = tdAll.claimOverpaymentResponse
    )
    EdhStub.getBankDetailsRiskResult(tdAll.getBankDetailsRiskResultRequest, tdAll.getBankDetailsRiskResultResponse)
    pages.verifyingBankAccountPage.open()
    pages.refundRequestNotSubmittedPage.assertPageIsDisplayed()
    EdhStub.verifyGetBankDetailsRiskResult(tdAll.claimId, tdAll.correlationId)
    P800RefundsExternalApiStub.verifyIsValid(tdAll.consentId)
    MakeBacsRepaymentStub.verifyNone(tdAll.nino)
    AuditConnectorStub.verifyEventAudited(
      AuditConnectorStub.bankClaimAttemptMadeAuditType,
      Json.parse(
        //format=JSON
        """
        {
          "outcome": {
            "isSuccessful": false,
            "actionsOutcome": {
              "fuzzyNameMatchingIsSuccessful": false,
              "hmrcFraudCheckIsSuccessful": true
            },
            "failureReasons": [
              "Ecospend names failed matching against NPS name"
            ]
          },
          "userEnteredDetails": {
            "chosenBank": "Barclays Personal",
            "p800Reference": 12345678,
            "nino": "LM001014C",
            "dob": "2000-01-01"
          },
          "repaymentAmount": 12.34,
          "repaymentInformation": {
            "reconciliationIdentifier": 123,
            "paymentNumber": 12345678,
            "payeNumber": "PayeNumber-123",
            "taxDistrictNumber": 717,
            "associatedPayableNumber": 1234,
            "customerAccountNumber": "customerAccountNumber-1234",
            "currentOptimisticLock": 15
          },
          "name": {
            "title": "Sir",
            "firstForename": "Greg",
            "secondForename": "Greggory",
            "surname": "Greggson"
          },
          "address": {
            "addressLine1": "Flat 1 Rose House",
            "addressLine2": "Worthing",
            "addressPostcode": "BN12 4XL"
          }
        }
        """.stripMargin
      ).as[JsObject]
    )
  }

  "/verify-bank-account renders the 'Refund Request not Submitted' page when the Name Matching Fails (because of unexpected title)" in {
    EcospendStub.AuthStubs.stubEcospendAuth2xxSucceeded
    EcospendStub.AccountStub.stubAccountSummary2xxSucceeded(tdAll.consentId, "Lord Greg Greggory Greggson")
    P800RefundsExternalApiStub.isValid(tdAll.consentId, EventValue.NotReceived)
    MakeBacsRepaymentStub.`makeBacsRepayment 200 OK`(
      nino     = tdAll.nino,
      request  = tdAll.claimOverpaymentRequest,
      response = tdAll.claimOverpaymentResponse
    )
    EdhStub.getBankDetailsRiskResult(tdAll.getBankDetailsRiskResultRequest, tdAll.getBankDetailsRiskResultResponse)
    pages.verifyingBankAccountPage.open()
    pages.refundRequestNotSubmittedPage.assertPageIsDisplayed()
    EdhStub.verifyGetBankDetailsRiskResult(tdAll.claimId, tdAll.correlationId)
    P800RefundsExternalApiStub.verifyIsValid(tdAll.consentId)
    MakeBacsRepaymentStub.verifyNone(tdAll.nino)
    AuditConnectorStub.verifyEventAudited(
      AuditConnectorStub.bankClaimAttemptMadeAuditType,
      Json.parse(
        //format=JSON
        """
        {
          "outcome": {
            "isSuccessful": false,
            "actionsOutcome": {
              "fuzzyNameMatchingIsSuccessful": false,
              "hmrcFraudCheckIsSuccessful": true
            },
            "failureReasons": [
              "Ecospend names failed matching against NPS name"
            ]
          },
          "userEnteredDetails": {
            "chosenBank": "Barclays Personal",
            "p800Reference": 12345678,
            "nino": "LM001014C",
            "dob": "2000-01-01"
          },
          "repaymentAmount": 12.34,
          "repaymentInformation": {
            "reconciliationIdentifier": 123,
            "paymentNumber": 12345678,
            "payeNumber": "PayeNumber-123",
            "taxDistrictNumber": 717,
            "associatedPayableNumber": 1234,
            "customerAccountNumber": "customerAccountNumber-1234",
            "currentOptimisticLock": 15
          },
          "name": {
            "title": "Sir",
            "firstForename": "Greg",
            "secondForename": "Greggory",
            "surname": "Greggson"
          },
          "address": {
            "addressLine1": "Flat 1 Rose House",
            "addressLine2": "Worthing",
            "addressPostcode": "BN12 4XL"
          }
        }
        """.stripMargin
      ).as[JsObject]
    )
  }

  "/verify-bank-account renders the 'Refund Request not Submitted' page when the parties list is empty" in {
    val responseBody =
      //language=JSON
      s"""[{
          "id" : "${tdAll.consentId.value}",
          "bank_id" : "obie-barclays-personal",
          "type" : "Personal",
          "sub_type" : "CurrentAccount",
          "currency" : "GBP",
          "account_format" : "SortCode",
          "account_identification" : "44556610002333",
          "calculated_owner_name" : "Mr Greg Greggson",
          "account_owner_name" : "Greggson Gregory ",
          "display_name" : "bank account display name",
          "balance" : 123.7,
          "last_update_time" : "2059-11-25T16:33:51.88",
          "parties" : []
        }]""".stripMargin

    EcospendStub.AuthStubs.stubEcospendAuth2xxSucceeded
    EcospendStub.AccountStub.stubAccountSummaryWithJson2xxSucceeded(tdAll.consentId, responseBody)
    P800RefundsExternalApiStub.isValid(tdAll.consentId, EventValue.NotReceived)
    MakeBacsRepaymentStub.`makeBacsRepayment 200 OK`(
      nino     = tdAll.nino,
      request  = tdAll.claimOverpaymentRequest,
      response = tdAll.claimOverpaymentResponse
    )
    EdhStub.getBankDetailsRiskResult(tdAll.getBankDetailsRiskResultRequest, tdAll.getBankDetailsRiskResultResponse)
    pages.verifyingBankAccountPage.open()
    pages.refundRequestNotSubmittedPage.assertPageIsDisplayed()
    EdhStub.verifyGetBankDetailsRiskResult(tdAll.claimId, tdAll.correlationId)
    P800RefundsExternalApiStub.verifyIsValid(tdAll.consentId)
    MakeBacsRepaymentStub.verifyNone(tdAll.nino)
    AuditConnectorStub.verifyEventAudited(
      AuditConnectorStub.bankClaimAttemptMadeAuditType,
      Json.parse(
        """
        {
          "outcome": {
            "isSuccessful": false,
            "actionsOutcome": {
              "fuzzyNameMatchingIsSuccessful": false,
              "hmrcFraudCheckIsSuccessful": true
            },
            "failureReasons": [
              "Ecospend names failed matching against NPS name"
            ]
          },
          "userEnteredDetails": {
            "chosenBank": "Barclays Personal",
            "p800Reference": 12345678,
            "nino": "LM001014C",
            "dob": "2000-01-01"
          },
          "repaymentAmount": 12.34,
          "repaymentInformation": {
            "reconciliationIdentifier": 123,
            "paymentNumber": 12345678,
            "payeNumber": "PayeNumber-123",
            "taxDistrictNumber": 717,
            "associatedPayableNumber": 1234,
            "customerAccountNumber": "customerAccountNumber-1234",
            "currentOptimisticLock": 15
          },
          "name": {
            "title": "Sir",
            "firstForename": "Greg",
            "secondForename": "Greggory",
            "surname": "Greggson"
          },
          "address": {
            "addressLine1": "Flat 1 Rose House",
            "addressLine2": "Worthing",
            "addressPostcode": "BN12 4XL"
          }
        }
        """.stripMargin
      ).as[JsObject]
    )
  }

  "/verify-bank-account renders the 'Refund Request not Submitted' page when the parties list & account name is empty" in {
    val responseBody =
      //language=JSON
      s"""[{
          "id" : "${tdAll.consentId.value}",
          "bank_id" : "obie-barclays-personal",
          "type" : "Personal",
          "sub_type" : "CurrentAccount",
          "currency" : "GBP",
          "account_format" : "SortCode",
          "account_identification" : "44556610002333",
          "calculated_owner_name" : "Mr Greg Greggson",
          "display_name" : "bank account display name",
          "balance" : 123.7,
          "last_update_time" : "2059-11-25T16:33:51.88",
          "parties" : []
        }]""".stripMargin

    EcospendStub.AuthStubs.stubEcospendAuth2xxSucceeded
    EcospendStub.AccountStub.stubAccountSummaryWithJson2xxSucceeded(tdAll.consentId, responseBody)
    P800RefundsExternalApiStub.isValid(tdAll.consentId, EventValue.NotReceived)
    MakeBacsRepaymentStub.`makeBacsRepayment 200 OK`(
      nino     = tdAll.nino,
      request  = tdAll.claimOverpaymentRequest,
      response = tdAll.claimOverpaymentResponse
    )
    EdhStub.getBankDetailsRiskResult(tdAll.getBankDetailsRiskResultRequest, tdAll.getBankDetailsRiskResultResponse)
    pages.verifyingBankAccountPage.open()
    pages.refundRequestNotSubmittedPage.assertPageIsDisplayed()
    EdhStub.verifyGetBankDetailsRiskResult(tdAll.claimId, tdAll.correlationId)
    P800RefundsExternalApiStub.verifyIsValid(tdAll.consentId)
    MakeBacsRepaymentStub.verifyNone(tdAll.nino)
    AuditConnectorStub.verifyEventAudited(
      AuditConnectorStub.bankClaimAttemptMadeAuditType,
      Json.parse(
        """
        {
          "outcome": {
            "isSuccessful": false,
            "actionsOutcome": {
              "fuzzyNameMatchingIsSuccessful": false,
              "hmrcFraudCheckIsSuccessful": true
            },
            "failureReasons": [
              "Ecospend names failed matching against NPS name"
            ]
          },
          "userEnteredDetails": {
            "chosenBank": "Barclays Personal",
            "p800Reference": 12345678,
            "nino": "LM001014C",
            "dob": "2000-01-01"
          },
          "repaymentAmount": 12.34,
          "repaymentInformation": {
            "reconciliationIdentifier": 123,
            "paymentNumber": 12345678,
            "payeNumber": "PayeNumber-123",
            "taxDistrictNumber": 717,
            "associatedPayableNumber": 1234,
            "customerAccountNumber": "customerAccountNumber-1234",
            "currentOptimisticLock": 15
          },
          "name": {
            "title": "Sir",
            "firstForename": "Greg",
            "secondForename": "Greggory",
            "surname": "Greggson"
          },
          "address": {
            "addressLine1": "Flat 1 Rose House",
            "addressLine2": "Worthing",
            "addressPostcode": "BN12 4XL"
          }
        }
        """.stripMargin
      ).as[JsObject]
    )
  }

  "/verify-bank-account renders the 'We are verifying your bank account' page" in {
    EcospendStub.AuthStubs.stubEcospendAuth2xxSucceeded
    EcospendStub.AccountStub.stubAccountSummary2xxSucceeded(tdAll.consentId)
    P800RefundsExternalApiStub.isValid(tdAll.consentId, EventValue.NotReceived)
    MakeBacsRepaymentStub.`makeBacsRepayment 200 OK`(
      nino     = tdAll.nino,
      request  = tdAll.claimOverpaymentRequest,
      response = tdAll.claimOverpaymentResponse
    )
    EdhStub.getBankDetailsRiskResult(tdAll.getBankDetailsRiskResultRequest, tdAll.getBankDetailsRiskResultResponse)
    pages.verifyingBankAccountPage.open()
    pages.verifyingBankAccountPage.assertPageIsDisplayed()
    EdhStub.verifyGetBankDetailsRiskResult(tdAll.claimId, tdAll.correlationId)
    P800RefundsExternalApiStub.verifyIsValid(tdAll.consentId)
    MakeBacsRepaymentStub.verifyNone(tdAll.nino)
    AuditConnectorStub.verifyNoAuditEvent(AuditConnectorStub.bankClaimAttemptMadeAuditType)
  }

  "/verify-bank-account renders the 'We are verifying your bank account' page when using the fallback joint account name" in {

    val responseBody =
      //language=JSON
      s"""[{
          "id" : "${tdAll.consentId.value}",
          "bank_id" : "obie-barclays-personal",
          "type" : "Personal",
          "sub_type" : "CurrentAccount",
          "currency" : "GBP",
          "account_format" : "SortCode",
          "account_identification" : "44556610002333",
          "calculated_owner_name" : "Mr Greg Greggson",
          "account_owner_name" : "Margaretta Greggson,Greg Greggory Greggson",
          "display_name" : "bank account display name",
          "balance" : 123.7,
          "last_update_time" : "2059-11-25T16:33:51.88",
          "parties" : []
        }]""".stripMargin

    EcospendStub.AuthStubs.stubEcospendAuth2xxSucceeded
    EcospendStub.AccountStub.stubAccountSummaryWithJson2xxSucceeded(tdAll.consentId, responseBody)
    P800RefundsExternalApiStub.isValid(tdAll.consentId, EventValue.NotReceived)
    MakeBacsRepaymentStub.`makeBacsRepayment 200 OK`(
      nino     = tdAll.nino,
      request  = tdAll.claimOverpaymentRequest,
      response = tdAll.claimOverpaymentResponse
    )
    EdhStub.getBankDetailsRiskResult(tdAll.getBankDetailsRiskResultRequest, tdAll.getBankDetailsRiskResultResponse)
    pages.verifyingBankAccountPage.open()
    pages.verifyingBankAccountPage.assertPageIsDisplayed()
    EdhStub.verifyGetBankDetailsRiskResult(tdAll.claimId, tdAll.correlationId)
    P800RefundsExternalApiStub.verifyIsValid(tdAll.consentId)
    MakeBacsRepaymentStub.verifyNone(tdAll.nino)
    AuditConnectorStub.verifyNoAuditEvent(AuditConnectorStub.bankClaimAttemptMadeAuditType)
  }

  "/verify-bank-account renders the 'We are verifying your bank account' page when going through the bankIdBasedAccountNameParsing fallback joint account name" in {

    val responseBody =
      //language=JSON
      s"""[{
          "id" : "${tdAll.consentId.value}",
          "bank_id" : "obie-natwest-production",
          "type" : "Personal",
          "sub_type" : "CurrentAccount",
          "currency" : "GBP",
          "account_format" : "SortCode",
          "account_identification" : "44556610002333",
          "calculated_owner_name" : "Mr Greg Greggson",
          "account_owner_name" : "Greggson M & G",
          "display_name" : "bank account display name",
          "balance" : 123.7,
          "last_update_time" : "2059-11-25T16:33:51.88",
          "parties" : []
        }]""".stripMargin

    EcospendStub.AuthStubs.stubEcospendAuth2xxSucceeded
    EcospendStub.AccountStub.stubAccountSummaryWithJson2xxSucceeded(tdAll.consentId, responseBody)
    P800RefundsExternalApiStub.isValid(tdAll.consentId, EventValue.NotReceived)
    MakeBacsRepaymentStub.`makeBacsRepayment 200 OK`(
      nino     = tdAll.nino,
      request  = tdAll.claimOverpaymentRequest,
      response = tdAll.claimOverpaymentResponse
    )
    EdhStub.getBankDetailsRiskResult(tdAll.getBankDetailsRiskResultRequest, tdAll.getBankDetailsRiskResultResponse)
    pages.verifyingBankAccountPage.open()
    pages.verifyingBankAccountPage.assertPageIsDisplayed()
    EdhStub.verifyGetBankDetailsRiskResult(tdAll.claimId, tdAll.correlationId)
    P800RefundsExternalApiStub.verifyIsValid(tdAll.consentId)
    MakeBacsRepaymentStub.verifyNone(tdAll.nino)
    AuditConnectorStub.verifyNoAuditEvent(AuditConnectorStub.bankClaimAttemptMadeAuditType)
  }

  "clicking 'refresh this page' refreshes the page - showing the same page if bank is not verified yet" in {
    EcospendStub.AuthStubs.stubEcospendAuth2xxSucceeded
    EcospendStub.AccountStub.stubAccountSummary2xxSucceeded(tdAll.consentId)
    P800RefundsExternalApiStub.isValid(tdAll.consentId, EventValue.NotReceived)
    MakeBacsRepaymentStub.`makeBacsRepayment 200 OK`(
      nino     = tdAll.nino,
      request  = tdAll.claimOverpaymentRequest,
      response = tdAll.claimOverpaymentResponse
    )
    EdhStub.getBankDetailsRiskResult(tdAll.getBankDetailsRiskResultRequest, tdAll.getBankDetailsRiskResultResponse)

    pages.verifyingBankAccountPage.open()
    pages.verifyingBankAccountPage.assertPageIsDisplayed()
    pages.verifyingBankAccountPage.clickRefreshThisPageLink()
    pages.verifyingBankAccountPage.assertPageIsDisplayed()
    EdhStub.verifyGetBankDetailsRiskResult(tdAll.claimId, tdAll.correlationId)
    P800RefundsExternalApiStub.verifyIsValid(tdAll.consentId, 2)
    MakeBacsRepaymentStub.verifyNone(tdAll.nino)
    AuditConnectorStub.verifyNoAuditEvent(AuditConnectorStub.bankClaimAttemptMadeAuditType)
  }

  "redirect to bank transfer 'Request received' page when verification call returns Successful" in {
    EcospendStub.AuthStubs.stubEcospendAuth2xxSucceeded
    EcospendStub.AccountStub.stubAccountSummary2xxSucceeded(tdAll.consentId)
    P800RefundsExternalApiStub.isValid(tdAll.consentId, EventValue.NotReceived)
    EdhStub.getBankDetailsRiskResult(tdAll.getBankDetailsRiskResultRequest, tdAll.getBankDetailsRiskResultResponse)
    DateCalculatorStub.addWorkingDays()
    MakeBacsRepaymentStub.`makeBacsRepayment 200 OK`(
      nino     = tdAll.nino,
      request  = tdAll.claimOverpaymentRequest,
      response = tdAll.claimOverpaymentResponse
    )

    pages.verifyingBankAccountPage.open()
    pages.verifyingBankAccountPage.assertPageIsDisplayed()
    EdhStub.verifyGetBankDetailsRiskResult(tdAll.claimId, tdAll.correlationId)
    MakeBacsRepaymentStub.verifyNone(tdAll.nino)
    P800RefundsExternalApiStub.isValid(tdAll.consentId, EventValue.Valid)
    MakeBacsRepaymentStub.`makeBacsRepayment 200 OK`(
      nino     = tdAll.nino,
      request  = tdAll.claimOverpaymentRequest,
      response = tdAll.claimOverpaymentResponse
    )
    pages.verifyingBankAccountPage.clickRefreshThisPageLink()
    pages.requestReceivedBankTransferPage.assertPageIsDisplayedForBankTransfer()
    DateCalculatorStub.verifyAddWorkingDays()
    MakeBacsRepaymentStub.verify(tdAll.nino, tdAll.correlationId)

    AuditConnectorStub.verifyEventAudited(
      AuditConnectorStub.bankClaimAttemptMadeAuditType,
      Json.parse(
        //format=JSON
        """
        {
          "outcome": {
            "isSuccessful": true,
            "actionsOutcome": {
              "ecospendFraudCheckIsSuccessful": true,
              "fuzzyNameMatchingIsSuccessful": true,
              "hmrcFraudCheckIsSuccessful": true,
              "claimOverpaymentIsSuccessful": true
            }
          },
          "userEnteredDetails": {
            "chosenBank": "Barclays Personal",
            "p800Reference": 12345678,
            "nino": "LM001014C",
            "dob": "2000-01-01"
          },
          "repaymentAmount": 12.34,
          "repaymentInformation": {
            "reconciliationIdentifier": 123,
            "paymentNumber": 12345678,
            "payeNumber": "PayeNumber-123",
            "taxDistrictNumber": 717,
            "associatedPayableNumber": 1234,
            "customerAccountNumber": "customerAccountNumber-1234",
            "currentOptimisticLock": 15
          },
          "name": {
            "title": "Sir",
            "firstForename": "Greg",
            "secondForename": "Greggory",
            "surname": "Greggson"
          },
          "address": {
            "addressLine1": "Flat 1 Rose House",
            "addressLine2": "Worthing",
            "addressPostcode": "BN12 4XL"
          }
        }
        """.stripMargin
      ).as[JsObject]
    )
  }

  "redirect to 'Request not submitted' page when verification call returns NotValid" in {
    EcospendStub.AuthStubs.stubEcospendAuth2xxSucceeded
    EcospendStub.AccountStub.stubAccountSummary2xxSucceeded(tdAll.consentId)
    P800RefundsExternalApiStub.isValid(tdAll.consentId, EventValue.NotReceived)
    EdhStub.getBankDetailsRiskResult(tdAll.getBankDetailsRiskResultRequest, tdAll.getBankDetailsRiskResultResponse)
    MakeBacsRepaymentStub.`makeBacsRepayment 200 OK`(
      nino     = tdAll.nino,
      request  = tdAll.claimOverpaymentRequest,
      response = tdAll.claimOverpaymentResponse
    )

    pages.verifyingBankAccountPage.open()
    pages.verifyingBankAccountPage.assertPageIsDisplayed()
    EdhStub.verifyGetBankDetailsRiskResult(tdAll.claimId, tdAll.correlationId)

    P800RefundsExternalApiStub.isValid(tdAll.consentId, EventValue.NotValid)
    MakeBacsRepaymentStub.`makeBacsRepayment 200 OK`(
      nino     = tdAll.nino,
      request  = tdAll.claimOverpaymentRequest,
      response = tdAll.claimOverpaymentResponse
    )

    pages.verifyingBankAccountPage.clickRefreshThisPageLink()
    pages.refundRequestNotSubmittedPage.assertPageIsDisplayed()
    P800RefundsExternalApiStub.verifyIsValid(tdAll.consentId, 2)
    AuditConnectorStub.verifyEventAudited(
      AuditConnectorStub.bankClaimAttemptMadeAuditType,
      Json.parse(
        //format=JSON
        """
        {
          "outcome": {
            "isSuccessful": false,
            "actionsOutcome": {
              "ecospendFraudCheckIsSuccessful": false,
              "fuzzyNameMatchingIsSuccessful": true,
              "hmrcFraudCheckIsSuccessful": true
            },
            "failureReasons": [
              "Account assessment failed."
            ]
          },
          "userEnteredDetails": {
            "chosenBank": "Barclays Personal",
            "p800Reference": 12345678,
            "nino": "LM001014C",
            "dob": "2000-01-01"
          },
          "repaymentAmount": 12.34,
          "repaymentInformation": {
            "reconciliationIdentifier": 123,
            "paymentNumber": 12345678,
            "payeNumber": "PayeNumber-123",
            "taxDistrictNumber": 717,
            "associatedPayableNumber": 1234,
            "customerAccountNumber": "customerAccountNumber-1234",
            "currentOptimisticLock": 15
          },
          "name": {
            "title": "Sir",
            "firstForename": "Greg",
            "secondForename": "Greggory",
            "surname": "Greggson"
          },
          "address": {
            "addressLine1": "Flat 1 Rose House",
            "addressLine2": "Worthing",
            "addressPostcode": "BN12 4XL"
          }
        }
        """.stripMargin
      ).as[JsObject]
    )
  }

  "refreshing the page does not re-send the account summary request, nor edh request" in {
    EcospendStub.AuthStubs.stubEcospendAuth2xxSucceeded
    EcospendStub.AuthStubs.stubEcospendAuth2xxSucceeded
    EcospendStub.AccountStub.stubAccountSummary2xxSucceeded(tdAll.consentId)
    P800RefundsExternalApiStub.isValid(tdAll.consentId, EventValue.NotReceived)
    EdhStub.getBankDetailsRiskResult(tdAll.getBankDetailsRiskResultRequest, tdAll.getBankDetailsRiskResultResponse)

    pages.verifyingBankAccountPage.open()
    pages.verifyingBankAccountPage.assertPageIsDisplayed()
    EcospendStub.AccountStub.accountSummaryValidate(numberOfRequests = 1, tdAll.consentId)

    pages.verifyingBankAccountPage.clickRefreshThisPageLink()
    pages.verifyingBankAccountPage.assertPageIsDisplayed()
    EcospendStub.AccountStub.accountSummaryValidate(numberOfRequests = 1, tdAll.consentId)
    EdhStub.verifyGetBankDetailsRiskResult(tdAll.claimId, tdAll.correlationId, numberOfRequests = 1)
    AuditConnectorStub.verifyNoAuditEvent(AuditConnectorStub.bankClaimAttemptMadeAuditType)
  }

  "redirect to 'Request received' page when EDH call results in nextAction=DoNotPay" in {
    // NOTE: When the EDH risk call fails, the application should direct the user to the 'Request received' page.
    // Where the customer passes the Ecospend fraud algorithm but fails the RIS Risking/EDH, we need to mirror PTA
    // and advice the customer the refund is being processed
    EcospendStub.AuthStubs.stubEcospendAuth2xxSucceeded
    EcospendStub.AccountStub.stubAccountSummary2xxSucceeded(tdAll.consentId)
    P800RefundsExternalApiStub.isValid(tdAll.consentId, EventValue.Valid)
    DateCalculatorStub.addWorkingDays()

    val doNotPay: GetBankDetailsRiskResultResponse = tdAll.getBankDetailsRiskResultResponseDoNotPay
    EdhStub.getBankDetailsRiskResult(tdAll.getBankDetailsRiskResultRequest, doNotPay)
    CaseManagementStub.notifyCaseManagement2xx(tdAll.clientUId, tdAll.caseManagementRequest)
    NpsSuspendOverpaymentStub.suspendOverpayment(tdAll.nino, tdAll.suspendOverpaymentRequest)

    pages.verifyingBankAccountPage.open()
    pages.requestReceivedBankTransferPage.assertPageIsDisplayedForBankTransfer()

    EdhStub.verifyGetBankDetailsRiskResult(tdAll.claimId, tdAll.correlationId)
    CaseManagementStub.verifyNotifyCaseManagement(tdAll.clientUId, tdAll.correlationId)
    NpsSuspendOverpaymentStub.verify(tdAll.nino, tdAll.correlationId)
    P800RefundsExternalApiStub.verifyIsValid(tdAll.consentId)
    MakeBacsRepaymentStub.verifyNone(tdAll.nino)
    AuditConnectorStub.verifyEventAudited(
      AuditConnectorStub.bankClaimAttemptMadeAuditType,
      Json.parse(
        //format=JSON
        """
        {
          "outcome": {
            "isSuccessful": false,
            "actionsOutcome": {
              "ecospendFraudCheckIsSuccessful": true,
              "fuzzyNameMatchingIsSuccessful": true,
              "hmrcFraudCheckIsSuccessful": false
            },
            "failureReasons": [
              "EDH indicated DoNotPay"
            ]
          },
          "userEnteredDetails": {
            "chosenBank": "Barclays Personal",
            "p800Reference": 12345678,
            "nino": "LM001014C",
            "dob": "2000-01-01"
          },
          "repaymentAmount": 12.34,
          "repaymentInformation": {
            "reconciliationIdentifier": 123,
            "paymentNumber": 12345678,
            "payeNumber": "PayeNumber-123",
            "taxDistrictNumber": 717,
            "associatedPayableNumber": 1234,
            "customerAccountNumber": "customerAccountNumber-1234",
            "currentOptimisticLock": 15
          },
          "name": {
            "title": "Sir",
            "firstForename": "Greg",
            "secondForename": "Greggory",
            "surname": "Greggson"
          },
          "address": {
            "addressLine1": "Flat 1 Rose House",
            "addressLine2": "Worthing",
            "addressPostcode": "BN12 4XL"
          }
        }
        """.stripMargin
      ).as[JsObject]
    )
  }

  "Show technical difficulties error page when claim overpayment call returns 'Suspended'" in {
    EcospendStub.AuthStubs.stubEcospendAuth2xxSucceeded
    EcospendStub.AccountStub.stubAccountSummary2xxSucceeded(tdAll.consentId)
    P800RefundsExternalApiStub.isValid(tdAll.consentId, EventValue.Valid)
    EdhStub.getBankDetailsRiskResult(tdAll.getBankDetailsRiskResultRequest, tdAll.getBankDetailsRiskResultResponse)
    MakeBacsRepaymentStub.`refundSuspended 422`(
      nino = tdAll.nino
    )

    pages.verifyingBankAccountPage.open()
    pages.verifyingBankAccountPage.assertPageIsDisplayedWithTechnicalDifficultiesError()
    EdhStub.verifyGetBankDetailsRiskResult(tdAll.claimId, tdAll.correlationId)
    MakeBacsRepaymentStub.verify(tdAll.nino, tdAll.correlationId)

    AuditConnectorStub.verifyEventAudited(
      AuditConnectorStub.bankClaimAttemptMadeAuditType,
      Json.parse(
        //format=JSON
        """
        {
          "outcome": {
            "isSuccessful": false,
            "actionsOutcome": {
              "ecospendFraudCheckIsSuccessful": true,
              "fuzzyNameMatchingIsSuccessful": true,
              "hmrcFraudCheckIsSuccessful": true,
              "claimOverpaymentIsSuccessful": false
            },
            "failureReasons": [
              "POST of 'http://localhost:11112/p800-refunds-backend/nps/make-bacs-repayment/LM001014C' returned 422. Response body: '\n          {\n           \"failures\" : [\n             {\"reason\" : \"Reference \", \"code\": \"63480\"}\n           ]\n          }\n          '"
            ]
          },
          "userEnteredDetails": {
            "repaymentMethod": "bank",
            "chosenBank": "Barclays Personal",
            "p800Reference": 12345678,
            "nino": "LM001014C",
            "dob": "2000-01-01"
          },
          "repaymentAmount": 12.34,
          "repaymentInformation": {
            "reconciliationIdentifier": 123,
            "paymentNumber": 12345678,
            "payeNumber": "PayeNumber-123",
            "taxDistrictNumber": 717,
            "associatedPayableNumber": 1234,
            "customerAccountNumber": "customerAccountNumber-1234",
            "currentOptimisticLock": 15
          },
          "name": {
            "title": "Sir",
            "firstForename": "Greg",
            "secondForename": "Greggory",
            "surname": "Greggson"
          },
          "address": {
            "addressLine1": "Flat 1 Rose House",
            "addressLine2": "Worthing",
            "addressPostcode": "BN12 4XL"
          }
        }
        """.stripMargin
      ).as[JsObject]
    )
  }

  "Show technical difficulties error page when claim overpayment call returns 'Already Taken'" in {
    EcospendStub.AuthStubs.stubEcospendAuth2xxSucceeded
    EcospendStub.AccountStub.stubAccountSummary2xxSucceeded(tdAll.consentId)
    P800RefundsExternalApiStub.isValid(tdAll.consentId, EventValue.Valid)
    EdhStub.getBankDetailsRiskResult(tdAll.getBankDetailsRiskResultRequest, tdAll.getBankDetailsRiskResultResponse)
    MakeBacsRepaymentStub.`refundAlreadyTaken 422`(
      nino = tdAll.nino
    )
    pages.verifyingBankAccountPage.open()
    pages.verifyingBankAccountPage.assertPageIsDisplayedWithTechnicalDifficultiesError()
    EdhStub.verifyGetBankDetailsRiskResult(tdAll.claimId, tdAll.correlationId)
    MakeBacsRepaymentStub.verify(tdAll.nino, tdAll.correlationId)
    AuditConnectorStub.verifyEventAudited(
      AuditConnectorStub.bankClaimAttemptMadeAuditType,
      Json.parse(
        //format=JSON
        """
        {
          "outcome": {
            "isSuccessful": false,
            "actionsOutcome": {
              "ecospendFraudCheckIsSuccessful": true,
              "fuzzyNameMatchingIsSuccessful": true,
              "hmrcFraudCheckIsSuccessful": true,
              "claimOverpaymentIsSuccessful": false
            },
            "failureReasons": [
              "POST of 'http://localhost:11112/p800-refunds-backend/nps/make-bacs-repayment/LM001014C' returned 422. Response body: '\n          {\n           \"failures\" : [\n             {\"reason\" : \"Reference \", \"code\": \"63480\"}\n           ]\n          }\n          '"
            ]
          },
          "userEnteredDetails": {
            "repaymentMethod": "bank",
            "chosenBank": "Barclays Personal",
            "p800Reference": 12345678,
            "nino": "LM001014C",
            "dob": "2000-01-01"
          },
          "repaymentAmount": 12.34,
          "repaymentInformation": {
            "reconciliationIdentifier": 123,
            "paymentNumber": 12345678,
            "payeNumber": "PayeNumber-123",
            "taxDistrictNumber": 717,
            "associatedPayableNumber": 1234,
            "customerAccountNumber": "customerAccountNumber-1234",
            "currentOptimisticLock": 15
          },
          "name": {
            "title": "Sir",
            "firstForename": "Greg",
            "secondForename": "Greggory",
            "surname": "Greggson"
          },
          "address": {
            "addressLine1": "Flat 1 Rose House",
            "addressLine2": "Worthing",
            "addressPostcode": "BN12 4XL"
          }
        }
        """.stripMargin
      ).as[JsObject]
    )
  }

  "Show technical difficulties error page when claim overpayments call returns 500 error" in {
    EcospendStub.AuthStubs.stubEcospendAuth2xxSucceeded
    EcospendStub.AccountStub.stubAccountSummary2xxSucceeded(tdAll.consentId)
    P800RefundsExternalApiStub.isValid(tdAll.consentId, EventValue.Valid)
    EdhStub.getBankDetailsRiskResult(tdAll.getBankDetailsRiskResultRequest, tdAll.getBankDetailsRiskResultResponse)
    MakeBacsRepaymentStub.`internalServerError 500`(
      nino = tdAll.nino
    )

    pages.verifyingBankAccountPage.open()
    pages.verifyingBankAccountPage.assertPageIsDisplayedWithTechnicalDifficultiesError()
    EdhStub.verifyGetBankDetailsRiskResult(tdAll.claimId, tdAll.correlationId)
    MakeBacsRepaymentStub.verify(tdAll.nino, tdAll.correlationId)
    AuditConnectorStub.verifyEventAudited(
      AuditConnectorStub.bankClaimAttemptMadeAuditType,
      Json.parse(
        //format=JSON
        """
        {
          "outcome": {
            "isSuccessful": false,
            "actionsOutcome": {
              "ecospendFraudCheckIsSuccessful": true,
              "fuzzyNameMatchingIsSuccessful": true,
              "hmrcFraudCheckIsSuccessful": true,
              "claimOverpaymentIsSuccessful": false
            },
            "failureReasons": [
              "POST of 'http://localhost:11112/p800-refunds-backend/nps/make-bacs-repayment/LM001014C' returned 500. Response body: ''"
            ]
          },
          "userEnteredDetails": {
            "repaymentMethod": "bank",
            "chosenBank": "Barclays Personal",
            "p800Reference": 12345678,
            "nino": "LM001014C",
            "dob": "2000-01-01"
          },
          "repaymentAmount": 12.34,
          "repaymentInformation": {
            "reconciliationIdentifier": 123,
            "paymentNumber": 12345678,
            "payeNumber": "PayeNumber-123",
            "taxDistrictNumber": 717,
            "associatedPayableNumber": 1234,
            "customerAccountNumber": "customerAccountNumber-1234",
            "currentOptimisticLock": 15
          },
          "name": {
            "title": "Sir",
            "firstForename": "Greg",
            "secondForename": "Greggory",
            "surname": "Greggson"
          },
          "address": {
            "addressLine1": "Flat 1 Rose House",
            "addressLine2": "Worthing",
            "addressPostcode": "BN12 4XL"
          }
        }
        """.stripMargin
      ).as[JsObject]
    )
  }

  "Show technical difficulties error page when EDH endpoint fails" in {
    EcospendStub.AuthStubs.stubEcospendAuth2xxSucceeded
    EcospendStub.AccountStub.stubAccountSummary2xxSucceeded(tdAll.consentId)
    P800RefundsExternalApiStub.isValid(tdAll.consentId, EventValue.Valid)
    EdhStub.getBankDetailsRiskResult5xx(tdAll.getBankDetailsRiskResultRequest)

    pages.verifyingBankAccountPage.open()
    pages.verifyingBankAccountPage.assertPageIsDisplayedWithTechnicalDifficultiesError()
    EdhStub.verifyGetBankDetailsRiskResult(tdAll.claimId, tdAll.correlationId)
    MakeBacsRepaymentStub.verifyNone(tdAll.nino)
    AuditConnectorStub.verifyEventAudited(
      AuditConnectorStub.bankClaimAttemptMadeAuditType,
      Json.parse(
        //format=JSON
        """
        {
          "outcome": {
            "isSuccessful": false,
            "actionsOutcome": {
              "ecospendFraudCheckIsSuccessful": true,
              "hmrcFraudCheckIsSuccessful": false
            },
            "failureReasons": [
              "POST of 'http://localhost:11112/p800-refunds-backend/risking/claims/a3d35e50-ea17-4865-a2d2-38b0069b2665/bank-details' returned 503. Response body: '{\"reason\" : \"Dependent systems are currently not responding\"}'"
            ]
          },
          "userEnteredDetails": {
            "repaymentMethod": "bank",
            "chosenBank": "Barclays Personal",
            "p800Reference": 12345678,
            "nino": "LM001014C",
            "dob": "2000-01-01"
          },
          "repaymentAmount": 12.34,
          "repaymentInformation": {
            "reconciliationIdentifier": 123,
            "paymentNumber": 12345678,
            "payeNumber": "PayeNumber-123",
            "taxDistrictNumber": 717,
            "associatedPayableNumber": 1234,
            "customerAccountNumber": "customerAccountNumber-1234",
            "currentOptimisticLock": 15
          },
          "name": {
            "title": "Sir",
            "firstForename": "Greg",
            "secondForename": "Greggory",
            "surname": "Greggson"
          },
          "address": {
            "addressLine1": "Flat 1 Rose House",
            "addressLine2": "Worthing",
            "addressPostcode": "BN12 4XL"
          }
        }
        """.stripMargin
      ).as[JsObject]
    )
  }

  "Show technical difficulties page when 'Notify case management' call fails" in {
    EcospendStub.AuthStubs.stubEcospendAuth2xxSucceeded
    EcospendStub.AccountStub.stubAccountSummary2xxSucceeded(tdAll.consentId)
    P800RefundsExternalApiStub.isValid(tdAll.consentId, EventValue.Valid)

    val doNotPay: GetBankDetailsRiskResultResponse = tdAll.getBankDetailsRiskResultResponseDoNotPay
    EdhStub.getBankDetailsRiskResult(tdAll.getBankDetailsRiskResultRequest, doNotPay)
    CaseManagementStub.notifyCaseManagement4xx(tdAll.clientUId, tdAll.caseManagementRequest)

    pages.verifyingBankAccountPage.open()
    pages.verifyingBankAccountPage.assertPageIsDisplayedWithTechnicalDifficultiesError()

    EdhStub.verifyGetBankDetailsRiskResult(tdAll.claimId, tdAll.correlationId)
    CaseManagementStub.verifyNotifyCaseManagement(tdAll.clientUId, tdAll.correlationId)
    P800RefundsExternalApiStub.verifyIsValid(tdAll.consentId)
    MakeBacsRepaymentStub.verifyNone(tdAll.nino)
    AuditConnectorStub.verifyNoAuditEvent(AuditConnectorStub.bankClaimAttemptMadeAuditType)
  }

  "Show Refund Request not Submitted when BankAccountSummaryResponse 'account_identification' is None" in {
    val responseBody =
      //language=JSON
      s"""[{
          "id" : "${tdAll.consentId.value}",
          "type" : "Personal",
          "sub_type" : "CurrentAccount",
          "currency" : "GBP",
          "account_format" : "SortCode",
          "balance" : 123.7,
          "last_update_time" : "2059-11-25T16:33:51.88"
        }]""".stripMargin

    EcospendStub.AuthStubs.stubEcospendAuth2xxSucceeded
    EcospendStub.AccountStub.stubAccountSummaryWithJson2xxSucceeded(tdAll.consentId, responseBody)

    pages.verifyingBankAccountPage.open()
    pages.refundRequestNotSubmittedPage.assertPageIsDisplayed()

    P800RefundsExternalApiStub.verifyNoneIsValid(tdAll.consentId)
    MakeBacsRepaymentStub.verifyNone(tdAll.nino)
    AuditConnectorStub.verifyNoAuditEvent(AuditConnectorStub.bankClaimAttemptMadeAuditType)
  }

  "Show Refund Request Not Submitted Page when BankAccountSummaryResponse 'display_name' is None" in {
    val responseBody =
      //language=JSON
      s"""[{
          "id" : "${tdAll.consentId.value}",
          "bank_id" : "obie-barclays-personal",
          "type" : "Personal",
          "sub_type" : "CurrentAccount",
          "currency" : "GBP",
          "account_format" : "SortCode",
          "account_identification" : "44556610002333",
          "calculated_owner_name" : "Mr Greg Greggson",
          "account_owner_name" : "Greggson Gregory ",
          "balance" : 123.7,
          "last_update_time" : "2059-11-25T16:33:51.88",
          "parties" : []
        }]""".stripMargin

    EcospendStub.AuthStubs.stubEcospendAuth2xxSucceeded
    EcospendStub.AccountStub.stubAccountSummaryWithJson2xxSucceeded(tdAll.consentId, responseBody)
    P800RefundsExternalApiStub.isValid(tdAll.consentId, EventValue.Valid)
    MakeBacsRepaymentStub.`makeBacsRepayment 200 OK`(
      nino     = tdAll.nino,
      request  = tdAll.claimOverpaymentRequest,
      response = tdAll.claimOverpaymentResponse
    )
    val edhRiskRequest: GetBankDetailsRiskResultRequest = tdAll.getBankDetailsRiskResultRequest.copy(
      riskData = List(RiskDataObject(
        personType  = PersonType.Customer,
        person      = Some(Person(
          //TODO: according to the analysis all those fields come from the CitisenDetails API, which we don't call
          surname                 = Some(Surname(tdAll.surnameGreg)),
          firstForenameOrInitial  = Some(FirstForenameOrInitial(tdAll.firstNameGreg)),
          secondForenameOrInitial = Some(SecondForenameOrInitial(tdAll.secondForenameGreggson)),
          nino                    = tdAll.nino,
          dateOfBirth             = DateOfBirth(tdAll.`dateOfBirthFormatted YYYY-MM-DD`),
          title                   = Some(Title(tdAll.title)),
          address                 = None
        )),
        bankDetails = Some(BankDetails(
          bankAccountNumber     = Some(BankAccountNumber(tdAll.bankAccountNumber)),
          bankSortCode          = Some(BankSortCode(tdAll.sortCode)),
          bankAccountName       = None,
          buildingSocietyRef    = None,
          designatedAccountFlag = None,
          currency              = None
        ))
      ))
    )
    EdhStub.getBankDetailsRiskResult(edhRiskRequest, tdAll.getBankDetailsRiskResultResponse)

    pages.verifyingBankAccountPage.open()
    pages.refundRequestNotSubmittedPage.assertPageIsDisplayed()

    P800RefundsExternalApiStub.verifyIsValid(tdAll.consentId)
    MakeBacsRepaymentStub.verifyNone(tdAll.nino)
    AuditConnectorStub.verifyEventAudited(
      AuditConnectorStub.bankClaimAttemptMadeAuditType,
      Json.parse(
        """
        {
          "outcome": {
            "isSuccessful": false,
            "actionsOutcome": {
              "ecospendFraudCheckIsSuccessful": true,
              "fuzzyNameMatchingIsSuccessful": false,
              "hmrcFraudCheckIsSuccessful": true
            },
            "failureReasons": [
              "Ecospend names failed matching against NPS name"
            ]
          },
          "userEnteredDetails": {
            "repaymentMethod": "bank",
            "chosenBank": "Barclays Personal",
            "p800Reference": 12345678,
            "nino": "LM001014C",
            "dob": "2000-01-01"
          },
          "repaymentAmount": 12.34,
          "repaymentInformation": {
            "reconciliationIdentifier": 123,
            "paymentNumber": 12345678,
            "payeNumber": "PayeNumber-123",
            "taxDistrictNumber": 717,
            "associatedPayableNumber": 1234,
            "customerAccountNumber": "customerAccountNumber-1234",
            "currentOptimisticLock": 15
          },
          "name": {
            "title": "Sir",
            "firstForename": "Greg",
            "secondForename": "Greggory",
            "surname": "Greggson"
          },
          "address": {
            "addressLine1": "Flat 1 Rose House",
            "addressLine2": "Worthing",
            "addressPostcode": "BN12 4XL"
          }
        }
        """.stripMargin
      ).as[JsObject]
    )
  }

  "Fail fuzzy name matching when BankAccountSummaryResponse 'parties' AND 'account_owner_name' is None" in {
    val responseBody =
      //language=JSON
      s"""[{
          "id" : "${tdAll.consentId.value}",
          "type" : "Personal",
          "sub_type" : "CurrentAccount",
          "currency" : "GBP",
          "account_format" : "SortCode",
          "account_identification" : "44556610002333",
          "display_name" : "bank account display name",
          "balance" : 123.7,
          "last_update_time" : "2059-11-25T16:33:51.88"
        }]""".stripMargin

    EcospendStub.AuthStubs.stubEcospendAuth2xxSucceeded
    EcospendStub.AccountStub.stubAccountSummaryWithJson2xxSucceeded(tdAll.consentId, responseBody)
    P800RefundsExternalApiStub.isValid(tdAll.consentId, EventValue.Valid)
    EdhStub.getBankDetailsRiskResult(tdAll.getBankDetailsRiskResultRequest, tdAll.getBankDetailsRiskResultResponse)

    pages.verifyingBankAccountPage.open()
    pages.refundRequestNotSubmittedPage.assertPageIsDisplayed()

    EdhStub.verifyGetBankDetailsRiskResult(tdAll.claimId, tdAll.correlationId)
    P800RefundsExternalApiStub.verifyIsValid(tdAll.consentId)
    MakeBacsRepaymentStub.verifyNone(tdAll.nino)
    AuditConnectorStub.verifyEventAudited(
      AuditConnectorStub.bankClaimAttemptMadeAuditType,
      Json.parse(
        //format=JSON
        """
        {
          "outcome": {
            "isSuccessful": false,
            "actionsOutcome": {
              "ecospendFraudCheckIsSuccessful": true,
              "hmrcFraudCheckIsSuccessful": true,
              "fuzzyNameMatchingIsSuccessful": false
            },
            "failureReasons": [
              "Ecospend names failed matching against NPS name"
            ]
          },
          "userEnteredDetails": {
            "chosenBank": "Barclays Personal",
            "p800Reference": 12345678,
            "nino": "LM001014C",
            "dob": "2000-01-01"
          },
          "repaymentAmount": 12.34,
          "repaymentInformation": {
            "reconciliationIdentifier": 123,
            "paymentNumber": 12345678,
            "payeNumber": "PayeNumber-123",
            "taxDistrictNumber": 717,
            "associatedPayableNumber": 1234,
            "customerAccountNumber": "customerAccountNumber-1234",
            "currentOptimisticLock": 15
          },
          "name": {
            "title": "Sir",
            "firstForename": "Greg",
            "secondForename": "Greggory",
            "surname": "Greggson"
          },
          "address": {
            "addressLine1": "Flat 1 Rose House",
            "addressLine2": "Worthing",
            "addressPostcode": "BN12 4XL"
          }
        }
        """.stripMargin
      ).as[JsObject]
    )
  }

  "Fall back to account_owner_name when BankAccountSummaryResponse 'parties' is None" in {
    val responseBody =
      //language=JSON
      s"""[{
          "id" : "${tdAll.consentId.value}",
          "bank_id" : "obie-barclays-personal",
          "type" : "Personal",
          "sub_type" : "CurrentAccount",
          "currency" : "GBP",
          "account_format" : "SortCode",
          "account_identification" : "44556610002333",
          "calculated_owner_name" : "Mr Greg Greggson",
          "account_owner_name" : "Margaretta Greggson,Greg Greggory Greggson",
          "display_name" : "bank account display name",
          "balance" : 123.7,
          "last_update_time" : "2059-11-25T16:33:51.88"
        }]""".stripMargin

    EcospendStub.AuthStubs.stubEcospendAuth2xxSucceeded
    EcospendStub.AccountStub.stubAccountSummaryWithJson2xxSucceeded(tdAll.consentId, responseBody)
    P800RefundsExternalApiStub.isValid(tdAll.consentId, EventValue.NotReceived)
    MakeBacsRepaymentStub.`makeBacsRepayment 200 OK`(
      nino     = tdAll.nino,
      request  = tdAll.claimOverpaymentRequest,
      response = tdAll.claimOverpaymentResponse
    )
    EdhStub.getBankDetailsRiskResult(tdAll.getBankDetailsRiskResultRequest, tdAll.getBankDetailsRiskResultResponse)
    pages.verifyingBankAccountPage.open()
    pages.verifyingBankAccountPage.assertPageIsDisplayed()
    EdhStub.verifyGetBankDetailsRiskResult(tdAll.claimId, tdAll.correlationId)
    P800RefundsExternalApiStub.verifyIsValid(tdAll.consentId)
    MakeBacsRepaymentStub.verifyNone(tdAll.nino)
    AuditConnectorStub.verifyNoAuditEvent(AuditConnectorStub.bankClaimAttemptMadeAuditType)
  }

  "Fall back to 'account_owner_name' when BankAccountSummaryResponse 'parties.full_legal_name' is None" in {
    val responseBody =
      //language=JSON
      s"""[{
          "id" : "${tdAll.consentId.value}",
          "type" : "Personal",
          "sub_type" : "CurrentAccount",
          "currency" : "GBP",
          "account_format" : "SortCode",
          "account_identification" : "44556610002333",
          "display_name" : "bank account display name",
          "account_owner_name" : "Margaretta Greggson,Greg Greggory Greggson",
          "balance" : 123.7,
          "last_update_time" : "2059-11-25T16:33:51.88",
          "parties" : [{
            "name" : "Greg Greggson"
          }]
        }]""".stripMargin

    EcospendStub.AuthStubs.stubEcospendAuth2xxSucceeded
    EcospendStub.AccountStub.stubAccountSummaryWithJson2xxSucceeded(tdAll.consentId, responseBody)
    P800RefundsExternalApiStub.isValid(tdAll.consentId, EventValue.Valid)
    DateCalculatorStub.addWorkingDays()
    MakeBacsRepaymentStub.`makeBacsRepayment 200 OK`(
      nino     = tdAll.nino,
      request  = tdAll.claimOverpaymentRequest,
      response = tdAll.claimOverpaymentResponse
    )
    EdhStub.getBankDetailsRiskResult(tdAll.getBankDetailsRiskResultRequest, tdAll.getBankDetailsRiskResultResponse)

    pages.verifyingBankAccountPage.open()
    pages.requestReceivedBankTransferPage.assertPageIsDisplayedForBankTransfer()

    EdhStub.verifyGetBankDetailsRiskResult(tdAll.claimId, tdAll.correlationId)
    P800RefundsExternalApiStub.verifyIsValid(tdAll.consentId)
    MakeBacsRepaymentStub.verify(tdAll.nino, tdAll.correlationId)
    AuditConnectorStub.verifyEventAudited(
      AuditConnectorStub.bankClaimAttemptMadeAuditType,
      Json.parse(
        //format=JSON
        """
        {
          "outcome": {
            "isSuccessful": true,
            "actionsOutcome": {
              "ecospendFraudCheckIsSuccessful": true,
              "fuzzyNameMatchingIsSuccessful": true,
              "hmrcFraudCheckIsSuccessful": true,
              "claimOverpaymentIsSuccessful": true
            }
          },
          "userEnteredDetails": {
            "chosenBank": "Barclays Personal",
            "p800Reference": 12345678,
            "nino": "LM001014C",
            "dob": "2000-01-01"
          },
          "repaymentAmount": 12.34,
          "repaymentInformation": {
            "reconciliationIdentifier": 123,
            "paymentNumber": 12345678,
            "payeNumber": "PayeNumber-123",
            "taxDistrictNumber": 717,
            "associatedPayableNumber": 1234,
            "customerAccountNumber": "customerAccountNumber-1234",
            "currentOptimisticLock": 15
          },
          "name": {
            "title": "Sir",
            "firstForename": "Greg",
            "secondForename": "Greggory",
            "surname": "Greggson"
          },
          "address": {
            "addressLine1": "Flat 1 Rose House",
            "addressLine2": "Worthing",
            "addressPostcode": "BN12 4XL"
          }
        }
        """.stripMargin
      ).as[JsObject]
    )
  }

  "Fall back to 'account_owner_name' when BankAccountSummaryResponse one 'parties.full_legal_name' is None" in {
    val responseBody =
      //language=JSON
      s"""[{
          "id" : "${tdAll.consentId.value}",
          "type" : "Personal",
          "sub_type" : "CurrentAccount",
          "currency" : "GBP",
          "account_format" : "SortCode",
          "account_identification" : "44556610002333",
          "display_name" : "bank account display name",
          "account_owner_name" : "Margaretta Greggson,Greg Greggory Greggson",
          "balance" : 123.7,
          "last_update_time" : "2059-11-25T16:33:51.88",
          "parties" : [
            {
              "name" : "Greg Greggson"
            },
            {
              "name" : "Margaret Greggson",
              "full_legal_name" : "Margaretta Greggson"
            }
          ]
        }]""".stripMargin

    EcospendStub.AuthStubs.stubEcospendAuth2xxSucceeded
    EcospendStub.AccountStub.stubAccountSummaryWithJson2xxSucceeded(tdAll.consentId, responseBody)
    P800RefundsExternalApiStub.isValid(tdAll.consentId, EventValue.Valid)
    DateCalculatorStub.addWorkingDays()
    MakeBacsRepaymentStub.`makeBacsRepayment 200 OK`(
      nino     = tdAll.nino,
      request  = tdAll.claimOverpaymentRequest,
      response = tdAll.claimOverpaymentResponse
    )
    EdhStub.getBankDetailsRiskResult(tdAll.getBankDetailsRiskResultRequest, tdAll.getBankDetailsRiskResultResponse)

    pages.verifyingBankAccountPage.open()
    pages.requestReceivedBankTransferPage.assertPageIsDisplayedForBankTransfer()

    EdhStub.verifyGetBankDetailsRiskResult(tdAll.claimId, tdAll.correlationId)
    P800RefundsExternalApiStub.verifyIsValid(tdAll.consentId)
    MakeBacsRepaymentStub.verify(tdAll.nino, tdAll.correlationId)
    AuditConnectorStub.verifyEventAudited(
      AuditConnectorStub.bankClaimAttemptMadeAuditType,
      Json.parse(
        //format=JSON
        """
        {
          "outcome": {
            "isSuccessful": true,
            "actionsOutcome": {
              "ecospendFraudCheckIsSuccessful": true,
              "fuzzyNameMatchingIsSuccessful": true,
              "hmrcFraudCheckIsSuccessful": true,
              "claimOverpaymentIsSuccessful": true
            }
          },
          "userEnteredDetails": {
            "chosenBank": "Barclays Personal",
            "p800Reference": 12345678,
            "nino": "LM001014C",
            "dob": "2000-01-01"
          },
          "repaymentAmount": 12.34,
          "repaymentInformation": {
            "reconciliationIdentifier": 123,
            "paymentNumber": 12345678,
            "payeNumber": "PayeNumber-123",
            "taxDistrictNumber": 717,
            "associatedPayableNumber": 1234,
            "customerAccountNumber": "customerAccountNumber-1234",
            "currentOptimisticLock": 15
          },
          "name": {
            "title": "Sir",
            "firstForename": "Greg",
            "secondForename": "Greggory",
            "surname": "Greggson"
          },
          "address": {
            "addressLine1": "Flat 1 Rose House",
            "addressLine2": "Worthing",
            "addressPostcode": "BN12 4XL"
          }
        }
        """.stripMargin
      ).as[JsObject]
    )
  }

  "Redirect to bank transfer 'Request received' page when unused optional BankAccountSummaryResponse field are None" in {

    // Response body is missing "bank_id", "calculated_owner_name", "account_owner_name"
    val responseBody =
      //language=JSON
      s"""[{
          "id" : "${tdAll.consentId.value}",
          "type" : "Personal",
          "sub_type" : "CurrentAccount",
          "currency" : "GBP",
          "account_format" : "SortCode",
          "account_identification" : "44556610002333",
          "display_name" : "bank account display name",
          "balance" : 123.7,
          "last_update_time" : "2059-11-25T16:33:51.88",
          "parties" : [ {
            "name" : "Greg Greggson",
            "full_legal_name" : "Greg Greggory Greggson"
          }, {
            "name" : "Margaret Greggson",
            "full_legal_name" : "Margaretta Greggson"
          } ]
        }]""".stripMargin

    EcospendStub.AuthStubs.stubEcospendAuth2xxSucceeded
    EcospendStub.AccountStub.stubAccountSummaryWithJson2xxSucceeded(tdAll.consentId, responseBody)
    P800RefundsExternalApiStub.isValid(tdAll.consentId, EventValue.Valid)
    EdhStub.getBankDetailsRiskResult(tdAll.getBankDetailsRiskResultRequest, tdAll.getBankDetailsRiskResultResponse)
    DateCalculatorStub.addWorkingDays()
    MakeBacsRepaymentStub.`makeBacsRepayment 200 OK`(
      nino     = tdAll.nino,
      request  = tdAll.claimOverpaymentRequest,
      response = tdAll.claimOverpaymentResponse
    )

    pages.verifyingBankAccountPage.open()
    pages.requestReceivedBankTransferPage.assertPageIsDisplayedForBankTransfer()

    EdhStub.verifyGetBankDetailsRiskResult(tdAll.claimId, tdAll.correlationId)
    P800RefundsExternalApiStub.verifyIsValid(tdAll.consentId)
    MakeBacsRepaymentStub.verify(tdAll.nino, tdAll.correlationId)
    DateCalculatorStub.verifyAddWorkingDays()
    AuditConnectorStub.verifyEventAudited(
      AuditConnectorStub.bankClaimAttemptMadeAuditType,
      Json.parse(
        //format=JSON
        """
        {
          "outcome": {
            "isSuccessful": true,
            "actionsOutcome": {
              "ecospendFraudCheckIsSuccessful": true,
              "fuzzyNameMatchingIsSuccessful": true,
              "hmrcFraudCheckIsSuccessful": true,
              "claimOverpaymentIsSuccessful": true
            }
          },
          "userEnteredDetails": {
            "chosenBank": "Barclays Personal",
            "p800Reference": 12345678,
            "nino": "LM001014C",
            "dob": "2000-01-01"
          },
          "repaymentAmount": 12.34,
          "repaymentInformation": {
            "reconciliationIdentifier": 123,
            "paymentNumber": 12345678,
            "payeNumber": "PayeNumber-123",
            "taxDistrictNumber": 717,
            "associatedPayableNumber": 1234,
            "customerAccountNumber": "customerAccountNumber-1234",
            "currentOptimisticLock": 15
          },
          "name": {
            "title": "Sir",
            "firstForename": "Greg",
            "secondForename": "Greggory",
            "surname": "Greggson"
          },
          "address": {
            "addressLine1": "Flat 1 Rose House",
            "addressLine2": "Worthing",
            "addressPostcode": "BN12 4XL"
          }
        }
        """.stripMargin
      ).as[JsObject]
    )
  }
}

class VerifyingYourBankAccountFeatureFlagOffPageSpec extends ItSpec {

  override lazy val configMap: Map[String, Any] = super.configMap ++ Map("feature-flags.isCaseManagementApiEnabled" -> false)

  override def beforeEach(): Unit = {
    super.beforeEach()
    addJourneyIdToSession(tdAll.journeyId)
    upsertJourneyToDatabase(tdAll.BankTransfer.journeyBankAccountConsentSuccessfulNameMatch)
  }

  "Should not make the 'Notify case management' call when the feature flag is off" in {
    EcospendStub.AuthStubs.stubEcospendAuth2xxSucceeded
    EcospendStub.AccountStub.stubAccountSummary2xxSucceeded(tdAll.consentId)
    P800RefundsExternalApiStub.isValid(tdAll.consentId, EventValue.Valid)
    DateCalculatorStub.addWorkingDays()

    val doNotPay: GetBankDetailsRiskResultResponse = tdAll.getBankDetailsRiskResultResponseDoNotPay
    EdhStub.getBankDetailsRiskResult(tdAll.getBankDetailsRiskResultRequest, doNotPay)
    CaseManagementStub.notifyCaseManagement2xx(tdAll.clientUId, tdAll.caseManagementRequest)
    NpsSuspendOverpaymentStub.suspendOverpayment(tdAll.nino, tdAll.suspendOverpaymentRequest)

    pages.verifyingBankAccountPage.open()
    pages.requestReceivedBankTransferPage.assertPageIsDisplayedForBankTransfer()

    EdhStub.verifyGetBankDetailsRiskResult(tdAll.claimId, tdAll.correlationId)
    CaseManagementStub.verifyNotifyCaseManagement(tdAll.clientUId, tdAll.correlationId, 0) //Checking case management isn't called
    NpsSuspendOverpaymentStub.verify(tdAll.nino, tdAll.correlationId)
    P800RefundsExternalApiStub.verifyIsValid(tdAll.consentId)
    MakeBacsRepaymentStub.verifyNone(tdAll.nino)
    AuditConnectorStub.verifyEventAudited(
      AuditConnectorStub.bankClaimAttemptMadeAuditType,
      Json.parse(
        //format=JSON
        """
        {
          "outcome": {
            "isSuccessful": false,
            "actionsOutcome": {
              "ecospendFraudCheckIsSuccessful": true,
              "fuzzyNameMatchingIsSuccessful": true,
              "hmrcFraudCheckIsSuccessful": false
            },
            "failureReasons": [
              "EDH indicated DoNotPay"
            ]
          },
          "userEnteredDetails": {
            "chosenBank": "Barclays Personal",
            "p800Reference": 12345678,
            "nino": "LM001014C",
            "dob": "2000-01-01"
          },
          "repaymentAmount": 12.34,
          "repaymentInformation": {
            "reconciliationIdentifier": 123,
            "paymentNumber": 12345678,
            "payeNumber": "PayeNumber-123",
            "taxDistrictNumber": 717,
            "associatedPayableNumber": 1234,
            "customerAccountNumber": "customerAccountNumber-1234",
            "currentOptimisticLock": 15
          },
          "name": {
            "title": "Sir",
            "firstForename": "Greg",
            "secondForename": "Greggory",
            "surname": "Greggson"
          },
          "address": {
            "addressLine1": "Flat 1 Rose House",
            "addressLine2": "Worthing",
            "addressPostcode": "BN12 4XL"
          }
        }
        """.stripMargin
      ).as[JsObject]
    )
  }
}

