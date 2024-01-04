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
import models.P800Reference
import models.forms.EnterP800ReferenceForm
import models.journeymodels._
import play.api.mvc._
import requests.RequestSupport
import services.JourneyService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
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
      case j: JTerminal                     => JourneyRouter.handleFinalJourneyOnNonFinalPageF(j)
      case j: JBeforeDoYouWantToSignInNo    => JourneyRouter.sendToCorrespondingPageF(j)
      case _: JourneyDoYouWantToSignInNo    => Future.successful(getResult(None))
      case j: JourneyCheckYourAnswersChange => Future.successful(getResult(Some(j.p800Reference)))
      case j: JAfterDoYouWantToSignInNo =>
        journeyService
          .upsert(
            j
              .into[JourneyDoYouWantToSignInNo]
              .enableInheritedAccessors
              .transform
          )
          .map(_ => getResult(Some(j.p800Reference)))
    }
  }

  private def getResult(maybeP800Reference: Option[P800Reference])(implicit request: Request[_]): Result =
    maybeP800Reference.fold(
      Ok(views.enterP800ReferencePage(EnterP800ReferenceForm.form))
    ) { p800Reference: P800Reference =>
        Ok(views.enterP800ReferencePage(EnterP800ReferenceForm.form.fill(p800Reference)))
      }

  val post: Action[AnyContent] = actions.journeyAction.async { implicit request =>
    request.journey match {
      case j: JTerminal                  => JourneyRouter.handleFinalJourneyOnNonFinalPageF(j)
      case j: JBeforeDoYouWantToSignInNo => JourneyRouter.sendToCorrespondingPageF(j)
      case j: JAfterStarted              => processForm(j)
    }
  }

  private def processForm(journey: JAfterStarted)(implicit request: Request[_]): Future[Result] =
    EnterP800ReferenceForm
      .form
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(views.enterP800ReferencePage(
          form = formWithErrors
        ))),
        p800Reference => {
          val newJourney = journey match {
            case j: JourneyDoYouWantToSignInNo    => j.into[JourneyWhatIsYourP800Reference].withFieldConst(_.p800Reference, p800Reference).transform
            case j: JourneyCheckYourAnswersChange => j.into[JourneyWhatIsYourNationalInsuranceNumber].enableInheritedAccessors.withFieldConst(_.p800Reference, p800Reference).transform
            case j: JAfterDoYouWantToSignInNo     => j.into[JourneyWhatIsYourP800Reference].enableInheritedAccessors.withFieldConst(_.p800Reference, p800Reference).transform
          }
          journeyService
            .upsert(newJourney)
            .map(_ =>
              journey match {
                case _: JourneyCheckYourAnswersChange => Redirect(controllers.routes.CheckYourAnswersController.get)
                case _                                => Redirect(controllers.routes.CheckYourReferenceController.get)
              })
        }
      )
}
