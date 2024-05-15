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

import crypto.JourneyCrypto
import models.journeymodels.{Journey, JourneyId}
import play.api.mvc.Request
import repository.JourneyRepo
import util.JourneyLogger

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class JourneyService @Inject() (
    journeyRepo:    JourneyRepo,
    journeyFactory: JourneyFactory,
    journeyCrypto:  JourneyCrypto
)(implicit ec: ExecutionContext) {

  def newJourney()(implicit request: Request[_]): Future[Journey] = {
    val journey: Journey = journeyFactory.makeNewJourney()
    upsert(journey)
      .map{ _ =>
        JourneyLogger.info(s"Started new journey [journeyId:${journey.id.value}]")
        journey
      }
  }

  def get(journeyId: JourneyId)(implicit request: Request[_]): Future[Journey] = find(journeyId).map { maybeJourney =>
    maybeJourney
      .getOrElse(throw new RuntimeException(s"Expected journey to be found ${request.path} [journeyId:${journeyId.value}]"))
  }

  def upsert[J <: Journey](journey: J)(implicit request: Request[_]): Future[J] = {
    JourneyLogger.info(s"Upserting new journey [${journey.journeyType.toString}] [${journey.journeyId.toString}]")
    val encrypted = journeyCrypto.encryptJourney(journey)
    journeyRepo.upsert(encrypted).map(_ => journey)
  }

  def find(journeyId: JourneyId): Future[Option[Journey]] =
    journeyRepo
      .findById(journeyId)
      .map(_.map(journeyCrypto.decryptJourney))

  def remove(journeyId: JourneyId): Future[Option[Journey]] =
    journeyRepo
      .removeById(journeyId)
}
