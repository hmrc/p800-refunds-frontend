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
import models.journeymodels.{Journey, JourneyType}
import play.api.mvc._
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.Views

import javax.inject.{Inject, Singleton}

@Singleton
class WeHaveConfirmedYourIdentityController @Inject() (
    mcc:     MessagesControllerComponents,
    views:   Views,
    actions: Actions
) extends FrontendController(mcc) {

  def getBankTransfer: Action[AnyContent] = actions.journeyInProgress { implicit request =>
    getResult
  }

  def getCheque: Action[AnyContent] = actions.journeyInProgress { implicit request =>
    getResult
  }

  private def getResult(implicit request: JourneyRequest[_]) = {
    Ok(views.yourIdentityIsConfirmedPage())
  }

  def post: Action[AnyContent] = actions.journeyInProgress { implicit request =>
    val journey = request.journey
    val nextCall: Call = journey.getJourneyType match {
      case JourneyType.BankTransfer => controllers.routes.EnterTheNameOfYourBankController.get
      case JourneyType.Cheque       => controllers.routes.IsYourAddressUpToDateController.get
    }
    Redirect(nextCall.url)
  }
}

object WeHaveConfirmedYourIdentityController {
  def redirectLocation(journey: Journey)(implicit request: Request[_]): Call = Journey.deriveRedirectByJourneyType(
    journeyType           = journey.getJourneyType,
    chequeJourneyRedirect = controllers.routes.WeHaveConfirmedYourIdentityController.getCheque,
    bankJourneyRedirect   = controllers.routes.WeHaveConfirmedYourIdentityController.getBankTransfer
  )
}
