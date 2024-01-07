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
import CheckYourAnswersController._

@Singleton
class WhatIsYourP800ReferenceController @Inject() (
    mcc:            MessagesControllerComponents,
    requestSupport: RequestSupport,
    journeyService: JourneyService,
    views:          Views,
    actions:        Actions
)(implicit ec: ExecutionContext) extends FrontendController(mcc) {

  import requestSupport._

  val get: Action[AnyContent] = actions.journeyInProgress { implicit request =>
    val journey: Journey = request.journey
    getResult(journey.p800Reference)
  }

  private def getResult(maybeP800Reference: Option[P800Reference])(implicit request: Request[_]): Result = {
    Ok(views.enterP800ReferencePage(
      form = maybeP800Reference.fold(
        EnterP800ReferenceForm.form
      )(
          EnterP800ReferenceForm.form.fill
        )
    ))
      .makeChanging()
  }

  val post: Action[AnyContent] = actions.journeyInProgress.async { implicit request =>
    processForm(request.journey)
  }

  private def processForm(journey: Journey)(implicit request: Request[_]): Future[Result] = {
    /**
     * It must navigate to the next page or to the checkYourAnswers page depending if it was a "change" or not.
     */
    val nextCall = if (journey.isChanging) controllers.routes.CheckYourAnswersController.get else controllers.routes.WhatIsYourNationalInsuranceNumberController.get

    EnterP800ReferenceForm
      .form
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(views.enterP800ReferencePage(
          form = formWithErrors
        ))),
        p800Reference => {
          journeyService
            .upsert(journey.copy(
              p800Reference = Some(p800Reference),
              isChanging    = false
            ))
            .map(_ => Redirect(nextCall))
        }
      )
  }

}
