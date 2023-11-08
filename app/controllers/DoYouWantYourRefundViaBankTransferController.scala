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
import models.forms.DoYouWantYourRefundViaBankTransferForm
import models.forms.enumsforforms.DoYouWantYourRefundViaBankTransferFormValue
import models.journeymodels._
import play.api.mvc._
import requests.RequestSupport
import services.JourneyService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import util.JourneyLogger
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

  val get: Action[AnyContent] = actions.default { implicit request =>
    Ok(views.doYouWantYourRefundViaBankTransferPage(
      DoYouWantYourRefundViaBankTransferForm.form
    ))
  }

  val post: Action[AnyContent] = actions.journeyAction.async { implicit request =>
    request.journey match {
      case j: JourneyCheckYourReferenceValid =>
        DoYouWantYourRefundViaBankTransferForm.form.bindFromRequest().fold(
          formWithErrors => Future.successful(BadRequest(views.doYouWantYourRefundViaBankTransferPage(
            form = formWithErrors
          ))), {
            case DoYouWantYourRefundViaBankTransferFormValue.Yes =>
              journeyService
                .upsert(j.transformInto[JourneyDoYouWantYourRefundViaBankTransferYes])
                .map(_ => Redirect(controllers.routes.WeNeedYouToConfirmYourIdentityController.get))
            case DoYouWantYourRefundViaBankTransferFormValue.No =>
              journeyService
                .upsert(j.transformInto[JourneyDoYouWantYourRefundViaBankTransferNo])
                .map(_ => Redirect(controllers.routes.YourChequeWillBePostedToYouController.get))
          }
        )
      case j =>
        JourneyLogger.error(s"Unsupported journey state ${j.name}, redirecting to corresponding page")
        // TODO: Handle other cases more appropriately
        throw new Exception("Check your reference page with unexpected state")
    }
  }

}
