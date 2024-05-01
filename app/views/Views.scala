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

import javax.inject.Inject

class Views @Inject() (
    val doYouWantToSignInPage:                       views.html.DoYouWantToSignInPage,
    val enterYourP800ReferencePage:                  views.html.EnterYourP800ReferencePage,
    val checkYourAnswersPage:                        views.html.identityverification.CheckYourAnswersPage,
    val requestReceivedChequePage:                   views.html.chequejourney.RequestReceivedPage,
    val confirmYourIdentityPage:                     views.html.identityverification.ConfirmYourIdentityPage,
    val enterYourDateOfBirthPage:                    views.html.identityverification.EnterYourDateOfBirthPage,
    val enterYourNationalInsuranceNumberPage:        views.html.identityverification.EnterYourNationalInsuranceNumberPage,
    val yourIdentityIsConfirmedPage:                 views.html.identityverification.YourIdentityIsConfirmedPage,
    val cannotConfirmYourIdentityTryAgainPage:       views.html.identityverification.CannotConfirmYourIdentityTryAgainPage,
    val noMoreAttemptsLeftToConfirmYourIdentityPage: views.html.identityverification.NoMoreAttemptsLeftToConfirmYourIdentityPage,
    val refundRequestNotSubmittedPage:               views.html.RefundRequestNotSubmittedPage,
    val giveYourPermissionPage:                      views.html.GiveYourPermissionPage,
    val verifyingYourBankAccountPage:                views.html.VerifyingYourBankAccountPage,
    val doYouWantYourRefundViaBankTransferPage:      views.html.DoYouWantYourRefundViaBankTransferPage,
    val chooseAnotherWayToReceiveYourRefundPage:     views.html.chooseanotherway.ChooseAnotherWayToReceiveYourRefundPage,
    val claimYourRefundByBankTransfer:               views.html.chooseanotherway.ClaimYourRefundByBankTransfer,
    val enterNameOfYourBankPage:                     views.html.EnterNameOfYourBankPage,
    val requestReceivedBankTransferPage:             views.html.banktransferjourney.RequestReceivedPage,
    val youCannotConfirmYourIdentityDetailsYet:      views.html.YouCannotConfirmYourIdentityYet,
    val yourRefundRequestHasNotBeenSubmitted:        views.html.YourRefundRequestHasNotBeenSubmitted,
    val isYourAddressUpToDatePage:                   views.html.chequejourney.IsYourAddressUpToDatePage,
    val updateYourAddressPage:                       views.html.chequejourney.UpdateYourAddressPage,
    val thereIsAProblemPage:                         views.html.ThereIsAProblemPage,
    val refundCancelledPage:                         views.html.RefundCancelledPage
)

class ViewsTestOnly @Inject() (
    val landingTestOnlyPage:               views.html.testonly.LandingTestOnlyPage,
    val incomeTaxGeneralEnquiriesStubPage: views.html.testonly.IncomeTaxGeneralEnquiriesStubPage,
    val govUkStubPage:                     views.html.testonly.GovUkStubPage,
    val ptaSignInStubPage:                 views.html.testonly.PtaSignInStubPage,
    val bankStubPage:                      views.html.testonly.BankStubPage,
    val webhookNotificationSimulationPage: views.html.testonly.WebhookNotificationSimulationPage,
    val feedbackFrontendStubPage:          views.html.testonly.FeedbackFrontendStubPage
)
