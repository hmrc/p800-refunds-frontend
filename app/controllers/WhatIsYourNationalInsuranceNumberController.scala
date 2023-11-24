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
import play.api.mvc._
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.Views
import models.NationalInsuranceNumber
import models.forms.WhatIsYourNationalInsuranceNumberForm
import models.journeymodels._
import requests.RequestSupport
import services.JourneyService
import io.scalaland.chimney.dsl._

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

  val get: Action[AnyContent] = actions.journeyAction { implicit request =>
    request.journey match {
      case j: JTerminal                                => JourneyRouter.handleFinalJourneyOnNonFinalPage(j)
      case j: JBeforeWhatIsYourDateOfBirth             => JourneyRouter.sendToCorrespondingPage(j)
      case _: JourneyWhatIsYourDateOfBirth             => getResult(None)
      case j: JourneyWhatIsYourNationalInsuranceNumber => getResult(Some(j.nationalInsuranceNumber))
      case j: JAfterWhatIsYourNationalInsuranceNumber  => getResult(Some(j.nationalInsuranceNumber))
    }
  }

  private def getResult(maybeNationalInsuranceNumber: Option[NationalInsuranceNumber])(implicit request: JourneyRequest[_]): Result = {
    maybeNationalInsuranceNumber.fold(
      Ok(views.whatIsYourNationalInsuranceNumberPage(WhatIsYourNationalInsuranceNumberForm.form))
    ) { nationalInsuranceNumber: NationalInsuranceNumber =>
        Ok(views.whatIsYourNationalInsuranceNumberPage(WhatIsYourNationalInsuranceNumberForm.form.fill(nationalInsuranceNumber)))
      }
  }

  val post: Action[AnyContent] = actions.journeyAction.async { implicit request: JourneyRequest[_] =>
    request.journey match {
      case j: JTerminal                                   => JourneyRouter.handleFinalJourneyOnNonFinalPageF(j)
      case j: JBeforeWhatIsYourDateOfBirth                => JourneyRouter.sendToCorrespondingPageF(j)
      case j: JAfterDoYouWantYourRefundViaBankTransferYes => processForm(j)
    }
  }

  private def processForm(journey: JAfterDoYouWantYourRefundViaBankTransferYes)(implicit request: JourneyRequest[_]): Future[Result] = {
    WhatIsYourNationalInsuranceNumberForm
      .form
      .bindFromRequest()
      .fold(
        formWithErrors => {
          Future.successful(BadRequest(views.whatIsYourNationalInsuranceNumberPage(form = formWithErrors)))
        },
        nationalInsuranceNumber => {
          val newJourney = journey match {
            case j: JourneyWhatIsYourFullName => j
            case j: JourneyWhatIsYourDateOfBirth => j.into[JourneyWhatIsYourNationalInsuranceNumber]
              .withFieldConst(_.nationalInsuranceNumber, nationalInsuranceNumber)
              .transform
            case j: JAfterWhatIsYourDateOfBirth =>
              j
                .into[JourneyWhatIsYourNationalInsuranceNumber]
                .enableInheritedAccessors
                .withFieldConst(_.nationalInsuranceNumber, nationalInsuranceNumber)
                .transform
          }
          journeyService
            .upsert(newJourney)
            .map(_ => Redirect(controllers.routes.CheckYourAnswersController.get))
        }
      )
  }

  // private def processForm()
}
