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

import models.dateofbirth.DateOfBirth
import models.{FullName, IdentityVerificationResponse, NationalInsuranceNumber, P800Reference}
import models.ecospend.BankId
import play.api.libs.json.OFormat

import java.time.{Clock, Instant}

sealed trait Journey {
  val _id: JourneyId
  val createdAt: Instant

  val lastUpdated: Instant = Instant.now(Clock.systemUTC())

  /* derived stuff: */
  def id: JourneyId = _id
  def journeyId: JourneyId = _id

  def name: String = {
    val className = getClass.getName
    val packageName = getClass.getPackage.getName
    className
      .replaceAll(s"$packageName.", "")
      .replaceAll("\\$", ".")
  }
}

object Journey {
  implicit val format: OFormat[Journey] = JourneyFormat.format
}

/**
 * [[Journey]], when finishing processing /start endpoint.
 * It's the initial state of the journey.
 */
final case class JourneyStarted(
    override val _id:       JourneyId,
    override val createdAt: Instant
) extends Journey
  with JBeforeDoYouWantToSignInNo
  with JBeforeWhatIsYourP800Reference
  with JBeforeCheckYourReferenceValid
  with JBeforeDoYouWantYourRefundViaBankTransferYes
  with JBeforeDoYouWantYourRefundViaBankTransferNo
  with JBeforeYourChequeWillBePostedToYou
  with JBeforeWhatIsYourFullName
  with JBeforeWhatIsYourDateOfBirth
  with JBeforeWhatIsYourNationalInsuranceNumber
  with JBeforeCheckYourAnswers
  with JBeforeIdentityVerified

/**
 * [[Journey]] when finishing submission on DoYouWantToSignIn page,
 * when selected No.
 */
final case class JourneyDoYouWantToSignInNo(
    override val _id:       JourneyId,
    override val createdAt: Instant
) extends Journey
  with JAfterStarted
  with JBeforeWhatIsYourP800Reference
  with JBeforeCheckYourReferenceValid
  with JBeforeDoYouWantYourRefundViaBankTransferYes
  with JBeforeDoYouWantYourRefundViaBankTransferNo
  with JBeforeYourChequeWillBePostedToYou
  with JBeforeWhatIsYourFullName
  with JBeforeWhatIsYourDateOfBirth
  with JBeforeWhatIsYourNationalInsuranceNumber
  with JBeforeCheckYourAnswers
  with JBeforeIdentityVerified

/**
 * [[Journey]] when finishing submission on WhatIsYourP800Reference page.
 */
final case class JourneyWhatIsYourP800Reference(
    override val _id:       JourneyId,
    override val createdAt: Instant,
    p800Reference:          P800Reference
) extends Journey
  with JAfterStarted
  with JAfterDoYouWantToSignInNo
  with JBeforeCheckYourReferenceValid
  with JBeforeDoYouWantYourRefundViaBankTransferYes
  with JBeforeDoYouWantYourRefundViaBankTransferNo
  with JBeforeYourChequeWillBePostedToYou
  with JBeforeWhatIsYourFullName
  with JBeforeWhatIsYourDateOfBirth
  with JBeforeWhatIsYourNationalInsuranceNumber
  with JBeforeCheckYourAnswers
  with JBeforeIdentityVerified

/**
 * [[Journey]] when finishing submission on CheckYourReference page,
 * when the validation of the [[P800Reference]] succeeded
 */
final case class JourneyCheckYourReferenceValid(
    override val _id:           JourneyId,
    override val createdAt:     Instant,
    override val p800Reference: P800Reference
) extends Journey
  with JAfterStarted
  with JAfterDoYouWantToSignInNo
  with JAfterWhatIsYourP800Reference
  with JBeforeDoYouWantYourRefundViaBankTransferYes
  with JBeforeDoYouWantYourRefundViaBankTransferNo
  with JBeforeYourChequeWillBePostedToYou
  with JBeforeWhatIsYourFullName
  with JBeforeWhatIsYourDateOfBirth
  with JBeforeWhatIsYourNationalInsuranceNumber
  with JBeforeCheckYourAnswers
  with JBeforeIdentityVerified

