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
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.Views

import javax.inject.{Inject, Singleton}

@Singleton
class RequestNotSubmittedController @Inject() (
    mcc:     MessagesControllerComponents,
    views:   Views,
    actions: Actions
) extends FrontendController(mcc) {

  val get: Action[AnyContent] = actions.journeyAction { implicit request: JourneyRequest[_] =>
    request.journey match {
      case _: JourneyNotApprovedRefund => getResult
      case j: JTerminal                => JourneyRouter.handleFinalJourneyOnNonFinalPage(j)
      case j: JBeforeNotApprovedRefund => JourneyRouter.sendToCorrespondingPage(j)
    }
  }

  private def getResult(implicit request: JourneyRequest[_]): Result = {
    Ok(views.requestNotSubmittedPage())
  }

}
