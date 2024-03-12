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

import models.Nino
import testdata.TdAll
import testsupport.UnitSpec

class JourneySpec extends UnitSpec {

  "changing dependent fields reset API responses" in {
    /*
     * This test operates on assumption that all API
     * responses are defined in journey after all other fields starting with 'referenceCheckResult'
     */

    /**
     * Example journey with all Api Responses defiend in it
     */
    val journey = TdAll.tdAll.BankTransfer.journeyReceivedNotificationFromEcospendValid

      /**
       * A heuristic to collect all API responses which should be defined after referenceCheckResult (inclusive)
       * @return Option[_] of api response along with name of the field
       */
      def collectApiResponses(journey: Journey): List[(Option[_], String)] =
        journey
          .productIterator
          .zip(journey.productElementNames)
          .toList
          .drop(journey.productElementNames.indexOf("referenceCheckResult")) //remove journey fields before first Api Result
          .map(x => (x._1.asInstanceOf[Option[_]], x._2))

    collectApiResponses(journey).foreach { case (apiResponse, name) => apiResponse shouldBe defined withClue name }
    val newJourney = journey.update(nino = Nino("AB111222C"))
    collectApiResponses(newJourney).foreach { case (apiResponse, name) => apiResponse shouldBe empty withClue name }
  }

}
