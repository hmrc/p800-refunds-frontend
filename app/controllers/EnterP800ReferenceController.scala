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
import models.forms.EnterP800ReferenceForm
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
class EnterP800ReferenceController @Inject() (
    mcc:            MessagesControllerComponents,
    requestSupport: RequestSupport,
    journeyService: JourneyService,
    views:          Views,
    actions:        Actions
)(implicit ec: ExecutionContext) extends FrontendController(mcc) {

  import requestSupport._

  val get: Action[AnyContent] = actions.journeyAction.async { implicit request =>
    request.journey match {
      case _: JTerminal                  => JourneyController.handleFinalJourneyOnNonFinalPage()
      case j: JBeforeDoYouWantToSignInNo => JourneyController.sendToCorrespondingPageF(j)
      case _: JourneyDoYouWantToSignInNo => Future.successful(getResult)
      case j: JAfterDoYouWantToSignInNo =>
        journeyService
          .upsert(
            j
              .into[JourneyDoYouWantToSignInNo]
              .enableInheritedAccessors
              .transform
          )
          .map(_ => getResult)
    }
  }

  private def getResult(implicit request: Request[_]): Result = Ok(views.enterP800ReferencePage(
    form = EnterP800ReferenceForm.form
  ))

  val post: Action[AnyContent] = actions.journeyAction.async { implicit request =>
    request.journey match {
      case _: JTerminal                  => JourneyController.handleFinalJourneyOnNonFinalPage()
      case j: JBeforeDoYouWantToSignInNo => JourneyController.sendToCorrespondingPageF(j)
      case j: JourneyDoYouWantToSignInNo => processForm(j)
      case _: JAfterDoYouWantToSignInNo =>
        Errors.throwServerErrorException(s"This endpoint supports only ${classOf[JourneyDoYouWantToSignInNo].toString}")
      //TODO: discuss alternative approach
      //val journey = j
      //              .into[JourneyStarted]
      //              .enableInheritedAccessors
      //              .transform
      //processForm(j)
    }
  }

  private def processForm(journey: JourneyDoYouWantToSignInNo)(implicit request: Request[_]): Future[Result] =
    EnterP800ReferenceForm
      .form
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(views.enterP800ReferencePage(
          form = formWithErrors
        ))),
        p800Reference => {
          journeyService
            .upsert(
              journey
                .into[JourneyWhatIsYourP800Reference]
                .withFieldConst(_.p800Reference, p800Reference)
                .transform
            )
            .map(_ => Redirect(controllers.routes.CheckYourReferenceController.get))
        }
      )
}
