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
import config.AppConfig
import forms.DoYouWantToSignInForm
import models.journeymodels._
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import requests.RequestSupport
import services.JourneyService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import io.scalaland.chimney.dsl._
import views.Views

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class JourneyController @Inject() (
    mcc:            MessagesControllerComponents,
    requestSupport: RequestSupport,
    journeyService: JourneyService,
    views:          Views,
    actions:        Actions,
    appConfig:      AppConfig
)(implicit ec: ExecutionContext) extends FrontendController(mcc) {

  import requestSupport._

  val start: Action[AnyContent] = Action.async { implicit request =>
    journeyService
      .newJourney()
      .map(journey => Redirect(
        routes.JourneyController.doYouWantToSignIn
      ).addingToSession(JourneyController.journeyIdKey -> journey.id.value))
  }

  val doYouWantToSignIn: Action[AnyContent] = actions.journeyAction { implicit request =>
    Ok(views.doYouWantToSignInPage(DoYouWantToSignInForm.form, controllers.routes.JourneyController.doYouWantToSignInSubmit))
  }

  val doYouWantToSignInSubmit: Action[AnyContent] = actions.journeyAction.async { implicit request =>
    DoYouWantToSignInForm.form.bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(views.doYouWantToSignInPage(formWithErrors, controllers.routes.JourneyController.doYouWantToSignInSubmit))),
      form => {
        form.signIn match {
          case Some("sign-in") => Future.successful(Redirect(appConfig.ptaSignInUrl))
          case Some("do-not-sign-in") =>
            journeyService.upsert(request.journey.transformInto[JourneyDoYouWantToSignInNo]).map(_ =>
              Redirect(controllers.routes.JourneyController.whatIsYourP800Reference))
          case _ => throw new Exception("Invalid case - This should not be possible!")
        }
      }
    )
  }

  //TODO: remove once we have all pages
  val underConstruction: Action[AnyContent] = actions.default { implicit request =>
    Ok(views.underConstructionPage())
  }

  val whatIsYourP800Reference: Action[AnyContent] = underConstruction
}

object JourneyController {
  val journeyIdKey: String = "p800-refunds-frontend.journeyId"
}
