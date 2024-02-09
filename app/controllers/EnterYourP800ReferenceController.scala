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
class EnterYourP800ReferenceController @Inject() (
    mcc:            MessagesControllerComponents,
    requestSupport: RequestSupport,
    journeyService: JourneyService,
    views:          Views,
    actions:        Actions
)(implicit ec: ExecutionContext) extends FrontendController(mcc) {

  import requestSupport._

  def getBankTransfer: Action[AnyContent] = actions.journeyInProgress { implicit request: JourneyRequest[_] =>
    val journey: Journey = request.journey
    getResult(journey.p800Reference)
  }

  def getCheque: Action[AnyContent] = actions.journeyInProgress { implicit request: JourneyRequest[_] =>
    val journey: Journey = request.journey
    getResult(journey.p800Reference)
  }

  private def getResult(maybeP800Reference: Option[P800Reference])(implicit request: JourneyRequest[_]): Result = {
    Ok(views.enterP800ReferencePage(
      form = maybeP800Reference.fold(
        EnterP800ReferenceForm.form
      )(
          EnterP800ReferenceForm.form.fill
        )
    ))
      .makeChanging()
  }

  def postBankTransfer: Action[AnyContent] = actions.journeyInProgress.async { implicit request =>
    processForm(request.journey)
  }

  def postCheque: Action[AnyContent] = actions.journeyInProgress.async { implicit request =>
    processForm(request.journey)
  }

  private def processForm(journey: Journey)(implicit request: JourneyRequest[_]): Future[Result] = {
    /*
     * It must navigate to the next page or to the checkYourAnswers page depending if it was a "change" or not.
     */
    val nextCall =
      if (journey.isChanging) controllers.CheckYourAnswersController.redirectLocation(request.journey)
      else EnterYourNationalInsuranceNumberController.redirectLocation(request.journey)

    EnterP800ReferenceForm
      .form
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(views.enterP800ReferencePage(
          form = formWithErrors
        ))),
        p800Reference => {
          journeyService
            .upsert(journey.update(
              p800Reference = p800Reference
            ).update(
              isChanging = false
            ))
            .map(_ => Redirect(nextCall))
        }
      )
  }

}

object EnterYourP800ReferenceController {
  def redirectLocation(journey: Journey)(implicit request: Request[_]): Call = Journey.deriveRedirectByJourneyType(
    journeyType           = journey.getJourneyType,
    chequeJourneyRedirect = controllers.routes.EnterYourP800ReferenceController.getCheque,
    bankJourneyRedirect   = controllers.routes.EnterYourP800ReferenceController.getBankTransfer
  )

  def derivePostEndpoint(journey: Journey)(implicit request: Request[_]): Call = Journey.deriveRedirectByJourneyType(
    journeyType           = journey.getJourneyType,
    chequeJourneyRedirect = controllers.routes.EnterYourP800ReferenceController.postCheque,
    bankJourneyRedirect   = controllers.routes.EnterYourP800ReferenceController.postBankTransfer
  )
}
