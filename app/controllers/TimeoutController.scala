/*
 * Copyright 2024 HM Revenue & Customs
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
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.JourneyService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import util.JourneyLogger
import views.html.TimeoutPage

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class TimeoutController @Inject() (
    mcc:            MessagesControllerComponents,
    timeOutPage:    TimeoutPage,
    actions:        Actions,
    journeyService: JourneyService
)(implicit ec: ExecutionContext) extends FrontendController(mcc) {

  def get(didUserDelete: Boolean): Action[AnyContent] = actions.journeyTimedOut.async { implicit request: JourneyRequest[_] =>
    journeyService.remove(request.journeyId).map{ _ =>
      JourneyLogger.info(s"Removing journey for [journeyId:${request.journeyId.value}]")
      Ok(timeOutPage(didUserDelete))
    }
  }

}
