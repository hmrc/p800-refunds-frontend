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
import models.journeymodels.Journey
import models.journeymodels.JourneyType.{BankTransfer, Cheque}
import play.api.mvc._
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import util.Errors
import views.Views

import javax.inject.{Inject, Singleton}

@Singleton
class ConfirmYourIdentityController @Inject() (
    mcc:     MessagesControllerComponents,
    views:   Views,
    actions: Actions
) extends FrontendController(mcc) {

  def getCheque: Action[AnyContent] = actions.journeyInProgress { implicit request: JourneyRequest[_] =>
    get
  }

  def getBankTransfer: Action[AnyContent] = actions.journeyInProgress { implicit request: JourneyRequest[_] =>
    get
  }

  private def get(implicit request: JourneyRequest[_]): Result =
    Ok(views.confirmYourIdentityPage(request.journey.getJourneyType))

  def post: Action[AnyContent] = actions.journeyInProgress { implicit request =>
    request.journey.journeyType match {
      case Some(BankTransfer) => Redirect(routes.EnterYourP800ReferenceController.getBankTransfer.url)
      case Some(Cheque)       => Redirect(routes.EnterYourP800ReferenceController.getCheque.url)
      case None               => Errors.throwServerErrorException("No journey type found in journey, this should never happen")
    }
  }
}

object ConfirmYourIdentityController {
  def redirectLocation(journey: Journey)(implicit request: Request[_]): Call = Journey.deriveRedirectByJourneyType(
    journeyType           = journey.getJourneyType,
    chequeJourneyRedirect = controllers.routes.ConfirmYourIdentityController.getCheque,
    bankJourneyRedirect   = controllers.routes.ConfirmYourIdentityController.getBankTransfer
  )
}
