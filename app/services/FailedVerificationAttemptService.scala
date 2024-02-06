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
import models.attemptmodels.{AttemptInfo, IpAddress}
import play.api.mvc.RequestHeader
import repository.FailedVerificationAttemptRepo

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FailedVerificationAttemptService @Inject() (failedVerificationAttemptRepo: FailedVerificationAttemptRepo)(implicit ec: ExecutionContext) {

  //TODO: encrypt/decrypt the ip address in service layer - OPS-11735
  //TODO: find in action when landing, so we can lock users out straight away - OPS-11736

  def find()(implicit requestHeader: RequestHeader): Future[Option[AttemptInfo]] =
    failedVerificationAttemptRepo.findByIpAddress(IpAddress(requestHeader.remoteAddress))

  def upsertRecordIfVerificationFails(implicit requestHeader: RequestHeader): Future[Option[AttemptInfo]] = {
    for {
      maybeAttemptInfo <- failedVerificationAttemptRepo.findByIpAddress(IpAddress(requestHeader.remoteAddress))
      newAttemptInfo = maybeAttemptInfo.fold(AttemptInfo.newAttemptInfo)(a => a.incrementAttemptNumberByOne)
      maybeAttemptInfoAfterUpsert <- failedVerificationAttemptRepo.upsert(newAttemptInfo).map(_ => Some(newAttemptInfo))
    } yield maybeAttemptInfoAfterUpsert
  }

  def drop(): Future[Unit] = failedVerificationAttemptRepo.drop()

  def findAll(): Future[Seq[AttemptInfo]] = failedVerificationAttemptRepo.collection.find().toFuture()

}
