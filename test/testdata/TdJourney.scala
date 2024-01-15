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

import models.journeymodels._

/**
 * Test Data (Td) Journey. It has journey examples in all possible states.
 */
trait TdJourney {
  dependencies: TdBase =>

  lazy val journeyId: JourneyId = JourneyId("64886ed616fe8b501cbf0088")

  lazy val journeyStarted: Journey = Journey(
    _id                          = journeyId,
    createdAt                    = dependencies.instant,
    hasFinished                  = false,
    journeyType                  = None,
    p800Reference                = None,
    nationalInsuranceNumber      = None,
    isChanging                   = false,
    dateOfBirth                  = None,
    identityVerificationResponse = None,
    bankDescription              = None
  )

  object BankTransfer {

    lazy val journeySelectedType: Journey = journeyStarted.copy(
      journeyType = Some(JourneyType.BankTransfer)
    )

    lazy val journeyEnteredP800Reference: Journey = journeySelectedType.copy(
      p800Reference = Some(dependencies.p800Reference)
    )

    lazy val journeyEnteredNino: Journey = journeyEnteredP800Reference.copy(
      nationalInsuranceNumber = Some(dependencies.nationalInsuranceNumber)
    )

    lazy val journeyEnteredDateOfBirth: Journey = journeyEnteredNino.copy(
      dateOfBirth = Some(dependencies.dateOfBirth)
    )

    lazy val journeyIdentityVerified: Journey = journeyEnteredDateOfBirth.copy(
      identityVerificationResponse = Some(dependencies.identityVerifiedResponse)
    )

    lazy val journeyIdentityNotVerified: Journey = {
      val j = journeyEnteredDateOfBirth.copy(
        identityVerificationResponse = Some(dependencies.identityNotVerifiedResponse)
      )
      require(!j.isIdentityVerified, "this journey instance has to have NOT verified identity")
      j
    }

    lazy val journeySelectedBank: Journey = journeyIdentityVerified.copy(
      bankDescription = Some(dependencies.bankDescription)
    )

    lazy val journeyPermissionGiven: Journey = {
      //TODO: here responsed from consent API
      journeySelectedBank
    }

    lazy val journeyReceivedNotificationFromEcospend: Journey =
      //TODO: API responses
      journeyPermissionGiven

    lazy val journeyClaimedOverpayment: Journey =
      //TODO: API responses
      journeyPermissionGiven

    lazy val journeyClaimOverpaymentFailed: Journey =
      //TODO: API responses
      journeyPermissionGiven

  }

  object Cheque {

    lazy val journeySelectedType: Journey = journeyStarted.copy(
      journeyType = Some(JourneyType.Cheque)
    )

    lazy val journeyEnteredP800Reference: Journey = journeySelectedType.copy(
      p800Reference = Some(dependencies.p800Reference)
    )

    lazy val journeyEnteredNino: Journey = journeyEnteredP800Reference.copy(
      nationalInsuranceNumber = Some(dependencies.nationalInsuranceNumber)
    )

    lazy val journeyIdentityVerified: Journey = journeyEnteredNino.copy(
      identityVerificationResponse = Some(dependencies.identityVerifiedResponse)
    )

    lazy val journeyIdentityNotVerified: Journey = {
      val j = journeyEnteredNino.copy(
        identityVerificationResponse = Some(dependencies.identityNotVerifiedResponse)
      )
      require(!j.isIdentityVerified, "this journey instance has to have NOT verified identity")
      j
    }

    lazy val journeyClaimedOverpayment: Journey =
      //TODO: API responses
      journeyIdentityVerified

    lazy val journeyClaimOverpaymentFailed: Journey =
      //TODO: API responses
      journeyIdentityVerified

  }

}
