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

package action

import config.AppConfig
import models.journeymodels.JourneyId
import play.api.mvc.Results.Redirect
import play.api.mvc.{ActionRefiner, Request, Result}
import services.JourneyService
import util.JourneyLogger

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GetJourneyActionRefiner @Inject() (
    journeyService: JourneyService,
    appConfig:      AppConfig
)(implicit ec: ExecutionContext) extends ActionRefiner[Request, JourneyRequest] {

  override protected def refine[A](request: Request[A]): Future[Either[Result, JourneyRequest[A]]] = {
    implicit val r: Request[A] = request
    val redirectToStart: Either[Result, JourneyRequest[A]] = Left(Redirect(appConfig.govUkRouteIn))

    request.session.get(JourneyIdKey.journeyIdKey).map(JourneyId.apply) match {
      case Some(journeyId) =>
        for {
          maybeJourney <- journeyService.find(journeyId)
        } yield {
          maybeJourney match {
            case Some(journey) => Right(new JourneyRequest(journey, request))
            case None =>
              JourneyLogger.error(s"Journey not found based on the journeyId from session, redirecting to the start page [journeyIdFromSession:${journeyId.value}]")
              redirectToStart
          }
        }
      case None =>
        JourneyLogger.error(s"There was missing journeyId in the play session. Redirecting to the start page")
        Future.successful(redirectToStart)
    }
  }

  override protected def executionContext: ExecutionContext = ec
}
