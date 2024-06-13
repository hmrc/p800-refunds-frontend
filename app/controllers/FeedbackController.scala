/*
 * Copyright 2024 HM Revenue & Customs
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
import models.journeymodels.{Journey, JourneyType}
import play.api.mvc._
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}

@Singleton
class FeedbackController @Inject() (
    actions:   Actions,
    mcc:       MessagesControllerComponents,
    appConfig: AppConfig
) extends FrontendController(mcc) {
  private val feedbackFrontendUrl: String = appConfig.Feedback.feedbackFrontendUrl

  def get: Action[AnyContent] = actions.journeyFinished { implicit request =>
    val journey: Journey = request.journey
    val feedbackId: String = journey.correlationId.value.toString

    val serviceName: String = journey.getJourneyType match {
      case JourneyType.Cheque       => "p800-refunds-cheque"
      case JourneyType.BankTransfer => "p800-refunds-bank-transfer"
    }

    Redirect(s"${feedbackFrontendUrl}/feedback/$serviceName")
      .withSession(("feedbackId", feedbackId))
  }
}
