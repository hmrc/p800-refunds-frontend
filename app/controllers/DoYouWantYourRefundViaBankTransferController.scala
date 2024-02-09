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
import models.forms.DoYouWantYourRefundViaBankTransferForm
import models.forms.enumsforforms.DoYouWantYourRefundViaBankTransferFormValue
import models.journeymodels._
import play.api.mvc._
import requests.RequestSupport
import services.JourneyService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.Views

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DoYouWantYourRefundViaBankTransferController @Inject() (
    mcc:            MessagesControllerComponents,
    requestSupport: RequestSupport,
    journeyService: JourneyService,
    views:          Views,
    actions:        Actions
)(implicit ec: ExecutionContext) extends FrontendController(mcc) {

  import requestSupport._

  def get: Action[AnyContent] = actions.journeyInProgress { implicit request =>
    Ok(views.doYouWantYourRefundViaBankTransferPage(
      DoYouWantYourRefundViaBankTransferForm.form
    ))
  }

  def post: Action[AnyContent] = actions.journeyInProgress.async { implicit request =>
    val journey = request.journey
    DoYouWantYourRefundViaBankTransferForm
      .form
      .bindFromRequest()
      .fold(
        formWithErrors =>
          Future.successful(BadRequest(views.doYouWantYourRefundViaBankTransferPage(
            form = formWithErrors
          ))), {
          formValue =>

            val journeyType: JourneyType = formValue match {
              case DoYouWantYourRefundViaBankTransferFormValue.Yes => JourneyType.BankTransfer
              case DoYouWantYourRefundViaBankTransferFormValue.No  => JourneyType.Cheque
            }
            journeyService
              .upsert(journey.update(journeyType = journeyType))
              .map { _ =>
                val redirectLocation: Call = journeyType match {
                  case JourneyType.Cheque       => controllers.routes.WeNeedYouToConfirmYourIdentityController.getCheque
                  case JourneyType.BankTransfer => controllers.routes.WeNeedYouToConfirmYourIdentityController.getBankTransfer
                }
                Redirect(redirectLocation)
              }
        }
      )
  }

}
