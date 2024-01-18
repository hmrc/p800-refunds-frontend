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
import models.journeymodels._
import play.api.mvc._
import services.JourneyService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import util.Errors
import util.SafeEquals.EqualsOps
import views.Views

import javax.inject.{Inject, Singleton}
import scala.annotation.unused
import scala.concurrent.ExecutionContext

@Singleton
class CompleteYourRefundRequestController @Inject() (
    mcc:            MessagesControllerComponents,
    views:          Views,
    actions:        Actions,
    journeyService: JourneyService
)(implicit @unused ec: ExecutionContext) extends FrontendController(mcc) {

  def get: Action[AnyContent] = actions.journeyInProgress{ implicit request =>
    Errors.require(request.journey.getJourneyType === JourneyType.Cheque, "This endpoint supports only Cheque journey")
    Ok(views.completeYourRefundRequestPage())
  }

  def post: Action[AnyContent] = actions.journeyInProgress.async { implicit request =>
    val journey: Journey = request.journey
    Errors.require(journey.getJourneyType === JourneyType.Cheque, "This endpoint supports only Cheque journey")

    //TODO: API call
    journeyService
      .upsert(
        //TODO: update journey with results of the API call
        //TODO: hasFinished=true is for happy an unhappy path
        journey.copy(hasFinished = HasFinished.YesSucceeded)
      )
      .map(_ => Redirect(controllers.routes.RequestReceivedController.get))
  }

}
