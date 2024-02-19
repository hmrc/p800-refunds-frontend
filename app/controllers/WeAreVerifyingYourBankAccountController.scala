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
import models.ecospend.account.BankAccountSummary
import models.ecospend.consent.{ConsentStatus, BankReferenceId}
import models.journeymodels._
import play.api.mvc._
import services.{EcospendService, JourneyService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import util.Errors
import util.SafeEquals.EqualsOps
import views.Views

import java.util.UUID
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class WeAreVerifyingYourBankAccountController @Inject() (
    mcc:             MessagesControllerComponents,
    views:           Views,
    ecospendService: EcospendService,
    journeyService:  JourneyService,
    actions:         Actions
)(implicit ec: ExecutionContext) extends FrontendController(mcc) {

  def get(status: Option[ConsentStatus], consent_id: Option[UUID], bank_reference_id: Option[BankReferenceId]): Action[AnyContent] = actions.journeyInProgress.async { implicit request: JourneyRequest[_] =>
    val journey: Journey = request.journey
    Errors.require(journey.getJourneyType === JourneyType.BankTransfer, "This endpoint supports only BankTransfer journey")

    consent_id.fold(Errors.throwBadRequestException("This endpoint requires a valid consent_id query parameter")) { consentId: UUID =>
      Errors.require(journey.getBankConsent.id === consentId, "The consent_id supplied via the query parameter must match that stored in the journey. This should be investigated")
    }

    for {
      // TODO: Assert status, consent_id & bank_reference_id match that contained within the journey
      // TODO: Call backend and check what is the outcome of the Ecospend Webhook, if its not there, redirect to itself, if it succeeds and validation ok then progress, if validation fails then redirect to RequestNotSubmitted

      // Call Ecospend - Get account details API to get more info about account
      bankAccountSummary: BankAccountSummary <- ecospendService.getAccountSummary(journey)

      // TODO: Call API#1133: Get Bank Details Risk Result (aka EDH Repayment Details Risk)
      // TODO: Call API#JF72745 Claim Overpayment
      // TODO: If API#1133 or API#JF72745 fails, call (JF72755) Suspend Overpayment
      // TODO: If API#1133 or API#JF72745 fails, call API#1132 (EPID0771) Case Management Notified
      // TODO: If API#1133 or API#JF72745 fails, redirect to RequestNotSubmitted
      _ <- journeyService.upsert(journey.update(
        bankAccountSummary = bankAccountSummary
      ))
    } yield Ok(views.weAreVerifyingYourBankAccountPage(status, consent_id, bank_reference_id))
  }
}