/**
 * [[Journey]] when finishing submission on DoYouWantYourRefundViaBankTransfer page,
 * when selected Yes
 */
final case class JourneyDoYouWantYourRefundViaBankTransferYes(
    override val _id:           JourneyId,
    override val createdAt:     Instant,
    override val p800Reference: P800Reference
) extends Journey
  with JAfterStarted
  with JAfterDoYouWantToSignInNo
  with JAfterWhatIsYourP800Reference
  with JAfterCheckYourReferenceValid
  with JBeforeWhatIsYourFullName
  with JBeforeWhatIsYourDateOfBirth
  with JBeforeWhatIsYourNationalInsuranceNumber
  with JBeforeCheckYourAnswers
  with JBeforeIdentityVerified

/**
 * [[Journey]] when finishing submission on DoYouWantYourRefundViaBankTransfer page,
 * when selected Yes
 */
final case class JourneyDoYouWantYourRefundViaBankTransferNo(
    override val _id:           JourneyId,
    override val createdAt:     Instant,
    override val p800Reference: P800Reference
) extends Journey
  with JAfterStarted
  with JAfterDoYouWantToSignInNo
  with JAfterWhatIsYourP800Reference
  with JAfterCheckYourReferenceValid
  with JBeforeYourChequeWillBePostedToYou
  with JBeforeIdentityVerified

/**
 * [[Journey]] when finishing submission on YourChequeWillBePostedToYou page.
 * It's the final state of the journey.
 */
final case class JourneyYourChequeWillBePostedToYou(
    override val _id:           JourneyId,
    override val createdAt:     Instant,
    override val p800Reference: P800Reference
) extends Journey
  with JTerminal
  with JAfterStarted
  with JAfterDoYouWantToSignInNo
  with JAfterWhatIsYourP800Reference
  with JAfterCheckYourReferenceValid
  with JAfterDoYouWantYourRefundViaBankTransferNo
  with JBeforeIdentityVerified

/**
 * [[Journey]] when finishing submission on WhatIsYourFullName page.
 */
final case class JourneyWhatIsYourFullName(
    override val _id:           JourneyId,
    override val createdAt:     Instant,
    override val p800Reference: P800Reference,
    fullName:                   FullName
) extends Journey
  with JAfterStarted
  with JAfterDoYouWantToSignInNo
  with JAfterWhatIsYourP800Reference
  with JAfterCheckYourReferenceValid
  with JAfterDoYouWantYourRefundViaBankTransferYes
  with JBeforeWhatIsYourDateOfBirth
  with JBeforeWhatIsYourNationalInsuranceNumber
  with JBeforeCheckYourAnswers
  with JBeforeIdentityVerified

/**
 * [[Journey]] when finishing submission on WhatIsYourDateOfBirth page,
 * when the validation of date of birth is successful
 */
final case class JourneyWhatIsYourDateOfBirth(
    override val _id:           JourneyId,
    override val createdAt:     Instant,
    override val p800Reference: P800Reference,
    override val fullName:      FullName,
    dateOfBirth:                DateOfBirth
) extends Journey
  with JAfterStarted
  with JAfterDoYouWantToSignInNo
  with JAfterWhatIsYourP800Reference
  with JAfterCheckYourReferenceValid
  with JAfterDoYouWantYourRefundViaBankTransferYes
  with JAfterWhatIsYourFullName
  with JBeforeWhatIsYourNationalInsuranceNumber
  with JBeforeCheckYourAnswers
  with JBeforeIdentityVerified

