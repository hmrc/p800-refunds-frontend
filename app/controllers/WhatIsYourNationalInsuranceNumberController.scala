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
import models.forms.WhatIsYourNationalInsuranceNumberForm
import models.journeymodels._
import play.api.mvc._
import requests.RequestSupport
import services.JourneyService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.Views

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class WhatIsYourNationalInsuranceNumberController @Inject() (
    mcc:            MessagesControllerComponents,
    views:          Views,
    actions:        Actions,
    journeyService: JourneyService,
    requestSupport: RequestSupport
)(implicit ec: ExecutionContext) extends FrontendController(mcc) {

  import requestSupport._

  def get: Action[AnyContent] = actions.journeyInProgress { implicit request =>
    val journey: Journey = request.journey

    Ok(views.whatIsYourNationalInsuranceNumberPage(
      form = journey.nationalInsuranceNumber.fold(
        WhatIsYourNationalInsuranceNumberForm.form
      )(
          WhatIsYourNationalInsuranceNumberForm.form.fill
        )
    ))
  }

  def post: Action[AnyContent] = actions.journeyInProgress.async { implicit request: JourneyRequest[_] =>
    val journey: Journey = request.journey
    processForm(journey)
  }

  private def processForm(journey: Journey)(implicit request: JourneyRequest[_]): Future[Result] = {
    val defaultNextCall = journey.getJourneyType match {
      case JourneyType.BankTransfer => controllers.routes.WhatIsYourDateOfBirthController.get
      case JourneyType.Cheque       => controllers.routes.CheckYourAnswersController.get
    }
    val nextCall = if (request.journey.isChanging) controllers.routes.CheckYourAnswersController.get else defaultNextCall

    WhatIsYourNationalInsuranceNumberForm
      .form
      .bindFromRequest()
      .fold(
        formWithErrors => {
          Future.successful(BadRequest(views.whatIsYourNationalInsuranceNumberPage(form = formWithErrors)))
        },
        nationalInsuranceNumber =>
          journeyService
            .upsert(journey.copy(
              nationalInsuranceNumber = Some(nationalInsuranceNumber),
              isChanging              = false
            ))
            .map(_ => Redirect(nextCall))

      )
  }

}

