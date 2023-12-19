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
import config.AppConfig
import io.scalaland.chimney.dsl._
import models.forms.ChooseAnotherWayToGetYourRefundForm
import models.forms.enumsforforms.ChooseAnotherWayToGetYourRefundFormValue
import models.journeymodels._
import play.api.mvc._
import requests.RequestSupport
import services.JourneyService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
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

  val get: Action[AnyContent] = actions.journeyAction { implicit request =>
    request.journey match {
      case _: JourneyNotApprovedRefund              => getResult
      case j: JTerminal                             => JourneyRouter.handleFinalJourneyOnNonFinalPage(j)
      case j: JBeforeIdentityVerified               => JourneyRouter.sendToCorrespondingPage(j)
      case _: JourneyIdentityVerified               => getResult
      case _: JourneyIdentityNotVerified            => getResult
      case _: JourneyWhatIsTheNameOfYourBankAccount => getResult
      case j: JAfterIdentityVerified                => JourneyRouter.sendToCorrespondingPage(j)
    }
  }

  private def getResult(implicit request: Request[_]) = Ok(views.chooseAnotherWayToReceiveYourRefundPage(form = ChooseAnotherWayToGetYourRefundForm.form))

  val post: Action[AnyContent] = actions.journeyAction.async { implicit request =>
    request.journey match {
      case j: JourneyNotApprovedRefund              => processFormNotApprovedRefund(j)
      case j: JTerminal                             => JourneyRouter.handleFinalJourneyOnNonFinalPageF(j)
      case j: JBeforeIdentityVerified               => JourneyRouter.sendToCorrespondingPageF(j)
      case j: JourneyIdentityVerified               => JourneyRouter.sendToCorrespondingPageF(j)
      case j: JourneyIdentityNotVerified            => processForm(Left(j))
      case j: JourneyWhatIsTheNameOfYourBankAccount => processForm(Right(j))
      case j: JAfterIdentityVerified                => JourneyRouter.sendToCorrespondingPageF(j)
    }
  }

  private def processForm(journey: Either[JourneyIdentityNotVerified, JourneyWhatIsTheNameOfYourBankAccount])(implicit request: Request[_]): Future[Result] = {
    ChooseAnotherWayToGetYourRefundForm.form.bindFromRequest().fold(
      formWithErrors => Future.successful(
        BadRequest(views.chooseAnotherWayToReceiveYourRefundPage(form = formWithErrors))
      ), (validForm: ChooseAnotherWayToGetYourRefundFormValue) => {
        val (newJourney, redirect) = validForm match {
          case ChooseAnotherWayToGetYourRefundFormValue.BankTransfer =>
            journey.merge -> appConfig.PersonalTaxAccountUrls.personalTaxAccountSignInUrl
          case ChooseAnotherWayToGetYourRefundFormValue.Cheque =>
            journey.merge -> routes.YourChequeWillBePostedToYouController.get.url
        }
        journeyService
          .upsert(newJourney)
          .map(_ => Redirect(redirect))
      }
    )
  }

  private def processFormNotApprovedRefund(journey: JourneyNotApprovedRefund)(implicit request: Request[_]): Future[Result] = {
    ChooseAnotherWayToGetYourRefundForm.form.bindFromRequest().fold(
      formWithErrors => Future.successful(
        BadRequest(views.chooseAnotherWayToReceiveYourRefundPage(form = formWithErrors))
      ), (validForm: ChooseAnotherWayToGetYourRefundFormValue) => {
        val (newJourney, redirect) = validForm match {
          case ChooseAnotherWayToGetYourRefundFormValue.BankTransfer =>
            journey -> appConfig.PersonalTaxAccountUrls.personalTaxAccountSignInUrl
          case ChooseAnotherWayToGetYourRefundFormValue.Cheque =>
            journey
              .into[JourneyDoYouWantYourRefundViaBankTransferNo]
              .enableInheritedAccessors
              .transform ->
              routes.YourChequeWillBePostedToYouController.get.url
        }
        journeyService
          .upsert(newJourney)
          .map(_ => Redirect(redirect))
      }
    )
  }

}
