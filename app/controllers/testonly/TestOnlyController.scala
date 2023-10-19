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

package controllers.testonly

import action.Actions
import controllers.JourneyController
import models.journeymodels.{Journey, JourneyId}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.JourneyService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.TestOnlyViews

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TestOnlyController @Inject() (
    mcc:            MessagesControllerComponents,
    testOnlyViews:  TestOnlyViews,
    journeyService: JourneyService,
    as:             Actions
)(implicit ec: ExecutionContext) extends FrontendController(mcc) {

  val getTestOnlyLanding: Action[AnyContent] = as.default { implicit request =>
    Ok(testOnlyViews.testOnlyStartPage())
  }

  val showJourney: Action[AnyContent] = as.default.async { implicit request =>
    request.session.get(JourneyController.journeyIdKey).map(JourneyId.apply) match {
      case None            => Future.successful(Ok(s"No ${JourneyController.journeyIdKey} in play session"))
      case Some(journeyId) => showJourney(journeyId)
    }
  }

  def showJourneyById(journeyId: JourneyId): Action[AnyContent] = as.default.async { _ =>
    showJourney(journeyId)
  }

  private def showJourney(journeyId: JourneyId): Future[Result] = {
    for {
      maybeJourney: Option[Journey] <- journeyService.find(journeyId)
    } yield Ok(
      maybeJourney
        .map(journey => Json.prettyPrint(Json.toJson(journey)))
        .getOrElse(s"No Journey in mongo with journeyId: [${journeyId.value}]")
    )
  }
}
