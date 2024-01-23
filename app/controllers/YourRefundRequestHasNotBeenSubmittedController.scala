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

import action.{Actions, JourneyRequest}
import models.journeymodels._
import play.api.mvc._
import services.JourneyService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import util.Errors
import util.SafeEquals.EqualsOps
import views.Views

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class YourRefundRequestHasNotBeenSubmittedController @Inject() (
    mcc:            MessagesControllerComponents,
    views:          Views,
    actions:        Actions,
    journeyService: JourneyService
)(implicit ec: ExecutionContext) extends FrontendController(mcc) {

  def get: Action[AnyContent] = actions.journeyFinished { implicit request: JourneyRequest[_] =>
    val journey = request.journey
    Errors.require(journey.getJourneyType === JourneyType.BankTransfer, "This page is only for BankTransfer journey")

    if (journey.hasFinished === HasFinished.YesSucceeded) {
      Redirect(request.journey.getJourneyType match {
        case JourneyType.Cheque       => controllers.routes.RequestReceivedController.getCheque
        case JourneyType.BankTransfer => controllers.routes.RequestReceivedController.getBankTransfer
      })
    } else {
      Ok(views.requestNotSubmittedPage())
    }
  }

  def post: Action[AnyContent] = actions.journeyFinished.async { implicit request: JourneyRequest[_] =>
    Errors.require(request.journey.getJourneyType === JourneyType.BankTransfer, "This page is only for BankTransfer journey")

    journeyService
      .upsert(request.journey.copy(hasFinished = HasFinished.No))
      //TODO: invalidate API calls
      .map(_ => Redirect(controllers.routes.ChooseAnotherWayToGetYourRefundController.get))
  }

}
