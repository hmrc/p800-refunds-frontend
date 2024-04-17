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
import play.api.Logging
import play.api.mvc._
import services.FailedVerificationAttemptService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import util.DateTimeFormatsUtil
import views.Views

import java.time.temporal.ChronoUnit.DAYS
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class YouCannotConfirmYourIdentityDetailsYetController @Inject() (
    mcc:                              MessagesControllerComponents,
    failedVerificationAttemptService: FailedVerificationAttemptService,
    views:                            Views,
    actions:                          Actions
)(implicit val ec: ExecutionContext) extends FrontendController(mcc) with Logging {

  def get: Action[AnyContent] = actions.default.async { implicit request: Request[_] =>

    failedVerificationAttemptService.find().map{ optAttemptInfo =>
      val attemptInfo = optAttemptInfo.getOrElse(throw new RuntimeException(s"[YouCannotConfirmYourIdentityDetailsYetController] Attempt info not found"))
      val formattedDate = DateTimeFormatsUtil.lockoutUnlockDateFormatter(attemptInfo.createdAt.plus(1, DAYS))
      Ok(views.youCannotConfirmYourIdentityDetailsYet(formattedDate))
    }
  }
}

