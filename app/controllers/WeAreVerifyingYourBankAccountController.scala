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
import connectors.P800RefundsExternalApiConnector
import models.ecospend.account.BankAccountSummary
import models.ecospend.consent.{BankReferenceId, ConsentId, ConsentStatus}
import models.journeymodels._
import models.p800externalapi.EventValue
import play.api.mvc._
import services.{EcospendService, JourneyService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import util.Errors
import util.SafeEquals.EqualsOps
import views.Views

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}


@Singleton
class WeAreVerifyingYourBankAccountController @Inject() (
    actions:                         Actions,
    ecospendService:                 EcospendService,
    edhConnector: EdhConnector,
    journeyService:                  JourneyService,
    mcc:                             MessagesControllerComponents,
    p800RefundsExternalApiConnector: P800RefundsExternalApiConnector,
    views:                           Views
)(implicit ec: ExecutionContext) extends FrontendController(mcc) {

  def get(status: Option[ConsentStatus], consent_id: Option[ConsentId], bank_reference_id: Option[BankReferenceId]): Action[AnyContent] = actions.journeyInProgress.async { implicit request: JourneyRequest[_] =>
    val journey: Journey = request.journey
    //sanity checks
    Errors.require(journey.getJourneyType === JourneyType.BankTransfer, "This endpoint supports only BankTransfer journey")
    consent_id.fold(Errors.throwBadRequestException("This endpoint requires a valid consent_id query parameter")) { consentId: UUID =>
      Errors.require(journey.getBankConsent.id === consentId, "The consent_id supplied via the query parameter must match that stored in the journey. This should be investigated")
    }
    bank_reference_id.fold(Errors.throwBadRequestException("This endpoint requires a valid bank_reference_id query parameter")) { bankReferenceId: BankReferenceId =>
      Errors.require(journey.getBankConsent.bankReferenceId === bankReferenceId, "The bank_reference_id supplied via the query parameter must match that stored in the journey. This should be investigated")
    }

    for {
      // TODO: Assert status, consent_id & bank_reference_id match that contained within the journey
      isValid <- p800RefundsExternalApiConnector.isValid(journey.getBankConsent.id)
      // Call Ecospend - Get account details API to get more info about account
      bankAccountSummary <- ecospendService.getAccountSummary(journey)

      // TODO: Call API#1133: Get Bank Details Risk Result (aka EDH Repayment Details Risk)
      // TODO: Call API#JF72745 Claim Overpayment
      // TODO: If API#1133 or API#JF72745 fails, call (JF72755) Suspend Overpayment
      // TODO: If API#1133 or API#JF72745 fails, call API#1132 (EPID0771) Case Management Notified
      // TODO: If API#1133 or API#JF72745 fails, redirect to RequestNotSubmitted
      newJourney = updateJourneyWithApiCalls(journey, isValid, bankAccountSummary)
      _ <- journeyService.upsert(newJourney)
    } yield isValid match {
      case EventValue.Valid       => Redirect(routes.RequestReceivedController.getBankTransfer)
      case EventValue.NotValid    => Redirect(routes.RefundRequestNotSubmittedController.get)
      case EventValue.NotReceived => Ok(views.weAreVerifyingYourBankAccountPage(status, consent_id, bank_reference_id))
    }
  }

  sealed trait Op {
  }

  case class Next(updatedJourney: Journey) extends Op
  case class Stop(result: Result, journeyToUpsert: Journey) extends Op
  case class StopAndUpdateJourney(result: Result, journeyToUpsert: Journey) extends Op

  def processEcospend(journey: Journey): Future[Op] = journey.isValidEventValue match {
    case Some(isValid) =>  Future.successful(isValid match {
      case EventValue.Valid       => Redirect(routes.RequestReceivedController.getBankTransfer)
      case EventValue.NotValid    =>  Stop(Redirect(routes.RefundRequestNotSubmittedController.get))
      case EventValue.NotReceived => Stop(Ok(views.weAreVerifyingYourBankAccountPage(status, consent_id, bank_reference_id)))
    })
    case None =>  for {
      isValid <- p800RefundsExternalApiConnector.isValid(journey.getBankConsent.)
      // Call Ecospend - Get account details API to get more info about account
      bankAccountSummary <- ecospendService.getAccountSummary(journey)
    }
  }

  //TODO: include more API updates when they're ready
  private def updateJourneyWithApiCalls(journey: Journey, eventValue: EventValue, bankAccountSummary: BankAccountSummary): Journey = {
    val hasFinished: HasFinished = eventValue match {
      case EventValue.Valid       => HasFinished.YesSucceeded
      case EventValue.NotValid    => HasFinished.YesRefundNotSubmitted
      case EventValue.NotReceived => HasFinished.No
    }
    journey
      .update(bankAccountSummary)
      .update(eventValue)
      .update(hasFinished)
  }
}
