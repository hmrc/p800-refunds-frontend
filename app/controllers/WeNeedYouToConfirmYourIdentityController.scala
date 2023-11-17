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
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.Views

import javax.inject.{Inject, Singleton}

@Singleton
class WeNeedYouToConfirmYourIdentityController @Inject() (
    mcc:     MessagesControllerComponents,
    views:   Views,
    actions: Actions
) extends FrontendController(mcc) {

  val get: Action[AnyContent] = actions.journeyAction { implicit request =>
    request.journey match {
      case j: JTerminal                                    => JourneyRouter.handleFinalJourneyOnNonFinalPage(j)
      case j: JBeforeDoYouWantYourRefundViaBankTransferYes => JourneyRouter.sendToCorrespondingPage(j)
      case j: JBeforeDoYouWantYourRefundViaBankTransferNo  => JourneyRouter.sendToCorrespondingPage(j)
      case j: JAfterDoYouWantYourRefundViaBankTransferNo   => JourneyRouter.sendToCorrespondingPage(j)
      case j: JourneyDoYouWantYourRefundViaBankTransferNo  => JourneyRouter.sendToCorrespondingPage(j)
      case _: JAfterDoYouWantYourRefundViaBankTransferYes  => getResult
      case _: JourneyDoYouWantYourRefundViaBankTransferYes => getResult
    }
  }

  private def getResult(implicit request: Request[_]) = Ok(views.weNeedYouToConfirmYourIdentityPage())
}
