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

import play.api.Logger
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

/**
 * Controller to contain actions responsible for journey
 */
@Singleton
class JourneyController @Inject() (
    mcc: MessagesControllerComponents
) extends FrontendController(mcc) {

  //todo remove this and utilise journey logger perhaps, once we have journey up and running.
  val logger: Logger = Logger("journeyLogger")

  val start: Action[AnyContent] = Action.async { _ =>
    //todo remove this and utilise journey logger perhaps, once we have journey up and running.
    logger.info("Start endpoint called journey starting")
    //todo start an actual journey at this point
    Future.successful(Redirect(routes.FrontendActionsController.getDoYouWantToSignIn))
  }
}
