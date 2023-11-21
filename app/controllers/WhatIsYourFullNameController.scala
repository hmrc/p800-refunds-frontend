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
import models.forms.WhatIsYourFullNameForm
import models.journeymodels._
import play.api.mvc._
import requests.RequestSupport
import services.JourneyService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import util.Errors
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
      case _: JourneyDoYouWantYourRefundViaBankTransferYes => Future.successful(getResult)
      case j: JAfterDoYouWantYourRefundViaBankTransferYes =>
        journeyService
          .upsert(
            j
              .into[JourneyDoYouWantYourRefundViaBankTransferYes]
              .enableInheritedAccessors
              .transform
          )
          .map(_ => getResult)
    }
  }

  private def getResult(implicit request: Request[_]): Result = Ok(views.whatIsYourFullNamePage(
    form = WhatIsYourFullNameForm.form
  ))

  val post: Action[AnyContent] = actions.journeyAction.async { implicit request =>
    request.journey match {
      case j: JTerminal                                    => JourneyRouter.handleFinalJourneyOnNonFinalPageF(j)
      case j: JBeforeDoYouWantYourRefundViaBankTransferYes => JourneyRouter.sendToCorrespondingPageF(j)
      case j: JourneyDoYouWantYourRefundViaBankTransferNo  => JourneyRouter.sendToCorrespondingPageF(j)
      case j: JourneyDoYouWantYourRefundViaBankTransferYes => processForm(j)
      case _: JAfterDoYouWantYourRefundViaBankTransferYes =>
        Errors.throwServerErrorException(s"This endpoint supports only ${classOf[JourneyDoYouWantYourRefundViaBankTransferYes].toString}")
      // TODO: Discuss alternative approach
    }
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

}
