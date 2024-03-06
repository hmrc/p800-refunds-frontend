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
import models.forms.DoYouWantToSignInForm
import models.forms.enumsforforms.DoYouWantToSignInFormValue
import models.journeymodels.{Journey, JourneyType}
import play.api.mvc._
import requests.RequestSupport
import services.JourneyService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import util.Errors
import util.SafeEquals.EqualsOps
import views.Views

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ClaimYourRefundByBankTransferController @Inject() (
    actions:        Actions,
    appConfig:      AppConfig,
    journeyService: JourneyService,
    mcc:            MessagesControllerComponents,
    requestSupport: RequestSupport,
    views:          Views
)(implicit executionContext: ExecutionContext) extends FrontendController(mcc) {

  import requestSupport._

  def get: Action[AnyContent] = actions.journeyInProgress { implicit journeyRequest: JourneyRequest[_] =>
    Ok(views.claimYourRefundByBankTransfer(DoYouWantToSignInForm.form))
  }

  def post: Action[AnyContent] = actions.journeyInProgress.async { implicit request =>
    val journey: Journey = request.journey
    Errors.require(journey.getJourneyType === JourneyType.Cheque, "This endpoint supports only Cheque journey")

    DoYouWantToSignInForm.form.bindFromRequest().fold(
      formWithErrors => Future.successful(
        BadRequest(views.claimYourRefundByBankTransfer(form = formWithErrors))
      ),
      {
        case DoYouWantToSignInFormValue.Yes =>
          Future.successful(
            Redirect(appConfig.PersonalTaxAccountUrls.personalTaxAccountSignInUrl)
          )
        case DoYouWantToSignInFormValue.No =>
          journeyService
            .upsert(journey.copy(journeyType = Some(JourneyType.BankTransfer)))
            .map(updatedJourney => Redirect(WeNeedYouToConfirmYourIdentityController.redirectLocation(updatedJourney)))
      }
    )
  }

}

