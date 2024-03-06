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
import config.AppConfig
import models.forms.PtaOrChequeForm
import models.forms.enumsforforms.PtaOrChequeFormValue
import models.journeymodels._
import play.api.mvc._
import requests.RequestSupport
import services.JourneyService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import util.Errors
import util.SafeEquals._
import views.Views

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ChooseAnotherWayToGetYourRefundController @Inject() (
    mcc:            MessagesControllerComponents,
    views:          Views,
    actions:        Actions,
    requestSupport: RequestSupport,
    journeyService: JourneyService,
    appConfig:      AppConfig
)(implicit executionContext: ExecutionContext) extends FrontendController(mcc) {

  import requestSupport._

  def getBankTransfer: Action[AnyContent] = actions.journeyInProgress { implicit request: JourneyRequest[AnyContent] =>
    Ok(views.chooseAnotherWayPtaOrChequePage(form = PtaOrChequeForm.form))
  }

  def postBankTransferViaPtaOrCheque: Action[AnyContent] = actions.journeyInProgress.async { implicit request =>
    val journey: Journey = request.journey
    Errors.require(journey.getJourneyType === JourneyType.BankTransfer, "This endpoint supports only BankTransfer journey")

    PtaOrChequeForm.form.bindFromRequest().fold(
      formWithErrors => Future.successful(
        BadRequest(views.chooseAnotherWayPtaOrChequePage(form = formWithErrors))
      ),
      {
        case PtaOrChequeFormValue.BankTransferViaPta =>
          Future.successful(
            Redirect(appConfig.PersonalTaxAccountUrls.personalTaxAccountSignInUrl)
          )
        case PtaOrChequeFormValue.Cheque =>
          journeyService
            .upsert(
              journey.copy(journeyType = Some(JourneyType.Cheque))
            )
            .map { updatedJourney =>
              if (journey.isIdentityVerified) {
                Redirect(controllers.routes.CompleteYourRefundRequestController.get)
              } else {
                Redirect(WeNeedYouToConfirmYourIdentityController.redirectLocation(updatedJourney))
              }
            }
      }
    )
  }

}
