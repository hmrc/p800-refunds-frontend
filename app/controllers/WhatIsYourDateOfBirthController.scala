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
import models.dateofbirth.{DateOfBirth, DayOfMonth, Month, Year}
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
    //todo match on journey stages... will need page before this to be able to do it.
    request.journey match {
      case j: JTerminal                                    => JourneyRouter.handleFinalJourneyOnNonFinalPage(j)
      case j: JBeforeDoYouWantYourRefundViaBankTransferYes => JourneyRouter.sendToCorrespondingPage(j)
      case j: JourneyDoYouWantYourRefundViaBankTransferNo  => JourneyRouter.sendToCorrespondingPage(j)
      case _: JourneyDoYouWantYourRefundViaBankTransferYes => getResult(None)
      case j: JAfterWhatIsYourDateOfBirth                  => getResult(Some(j))
    }
  }

  private def getResult(maybeJourneyToPrepop: Option[JAfterWhatIsYourDateOfBirth])(implicit request: JourneyRequest[_]): Result = {
    maybeJourneyToPrepop.fold(Ok(views.whatIsYourDateOfBirthPage(WhatIsYourDateOfBirthForm.form))) { j =>
      Ok(views.whatIsYourDateOfBirthPage(WhatIsYourDateOfBirthForm.form.fill(WhatIsYourDateOfBirthForm(j.dateOfBirth))))
    }
  }

  val post: Action[AnyContent] = actions.journeyAction.async { implicit request =>
    println("Inside the post action")
    request.journey match {
      case j: JTerminal                                    => JourneyRouter.handleFinalJourneyOnNonFinalPageF(j)
      case j: JBeforeDoYouWantYourRefundViaBankTransferYes => JourneyRouter.sendToCorrespondingPageF(j)
      case j: JourneyDoYouWantYourRefundViaBankTransferNo  => JourneyRouter.sendToCorrespondingPageF(j)
      case j: JourneyDoYouWantYourRefundViaBankTransferYes => processForm(j)
      //      case j: JourneyDoYouWantYourRefundViaBankTransferYes => processForm(Left(j))
      case j: JAfterWhatIsYourDateOfBirth                  => JourneyRouter.sendToCorrespondingPageF(j) //todo change back to process form
      //      case j: JAfterWhatIsYourDateOfBirth                  => processForm(Right(j))
    }
  }

  //  private def processForm(journey: Either[JourneyDoYouWantYourRefundViaBankTransferYes, JAfterWhatIsYourDateOfBirth])(implicit request: Request[_]): Future[Result] = {
  private def processForm(journey: JourneyDoYouWantYourRefundViaBankTransferYes)(implicit request: Request[_]): Future[Result] = {
    println("Inside process form")
    WhatIsYourDateOfBirthForm
      .form
      .bindFromRequest()
      .fold(
        formWithErrors => {
          println(s"bad form: ${formWithErrors.data.toString()}")
          println(s"bad form: ${formWithErrors.errors.toString()}")
          Future.successful(BadRequest(views.whatIsYourDateOfBirthPage(form = formWithErrors)))
        },
        validForm => {

          //          val dobFromForm = DateOfBirth(
          //            dayOfMonth = validForm.dayOfMonth,
          //            month      = validForm.month,
          //            year       = validForm.year
          //          )
          //
          //          val transformedJourney: JourneyDoYouWantYourRefundViaBankTransferYes => JourneyWhatIsYourDateOfBirth = j => j.into[JourneyWhatIsYourDateOfBirth]
          //            .withFieldConst(_.dateOfBirth, dobFromForm)
          //            .enableInheritedAccessors
          //            .transform
          //
          //          val journeyToUpsert = journey.fold[Journey](
          //            (o: JourneyDoYouWantYourRefundViaBankTransferYes) => transformedJourney(o),
          //            (o: JAfterWhatIsYourDateOfBirth) => o.into[JAfterWhatIsYourDateOfBirth].withFieldConst(_.dateOfBirth, dobFromForm).transform
          //          )

          println(s"I am the journey: ${journey.toString}")
          val dob = DateOfBirth(
            DayOfMonth(validForm.date.dayOfMonth.value),
            Month(validForm.date.month.value),
            Year(validForm.date.year.value)
          )
          journeyService
            .upsert(journey.into[JourneyWhatIsYourDateOfBirth].withFieldConst(_.dateOfBirth, dob).transform)
            .map(_ => Redirect(controllers.routes.WhatIsYourNationalInsuranceNumberController.get))
        }
      )
  }

}
