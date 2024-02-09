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
import models.forms.WhatIsYourDateOfBirthForm
import models.journeymodels._
import play.api.mvc._
import requests.RequestSupport
import services.JourneyService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import util.Errors
import util.SafeEquals.EqualsOps
import views.Views

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EnterYourDateOfBirthController @Inject() (
    mcc:            MessagesControllerComponents,
    requestSupport: RequestSupport,
    views:          Views,
    actions:        Actions,
    journeyService: JourneyService
)(implicit execution: ExecutionContext) extends FrontendController(mcc) {

  import requestSupport._

  def get: Action[AnyContent] = actions.journeyInProgress { implicit request: JourneyRequest[_] =>
    val journey: Journey = request.journey
    Errors.require(journey.getJourneyType === JourneyType.BankTransfer, "This page is only for BankTransfer journey")

    Ok(views.enterYourDateOfBirthPage(
      journey.dateOfBirth.fold(
        WhatIsYourDateOfBirthForm.form
      )(dob =>
          WhatIsYourDateOfBirthForm.form.fill(WhatIsYourDateOfBirthForm(dob)))
    ))
  }

  def post: Action[AnyContent] = actions.journeyInProgress.async { implicit request =>
    val journey: Journey = request.journey
    processForm(journey)
  }

  private def processForm(journey: Journey)(implicit request: JourneyRequest[_]): Future[Result] = {
    WhatIsYourDateOfBirthForm
      .form
      .bindFromRequest()
      .fold(
        formWithErrors => {
          Future.successful(BadRequest(views.enterYourDateOfBirthPage(form = formWithErrors)))
        },
        validForm => {
          journeyService
            .upsert(journey.update(
              dateOfBirth = validForm.date
            ))
            .map(_ => Redirect(controllers.CheckYourAnswersController.redirectLocation(request.journey)))
        }
      )
  }

}

