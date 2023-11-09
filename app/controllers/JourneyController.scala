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
import play.api.mvc.Result
import play.api.mvc.Results.Redirect
import util.Errors

import scala.concurrent.Future

object JourneyController {

  /**
   * Handle journey in final state on non final page.
   * TODO OPS-11309
   * We don't know yet what to do here.
   * The case is when user is on final page (i.e. RequestReceived) and
   * clicks back. Shall we render the previous pages? Or remove the journeyId
   * from the session upon displaying final page (but what if user refreshes it?)
   */
  def handleFinalJourneyOnNonFinalPage(): Nothing = {
    Errors.throwBadRequestException("Not decided yet, what to do")
  }

  /**
   * Based on the journey state it redirects to the corresponding to this state page.
   */
  def sendToCorrespondingPage(journey: Journey): Result = journey match {
    case _: JourneyStarted                               => Redirect(controllers.routes.DoYouWantToSignInController.get)
    case _: JourneyDoYouWantToSignInNo                   => Redirect(controllers.routes.EnterP800ReferenceController.get)
    case _: JourneyWhatIsYourP800Reference               => Redirect(controllers.routes.CheckYourReferenceController.get)
    case _: JourneyCheckYourReferenceValid               => Redirect(controllers.routes.DoYouWantYourRefundViaBankTransferController.get)
    case _: JourneyDoYouWantYourRefundViaBankTransferYes => Redirect(controllers.routes.WeNeedYouToConfirmYourIdentityController.get)
    case _: JourneyDoYouWantYourRefundViaBankTransferNo  => Redirect(controllers.routes.YourChequeWillBePostedToYouController.get)
    case _: JourneyYourChequeWillBePostedToYou           => Redirect(controllers.routes.RequestReceivedController.get)
  }

  def sendToCorrespondingPageF(journey: Journey): Future[Result] = Future.successful(sendToCorrespondingPage(journey))

}
