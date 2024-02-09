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

import models.journeymodels.HasFinished.hasFinished
import models.journeymodels._
import nps.models.ReferenceCheckResult

/**
 * Test Data (Td) Journey. It has journey examples in all possible states.
 */
trait TdJourney {
  dependencies: TdBase =>

  //  implicit def toJourney(internal: JourneyInternal): Journey = new Journey(internal)
  //  implicit def toJourneyInternal(journey: Journey): JourneyInternal = journey.internal

  lazy val journeyId: JourneyId = JourneyId("64886ed616fe8b501cbf0088")

  lazy val journeyStarted: JourneyInternal = JourneyInternal(
    _id                  = journeyId,
    createdAt            = dependencies.instant,
    hasFinished          = HasFinished.No,
    journeyType          = None,
    p800Reference        = None,
    nino                 = None,
    isChanging           = false,
    dateOfBirth          = None,
    referenceCheckResult = None,
    bankDescription      = None,
    bankConsentResponse  = None
  )

  object BankTransfer {

    lazy val journeySelectedType: JourneyInternal = journeyStarted.copy(
      journeyType = Some(JourneyType.BankTransfer)
    )

    lazy val journeyEnteredP800Reference: JourneyInternal = journeySelectedType.copy(
      p800Reference = Some(dependencies.p800Reference)
    )

    lazy val journeyEnteredNino: JourneyInternal = journeyEnteredP800Reference.copy(
      nino = Some(dependencies.nino)
    )

    lazy val journeyEnteredDateOfBirth: JourneyInternal = journeyEnteredNino.copy(
      dateOfBirth = Some(dependencies.dateOfBirth)
    )

    object AfterReferenceCheck {

      lazy val journeyReferenceChecked: JourneyInternal = journeyEnteredDateOfBirth.copy(
        referenceCheckResult = Some(dependencies.p800ReferenceChecked)
      )

      lazy val journeyReferenceDidntMatchNino: JourneyInternal = {
        val j = journeyEnteredDateOfBirth.copy(
          referenceCheckResult = Some(ReferenceCheckResult.ReferenceDidntMatchNino)
        )
        require(!new Journey(j).isIdentityVerified, "this journey instance has to have NOT verified identity")
        j
      }

      lazy val journeyRefundAlreadyTaken: JourneyInternal = {
        val j = journeyEnteredDateOfBirth.copy(
          referenceCheckResult = Some(ReferenceCheckResult.RefundAlreadyTaken)
        )
        require(!new Journey(j).isIdentityVerified, "this journey instance has to have NOT verified identity")
        j
      }
    }

    lazy val journeyLockedOutFromFailedAttempts: JourneyInternal = {
      val j = AfterReferenceCheck.journeyReferenceDidntMatchNino.copy(
        hasFinished = HasFinished.LockedOut
      )
      require(!new Journey(j).isIdentityVerified, "this journey instance has to have NOT verified identity")
      require(hasFinished(j.hasFinished), "this journey instance should be in finished state")
      j
    }

    lazy val journeySelectedBank: JourneyInternal = AfterReferenceCheck.journeyReferenceChecked.copy(
      bankDescription = Some(dependencies.bankDescription)
    )

    lazy val journeyPermissionGiven: JourneyInternal = journeySelectedBank.copy(
      bankConsentResponse = Some(dependencies.bankConsentResponse)
    )

    lazy val journeyReceivedNotificationFromEcospend: JourneyInternal =
      //TODO: API responses
      journeyPermissionGiven

    lazy val journeyClaimedOverpayment: JourneyInternal =
      //TODO: API responses
      journeyReceivedNotificationFromEcospend.copy(
        hasFinished = HasFinished.YesSucceeded
      )

    lazy val journeyClaimOverpaymentFailed: JourneyInternal =
      //TODO: API responses
      journeyReceivedNotificationFromEcospend.copy(
        hasFinished = HasFinished.RefundNotSubmitted
      )

    /**
     * When user was on ClaimOverpaymentFailed but clicked "Choose another way" button
     * (journey no more final)
     */
    lazy val journeyClaimOverpaymentFailedButIsChoosingAnotherWay: JourneyInternal =
      //TODO: API responses
      journeyClaimOverpaymentFailed.copy(
        hasFinished = HasFinished.No
      )

  }

  object Cheque {

    lazy val journeySelectedType: JourneyInternal = journeyStarted.copy(
      journeyType = Some(JourneyType.Cheque)
    )

    lazy val journeyEnteredP800Reference: JourneyInternal = journeySelectedType.copy(
      p800Reference = Some(dependencies.p800Reference)
    )

    lazy val journeyEnteredNino: JourneyInternal = journeyEnteredP800Reference.copy(
      nino = Some(dependencies.nino)
    )

    object AfterReferenceCheck {

      lazy val journeyReferenceChecked: JourneyInternal = journeyEnteredNino.copy(
        referenceCheckResult = Some(dependencies.p800ReferenceChecked)
      )

      lazy val journeyReferenceDidntMatchNino: JourneyInternal = {
        val j = journeyEnteredNino.copy(
          referenceCheckResult = Some(ReferenceCheckResult.ReferenceDidntMatchNino)
        )
        require(!new Journey(j).isIdentityVerified, "this journey instance has to have NOT verified identity")
        j
      }

      lazy val journeyRefundAlreadyTaken: JourneyInternal = {
        val j = journeyEnteredNino.copy(
          referenceCheckResult = Some(ReferenceCheckResult.RefundAlreadyTaken)
        )
        require(!new Journey(j).isIdentityVerified, "this journey instance has to have NOT verified identity")
        j
      }
    }

    lazy val journeyLockedOutFromFailedAttempts: JourneyInternal = {
      val j = AfterReferenceCheck.journeyReferenceDidntMatchNino.copy(
        hasFinished = HasFinished.LockedOut
      )
      require(!new Journey(j).isIdentityVerified, "this journey instance has to have NOT verified identity")
      require(hasFinished(j.hasFinished), "this journey instance should be in finished state")
      j
    }

    lazy val journeyClaimedOverpayment: JourneyInternal =
      //TODO: API responses
      AfterReferenceCheck.journeyReferenceChecked.copy(
        hasFinished = HasFinished.YesSucceeded
      )
  }
}