final case class JourneyWhatIsYourNationalInsuranceNumber(
    override val _id:           JourneyId,
    override val createdAt:     Instant,
    override val p800Reference: P800Reference,
    override val fullName:      FullName,
    override val dateOfBirth:   DateOfBirth,
    nationalInsuranceNumber:    NationalInsuranceNumber
) extends Journey
  with JAfterStarted
  with JAfterDoYouWantToSignInNo
  with JAfterWhatIsYourP800Reference
  with JAfterCheckYourReferenceValid
  with JAfterDoYouWantYourRefundViaBankTransferYes
  with JAfterWhatIsYourFullName
  with JAfterWhatIsYourDateOfBirth
  with JBeforeCheckYourAnswers
  with JBeforeIdentityVerified

/**
 * This state represents journey leaving CheckYourAnswers page via the "Change" link
 */
final case class JourneyCheckYourAnswersChange(
    override val _id:                     JourneyId,
    override val createdAt:               Instant,
    override val p800Reference:           P800Reference,
    override val fullName:                FullName,
    override val dateOfBirth:             DateOfBirth,
    override val nationalInsuranceNumber: NationalInsuranceNumber
) extends Journey
  with JAfterStarted
  with JAfterDoYouWantToSignInNo
  with JAfterWhatIsYourP800Reference
  with JAfterCheckYourReferenceValid
  with JAfterDoYouWantYourRefundViaBankTransferYes
  with JAfterWhatIsYourFullName
  with JAfterWhatIsYourDateOfBirth
  with JAfterWhatIsYourNationalInsuranceNumber
  with JBeforeIdentityVerified

final case class JourneyCheckYourAnswers(
    override val _id:                     JourneyId,
    override val createdAt:               Instant,
    override val p800Reference:           P800Reference,
    override val fullName:                FullName,
    override val dateOfBirth:             DateOfBirth,
    override val nationalInsuranceNumber: NationalInsuranceNumber
//TODO: results of API calls
) extends Journey
  with JAfterStarted
  with JAfterDoYouWantToSignInNo
  with JAfterWhatIsYourP800Reference
  with JAfterCheckYourReferenceValid
  with JAfterDoYouWantYourRefundViaBankTransferYes
  with JAfterWhatIsYourFullName
  with JAfterWhatIsYourDateOfBirth
  with JAfterWhatIsYourNationalInsuranceNumber
  with JBeforeIdentityVerified

final case class JourneyIdentityVerified(
    override val _id:                     JourneyId,
    override val createdAt:               Instant,
    override val p800Reference:           P800Reference,
    override val fullName:                FullName,
    override val dateOfBirth:             DateOfBirth,
    override val nationalInsuranceNumber: NationalInsuranceNumber,
    identityVerificationResponse:         IdentityVerificationResponse
) extends Journey
  with JAfterStarted
  with JAfterDoYouWantToSignInNo
  with JAfterWhatIsYourP800Reference
  with JAfterCheckYourReferenceValid
  with JAfterDoYouWantYourRefundViaBankTransferYes
  with JAfterWhatIsYourFullName
  with JAfterWhatIsYourDateOfBirth
  with JAfterWhatIsYourNationalInsuranceNumber
  with JAfterCheckYourAnswers

final case class JourneyIdentityNotVerified(
    override val _id:                     JourneyId,
    override val createdAt:               Instant,
    override val p800Reference:           P800Reference,
    override val fullName:                FullName,
    override val dateOfBirth:             DateOfBirth,
    override val nationalInsuranceNumber: NationalInsuranceNumber,
    identityVerificationResponse:         IdentityVerificationResponse
) extends Journey
  with JAfterStarted
  with JAfterDoYouWantToSignInNo
  with JAfterWhatIsYourP800Reference
  with JAfterCheckYourReferenceValid
  with JAfterDoYouWantYourRefundViaBankTransferYes
  with JAfterWhatIsYourFullName
  with JAfterWhatIsYourDateOfBirth
  with JAfterWhatIsYourNationalInsuranceNumber
  with JAfterCheckYourAnswers

