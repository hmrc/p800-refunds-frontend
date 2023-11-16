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
import io.scalaland.chimney.dsl._
import models.journeymodels._
import play.api.mvc._
import services.JourneyService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.Views

import javax.inject.{Inject, Singleton}
import scala.annotation.unused
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class YourChequeWillBePostedToYouController @Inject() (
    mcc:            MessagesControllerComponents,
    views:          Views,
    actions:        Actions,
    journeyService: JourneyService
)(implicit @unused ec: ExecutionContext) extends FrontendController(mcc) {

  val get: Action[AnyContent] = actions.journeyAction.async { implicit request =>
    request.journey match {
      case j: JTerminal                                   => JourneyRouter.handleFinalJourneyOnNonFinalPageF(j)
      case j: JBeforeDoYouWantYourRefundViaBankTransferNo => JourneyRouter.sendToCorrespondingPageF(j)
      case _: JourneyDoYouWantYourRefundViaBankTransferNo => Future.successful(getResult)
      case j: JAfterDoYouWantYourRefundViaBankTransferNo =>
        journeyService
          .upsert(
            j
              .into[JourneyDoYouWantYourRefundViaBankTransferNo]
              .enableInheritedAccessors
              .transform
          )
          .map(_ => getResult)
      case j: JourneyDoYouWantYourRefundViaBankTransferYes => JourneyRouter.sendToCorrespondingPageF(j)
      //TODO: missing implementation for further journey states, uncomment in future once provided
      //case j: JAfterDoYouWantYourRefundViaBankTransferYes  => JourneyRouter.sendToCorrespondingPageF(j)
    }
  }

  private def getResult(implicit request: Request[_]) = {
    Ok(views.yourChequeWillBePostedToYouPage())
  }

  val post: Action[AnyContent] = actions.journeyAction.async { implicit request =>
    request.journey match {
      case j: JTerminal                                    => JourneyRouter.handleFinalJourneyOnNonFinalPageF(j)
      case j: JBeforeDoYouWantYourRefundViaBankTransferNo  => JourneyRouter.sendToCorrespondingPageF(j)
      case j: JourneyDoYouWantYourRefundViaBankTransferNo  => processPost(j)
      case j: JourneyDoYouWantYourRefundViaBankTransferYes => JourneyRouter.sendToCorrespondingPageF(j)
      //TODO: missing implementation for further journey states, uncomment in future once provided
      //case j: JAfterDoYouWantYourRefundViaBankTransferYes  => JourneyRouter.sendToCorrespondingPageF(j)
    }
  }

  private def processPost(journey: JourneyDoYouWantYourRefundViaBankTransferNo)(implicit request: Request[_]): Future[Result] = {
    //TODO: API call
    journeyService
      .upsert(journey.transformInto[JourneyYourChequeWillBePostedToYou])
      .map(_ => Redirect(controllers.routes.RequestReceivedController.get))
  }
}
