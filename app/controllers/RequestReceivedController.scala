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
import models.journeymodels._
import play.api.mvc._
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import util.SafeEquals.EqualsOps
import views.Views

import java.time.LocalDate
import javax.inject.{Inject, Singleton}

@Singleton
class RequestReceivedController @Inject() (
    mcc:     MessagesControllerComponents,
    views:   Views,
    actions: Actions
) extends FrontendController(mcc) {

  def get: Action[AnyContent] = actions.journeyFinished { implicit request =>
    val journey: Journey = request.journey

    if (journey.hasFinished === HasFinished.YesFailed) {
      Redirect(controllers.routes.YourRefundRequestHasNotBeenSubmittedController.get)
    } else {

      journey.getJourneyType match {
        case JourneyType.Cheque       => getResultCheque(journey)
        case JourneyType.BankTransfer => getResultBankTransfer(journey)
      }
    }
  }

  private def getResultBankTransfer(journey: Journey)(implicit request: Request[_]): Result = {
    Ok(views.bankTransferRequestReceivedPage(
      journey.getP800Reference,
      journey.getIdentityVerificationResponse.amount,
      "1 December 2023" //TODO: get this from identity verification response
    ))
  }

  private def getResultCheque(journey: Journey)(implicit request: Request[_]) = {
    val dummyDate = LocalDate.of(2024, 1, 16)

    Ok(views.chequeRequestReceivedPage(
      p800Reference       = journey.getP800Reference,
      refundAmountInPence = journey.getIdentityVerificationResponse.amount,
      chequeArriveBy      = dummyDate //TODO: unhardcode this
    ))
  }
}
