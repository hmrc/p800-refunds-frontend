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

package pagespecs.pagesupport

import models.ecospend.consent.ConsentStatus
import org.openqa.selenium.WebDriver
import pagespecs.pages._
import pagespecs.pages.testonly._
import testdata.TdAll

class Pages(baseUrl: String)(implicit webDriver: WebDriver) {

  private val bankTransferRelativeUrl: String = "bank-transfer"
  private val chequeRelativeUrl: String = "cheque"

  val startEndpoint: Endpoint = new Endpoint(baseUrl = baseUrl, path = "/get-an-income-tax-refund/start")

  val doYouWantToSignInPage = new DoYouWantToSignInPage(baseUrl = baseUrl)
  val doYouWantYourRefundViaBankTransferPage = new DoYouWantYourRefundViaBankTransferPage(baseUrl = baseUrl)

  val weNeedYouToConfirmYourIdentityBankTransferPage = new WeNeedYouToConfirmYourIdentityPage(baseUrl            = baseUrl, pathForJourneyType = bankTransferRelativeUrl)
  val weNeedYouToConfirmYourIdentityChequePage = new WeNeedYouToConfirmYourIdentityPage(baseUrl            = baseUrl, pathForJourneyType = chequeRelativeUrl)

  val whatIsYourP800ReferenceBankTransferPage = new EnterYourP800ReferencePage(baseUrl            = baseUrl, pathForJourneyType = bankTransferRelativeUrl)
  val whatIsYourP800ReferenceChequePage = new EnterYourP800ReferencePage(baseUrl            = baseUrl, pathForJourneyType = chequeRelativeUrl)

  val enterYourNationalInsuranceNumberBankTransferPage = new EnterYourNationalInsuranceNumberPage(baseUrl            = baseUrl, pathForJourneyType = bankTransferRelativeUrl)
  val enterYourNationalInsuranceNumberChequePage = new EnterYourNationalInsuranceNumberPage(baseUrl            = baseUrl, pathForJourneyType = chequeRelativeUrl)

  // bank transfer only
  val enterYourDateOfBirthPage = new EnterYourDateOfBirthPage(baseUrl = baseUrl)

  val checkYourAnswersBankTransferPage = new CheckYourAnswersPage(baseUrl            = baseUrl, pathForJourneyType = bankTransferRelativeUrl)
  val checkYourAnswersChequePage = new CheckYourAnswersPage(baseUrl            = baseUrl, pathForJourneyType = chequeRelativeUrl)

  val yourIdentityIsConfirmedBankTransferPage = new YourIdentityIsConfirmedPage(baseUrl            = baseUrl, pathForJourneyType = bankTransferRelativeUrl)
  val yourIdentityIsConfirmedChequePage = new YourIdentityIsConfirmedPage(baseUrl            = baseUrl, pathForJourneyType = chequeRelativeUrl)

  val weCannotConfirmYourIdentityBankTransferPage = new WeCannotConfirmYourIdentityPage(baseUrl            = baseUrl, pathForJourneyType = bankTransferRelativeUrl)
  val weCannotConfirmYourIdentityChequePage = new WeCannotConfirmYourIdentityPage(baseUrl            = baseUrl, pathForJourneyType = chequeRelativeUrl)

  val noMoreAttemptsLeftToConfirmYourIdentityBankTransferPage = new NoMoreAttemptsLeftToConfirmYourIdentityPage(baseUrl            = baseUrl, pathForJourneyType = bankTransferRelativeUrl)
  val noMoreAttemptsLeftToConfirmYourIdentityChequePage = new NoMoreAttemptsLeftToConfirmYourIdentityPage(baseUrl            = baseUrl, pathForJourneyType = chequeRelativeUrl)

  val refundRequestNotSubmittedPage = new RefundRequestNotSubmittedPage(baseUrl = baseUrl)
  val verifyBankAccountPage = new VerifyBankAccountPage(baseUrl       = baseUrl, consentStatus = ConsentStatus.Authorised, TdAll.tdAll.consentId, TdAll.tdAll.bankReferenceId)

  //bank transfer specific page
  val chooseAnotherWayToReceiveYourRefundPage = new ChooseAnotherWayToReceiveYourRefundPage(baseUrl = baseUrl)
  //cheque specific page
  val claimYourRefundByBankTransferPage = new ClaimYourRefundByBankTransferPage(baseUrl = baseUrl)

  val giveYourPermissionPage = new GiveYourPermissionPage(baseUrl = baseUrl)

  val completeYourRefundRequestPage = new CompleteYourRefundRequestPage(baseUrl = baseUrl)

  val youCannotConfirmYourSecurityDetailsYetSpec = new YouCannotConfirmYourSecurityDetailsYetPage(baseUrl = baseUrl)

  val updateYourAddressPage = new UpdateYourAddressPage(baseUrl = baseUrl)

  val yourRefundRequestHasNotBeenSubmittedSpec = new YourRefundRequestHasNotBeenSubmittedPage(baseUrl = baseUrl)

  val thereIsAProblemPage = new ThereIsAProblemPage(baseUrl = baseUrl)

  val isYourAddressUpToDate = new IsYourAddressUpToDatePage(baseUrl = baseUrl)

  val enterTheNameOfYourBankAccountPage = new EnterTheNameOfYourBankAccountPage(baseUrl = baseUrl)

  val requestReceivedBankTransferPage = new RequestReceivedPage(baseUrl            = baseUrl, pathForJourneyType = bankTransferRelativeUrl)
  val requestReceivedChequePage = new RequestReceivedPage(baseUrl            = baseUrl, pathForJourneyType = chequeRelativeUrl)

  // Page Stubs
  val govUkRouteInPage = new GovUkRouteInPage(baseUrl = baseUrl)
  val ptaSignInPage = new PtaSignInPage(baseUrl = baseUrl)
  val generalIncomeTaxEnquiriesPage = new GeneralIncomeTaxEnquiriesPage(baseUrl = baseUrl)
  val bankStubPage = new BankStubPage(baseUrl = baseUrl)
}
