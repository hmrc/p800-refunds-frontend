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
    _id           = journeyId,
    createdAt     = dependencies.instant,
    p800Reference = dependencies.p800Reference
  )

  lazy val journeyCheckYourReferenceValid: JourneyCheckYourReferenceValid = JourneyCheckYourReferenceValid(
    _id           = journeyId,
    createdAt     = dependencies.instant,
    p800Reference = dependencies.p800Reference
  )

  lazy val journeyDoYouWantYourRefundViaBankTransferNo: JourneyDoYouWantYourRefundViaBankTransferNo = JourneyDoYouWantYourRefundViaBankTransferNo(
    _id           = journeyId,
    createdAt     = dependencies.instant,
    p800Reference = dependencies.p800Reference
  )

  lazy val journeyDoYouWantYourRefundViaBankTransferYes: JourneyDoYouWantYourRefundViaBankTransferYes = JourneyDoYouWantYourRefundViaBankTransferYes(
    _id           = journeyId,
    createdAt     = dependencies.instant,
    p800Reference = dependencies.p800Reference
  )

  lazy val journeyWhatIsYourFullName: JourneyWhatIsYourFullName = JourneyWhatIsYourFullName(
    _id           = journeyId,
    createdAt     = dependencies.instant,
    p800Reference = dependencies.p800Reference,
    fullName      = dependencies.fullName
  )

  lazy val journeyWhatIsYourDateOfBirth: JourneyWhatIsYourDateOfBirth = JourneyWhatIsYourDateOfBirth(
    _id           = journeyId,
    createdAt     = dependencies.instant,
    p800Reference = dependencies.p800Reference,
    fullName      = dependencies.fullName,
    dateOfBirth   = dependencies.dateOfBirth
  )

  lazy val journeyWhatIsYourNationalInsuranceNumber: JourneyWhatIsYourNationalInsuranceNumber = JourneyWhatIsYourNationalInsuranceNumber(
    _id                     = journeyId,
    createdAt               = dependencies.instant,
    p800Reference           = dependencies.p800Reference,
    fullName                = dependencies.fullName,
    dateOfBirth             = dependencies.dateOfBirth,
    nationalInsuranceNumber = dependencies.nationalInsuranceNumber
  )

  lazy val journeyCheckYourAnswers: JourneyCheckYourAnswers = JourneyCheckYourAnswers(
    _id                     = journeyId,
    createdAt               = dependencies.instant,
    p800Reference           = dependencies.p800Reference,
    fullName                = dependencies.fullName,
    dateOfBirth             = dependencies.dateOfBirth,
    nationalInsuranceNumber = dependencies.nationalInsuranceNumber
  )

  lazy val journeyIdentityVerified: JourneyIdentityVerified = JourneyIdentityVerified(
    _id                          = journeyId,
    createdAt                    = dependencies.instant,
    p800Reference                = dependencies.p800Reference,
    fullName                     = dependencies.fullName,
    dateOfBirth                  = dependencies.dateOfBirth,
    nationalInsuranceNumber      = dependencies.nationalInsuranceNumber,
    identityVerificationResponse = dependencies.identityVerifiedResponse
  )
  lazy val journeyIdentityNotVerified: JourneyIdentityNotVerified = JourneyIdentityNotVerified(
    _id                          = journeyId,
    createdAt                    = dependencies.instant,
    p800Reference                = dependencies.p800Reference,
    fullName                     = dependencies.fullName,
    dateOfBirth                  = dependencies.dateOfBirth,
    nationalInsuranceNumber      = dependencies.nationalInsuranceNumber,
    identityVerificationResponse = dependencies.identityNotVerifiedResponse
  )

  lazy val journeyWhatIsTheNameOfYourBankAccount: JourneyWhatIsTheNameOfYourBankAccount = JourneyWhatIsTheNameOfYourBankAccount(
    _id                          = journeyId,
    createdAt                    = dependencies.instant,
    p800Reference                = dependencies.p800Reference,
    fullName                     = dependencies.fullName,
    dateOfBirth                  = dependencies.dateOfBirth,
    nationalInsuranceNumber      = dependencies.nationalInsuranceNumber,
    identityVerificationResponse = dependencies.identityNotVerifiedResponse,
    bankDescription              = dependencies.bankDescription
  )

  lazy val journeyRefundConsentGiven: JourneyRefundConsentGiven = JourneyRefundConsentGiven(
    _id                          = journeyId,
    createdAt                    = dependencies.instant,
    p800Reference                = dependencies.p800Reference,
    fullName                     = dependencies.fullName,
    dateOfBirth                  = dependencies.dateOfBirth,
    nationalInsuranceNumber      = dependencies.nationalInsuranceNumber,
    identityVerificationResponse = dependencies.identityNotVerifiedResponse,
    bankDescription              = dependencies.bankDescription
  )

  lazy val journeyApprovedRefund: JourneyApprovedRefund = JourneyApprovedRefund(
    _id                          = journeyId,
    createdAt                    = dependencies.instant,
    p800Reference                = dependencies.p800Reference,
    fullName                     = dependencies.fullName,
    dateOfBirth                  = dependencies.dateOfBirth,
    nationalInsuranceNumber      = dependencies.nationalInsuranceNumber,
    identityVerificationResponse = dependencies.identityNotVerifiedResponse,
    bankDescription              = dependencies.bankDescription
  )

  lazy val journeyYourChequeWillBePostedToYou: JourneyYourChequeWillBePostedToYou = JourneyYourChequeWillBePostedToYou(
    _id           = journeyId,
    createdAt     = dependencies.instant,
    p800Reference = dependencies.p800Reference
  )

  lazy val journeyNotApprovedRefund: JourneyNotApprovedRefund = JourneyNotApprovedRefund(
    _id                          = journeyId,
    createdAt                    = dependencies.instant,
    p800Reference                = dependencies.p800Reference,
    fullName                     = dependencies.fullName,
    dateOfBirth                  = dependencies.dateOfBirth,
    nationalInsuranceNumber      = dependencies.nationalInsuranceNumber,
    identityVerificationResponse = dependencies.identityNotVerifiedResponse,
    bankDescription              = dependencies.bankDescription
  )

}
