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

import models.journeymodels.Journey
import play.api.Logger
import play.api.mvc.{ActionRefiner, Request, Result, Results}
import requests.RequestSupport._
import services.JourneyService

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GetJourneyActionRefiner @Inject() (journeyService: JourneyService)(
    implicit
    ec: ExecutionContext
) extends ActionRefiner[Request, JourneyRequest] {

  private val logger = Logger(getClass)

  override protected def refine[A](request: Request[A]): Future[Either[Result, JourneyRequest[A]]] = {
    implicit val r: Request[A] = request

    for {
      maybeJourney: Option[Journey] <- journeyService.findLatestJourneyUsingSessionId
    } yield maybeJourney match {
      case Some(journey) => Right(new JourneyRequest(journey, request))
      case None =>
        logger.error(s"No journey found for sessionId: [ ${hc.sessionId.toString} ]")
        Left(Results.Redirect(controllers.routes.JourneyController.start))
    }
  }

  override protected def executionContext: ExecutionContext = ec

}
