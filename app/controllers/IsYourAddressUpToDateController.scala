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
import models.forms.IsYourAddressUpToDateForm
import models.forms.enumsforforms.IsYourAddressUpToDateFormValue
import models.journeymodels.{HasFinished, Journey, JourneyType}
import nps.IssuePayableOrderConnector
import nps.models.IssuePayableOrderRequest
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
class IsYourAddressUpToDateController @Inject() (
    actions:                    Actions,
    issuePayableOrderConnector: IssuePayableOrderConnector,
    journeyService:             JourneyService,
    mcc:                        MessagesControllerComponents,
    views:                      Views,
    requestSupport:             RequestSupport
)(implicit executionContext: ExecutionContext) extends FrontendController(mcc) {

  import requestSupport._

  def get: Action[AnyContent] = actions.journeyInProgress { implicit journeyRequest: JourneyRequest[_] =>
    Ok(views.isYourAddressUpToDatePage(IsYourAddressUpToDateForm.form))
  }

  def post: Action[AnyContent] = actions.journeyInProgress.async { implicit journeyRequest: JourneyRequest[_] =>
    val journey: Journey = journeyRequest.journey
    Errors.require(journey.getJourneyType === JourneyType.Cheque, "This endpoint supports only Cheque journey")

    IsYourAddressUpToDateForm.form.bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(views.isYourAddressUpToDatePage(
        form = formWithErrors
      ))), {

        case IsYourAddressUpToDateFormValue.Yes =>
          for {
            _ <- issuePayableOrderConnector.issuePayableOrder(
              nino                     = journey.getNino,
              p800Reference            = journey.getP800Reference,
              issuePayableOrderRequest = IssuePayableOrderRequest(
                customerAccountNumber   = journey.getP800ReferenceChecked.customerAccountNumber,
                associatedPayableNumber = journey.getP800ReferenceChecked.associatedPayableNumber,
                currentOptimisticLock   = journey.getP800ReferenceChecked.currentOptimisticLock
              )
            )
            _ <- journeyService.upsert(
              journey.copy(hasFinished = HasFinished.YesSucceeded)
            )
          } yield Redirect(controllers.routes.RequestReceivedController.getCheque)

        case IsYourAddressUpToDateFormValue.No =>
          Future.successful(Redirect(controllers.routes.UpdateYourAddressController.get))

      }
    )
  }

}