final case class JourneyWhatIsTheNameOfYourBankAccount(
    override val _id:                          JourneyId,
    override val createdAt:                    Instant,
    override val p800Reference:                P800Reference,
    override val fullName:                     FullName,
    override val dateOfBirth:                  DateOfBirth,
    override val nationalInsuranceNumber:      NationalInsuranceNumber,
    override val identityVerificationResponse: IdentityVerificationResponse,
    bankId:                                    BankId
) extends Journey
  with JAfterStarted
  with JAfterDoYouWantToSignInNo
  with JAfterWhatIsYourP800Reference
  with JAfterCheckYourReferenceValid
  with JAfterDoYouWantYourRefundViaBankTransferYes
  with JAfterWhatIsYourFullName
  with JAfterWhatIsYourDateOfBirth
  with JAfterWhatIsYourNationalInsuranceNumber
  with JAfterCheckYourAnswers
  with JAfterIdentityVerified

/*
 * Below marking traits for all [[Journey]]s after/before certain state
 */

sealed trait JAfterStarted extends Journey
sealed trait JAfterDoYouWantToSignInNo extends Journey
sealed trait JAfterWhatIsYourP800Reference extends Journey {
  val p800Reference: P800Reference
}
sealed trait JAfterCheckYourReferenceValid extends Journey {
  val p800Reference: P800Reference
}
sealed trait JAfterDoYouWantYourRefundViaBankTransferYes extends Journey {
  val p800Reference: P800Reference
}
sealed trait JAfterDoYouWantYourRefundViaBankTransferNo extends Journey {
  val p800Reference: P800Reference
}
sealed trait JAfterWhatIsYourFullName extends Journey {
  val p800Reference: P800Reference
  val fullName: FullName
}
sealed trait JAfterWhatIsYourDateOfBirth extends Journey {
  val p800Reference: P800Reference
  val fullName: FullName
  val dateOfBirth: DateOfBirth
}

sealed trait JAfterWhatIsYourNationalInsuranceNumber extends Journey {
  val p800Reference: P800Reference
  val fullName: FullName
  val dateOfBirth: DateOfBirth
  val nationalInsuranceNumber: NationalInsuranceNumber
}

sealed trait JAfterCheckYourAnswers extends Journey {
  val p800Reference: P800Reference
  val fullName: FullName
  val dateOfBirth: DateOfBirth
  val nationalInsuranceNumber: NationalInsuranceNumber
}

sealed trait JAfterIdentityVerified extends Journey {
  val p800Reference: P800Reference
  val fullName: FullName
  val dateOfBirth: DateOfBirth
  val nationalInsuranceNumber: NationalInsuranceNumber
  val identityVerificationResponse: IdentityVerificationResponse
}

sealed trait JAfterWhatIsTheNameOfYourBankAccount extends Journey {
  val p800Reference: P800Reference
  val fullName: FullName
  val dateOfBirth: DateOfBirth
  val nationalInsuranceNumber: NationalInsuranceNumber
  val identityVerificationResponse: IdentityVerificationResponse
  val bankId: BankId
}

sealed trait JBeforeDoYouWantToSignInNo extends Journey
sealed trait JBeforeWhatIsYourP800Reference extends Journey
sealed trait JBeforeCheckYourReferenceValid extends Journey
sealed trait JBeforeDoYouWantYourRefundViaBankTransferYes extends Journey
sealed trait JBeforeDoYouWantYourRefundViaBankTransferNo extends Journey
sealed trait JBeforeYourChequeWillBePostedToYou extends Journey
sealed trait JBeforeWhatIsYourFullName extends Journey
sealed trait JBeforeWhatIsYourDateOfBirth extends Journey
sealed trait JBeforeWhatIsYourNationalInsuranceNumber extends Journey
sealed trait JBeforeCheckYourAnswers extends Journey
sealed trait JBeforeIdentityVerified extends Journey
sealed trait JBeforeWhatIsTheNameOfYourBankAccount extends Journey

/**
 * Marking trait for [[Journey]] in terminal state
 */
sealed trait JTerminal extends Journey
