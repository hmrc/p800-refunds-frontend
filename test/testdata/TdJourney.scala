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
import models.namematching.NameMatchingResult
import nps.models.ValidateReferenceResult

/**
 * Test Data (Td) Journey. It has journey examples in all possible states.
 */
trait TdJourney {
  dependencies: TdBase with TdEdh =>

  lazy val journeyId: JourneyId = JourneyId("64886ed616fe8b501cbf0088")

  lazy val journeyStarted: Journey = Journey(
    _id                           = journeyId,
    createdAt                     = dependencies.instant,
    correlationId                 = dependencies.correlationId,
    hasFinished                   = HasFinished.No,
    journeyType                   = None,
    p800Reference                 = None,
    nino                          = None,
    isChanging                    = IsChanging.No,
    dateOfBirth                   = None,
    nameMatchingResult            = None,
    referenceCheckResult          = None,
    traceIndividualResponse       = None,
    bankDescription               = None,
    bankConsentResponse           = None,
    bankAccountSummary            = None,
    isValidEventValue             = None,
    bankDetailsRiskResultResponse = None
  )

  object BankTransfer {

    lazy val journeySelectedType: Journey = journeyStarted.copy(
      journeyType = Some(JourneyType.BankTransfer)
    )

    lazy val journeyEnteredP800Reference: Journey = journeySelectedType.copy(
      p800Reference = Some(dependencies.userEnteredP800Reference)
    )

    lazy val journeyEnteredNino: Journey = journeyEnteredP800Reference.copy(
      nino = Some(dependencies.nino)
    )

    lazy val journeyEnteredDateOfBirth: Journey = journeyEnteredNino.copy(
      dateOfBirth = Some(dependencies.dateOfBirth)
    )

    object AfterReferenceCheck {

      lazy val journeyReferenceChecked: Journey = journeyEnteredDateOfBirth.copy(
        referenceCheckResult = Some(dependencies.p800ReferenceChecked)
      )

      lazy val journeyReferenceDidntMatchNino: Journey = {
        val j = journeyEnteredDateOfBirth.copy(
          referenceCheckResult = Some(ValidateReferenceResult.ReferenceDidntMatchNino)
        )
        require(!j.isIdentityVerified, "this journey instance has to have NOT verified identity")
        j
      }

      /**
       * When response from Nps indicates that refund has already been claimed
       */
      lazy val journeyRefundAlreadyTaken: Journey = {
        val j = journeyEnteredDateOfBirth.copy(
          referenceCheckResult = Some(ValidateReferenceResult.RefundAlreadyTaken),
          hasFinished          = HasFinished.YesRefundAlreadyTaken
        )
        require(!j.isIdentityVerified, "this journey instance has to have NOT verified identity")
        j
      }

      /**
       * When response from Nps indicates that refund is no longer available
       */
      lazy val journeyRefundNotAvailable: Journey = {
        val j = journeyEnteredDateOfBirth.copy(
          referenceCheckResult = Some(ValidateReferenceResult.RefundNoLongerAvailable),
          hasFinished          = HasFinished.YesRefundNoLongerAvailable
        )
        require(!j.isIdentityVerified, "this journey instance has to have NOT verified identity")
        j
      }
    }

    lazy val journeyLockedOutFromFailedAttempts: Journey = {
      val j = AfterReferenceCheck.journeyReferenceDidntMatchNino.copy(
        hasFinished = HasFinished.YesLockedOut
      )
      require(!j.isIdentityVerified, "this journey instance has to have NOT verified identity")
      require(hasFinished(j.hasFinished), "this journey instance should be in finished state")
      j
    }

    lazy val journeyAfterTracedIndividual: Journey = AfterReferenceCheck.journeyReferenceChecked.copy(
      traceIndividualResponse = Some(dependencies.traceIndividualResponse)
    )

    lazy val journeySelectedBank: Journey = journeyAfterTracedIndividual.copy(
      bankDescription = Some(dependencies.bankDescription)
    )

    lazy val journeySelectedBankWithSuccessfulMatch: Journey = journeyAfterTracedIndividual.copy(
      bankDescription         = Some(dependencies.bankDescription),
      traceIndividualResponse = Some(dependencies.traceIndividualResponseSuccessFulNameMatch)
    )

    lazy val journeyBankConsent: Journey = journeySelectedBank.copy(
      bankConsentResponse = Some(dependencies.bankConsent)
    )

    lazy val journeyBankAccountConsentSuccessfulNameMatch: Journey = journeySelectedBank.copy(
      bankConsentResponse     = Some(dependencies.bankConsent),
      traceIndividualResponse = Some(dependencies.traceIndividualResponseSuccessFulNameMatch)
    )

