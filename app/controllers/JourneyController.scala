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
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.JourneyService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.Views

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class JourneyController @Inject() (
    mcc:            MessagesControllerComponents,
    journeyService: JourneyService,
    views:          Views,
    actions:        Actions
)(implicit ec: ExecutionContext) extends FrontendController(mcc) {

  val start: Action[AnyContent] = Action.async { implicit request =>
    journeyService
      .newJourney()
      .map(journey => Redirect(
        routes.JourneyController.doYouWantToSignIn
      ).addingToSession(JourneyController.journeyIdKey -> journey.id.value))
  }

  val doYouWantToSignIn: Action[AnyContent] = actions.journeyAction { implicit request =>
    Ok(views.doYouWantToSignInPage())
  }

  //TODO: remove once we have all pages
  val underConstruction: Action[AnyContent] = actions.default { implicit request =>
    Ok(views.underConstructionPage())
  }
}

object JourneyController {
  val journeyIdKey: String = "p800-refunds-frontend.journeyId"
}
