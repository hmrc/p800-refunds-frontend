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

package services

import models.journeymodels.{HasFinished, Journey}

import java.time.{Clock, Instant}
import javax.inject.{Inject, Singleton}

@Singleton
class JourneyFactory @Inject() (
    journeyIdGenerator: JourneyIdGenerator,
    clock:              Clock
) {

  def makeNewJourney(): Journey = Journey(
    _id                          = journeyIdGenerator.nextJourneyId(),
    createdAt                    = Instant.now(clock),
    hasFinished                  = HasFinished.No,
    journeyType                  = None,
    p800Reference                = None,
    nationalInsuranceNumber      = None,
    isChanging                   = false,
    dateOfBirth                  = None,
    identityVerificationResponse = None,
    bankDescription              = None
  )

}
