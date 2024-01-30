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
import models.journeymodels.{Journey, JourneyType}
import play.api.mvc._
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import util.Errors
import util.SafeEquals.EqualsOps
import views.Views

import javax.inject.{Inject, Singleton}

@Singleton
class NoMoreAttemptsLeftToConfirmYourIdentityController @Inject() (
    mcc:     MessagesControllerComponents,
    views:   Views,
    actions: Actions
) extends FrontendController(mcc) {

  def getBankTransfer: Action[AnyContent] = actions.journeyFinished { implicit request =>
    val journey: Journey = request.journey
    Errors.require(journey.getJourneyType === JourneyType.BankTransfer, "This endpoint supports only BankTransfer journey")
    Ok(views.noMoreAttemptsLeftToConfirmYourIdentityPage())
  }

  def getCheque: Action[AnyContent] = actions.journeyFinished { implicit request =>
    val journey: Journey = request.journey
    Errors.require(journey.getJourneyType === JourneyType.Cheque, "This endpoint supports only Cheque journey")
    Ok(views.noMoreAttemptsLeftToConfirmYourIdentityPage())
  }

}

object NoMoreAttemptsLeftToConfirmYourIdentityController {
  def redirectLocation(journey: Journey)(implicit request: Request[_]): Call = Journey.deriveRedirectByJourneyType(
    journeyType           = journey.getJourneyType,
    chequeJourneyRedirect = controllers.routes.NoMoreAttemptsLeftToConfirmYourIdentityController.getCheque,
    bankJourneyRedirect   = controllers.routes.NoMoreAttemptsLeftToConfirmYourIdentityController.getBankTransfer
  )
}
