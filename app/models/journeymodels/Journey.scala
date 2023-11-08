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

import models.P800Reference
import play.api.libs.json.OFormat

import java.time.{Clock, Instant}

sealed trait Journey {
  val _id: JourneyId
  val createdAt: Instant

  val lastUpdated: Instant = Instant.now(Clock.systemUTC())

  /* derived stuff: */
  def id: JourneyId = _id
  def journeyId: JourneyId = _id

  def name: String = {
    val className = getClass.getName
    val packageName = getClass.getPackage.getName
    className
      .replaceAll(s"$packageName.", "")
      .replaceAll("\\$", ".")
  }
}

object Journey {
  implicit val format: OFormat[Journey] = JourneyFormat.format
}

/**
 * [[Journey]], when finishing processing /start endpoint.
 * It's the initial state of the journey.
 */
final case class JourneyStarted(
    override val _id:       JourneyId,
    override val createdAt: Instant
) extends Journey

/**
 * [[Journey]] when finishing submission on DoYouWantToSignIn page,
 * when selected Yes.
 */
final case class JourneyDoYouWantToSignInYes(
    override val _id:       JourneyId,
    override val createdAt: Instant
) extends Journey

/**
 * [[Journey]] when finishing submission on DoYouWantToSignIn page,
 * when selected No.
 * It's the final state of the journey.
 */
final case class JourneyDoYouWantToSignInNo(
    override val _id:       JourneyId,
    override val createdAt: Instant
) extends Journey

/**
 * [[Journey]] when finishing submission on WhatIsYourP800Reference page.
 */
final case class JourneyWhatIsYourP800Reference(
    override val _id:       JourneyId,
    override val createdAt: Instant,
    p800Reference:          P800Reference
) extends Journey

/**
 * [[Journey]] when finishing submission on CheckYourReference page,
 * when the validation of the [[P800Reference]] succeeded
 */
final case class JourneyCheckYourReferenceValid(
    override val _id:       JourneyId,
    override val createdAt: Instant,
    p800Reference:          P800Reference
) extends Journey

/**
 * [[Journey]] when finishing submission on CheckYourReference page,
 * when the validation of the [[P800Reference]] failed
 */
final case class JourneyCheckYourReferenceInvalid(
    override val _id:       JourneyId,
    override val createdAt: Instant,
    p800Reference:          P800Reference
) extends Journey

/**
 * [[Journey]] when finishing submission on DoYouWantYourRefundViaBankTransfer page,
 * when selected Yes
 */
final case class JourneyDoYouWantYourRefundViaBankTransferYes(
    override val _id:       JourneyId,
    override val createdAt: Instant,
    p800Reference:          P800Reference
) extends Journey

/**
 * [[Journey]] when finishing submission on DoYouWantYourRefundViaBankTransfer page,
 * when selected Yes
 */
final case class JourneyDoYouWantYourRefundViaBankTransferNo(
    override val _id:       JourneyId,
    override val createdAt: Instant,
    p800Reference:          P800Reference
) extends Journey

/**
 * [[Journey]] when finishing submission on YourChequeWillBePostedToYou page.
 * It's the final state of the journey.
 */
final case class JourneyYourChequeWillBePostedToYou(
    override val _id:       JourneyId,
    override val createdAt: Instant,
    p800Reference:          P800Reference
) extends Journey
