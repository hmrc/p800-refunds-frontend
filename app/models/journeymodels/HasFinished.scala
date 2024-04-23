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

package models.journeymodels

import julienrf.json.derived
import play.api.libs.json.OFormat

object HasFinished {

  def hasFinished(hasFinished: HasFinished): Boolean = hasFinished match {
    case No                      => false
    case YesSucceeded            => true
    case YesSentToCaseManagement => true
    case YesRefundNotSubmitted   => true
    case YesLockedOut            => true
    case YesRefundAlreadyTaken   => true
  }

  def isInProgress(hasFinished: HasFinished): Boolean = !HasFinished.hasFinished(hasFinished)

  /**
   * In other words journey in progress
   */
  case object No extends HasFinished

  /**
   * Journey has finished in successful way. The refund was claimed or standing order was requested
   */
  case object YesSucceeded extends HasFinished

  /**
   * Journey has finished in successful way. The refund has been sent to case management for manual review.
   */
  case object YesSentToCaseManagement extends HasFinished

  /**
   * Claiming refund failed
   */
  case object YesRefundNotSubmitted extends HasFinished

  /**
   * User entered too many times incorrect data. They are locked out.
   */
  case object YesLockedOut extends HasFinished

  /**
   * Response from p800 refund reference check api was a 422 with code indicating refund already claimed. They are locked out.
   */
  case object YesRefundAlreadyTaken extends HasFinished

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val format: OFormat[HasFinished] = derived.oformat()
}

sealed trait HasFinished
