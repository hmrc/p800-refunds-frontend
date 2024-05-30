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

package repository

import config.AppConfig
import models.attemptmodels.{AttemptId, AttemptInfo, IpAddress}
import org.mongodb.scala.model._
import repository.FailedVerificationAttemptRepo.{attemptId, attemptIdExtractor}
import repository.Repo.{Id, IdExtractor}
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.Codecs

import java.util.concurrent.TimeUnit
import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{ExecutionContext, Future}

@Singleton
final class FailedVerificationAttemptRepo @Inject() (
    mongoComponent: MongoComponent,
    config:         AppConfig
)(implicit ec: ExecutionContext)
  extends Repo[AttemptId, AttemptInfo](
    collectionName = "failed-verification-attempts",
    mongoComponent = mongoComponent,
    indexes        = FailedVerificationAttemptRepo.indexes(config.FailedAttemptRepo.failedAttemptRepoTtl),
    extraCodecs    = Seq(Codecs.playFormatCodec(AttemptInfo.format)),
    replaceIndexes = true
  ) {

  def findByIpAddress(ipAddress: IpAddress): Future[Option[AttemptInfo]] =
    collection
      .find(filter = Filters.eq("ipAddress", ipAddress.value))
      .headOption()

  def drop(): Future[Unit] =
    collection
      .drop()
      .toFuture()
      .map(_ => ())

}

object FailedVerificationAttemptRepo {

  implicit val attemptId: Id[AttemptId] = new Id[AttemptId] {
    override def value(i: AttemptId): String = i.value.toString
  }

  implicit val attemptIdExtractor: IdExtractor[AttemptInfo, AttemptId] = new IdExtractor[AttemptInfo, AttemptId] {
    override def id(j: AttemptInfo): AttemptId = j._id
  }

  def indexes(cacheTtl: FiniteDuration): Seq[IndexModel] = Seq(
    IndexModel(
      keys         = Indexes.ascending("lastUpdated"),
      indexOptions = IndexOptions().expireAfter(cacheTtl.toSeconds, TimeUnit.SECONDS).name("lastUpdatedIdx")
    ),
    IndexModel(
      keys         = Indexes.ascending("ipAddress"),
      indexOptions = IndexOptions().name("ipAddressIdx").unique(true)
    )
  )

}

