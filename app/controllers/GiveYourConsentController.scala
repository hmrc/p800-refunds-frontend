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
import models.AmountInPence
import models.journeymodels.{JAfterWhatIsTheNameOfYourBankAccount, JBeforeWhatIsTheNameOfYourBankAccount, JTerminal, JourneyApprovedRefund, JourneyNotApprovedRefund, JourneyRefundConsentGiven, JourneyWhatIsTheNameOfYourBankAccount}
import play.api.mvc._
import services.JourneyService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.Views

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import util.Errors

@Singleton
class GiveYourConsentController @Inject() (
    mcc:            MessagesControllerComponents,
    views:          Views,
    actions:        Actions,
    journeyService: JourneyService
)(implicit execution: ExecutionContext) extends FrontendController(mcc) {

  val get: Action[AnyContent] = actions.journeyAction { implicit request =>
    request.journey match {
      case j: JTerminal                             => JourneyRouter.handleFinalJourneyOnNonFinalPage(j)
      case j: JBeforeWhatIsTheNameOfYourBankAccount => JourneyRouter.sendToCorrespondingPage(j)
      case j: JourneyWhatIsTheNameOfYourBankAccount => getResult(j)
      case j: JAfterWhatIsTheNameOfYourBankAccount  => JourneyRouter.sendToCorrespondingPage(j)
    }
  }

  private def getResult(journey: JourneyWhatIsTheNameOfYourBankAccount)(implicit request: JourneyRequest[_]) = {
    Ok(views.giveYourConsentPage(journey.bankDescription.friendlyName, AmountInPence(123)))
  }

  //todo maybe we should make this a post? For now it is protected by journey state matching.
  val approveThisRefund: Action[AnyContent] = actions.journeyAction.async { implicit request =>
    request.journey match {
      case j: JTerminal                             => JourneyRouter.handleFinalJourneyOnNonFinalPageF(j)
      case j: JBeforeWhatIsTheNameOfYourBankAccount => JourneyRouter.sendToCorrespondingPageF(j)
      case j: JourneyWhatIsTheNameOfYourBankAccount => approveThisRefundButton(j)
      case j: JAfterWhatIsTheNameOfYourBankAccount  => approveThisRefundButton(j)
    }
  }

  //todo look into whether we should combine this def into the one below and just have one pattern match using JAfterIdentityVerified or something. Would be less duplication, but maybe less clear?
  private def approveThisRefundButton(journey: JourneyWhatIsTheNameOfYourBankAccount)(implicit request: JourneyRequest[_]): Future[Result] = {
    journeyService
      .upsert(journey.into[JourneyRefundConsentGiven].transform)
      .map(_ => Redirect(routes.VerifyBankAccountController.get))
  }

  private def approveThisRefundButton(journey: JAfterWhatIsTheNameOfYourBankAccount)(implicit request: JourneyRequest[_]): Future[Result] = {
    val newJourney = journey match {
      case j: JourneyRefundConsentGiven => j
      case j: JourneyApprovedRefund     => j.into[JourneyRefundConsentGiven].transform
      case _: JourneyNotApprovedRefund  => Errors.throwServerErrorException("Invalid state, it should not be possible to go back to GiveYourConsentController while in JourneyNotApprovedRefund state.")
    }

    journeyService
      .upsert(newJourney)
      .map(_ => Redirect(routes.VerifyBankAccountController.get))
  }

}
