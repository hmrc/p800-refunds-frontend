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
import models.journeymodels.{Journey, JourneyType}
import play.api.mvc._
import services.JourneyService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import util.Errors
import util.SafeEquals.EqualsOps
import views.Views

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class GiveYourConsentController @Inject() (
    mcc:            MessagesControllerComponents,
    views:          Views,
    actions:        Actions,
    journeyService: JourneyService
)(implicit execution: ExecutionContext) extends FrontendController(mcc) {

  val get: Action[AnyContent] = actions.journeyInProgress { implicit request =>
    val journey: Journey = request.journey
    Errors.require(journey.getJourneyType === JourneyType.BankTransfer, "This endpoint supports only BankTransfer journey")

    Ok(views.giveYourConsentPage(
      bankName      = journey.getBankDescription.friendlyName,
      amountInPence = journey.getIdentityVerificationResponse.amount
    ))
  }

  val post: Action[AnyContent] = actions.journeyInProgress.async { implicit request =>
    val journey: Journey = request.journey
    Errors.require(journey.getJourneyType === JourneyType.BankTransfer, "This endpoint supports only BankTransfer journey")

    //TODO: call CONSENTS ECOSPEND API here https://docs.ecospend.com/new/references.html?render=consents&url_render=ais
    //TODO: navigate to the bank URL (redirect_url from the response in above API)
    journeyService
      .upsert(journey) //TODO: update journey with the result of the API call
      .map(_ => Redirect(routes.WeAreVerifyingYourBankAccountController.get))
  }
}
