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
import models.ecospend.consent.BankConsentResponse
import models.journeymodels.{Journey, JourneyType}
import play.api.mvc._
import services.EcospendService
import services.JourneyService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import util.SafeEquals.EqualsOps
import util.Errors
import views.Views

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class GiveYourConsentController @Inject() (
    mcc:             MessagesControllerComponents,
    views:           Views,
    actions:         Actions,
    ecospendService: EcospendService,
    journeyService:  JourneyService
)(implicit execution: ExecutionContext) extends FrontendController(mcc) {

  def get: Action[AnyContent] = actions.journeyInProgress { implicit request =>
    val journey: Journey = request.journey
    Errors.require(journey.getJourneyType === JourneyType.BankTransfer, "This endpoint supports only BankTransfer journey")

    Ok(views.giveYourPermissionPage(
      bankName      = journey.getBankDescription.friendlyName,
      amountInPence = journey.getAmount
    ))
  }

  def post: Action[AnyContent] = actions.journeyInProgress.async { implicit request =>
    val journey: Journey = request.journey
    Errors.require(journey.getJourneyType === JourneyType.BankTransfer, "This endpoint supports only BankTransfer journey")

    for {
      bankConsentResponse: BankConsentResponse <- ecospendService.createConsent(journey)
      newJourney = journey.update(
        bankConsentResponse = bankConsentResponse
      )
      _ <- journeyService.upsert(newJourney)
    } yield Redirect(bankConsentResponse.bankConsentUrl.toString)

  }
}