    lazy val journeyReceivedNotificationFromEcospendNotReceived: Journey =
      journeyBankConsent.copy(
        isValidEventValue = Some(dependencies.isValidEventValueNotReceived),
        //Even if the notification was not received,
        // in the same controller call
        // other APIs were made
        // and their responses were stored in the journey:
        bankDetailsRiskResultResponse = Some(dependencies.getBankDetailsRiskResultResponse),
        bankAccountSummary            = Some(dependencies.bankAccountSummary)
      )

    lazy val journeyReceivedNotificationFromEcospendNotReceivedSuccessfulNameMatch: Journey =
      journeyBankAccountConsentSuccessfulNameMatch.copy(
        isValidEventValue = Some(dependencies.isValidEventValueNotReceived),
        //Even if the notification was not received,
        // in the same controller call
        // other APIs were made
        // and their responses were stored in the journey:
        bankDetailsRiskResultResponse = Some(dependencies.getBankDetailsRiskResultResponse),
        bankAccountSummary            = Some(dependencies.bankAccountSummary),
        nameMatchingResult            = Some(NameMatchingResult(true))
      )

    lazy val journeyReceivedNotificationFromEcospendValid: Journey =
      journeyReceivedNotificationFromEcospendNotReceived.copy(
        isValidEventValue = Some(dependencies.isValidEventValueValid)
      )

    lazy val journeyReceivedNotificationFromEcospendNotValid: Journey =
      journeyReceivedNotificationFromEcospendNotReceived.copy(
        isValidEventValue = Some(dependencies.isValidEventValueNotValid)
      )

    lazy val journeyClaimedOverpayment: Journey =
      journeyReceivedNotificationFromEcospendValid.copy(
        hasFinished = HasFinished.YesSucceeded
      )

    lazy val journeyClaimOverpaymentFailed: Journey =
      journeyReceivedNotificationFromEcospendValid.copy(
        bankDetailsRiskResultResponse = Some(dependencies.getBankDetailsRiskResultResponseDoNotPay),
        hasFinished                   = HasFinished.YesRefundNotSubmitted
      )

    /**
     * When user was on ClaimOverpaymentFailed but clicked "Choose another way" button
     * (journey no more final)
     */
    lazy val journeyClaimOverpaymentFailedButIsChoosingAnotherWay: Journey =
      //TODO: API responses
      journeyClaimOverpaymentFailed.copy(
        hasFinished = HasFinished.No
      )

  }

  object Cheque {

    lazy val journeySelectedType: Journey = journeyStarted.copy(
      journeyType = Some(JourneyType.Cheque)
    )

    lazy val journeyEnteredP800Reference: Journey = journeySelectedType.copy(
      p800Reference = Some(dependencies.userEnteredP800Reference)
    )

    lazy val journeyEnteredNino: Journey = journeyEnteredP800Reference.copy(
      nino = Some(dependencies.nino)
    )

    object AfterReferenceCheck {

      lazy val journeyReferenceChecked: Journey = journeyEnteredNino.copy(
        referenceCheckResult = Some(dependencies.p800ReferenceChecked)
      )

      lazy val journeyReferenceDidntMatchNino: Journey = {
        val j = journeyEnteredNino.copy(
          referenceCheckResult = Some(ValidateReferenceResult.ReferenceDidntMatchNino)
        )
        require(!j.isIdentityVerified, "this journey instance has to have NOT verified identity")
        j
      }

      /**
       * When response from Nps indicates that refund has already been claimed
       */
      lazy val journeyRefundAlreadyTaken: Journey = {
        val j = journeyEnteredNino.copy(
          referenceCheckResult = Some(ValidateReferenceResult.RefundAlreadyTaken),
          hasFinished          = HasFinished.YesRefundAlreadyTaken
        )
        require(!j.isIdentityVerified, "this journey instance has to have NOT verified identity")
        j
      }

      /**
       * When response from Nps indicates that refund is no longer available
       */
      lazy val journeyRefundNotAvailable: Journey = {
        val j = journeyEnteredNino.copy(
          referenceCheckResult = Some(ValidateReferenceResult.RefundNoLongerAvailable),
          hasFinished          = HasFinished.YesRefundNoLongerAvailable
        )
        require(!j.isIdentityVerified, "this journey instance has to have NOT verified identity")
        j
      }
    }

    lazy val journeyLockedOutFromFailedAttempts: Journey = {
      val j = AfterReferenceCheck.journeyReferenceDidntMatchNino.copy(
        hasFinished = HasFinished.YesLockedOut
      )
      require(!j.isIdentityVerified, "this journey instance has to have NOT verified identity")
      require(hasFinished(j.hasFinished), "this journey instance should be in finished state")
      j
    }

    lazy val journeyClaimedOverpayment: Journey =
      //TODO: API responses
      AfterReferenceCheck.journeyReferenceChecked.copy(
        hasFinished = HasFinished.YesSucceeded
      )
  }
}
