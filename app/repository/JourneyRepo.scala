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

package repository

import config.AppConfig
import models.journeymodels.{Journey, JourneyId}
import org.mongodb.scala.model.{IndexModel, IndexOptions, Indexes}
import repository.JourneyRepo._
import repository.Repo.{Id, IdExtractor}
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.Codecs

import java.util.concurrent.TimeUnit
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.FiniteDuration

@Singleton
final class JourneyRepo @Inject() (
    mongoComponent: MongoComponent,
    config:         AppConfig
)(implicit ec: ExecutionContext)
  extends Repo[JourneyId, Journey](
    collectionName = "journey",
    mongoComponent = mongoComponent,
    indexes        = JourneyRepo.indexes(config.journeyRepoTtl),
    extraCodecs    = Codecs.playFormatSumCodecs(Journey.format),
    replaceIndexes = true
  ) {

}

object JourneyRepo {

  implicit val journeyId: Id[JourneyId] = new Id[JourneyId] {
    override def value(i: JourneyId): String = i.value
  }

  implicit val journeyIdExtractor: IdExtractor[Journey, JourneyId] = new IdExtractor[Journey, JourneyId] {
    override def id(j: Journey): JourneyId = j.journeyId
  }

  def indexes(cacheTtl: FiniteDuration): Seq[IndexModel] = Seq(
    IndexModel(
      keys         = Indexes.ascending("lastUpdated"),
      indexOptions = IndexOptions().expireAfter(cacheTtl.toSeconds, TimeUnit.SECONDS).name("lastUpdatedIdx")
    )
  )

}

