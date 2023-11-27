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
import io.scalaland.chimney.dsl._
import models.dateofbirth.DateOfBirth
import models.forms.WhatIsYourDateOfBirthForm
import models.journeymodels._
import play.api.mvc._
import requests.RequestSupport
import services.JourneyService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.Views

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class WhatIsYourDateOfBirthController @Inject() (
    mcc:            MessagesControllerComponents,
    requestSupport: RequestSupport,
    views:          Views,
    actions:        Actions,
    journeyService: JourneyService
)(implicit execution: ExecutionContext) extends FrontendController(mcc) {

  import requestSupport._

  val get: Action[AnyContent] = actions.journeyAction { implicit request: JourneyRequest[_] =>
    request.journey match {
      case j: JTerminal                                   => JourneyRouter.handleFinalJourneyOnNonFinalPage(j)
      case j: JourneyDoYouWantYourRefundViaBankTransferNo => JourneyRouter.sendToCorrespondingPage(j)
      case j: JBeforeWhatIsYourFullName                   => JourneyRouter.sendToCorrespondingPage(j)
      case _: JourneyWhatIsYourFullName                   => getResult(None)
      case j: JourneyWhatIsYourDateOfBirth                => getResult(Some(j.dateOfBirth))
      case j: JAfterWhatIsYourDateOfBirth                 => getResult(Some(j.dateOfBirth))
    }
  }

  private def getResult(maybeDateOfBirth: Option[DateOfBirth])(implicit request: JourneyRequest[_]): Result = {
    maybeDateOfBirth.fold(
      Ok(views.whatIsYourDateOfBirthPage(WhatIsYourDateOfBirthForm.form))
    ) { dateOfBirth: DateOfBirth =>
        Ok(views.whatIsYourDateOfBirthPage(WhatIsYourDateOfBirthForm.form.fill(WhatIsYourDateOfBirthForm(dateOfBirth))))
      }
  }

  val post: Action[AnyContent] = actions.journeyAction.async { implicit request =>
    request.journey match {
      case j: JTerminal                                   => JourneyRouter.handleFinalJourneyOnNonFinalPageF(j)
      case j: JourneyDoYouWantYourRefundViaBankTransferNo => JourneyRouter.sendToCorrespondingPageF(j)
      case j: JBeforeWhatIsYourFullName                   => JourneyRouter.sendToCorrespondingPageF(j)
      case j: JAfterDoYouWantYourRefundViaBankTransferYes => processForm(j)
    }
  }

  private def processForm(journey: JAfterDoYouWantYourRefundViaBankTransferYes)(implicit request: Request[_]): Future[Result] = {
    WhatIsYourDateOfBirthForm
      .form
      .bindFromRequest()
      .fold(
        formWithErrors => {
          Future.successful(BadRequest(views.whatIsYourDateOfBirthPage(form = formWithErrors)))
        },
        validForm => {
          val newJourney = journey match {
            case j: JourneyWhatIsYourFullName                => j.into[JourneyWhatIsYourDateOfBirth].withFieldConst(_.dateOfBirth, validForm.date).transform
            case j: JourneyWhatIsYourDateOfBirth             => j.copy(dateOfBirth = validForm.date)
            case j: JourneyWhatIsYourNationalInsuranceNumber => j.copy(dateOfBirth = validForm.date)
            case j: JourneyCheckYourAnswers                  => j.copy(dateOfBirth = validForm.date)
            //other Journey states will just use copy, I guess, then we don't lose any extra info when they traverse through the journey and we can prepop when users progress.
          }
          journeyService
            .upsert(newJourney)
            .map(_ => Redirect(controllers.routes.WhatIsYourNationalInsuranceNumberController.get))
        }
      )
  }

}
