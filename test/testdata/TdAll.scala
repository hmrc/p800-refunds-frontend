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

package testdata

import models.journeymodels.JourneyId
import org.bson.types.ObjectId

object TdAll {

  /**
   * Create instance of Test Data with random journeyId.
   */
  def apply(jid: => JourneyId = JourneyId(ObjectId.get().toHexString)): TdAll = new TdAll {
    override lazy val journeyId: JourneyId = jid
  }

  val tdAll: TdAll = new TdAll {}
}

trait TdAll
  extends AnyRef
  with TdBase
  with TdRequest
  with TdJourney

