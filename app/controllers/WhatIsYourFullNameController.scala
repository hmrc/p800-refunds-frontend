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
import models.FullName
import models.forms.WhatIsYourFullNameForm
import models.journeymodels._
import play.api.mvc._
import requests.RequestSupport
import services.JourneyService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.Views

import scala.concurrent.{ExecutionContext, Future}
import javax.inject.{Inject, Singleton}

@Singleton
class WhatIsYourFullNameController @Inject() (
    mcc:            MessagesControllerComponents,
    requestSupport: RequestSupport,
    journeyService: JourneyService,
    views:          Views,
    actions:        Actions
)(implicit ec: ExecutionContext) extends FrontendController(mcc) {

  import requestSupport._

  val get: Action[AnyContent] = actions.journeyAction.async { implicit request =>
    request.journey match {
      case j: JTerminal                                    => JourneyRouter.handleFinalJourneyOnNonFinalPageF(j)
      case j: JBeforeDoYouWantYourRefundViaBankTransferYes => JourneyRouter.sendToCorrespondingPageF(j)
      case j: JourneyDoYouWantYourRefundViaBankTransferNo  => JourneyRouter.sendToCorrespondingPageF(j)
      case _: JourneyDoYouWantYourRefundViaBankTransferYes => Future.successful(getResult(None))
      case j: JourneyWhatIsYourFullName                    => Future.successful(getResult(Some(j.fullName)))
      case j: JAfterWhatIsYourFullName                     => Future.successful(getResult(Some(j.fullName)))
    }
  }

  private def getResult(maybeFullName: Option[FullName])(implicit request: Request[_]): Result = Ok(views.whatIsYourFullNamePage(
    form = maybeFullName.fold(WhatIsYourFullNameForm.form)(WhatIsYourFullNameForm.form.fill)
  ))

  val post: Action[AnyContent] = actions.journeyAction.async { implicit request =>
    request.journey match {
      case j: JTerminal                                    => JourneyRouter.handleFinalJourneyOnNonFinalPageF(j)
      case j: JBeforeDoYouWantYourRefundViaBankTransferYes => JourneyRouter.sendToCorrespondingPageF(j)
      case j: JourneyDoYouWantYourRefundViaBankTransferNo  => JourneyRouter.sendToCorrespondingPageF(j)
      case j: JourneyDoYouWantYourRefundViaBankTransferYes => processForm(j)
      case j: JourneyCheckYourAnswersChange                => processForm(j)
      case j: JAfterDoYouWantYourRefundViaBankTransferYes  => processFormJourneyAlreadyHasFullName(j)
    }
  }

  private def processFormJourneyAlreadyHasFullName(journey: JAfterDoYouWantYourRefundViaBankTransferYes)(implicit request: Request[_]): Future[Result] = {
      def updatedJourney(fullName: FullName): Journey = journey match {
        case j: JourneyWhatIsYourFullName                => j.copy(fullName = fullName)
        case j: JourneyWhatIsYourDateOfBirth             => j.copy(fullName = fullName)
        case j: JourneyWhatIsYourNationalInsuranceNumber => j.copy(fullName = fullName)
        case j: JourneyCheckYourAnswersChange            => j.into[JourneyWhatIsYourFullName].withFieldConst(_.fullName, fullName).transform
        case j: JourneyCheckYourAnswers                  => j.into[JourneyWhatIsYourFullName].withFieldConst(_.fullName, fullName).transform
        case j: JourneyIdentityVerified                  => j.into[JourneyWhatIsYourFullName].withFieldConst(_.fullName, fullName).transform
        case j: JourneyIdentityNotVerified               => j.into[JourneyWhatIsYourFullName].withFieldConst(_.fullName, fullName).transform
        case j: JourneyWhatIsTheNameOfYourBankAccount    => j.into[JourneyWhatIsYourFullName].withFieldConst(_.fullName, fullName).transform
      }
    WhatIsYourFullNameForm
      .form
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(views.whatIsYourFullNamePage(formWithErrors))),
        fullName => {
          journeyService
            .upsert(updatedJourney(fullName))
            .map(_ => Redirect(controllers.routes.WhatIsYourDateOfBirthController.get))
        }
      )
  }

  private def processForm(journey: JourneyDoYouWantYourRefundViaBankTransferYes)(implicit request: Request[_]): Future[Result] =
    WhatIsYourFullNameForm
      .form
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(views.whatIsYourFullNamePage(formWithErrors))),
        fullName => {
          journeyService
            .upsert(
              journey.into[JourneyWhatIsYourFullName]
                .withFieldConst(_.fullName, fullName)
                .transform
            )
            .map(_ => Redirect(controllers.routes.WhatIsYourDateOfBirthController.get))
        }
      )

  private def processForm(journey: JourneyCheckYourAnswersChange)(implicit request: Request[_]): Future[Result] =
    WhatIsYourFullNameForm
      .form
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(views.whatIsYourFullNamePage(formWithErrors))),
        fullName => {
          journeyService
            .upsert(
              journey.into[JourneyWhatIsYourNationalInsuranceNumber]
                .withFieldConst(_.fullName, fullName)
                .transform
            )
            .map(_ => Redirect(controllers.routes.CheckYourAnswersController.get))
        }
      )

}
