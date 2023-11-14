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
import models.journeymodels.{JAfterCheckYourReferenceValid, JBeforeYourChequeWillBePostedToYou, JourneyYourChequeWillBePostedToYou}
import models.{AmountInPence, P800Reference}
import play.api.mvc._
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.Views

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class RequestReceivedController @Inject() (
    mcc:     MessagesControllerComponents,
    views:   Views,
    actions: Actions
) extends FrontendController(mcc) {

  val get: Action[AnyContent] = actions.journeyAction.async { implicit request =>

    request.journey match {
      case j: JBeforeYourChequeWillBePostedToYou => JourneyRouter.sendToCorrespondingPageF(j)
      case _: JourneyYourChequeWillBePostedToYou =>
        //TODO get the p800 reference (when implementing API call, populate amount into the journey so it is available here)
        getResult
      case j: JAfterCheckYourReferenceValid => JourneyRouter.sendToCorrespondingPageF(j)
    }
  }

  private def getResult(implicit request: Request[_]) = Future.successful {
    val (dummyP800Ref, refundAmountInPence) = P800Reference("P800REFNO1") -> AmountInPence(231.60)
    Ok(views.requestReceivedPage(dummyP800Ref, refundAmountInPence))
  }
}
