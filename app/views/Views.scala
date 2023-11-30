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

package views

import views.html.testonly.{GovUkStubPage, IncomeTaxGeneralEnquiriesStubPage, PtaSignInStubPage}

import javax.inject.Inject

class Views @Inject() (
    //TODO: remove once all pages are developed
    val underConstructionPage:                  views.html.UnderConstructionPage,
    val doYouWantToSignInPage:                  views.html.DoYouWantToSignInPage,
    val enterP800ReferencePage:                 views.html.EnterP800ReferencePage,
    val checkYourReferencePage:                 views.html.CheckYourReferencePage,
    val checkYourAnswersPage:                   views.html.CheckYourAnswersPage,
    val cannotConfirmReferencePage:             views.html.CannotConfirmReferencePage,
    val yourChequeWillBePostedToYouPage:        views.html.chequejourney.YourChequeWillBePostedToYouPage,
    val requestReceivedPage:                    views.html.chequejourney.RequestReceivedPage,
    val requestNotSubmittedPage:                views.html.RequestNotSubmittedPage,
    val weNeedYouToConfirmYourIdentityPage:     views.html.WeNeedYouToConfirmYourIdentityPage,
    val whatIsYourFullNamePage:                 views.html.WhatIsYourFullNamePage,
    val whatIsYourDateOfBirthPage:              views.html.WhatIsYourDateOfBirthPage,
    val whatIsYourNationalInsuranceNumberPage:  views.html.WhatIsYourNationalInsuranceNumberPage,
    val doYouWantYourRefundViaBankTransferPage: views.html.DoYouWantYourRefundViaBankTransferPage,
    val weHaveConfirmedYourIdentityPage:        views.html.WeHaveConfirmedYourIdentityPage

)

class ViewsTestOnly @Inject() (
    val landingTestOnlyPage:               views.html.testonly.LandingTestOnlyPage,
    val incomeTaxGeneralEnquiriesStubPage: IncomeTaxGeneralEnquiriesStubPage,
    val govUkStubPage:                     GovUkStubPage,
    val ptaSignInStubPage:                 PtaSignInStubPage
)
