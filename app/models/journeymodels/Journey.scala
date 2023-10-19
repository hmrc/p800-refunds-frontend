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

package models.journeymodels

import models.TraceId
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats
import julienrf.json.derived
import models.stagemodels.ChosenToSignIn
import play.api.libs.json.{Json, OFormat, OWrites}
import util.Errors

import java.time.{Clock, Instant}

sealed trait Journey {
  def _id: JourneyId

  def createdOn: Instant

  val lastUpdated: Instant = Instant.now(Clock.systemUTC())

  def sessionId: SessionId

  def stage: Stage

  /* derived stuff: */
  def id: JourneyId = _id

  def journeyId: JourneyId = _id

  def name: String = {
    val className = getClass.getName
    val packageName = getClass.getPackage.getName
    className
      .replaceAll(s"\\$packageName.", "")
      .replaceAll("\\$", ".")
  }

  //todo will we use this, if not, delete
  val traceId: TraceId = TraceId(journeyId)

}

object Journey {
  implicit def format: OFormat[Journey] = {

    @SuppressWarnings(Array("org.wartremover.warts.Any"))
    val defaultFormat: OFormat[Journey] = derived.oformat[Journey]()

    //we need to write some extra fields on the top of the structure so it's
    //possible to index on them and use them in queries
    val customWrites = OWrites[Journey](j =>
      defaultFormat.writes(j) ++ Json.obj(
        "sessionId" -> j.sessionId,
        "createdAt" -> MongoJavatimeFormats.instantFormat.writes(j.createdOn),
        "lastUpdated" -> MongoJavatimeFormats.instantFormat.writes(j.lastUpdated)
      ))
    OFormat(
      defaultFormat,
      customWrites
    )
  }

  /**
   * Journey extractors extracting journeys in particular stage.
   * They correspond to actual [[Stage]] values
   */
  object Stages {

    sealed trait BeforeChosenToSignIn extends Journey with Stages.JourneyStage

    sealed trait AfterChosenToSignIn extends Journey {
      def chosenToSignIn: ChosenToSignIn
    }

    /**
     * Marking trait for selecting journey in stage
     */
    sealed trait JourneyStage extends Journey {
      def stage: Stage
    }

    private val sanityMessage = "Sanity check just in case if you messed journey traits up"

    sealed trait Started
      extends Journey
      with JourneyStage
      with BeforeChosenToSignIn {

      def stage: Stage.AfterStarted

      Errors.sanityCheck(Stage.AfterStarted.values.contains(stage), sanityMessage)
    }

  }

  object JourneyStages {
    final case class Started(
        override val _id:       JourneyId,
        override val createdOn: Instant,
        override val sessionId: SessionId,
        override val stage:     Stage.AfterStarted
    )
      extends Journey
      with Journey.Stages.Started
  }

}
