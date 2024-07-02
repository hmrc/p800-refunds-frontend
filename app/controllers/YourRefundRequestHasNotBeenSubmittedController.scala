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
import models.ecospend.consent.{BankReferenceId, ConsentId, ConsentStatus}
import play.api.mvc._
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.Views

import javax.inject.{Inject, Singleton}

@Singleton
class YourRefundRequestHasNotBeenSubmittedController @Inject() (
    mcc:     MessagesControllerComponents,
    views:   Views,
    actions: Actions
) extends FrontendController(mcc) {

  def get(status: Option[ConsentStatus], consent_id: Option[ConsentId], bank_reference_id: Option[BankReferenceId]): Action[AnyContent] = actions.journeyInProgress {
    implicit request: Request[_] =>
      Ok(views.yourRefundRequestHasNotBeenSubmitted(status, consent_id, bank_reference_id))
  }
}
