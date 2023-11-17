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
import io.scalaland.chimney.dsl._
import models.forms.DoYouWantToSignInForm
import models.forms.enumsforforms.DoYouWantToSignInFormValue
import models.journeymodels._
import play.api.mvc._
import requests.RequestSupport
import services.JourneyService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import util.Errors
import views.Views

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DoYouWantToSignInController @Inject() (
    mcc:            MessagesControllerComponents,
    requestSupport: RequestSupport,
    journeyService: JourneyService,
    views:          Views,
    actions:        Actions,
    appConfig:      AppConfig
)(implicit ec: ExecutionContext) extends FrontendController(mcc) {

  import requestSupport._

  val get: Action[AnyContent] = actions.journeyAction.async { implicit request =>
    request.journey match {
      case j: JTerminal      => JourneyRouter.handleFinalJourneyOnNonFinalPageF(j)
      case _: JourneyStarted => Future.successful(getResult)
      case j: JAfterStarted =>
        journeyService
          .upsert(
            j
              .into[JourneyStarted]
              .enableInheritedAccessors
              .transform
          )
          .map(_ => getResult)
    }
  }

  private def getResult(implicit request: Request[_]) = Ok(views.doYouWantToSignInPage(
    form = DoYouWantToSignInForm.form
  ))

  val post: Action[AnyContent] = actions.journeyAction.async { implicit request =>
    request.journey match {
      case j: JourneyStarted => processForm(j)
      case _: JAfterStarted =>
        Errors.throwServerErrorException(s"This endpoint supports only ${classOf[JourneyStarted].toString}")
      //TODO: discuss alternative approach
      //val journey = j
      //              .into[JourneyStarted]
      //              .enableInheritedAccessors
      //              .transform
      //processForm(j)
    }
  }

  private def processForm(journey: Journey)(implicit request: Request[_]): Future[Result] = {
    DoYouWantToSignInForm.form.bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(views.doYouWantToSignInPage(
        form = formWithErrors
      ))), {
        case DoYouWantToSignInFormValue.Yes =>
          Future.successful(Redirect(appConfig.ptaSignInUrl))
        case DoYouWantToSignInFormValue.No =>
          journeyService
            .upsert(journey.transformInto[JourneyDoYouWantToSignInNo])
            .map(_ => Redirect(controllers.routes.EnterP800ReferenceController.get))
      }
    )
  }
}
