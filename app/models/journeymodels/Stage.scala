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

import enumeratum._
import julienrf.json.derived
import play.api.libs.json.OFormat

import scala.collection.immutable

/**
 * Journey Stage
 * It defines how journey propagates through stages.
 * Each stage defines what data are available in journey at this stage.
 * Each enum value defines what states journey can be in within this stage.
 */
sealed trait Stage extends Product with Serializable

object Stage {

  sealed trait AfterStarted extends Stage with EnumEntry

  object AfterStarted extends Enum[AfterStarted] {
    @SuppressWarnings(Array("org.wartremover.warts.Any"))
    implicit val format: OFormat[AfterStarted] = derived.oformat[AfterStarted]()
    val values: immutable.IndexedSeq[AfterStarted] = findValues

    /**
     * Journey has been just started.
     * It's new bare bone journey,
     */
    case object Started extends AfterStarted
  }

}
