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

package services

import models.journeymodels.{Journey, JourneyId, SessionId}
import play.api.mvc.Request
import repository.JourneyRepo
import requests.RequestSupport
import uk.gov.hmrc.http.HeaderCarrier
import requests.RequestSupport._
import util.JourneyLogger

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class JourneyService @Inject() (journeyRepo: JourneyRepo, journeyFactory: JourneyFactory)(implicit ec: ExecutionContext) {

  def newJourney()(implicit request: Request[_]): Future[Journey] = {
    for {
      sessionId <- RequestSupport.getSessionId()
      journey: Journey = journeyFactory.makeNewJourney(sessionId)
      _ <- journeyRepo.upsert(journey)
    } yield {
      JourneyLogger.info(s"Started new journey [journeyId:${journey.id.toString}]")
      journey
    }
  }

  def findLatestJourneyUsingSessionId(implicit request: Request[_]): Future[Option[Journey]] = {
    val sessionId: SessionId = implicitly[HeaderCarrier]
      .sessionId
      .map(x => SessionId(x.value))
      .getOrElse(throw new RuntimeException("Missing required 'SessionId'"))

    journeyRepo.findLatestJourney(sessionId)
  }

  def get(journeyId: JourneyId)(implicit request: Request[_]): Future[Journey] = find(journeyId).map { maybeJourney =>
    maybeJourney.getOrElse(throw new RuntimeException(s"Expected journey to be found ${request.path}"))
  }

  def upsert(journey: Journey)(implicit request: Request[_]): Future[Journey] = {
    JourneyLogger.debug("Upserting new journey")
    journeyRepo.upsert(journey).map(_ => journey)
  }

  private def find(journeyId: JourneyId): Future[Option[Journey]] = journeyRepo.findById(journeyId)
}
