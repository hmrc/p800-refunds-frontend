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

package models.attemptmodels

import play.api.libs.json.{Format, Json, OFormat, OWrites}
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats

import java.time.{Clock, Instant}

final case class AttemptInfo(_id: AttemptId, createdAt: Instant, ipAddress: IpAddress, numberOfFailedAttempts: NumberOfAttempts) {
  val lastUpdated: Instant = Instant.now(Clock.systemUTC())

  def incrementAttemptNumberByOne: AttemptInfo = this.copy(numberOfFailedAttempts = numberOfFailedAttempts.plusOne)
}

object AttemptInfo {

  def newAttemptInfo(ipAddress: IpAddress): AttemptInfo = AttemptInfo(
    _id                    = AttemptId.newRandomAttemptId,
    createdAt              = Instant.now(Clock.systemUTC()),
    ipAddress              = ipAddress,
    numberOfFailedAttempts = NumberOfAttempts(1)
  )

  def shouldBeLockedOut(attemptInfo: AttemptInfo, maxFailedAttempts: Int): Boolean =
    attemptInfo.numberOfFailedAttempts.value >= maxFailedAttempts

  implicit val instantFormat: Format[Instant] = MongoJavatimeFormats.instantFormat

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val format: OFormat[AttemptInfo] = {
    val defaultFormat: OFormat[AttemptInfo] = Json.format[AttemptInfo]
    //we need to write some extra fields on the top of the structure so it's
    //possible to index on them and use them in queries
    val customWrites = OWrites[AttemptInfo](attemptInfo =>
      defaultFormat.writes(attemptInfo) ++ Json.obj(
        "createdAt" -> MongoJavatimeFormats.instantFormat.writes(attemptInfo.createdAt), //todo check if this line is needed
        "lastUpdated" -> MongoJavatimeFormats.instantFormat.writes(attemptInfo.lastUpdated)
      ))
    OFormat(
      defaultFormat,
      customWrites
    )
  }
}
