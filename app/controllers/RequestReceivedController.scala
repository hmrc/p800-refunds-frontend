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
import models.journeymodels._
import play.api.mvc._
import services.DateCalculatorService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import util.SafeEquals.EqualsOps
import views.Views

import java.time.{Clock, LocalDate}
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RequestReceivedController @Inject() (
    actions:               Actions,
    appConfig:             AppConfig,
    clock:                 Clock,
    dateCalculatorService: DateCalculatorService,
    mcc:                   MessagesControllerComponents,
    views:                 Views
)(implicit executionContext: ExecutionContext) extends FrontendController(mcc) {

  def getBankTransfer: Action[AnyContent] = actions.journeyFinished.async { implicit request =>
    val journey: Journey = request.journey

    if (journey.hasFinished === HasFinished.YesRefundNotSubmitted) {
      Future.successful(Redirect(controllers.routes.RefundRequestNotSubmittedController.get))
    } else {
      journey.getJourneyType match {
        case JourneyType.Cheque       => getResultCheque(journey)
        case JourneyType.BankTransfer => getResultBankTransfer(journey)
      }
    }
  }
  def getCheque: Action[AnyContent] = actions.journeyFinished.async { implicit request =>
    val journey: Journey = request.journey

    if (journey.hasFinished === HasFinished.YesRefundNotSubmitted) {
      Future.successful(Redirect(controllers.routes.RefundRequestNotSubmittedController.get))
    } else {

      journey.getJourneyType match {
        case JourneyType.Cheque       => getResultCheque(journey)
        case JourneyType.BankTransfer => getResultBankTransfer(journey)
      }
    }
  }

  private def getResultBankTransfer(journey: Journey)(implicit request: JourneyRequest[_]): Future[Result] = {
    dateCalculatorService.getFutureDate().map { futureDate =>
      Ok(views.requestReceivedBankTransferPage(
        journey.getP800Reference.sanitiseReference,
        journey.getAmount,
        futureDate
      ))
    }
  }

  private def getResultCheque(journey: Journey)(implicit request: JourneyRequest[_]): Future[Result] = {
    val chequeArrivalByDate: LocalDate = LocalDate.now(clock).plusWeeks(appConfig.JourneyVariables.chequeFutureDateAddition)
    Future.successful(Ok(views.requestReceivedChequePage(
      p800Reference       = journey.getP800Reference.sanitiseReference,
      refundAmountInPence = journey.getAmount,
      chequeArriveBy      = chequeArrivalByDate
    )))
  }
}
