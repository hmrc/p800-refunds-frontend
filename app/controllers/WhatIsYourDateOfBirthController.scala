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
import models.dateofbirth.DateOfBirth
import models.forms.WhatIsYourDateOfBirthForm
import models.journeymodels.{JAfterDoYouWantYourRefundViaBankTransferYes, JTerminal, JourneyWhatIsYourDateOfBirth}
import play.api.mvc._
import services.JourneyService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.Views

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class WhatIsYourDateOfBirthController @Inject() (
    mcc:     MessagesControllerComponents,
    views:   Views,
    actions: Actions,
    journeyService: JourneyService
)(implicit execution: ExecutionContext) extends FrontendController(mcc) {

  val get: Action[AnyContent] = actions.journeyAction { implicit request =>
    getResult
  }

  private def getResult(implicit request: Request[_]): Result = Ok(views.whatIsYourDateOfBirthPage(WhatIsYourDateOfBirthForm.form))

  val post: Action[AnyContent] = actions.journeyAction.async { implicit request =>
    request.journey match {
      case j: JTerminal                      => JourneyRouter.handleFinalJourneyOnNonFinalPageF(j)
      case j: JBeforeCheckYourReferenceValid => JourneyRouter.sendToCorrespondingPageF(j)
      case j: JourneyCheckYourReferenceValid => processForm(j)
      case _: JAfterCheckYourReferenceValid => ???
//        Errors.throwServerErrorException(s"This endpoint supports only ${classOf[JourneyCheckYourReferenceValid].toString}")
      //TODO: discuss alternative approach
      //val journey = j
      //              .into[JourneyCheckYourReferenceValid]
      //              .enableInheritedAccessors
      //              .transform
      //processForm(j)
    }
  }

  private def processForm(journey: JAfterDoYouWantYourRefundViaBankTransferYes)(implicit request: Request[_]): Future[Result] =
    WhatIsYourDateOfBirthForm
      .form
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(views.whatIsYourDateOfBirthPage(
          form = formWithErrors
        ))), validForm => journeyService
              .upsert(journey.into[JourneyWhatIsYourDateOfBirth].withFieldConst(_.dateOfBirth, DateOfBirth(validForm.dayOfMonth, validForm.month, validForm.year)))
              .map(_ => Redirect(controllers.routes.WhatIsYourNationalInsuranceNumberController.get))
      )

}
