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

import action.Actions
import language.Messages
import models.journeymodels.{Journey, JourneyType}
import play.api.mvc._
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.Views

import javax.inject.{Inject, Singleton}

@Singleton
class CannotConfirmYourIdentityTryAgainController @Inject() (
    mcc:     MessagesControllerComponents,
    views:   Views,
    actions: Actions
) extends FrontendController(mcc) {

  def getBankTransfer: Action[AnyContent] = actions.journeyInProgress { implicit request =>
    Ok(views.cannotConfirmYourIdentityTryAgainPage(
      chooseAnotherMethodCall    = routes.ChooseAnotherWayToGetYourRefundController.getBankTransfer,
      chooseAnotherMethodMessage = Messages.WeCannotConfirmYourIdentity.`Choose another way to get my refund`
    ))
  }

  def getCheque: Action[AnyContent] = actions.journeyInProgress { implicit request =>
    Ok(views.cannotConfirmYourIdentityTryAgainPage(
      chooseAnotherMethodCall    = routes.ClaimYourRefundByBankTransferController.get,
      chooseAnotherMethodMessage = Messages.WeCannotConfirmYourIdentity.`Claim your refund by bank transfer`
    ))
  }

  def tryAgain: Action[AnyContent] = actions.journeyInProgress { implicit journeyRequest =>
    Redirect(CheckYourAnswersController.redirectLocation(journeyRequest.journey))
  }

  def choseAnotherMethod: Action[AnyContent] = actions.journeyInProgress { implicit journeyRequest =>
    val journey = journeyRequest.journey
    Redirect(journey.getJourneyType match {
      case JourneyType.Cheque       => routes.ClaimYourRefundByBankTransferController.get
      case JourneyType.BankTransfer => routes.ChooseAnotherWayToGetYourRefundController.getBankTransfer
    })
  }

}
object CannotConfirmYourIdentityTryAgainController {
  def redirectLocation(journey: Journey)(implicit request: Request[_]): Call = Journey.deriveRedirectByJourneyType(
    journeyType           = journey.getJourneyType,
    chequeJourneyRedirect = controllers.routes.CannotConfirmYourIdentityTryAgainController.getCheque,
    bankJourneyRedirect   = controllers.routes.CannotConfirmYourIdentityTryAgainController.getBankTransfer
  )
}
