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
import play.api.mvc._
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.Views

import javax.inject.{Inject, Singleton}

@Singleton
class WeCannotConfirmYourIdentityController @Inject() (
    mcc:     MessagesControllerComponents,
    views:   Views,
    actions: Actions
) extends FrontendController(mcc) {

  def get: Action[AnyContent] = actions.journeyInProgress { implicit request =>
    //TODO: call attempts service (use IpAddress) and based on the result display apropriate page.
    Ok(views.weCannotConfirmYourIdentityPage())
  }

  def tryAgain: Action[AnyContent] = actions.journeyInProgress { _ =>
    Redirect(routes.WeNeedYouToConfirmYourIdentityController.get)
  }

  def choseAnotherMethod: Action[AnyContent] = actions.journeyInProgress { _ =>
    Redirect(routes.ChooseAnotherWayToGetYourRefundController.get)
  }

}

