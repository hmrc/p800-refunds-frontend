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
    case No                    => false
    case YesSucceeded          => true
    case YesRefundNotSubmitted => true
    case YesLockedOut          => true
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
   * Claiming refund failed
   */
  case object YesRefundNotSubmitted extends HasFinished

  /**
   * User entered too many times incorrect data. He was locked out.
   */
  case object YesLockedOut extends HasFinished

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val format: OFormat[HasFinished] = derived.oformat()
}

sealed trait HasFinished
