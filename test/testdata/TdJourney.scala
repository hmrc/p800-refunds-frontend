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

import models.P800ReferenceValidation
import models.journeymodels.{JourneyDoYouWantToSignInNo, JourneyId, JourneyStarted, JourneyWhatIsYourP800Reference}

/**
 * Test Data (Td) Journey. It has journey examples in all possible states.
 */
trait TdJourney { dependencies: TdBase =>

  lazy val journeyId: JourneyId = JourneyId("64886ed616fe8b501cbf0088")

  lazy val journeyStarted: JourneyStarted = JourneyStarted(
    _id       = journeyId,
    createdAt = dependencies.instant
  )

  lazy val journeyDoYouWantToSignInNo: JourneyDoYouWantToSignInNo = JourneyDoYouWantToSignInNo(
    _id       = journeyId,
    createdAt = dependencies.instant
  )

  lazy val journeyWhatIsYourP800Reference: JourneyWhatIsYourP800Reference = JourneyWhatIsYourP800Reference(
    _id                     = journeyId,
    createdAt               = dependencies.instant,
    p800Reference           = dependencies.p800Reference,
    p800ReferenceValidation = P800ReferenceValidation.NotValidatedYet
  )

  lazy val journeyWhatIsYourP800InvalidReference: JourneyWhatIsYourP800Reference = JourneyWhatIsYourP800Reference(
    _id                     = journeyId,
    createdAt               = dependencies.instant,
    p800Reference           = dependencies.p800Reference,
    p800ReferenceValidation = P800ReferenceValidation.Invalid
  )

}
