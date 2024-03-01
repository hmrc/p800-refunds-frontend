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

package services

import com.google.inject.{Inject, Singleton}
import config.AppConfig
import models.attemptmodels.{AttemptInfo, TrueClientIp}
import play.api.mvc.RequestHeader
import repository.FailedVerificationAttemptRepo

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FailedVerificationAttemptService @Inject() (
    failedVerificationAttemptRepo: FailedVerificationAttemptRepo,
    appConfig:                     AppConfig
)(implicit ec: ExecutionContext) {

  //TODO: encrypt/decrypt the ip address in service layer - OPS-11735
  //TODO: find in action when landing, so we can lock users out straight away - OPS-11736

  def find()(implicit requestHeader: RequestHeader): Future[Option[AttemptInfo]] =
    failedVerificationAttemptRepo.findByTrueClientIp(TrueClientIp.trueClientIp())

  def shouldBeLockedOut()(implicit requestHeader: RequestHeader): Future[Boolean] = {
    failedVerificationAttemptRepo
      .findByTrueClientIp(TrueClientIp.trueClientIp())
      .map{ _.map(AttemptInfo.shouldBeLockedOut(_, appConfig.FailedAttemptRepo.failedAttemptRepoMaxAttempts)) }
      .map(_.getOrElse(false))
  }

  /**
   * Returns if user should be locked out after update.
   */
  def updateNumberOfFailedAttempts()(implicit requestHeader: RequestHeader): Future[Boolean] = {
    for {
      maybeAttemptInfo <- failedVerificationAttemptRepo.findByTrueClientIp(TrueClientIp.trueClientIp())
      newAttemptInfo: AttemptInfo = maybeAttemptInfo.fold(AttemptInfo.newAttemptInfo)(a => a.incrementAttemptNumberByOne)
      attemptInfoAfterUpsert: AttemptInfo <- failedVerificationAttemptRepo.upsert(newAttemptInfo).map(_ => newAttemptInfo)
    } yield AttemptInfo.shouldBeLockedOut(attemptInfoAfterUpsert, appConfig.FailedAttemptRepo.failedAttemptRepoMaxAttempts)
  }

  def drop(): Future[Unit] = failedVerificationAttemptRepo.drop()

  def findAll(): Future[Seq[AttemptInfo]] = failedVerificationAttemptRepo.collection.find().toFuture()

}
