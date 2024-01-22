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

import org.openqa.selenium.WebDriver
import pagespecs.pages._
import pagespecs.pages.testonly.{GeneralIncomeTaxEnquiriesPage, GovUkRouteInPage, PtaSignInPage}

class Pages(baseUrl: String)(implicit webDriver: WebDriver) {

  private val bankTransferRelativeUrl: String = "bank-transfer"
  private val chequeRelativeUrl: String = "cheque"

  val startEndpoint: Endpoint = new Endpoint(baseUrl = baseUrl, path = "/get-an-income-tax-refund/start")

  val doYouWantToSignInPage = new DoYouWantToSignInPage(baseUrl = baseUrl)
  val doYouWantYourRefundViaBankTransferPage = new DoYouWantYourRefundViaBankTransferPage(baseUrl = baseUrl)

  val weNeedYouToConfirmYourIdentityBankTransferPage = new WeNeedYouToConfirmYourIdentityPage(baseUrl            = baseUrl, pathForJourneyType = bankTransferRelativeUrl)
  val weNeedYouToConfirmYourIdentityChequePage = new WeNeedYouToConfirmYourIdentityPage(baseUrl            = baseUrl, pathForJourneyType = chequeRelativeUrl)

  val whatIsYourP800ReferenceBankTransferPage = new WhatIsYourP800ReferencePage(baseUrl            = baseUrl, pathForJourneyType = bankTransferRelativeUrl)
  val whatIsYourP800ReferenceChequePage = new WhatIsYourP800ReferencePage(baseUrl            = baseUrl, pathForJourneyType = chequeRelativeUrl)

  val whatIsYourNationalInsuranceNumberBankTransferPage = new WhatIsYourNationalInsuranceNumberPage(baseUrl            = baseUrl, pathForJourneyType = bankTransferRelativeUrl)
  val whatIsYourNationalInsuranceNumberChequePage = new WhatIsYourNationalInsuranceNumberPage(baseUrl            = baseUrl, pathForJourneyType = chequeRelativeUrl)

  // bank transfer only
  val whatIsYourDateOfBirthPage = new WhatIsYourDateOfBirthPage(baseUrl = baseUrl)

  val checkYourAnswersBankTransferPage = new CheckYourAnswersPage(baseUrl            = baseUrl, pathForJourneyType = bankTransferRelativeUrl)
  val checkYourAnswersChequePage = new CheckYourAnswersPage(baseUrl            = baseUrl, pathForJourneyType = chequeRelativeUrl)

  val weHaveConfirmedYourIdentityBankTransferPage = new WeHaveConfirmedYourIdentityPage(baseUrl            = baseUrl, pathForJourneyType = bankTransferRelativeUrl)
  val weHaveConfirmedYourIdentityChequePage = new WeHaveConfirmedYourIdentityPage(baseUrl            = baseUrl, pathForJourneyType = chequeRelativeUrl)

  val weCannotConfirmYourIdentityPage = new WeCannotConfirmYourIdentityPage(baseUrl = baseUrl)

  val yourRequestHasNotBeenSubmittedPage = new YourRequestHasNotBeenSubmittedPage(baseUrl = baseUrl)
  val verifyBankAccountPage = new VerifyBankAccountPage(baseUrl = baseUrl)
  val chooseAnotherWayToReceiveYourRefundPage = new ChooseAnotherWayToReceiveYourRefundPage(baseUrl = baseUrl)
  val chooseAnotherWayToReceiveYourRefundPtaOrChequePage = new ChooseAnotherWayToReceiveYourRefundPtaOrChequePage(baseUrl = baseUrl)
  val chooseAnotherWayToReceiveYourRefundPtaOrBankTransferPage = new ChooseAnotherWayToReceiveYourRefundPtaOrBankTransferPage(baseUrl = baseUrl)
  val giveYourPermissionPage = new GiveYourPermissionPage(baseUrl = baseUrl)

  val completeYourRefundRequestPage = new CompleteYourRefundRequestPage(baseUrl = baseUrl)

  val whatIsTheNameOfYourBankAccountPage = new WhatIsTheNameOfYourBankAccountPage(baseUrl = baseUrl)

  val requestReceivedBankTransferPage = new RequestReceivedPage(baseUrl            = baseUrl, pathForJourneyType = bankTransferRelativeUrl)
  val requestReceivedChequePage = new RequestReceivedPage(baseUrl            = baseUrl, pathForJourneyType = chequeRelativeUrl)

  // Page Stubs
  val govUkRouteInPage = new GovUkRouteInPage(baseUrl = baseUrl)
  val ptaSignInPage = new PtaSignInPage(baseUrl = baseUrl)
  val generalIncomeTaxEnquiriesPage = new GeneralIncomeTaxEnquiriesPage(baseUrl = baseUrl)
}
