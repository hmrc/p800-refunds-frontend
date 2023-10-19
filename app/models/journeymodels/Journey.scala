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

import models.{HowManyAttempts, IsThisCorrect, P800Reference, RefundViaBankTransfer}
import play.api.libs.json.OFormat

import java.time.{Clock, Instant}

sealed trait Journey {
  val _id: JourneyId
  val createdAt: Instant

  val lastUpdated: Instant = Instant.now(Clock.systemUTC())

  val state: State
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
}

object Journey {
  implicit val format: OFormat[Journey] = JourneyFormat.format
}

final case class JourneyStarted(
    override val _id:       JourneyId,
    override val createdAt: Instant
) extends Journey {
  override val state: State.Started.type = State.Started
}

final case class JourneyDoYouWantToSignInYes(
    override val _id:       JourneyId,
    override val createdAt: Instant,
    override val state:     State.JourneyDoYouWantToSignInYes.type = State.JourneyDoYouWantToSignInYes
) extends Journey

final case class JourneyWhatIsYourP800Reference(
    override val _id:       JourneyId,
    override val createdAt: Instant,
    override val state:     State.WhatIsYourP800ReferenceState,
    p800Reference:          P800Reference,
    howManyAttempts:        HowManyAttempts //initially 0
) extends Journey

final case class JourneyDoYouWantYourRefundViaBankTransfer(
    override val _id:       JourneyId,
    override val createdAt: Instant,
    override val state:     State.DoYouWantYourRefundViaBankTransfer.type = State.DoYouWantYourRefundViaBankTransfer,
    p800Reference:          P800Reference,
    isThisCorrect:          IsThisCorrect,
    howManyAttempts:        HowManyAttempts,
    refundViaBankTransfer:  RefundViaBankTransfer
) extends Journey
