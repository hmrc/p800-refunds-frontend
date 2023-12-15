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

package controllers

import models.journeymodels._
import play.api.mvc.Results.Redirect
import play.api.mvc.{Call, Request, Result}
import util.JourneyLogger

import scala.concurrent.Future

object JourneyRouter {

  /**
   * Handle journey in final state on non final page.
   *
   * The case is when user is on final page (i.e. RequestReceived) and
   * clicks back. We ignore his request and redirect back to the final page.
   */
  def handleFinalJourneyOnNonFinalPageF(journey: JTerminal): Future[Result] = Future.successful(handleFinalJourneyOnNonFinalPage(journey))

  def handleFinalJourneyOnNonFinalPage(journey: JTerminal): Result = journey match {
    case _: JourneyYourChequeWillBePostedToYou => Redirect(controllers.routes.RequestReceivedController.chequeGet)
    case _: JourneyApprovedRefund              => Redirect(controllers.routes.RequestReceivedController.bankTransferGet)
  }

  /**
   * Based on the journey state it redirects to the corresponding to this state page.
   */
  def sendToCorrespondingPage(journey: Journey)(implicit request: Request[_]): Result = {

    val redirectLocation: Call = journey match {
      case _: JourneyStarted                               => controllers.routes.DoYouWantToSignInController.get
      case _: JourneyDoYouWantToSignInNo                   => controllers.routes.EnterP800ReferenceController.get
      case _: JourneyWhatIsYourP800Reference               => controllers.routes.CheckYourReferenceController.get
      case _: JourneyCheckYourReferenceValid               => controllers.routes.DoYouWantYourRefundViaBankTransferController.get
      case _: JourneyDoYouWantYourRefundViaBankTransferYes => controllers.routes.WeNeedYouToConfirmYourIdentityController.get
      case _: JourneyDoYouWantYourRefundViaBankTransferNo  => controllers.routes.YourChequeWillBePostedToYouController.get
      case _: JourneyWhatIsYourFullName                    => controllers.routes.WhatIsYourDateOfBirthController.get
      case _: JourneyWhatIsYourDateOfBirth                 => controllers.routes.WhatIsYourNationalInsuranceNumberController.get
      case _: JourneyWhatIsYourNationalInsuranceNumber     => controllers.routes.CheckYourAnswersController.get
      case _: JourneyCheckYourAnswersChange                => controllers.routes.WeHaveConfirmedYourIdentityController.get
      case _: JourneyCheckYourAnswers                      => controllers.routes.WeHaveConfirmedYourIdentityController.get
      case _: JourneyIdentityVerified                      => controllers.routes.WhatIsTheNameOfYourBankAccountController.get
      case _: JourneyIdentityNotVerified                   => controllers.routes.WeCannotConfirmYourIdentityController.get
      case _: JourneyYourChequeWillBePostedToYou           => controllers.routes.RequestReceivedController.chequeGet
      case _: JourneyWhatIsTheNameOfYourBankAccount        => controllers.routes.GiveYourConsentController.get
      case _: JourneyRefundConsentGiven                    => controllers.routes.GiveYourConsentController.get
      case _: JourneyApprovedRefund                        => controllers.routes.VerifyBankAccountController.get
    }

    JourneyLogger.warn(s"Incorrect journey state for this page. Redirecting to ${redirectLocation.url}")
    Redirect(redirectLocation)
  }

  def sendToCorrespondingPageF(journey: Journey)(implicit request: Request[_]): Future[Result] = Future.successful(sendToCorrespondingPage(journey))

}
