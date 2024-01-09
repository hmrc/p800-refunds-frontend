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
import util.Errors
import util.SafeEquals.EqualsOps
import views.Views

import javax.inject.{Inject, Singleton}

@Singleton
class WeAreVerifyingYourBankAccountController @Inject() (
    mcc:     MessagesControllerComponents,
    views:   Views,
    actions: Actions
) extends FrontendController(mcc) {

  def get: Action[AnyContent] = actions.journeyInProgress { implicit request =>
    val journey: Journey = request.journey
    Errors.require(journey.getJourneyType === JourneyType.BankTransfer, "This endpoint supports only BankTransfer journey")

    //TODO: call backend and check what is the outcome of the Ecospend Webhook, if its not there, redirect to itself, if it succeeds and validation ok then progress, if validation fails then redirect to RequestNotSubmitted
    //TODO: call Ecospend - Get account details API to get more info about account
    //TODO: call API#1133: Get Bank Details Risk Result (aka EDH Repayment Details Risk)
    //TODO: call API#JF72745 Claim Overpayment
    //TODO: if API#1133 or API#JF72745 fails, call (JF72755) Suspend Overpayment
    //TODO: if API#1133 or API#JF72745 fails, call API#1132 (EPID0771) Case Management Notified
    //TODO: if API#1133 or API#JF72745 fails, redirect to RequestNotSubmitted

    Ok(views.weAreVerifyingYourBankAccountPage())
  }
}
