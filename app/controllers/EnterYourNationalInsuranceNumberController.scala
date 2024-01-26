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
class EnterYourNationalInsuranceNumberController @Inject() (
    mcc:            MessagesControllerComponents,
    views:          Views,
    actions:        Actions,
    journeyService: JourneyService,
    requestSupport: RequestSupport
)(implicit ec: ExecutionContext) extends FrontendController(mcc) {

  import requestSupport._

  def getBankTransfer: Action[AnyContent] = actions.journeyInProgress { implicit request: JourneyRequest[_] =>
    getResult
  }

  def getCheque: Action[AnyContent] = actions.journeyInProgress { implicit request: JourneyRequest[_] =>
    getResult
  }

  private def getResult(implicit request: JourneyRequest[_]): Result = {
    val journey: Journey = request.journey

    Ok(views.enterYourNationalInsuranceNumberPage(
      form = journey.nationalInsuranceNumber.fold(
        WhatIsYourNationalInsuranceNumberForm.form
      )(
          WhatIsYourNationalInsuranceNumberForm.form.fill
        )
    ))
  }

  def postBankTransfer: Action[AnyContent] = actions.journeyInProgress.async { implicit request: JourneyRequest[_] =>
    val journey: Journey = request.journey
    processForm(journey)
  }

  def postCheque: Action[AnyContent] = actions.journeyInProgress.async { implicit request: JourneyRequest[_] =>
    val journey: Journey = request.journey
    processForm(journey)
  }

  private def processForm(journey: Journey)(implicit request: JourneyRequest[_]): Future[Result] = {
    val defaultNextCall = journey.getJourneyType match {
      case JourneyType.BankTransfer => controllers.routes.EnterYourDateOfBirthController.get
      case JourneyType.Cheque       => controllers.routes.CheckYourAnswersController.getCheque
    }
    val nextCall = if (request.journey.isChanging) controllers.CheckYourAnswersController.redirectLocation(request.journey) else defaultNextCall

    WhatIsYourNationalInsuranceNumberForm
      .form
      .bindFromRequest()
      .fold(
        formWithErrors => {
          Future.successful(BadRequest(views.enterYourNationalInsuranceNumberPage(form = formWithErrors)))
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

object EnterYourNationalInsuranceNumberController {
  def redirectLocation(journey: Journey)(implicit request: Request[_]): Call = Journey.deriveRedirectByJourneyType(
    journeyType           = journey.getJourneyType,
    chequeJourneyRedirect = controllers.routes.EnterYourNationalInsuranceNumberController.getCheque,
    bankJourneyRedirect   = controllers.routes.EnterYourNationalInsuranceNumberController.getBankTransfer
  )

  def derivePostEndpoint(journey: Journey)(implicit request: Request[_]): Call = Journey.deriveRedirectByJourneyType(
    journeyType           = journey.getJourneyType,
    chequeJourneyRedirect = controllers.routes.EnterYourNationalInsuranceNumberController.postCheque,
    bankJourneyRedirect   = controllers.routes.EnterYourNationalInsuranceNumberController.postBankTransfer
  )
}
