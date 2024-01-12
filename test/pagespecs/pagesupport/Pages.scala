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

  val startEndpoint: Endpoint = new Endpoint(baseUrl = baseUrl, path = "/get-an-income-tax-refund/start")

  val doYouWantToSignInPage = new DoYouWantToSignInPage(baseUrl = baseUrl)
  val doYouWantYourRefundViaBankTransferPage = new DoYouWantYourRefundViaBankTransferPage(baseUrl = baseUrl)

  val whatIsYourP800ReferencePage = new WhatIsYourP800ReferencePage(baseUrl = baseUrl)
  val whatIsYourNationalInsuranceNumberPage = new WhatIsYourNationalInsuranceNumberPage(baseUrl = baseUrl)

  val cannotConfirmReferencePage = new CannotConfirmReferencePage(baseUrl = baseUrl)
  val weNeedYouToConfirmYourIdentityPage = new WeNeedYouToConfirmYourIdentityPage(baseUrl = baseUrl)
  val whatIsYourDateOfBirthPage = new WhatIsYourDateOfBirthPage(baseUrl = baseUrl)
  val weHaveConfirmedYourIdentityPage = new WeHaveConfirmedYourIdentityPage(baseUrl = baseUrl)
  val weCannotConfirmYourIdentityPage = new WeCannotConfirmYourIdentityPage(baseUrl = baseUrl)
  val requestNotSubmittedPage = new RequestNotSubmittedPage(baseUrl = baseUrl)
  val verifyBankAccountPage = new VerifyBankAccountPage(baseUrl = baseUrl)
  val chooseAnotherWayToReceiveYourRefundPage = new ChooseAnotherWayToReceiveYourRefundPage(baseUrl = baseUrl)
  val giveYourPermissionPage = new GiveYourPermissionPage(baseUrl = baseUrl)

  val completeYourRefundRequestPage = new CompleteYourRefundRequestPage(baseUrl = baseUrl)
  val chequeRequestReceivedPage = new ChequeRequestReceivedPage(baseUrl = baseUrl)
  val bankTransferRequestReceivedPage = new BankTransferRequestReceivedPage(baseUrl = baseUrl)

  val checkYourAnswersPage = new CheckYourAnswersPage(baseUrl = baseUrl)
  val whatIsTheNameOfYourBankAccountPage = new WhatIsTheNameOfYourBankAccountPage(baseUrl = baseUrl)

  // Page Stubs
  val govUkRouteInPage = new GovUkRouteInPage(baseUrl = baseUrl)
  val ptaSignInPage = new PtaSignInPage(baseUrl = baseUrl)
  val generalIncomeTaxEnquiriesPage = new GeneralIncomeTaxEnquiriesPage(baseUrl = baseUrl)
}
