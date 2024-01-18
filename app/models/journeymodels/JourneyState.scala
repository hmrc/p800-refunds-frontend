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

import julienrf.json.derived
import play.api.libs.json.OFormat

sealed trait JourneyType

object JourneyType {
  case object Cheque extends JourneyType
  case object BankTransfer extends JourneyType

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val format: OFormat[JourneyType] = derived.oformat[JourneyType]()
}
//
///**
// * Journey State
// */
//sealed trait JourneyState
//
//object JourneyState {
//
//  /**
//   * Initial state
//   */
//  case object DoYouWantToSignIn
//    extends JourneyState
//    with BeforeTurn
//
//  case object DoYouWantYourRefundViaBankTransfer
//    extends JourneyState
//    with BeforeTurn
//
//  sealed trait BankTransfer extends JourneyState
//
//  object BankTransfer {
//
//    case object WeNeedYouToConfirmYourIdentity
//      extends BankTransfer
//        with BeforeWhatIsYourP800Reference
//        with BeforeWhatIsYourNationalInsuranceNumber
//        with BeforeWhatIsYourDateOfBirth
//        with BeforeCheckYourAnswers
//        with BeforeWeHaveConfirmedYourIdentity
//        with BeforeWeCannotConfirmYourIdentity
//        with BeforeChooseAnotherWayToGetYourRefund
//
//    case object WhatIsYourP800Reference
//      extends BankTransfer
//      with BeforeWhatIsYourNationalInsuranceNumber
//      with BeforeWhatIsYourDateOfBirth
//        with BeforeCheckYourAnswers
//        with BeforeWeHaveConfirmedYourIdentity
//        with BeforeWeCannotConfirmYourIdentity
//        with BeforeChooseAnotherWayToGetYourRefund
//
//    case object WhatIsYourNationalInsuranceNumber
//      extends BankTransfer
//      with BeforeWhatIsYourDateOfBirth
//        with BeforeCheckYourAnswers
//        with BeforeWeHaveConfirmedYourIdentity
//        with BeforeWeCannotConfirmYourIdentity
//        with BeforeChooseAnotherWayToGetYourRefund
//
//    case object WhatIsYourDateOfBirth //only in bank transfer BankTransfer
//      extends JourneyState
//      with BeforeCheckYourAnswers
//        with BeforeWeHaveConfirmedYourIdentity
//        with BeforeWeCannotConfirmYourIdentity
//        with BeforeChooseAnotherWayToGetYourRefund
//
//    case object CheckYourAnswers
//      extends BankTransfer
//      with BeforeWeHaveConfirmedYourIdentity
//        with BeforeWeCannotConfirmYourIdentity
//        with BeforeChooseAnotherWayToGetYourRefund
//
//    case object WeHaveConfirmedYourIdentity
//      extends BankTransfer
//      with BeforeWeCannotConfirmYourIdentity
//      with BeforeChooseAnotherWayToGetYourRefund
//
//
//    case object WeCannotConfirmYourIdentity
//      extends BankTransfer
//      with BeforeChooseAnotherWayToGetYourRefund
//
//    case object ChooseAnotherWayToGetYourRefund extends BankTransfer
//    case object WhatIsTheNameOfYourBank extends BankTransfer
//    case object GiveYourConsent extends BankTransfer
//    case object WeAreVerifyingYourBankAccount extends BankTransfer
//    case object RequestReceived extends BankTransfer
//    case object YourRefundRequestHasNotBeenSubmitted extends BankTransfer
//
//    /* marking traits for exhaustive pattern matching selections */
//
//    sealed trait BeforeWhatIsYourP800Reference extends BankTransfer
//    sealed trait BeforeWhatIsYourNationalInsuranceNumber extends BankTransfer
//    sealed trait BeforeWhatIsYourDateOfBirth extends BankTransfer
//    sealed trait BeforeCheckYourAnswers extends BankTransfer
//    sealed trait BeforeWeHaveConfirmedYourIdentity extends BankTransfer
//    sealed trait BeforeWeCannotConfirmYourIdentity extends BankTransfer
//    sealed trait BeforeChooseAnotherWayToGetYourRefund extends BankTransfer
//    sealed trait BeforeWhatIsTheNameOfYourBank extends BankTransfer
//    sealed trait BeforeGiveYourConsent extends BankTransfer
//    sealed trait BeforeWeAreVerifyingYourBankAccount extends BankTransfer
//
//    sealed trait AfterWhatIsYourP800Reference extends BankTransfer
//    sealed trait AfterWhatIsYourNationalInsuranceNumber extends BankTransfer
//    sealed trait AfterWhatIsYourDateOfBirth extends BankTransfer
//    sealed trait AfterCheckYourAnswers extends BankTransfer
//    sealed trait AfterWeHaveConfirmedYourIdentity extends BankTransfer
//    sealed trait AfterWeCannotConfirmYourIdentity extends BankTransfer
//    sealed trait AfterChooseAnotherWayToGetYourRefund extends BankTransfer
//    sealed trait AfterWhatIsTheNameOfYourBank extends BankTransfer
//    sealed trait AfterGiveYourConsent extends BankTransfer
//    sealed trait AfterWeAreVerifyingYourBankAccount extends BankTransfer
//    sealed trait BeforeRequestReceived extends BankTransfer
//    sealed trait BeforeYourRefundRequestHasNotBeenSubmitted extends BankTransfer
//
//  }
//
//  sealed trait Cheque extends JourneyState
//
//  object Cheque {
//    case object WeNeedYouToConfirmYourIdentity extends Cheque
//
//    case object WhatIsYourP800Reference extends Cheque
//
//    case object WhatIsYourNationalInsuranceNumber extends Cheque
//
//    case object CheckYourAnswers extends Cheque
//
//    case object WeHaveConfirmedYourIdentity extends Cheque
//
//    case object WeCannotConfirmYourIdentity extends Cheque
//
//    case object ChooseAnotherWayToGetYourRefund extends Cheque
//
//    case object CompleteYourRefundRequest extends Cheque
//
//    case object RequestReceived extends Cheque
//  }
//
//  //marking traits
//
//  /**
//   * Journey before it turns to be a BankTransfer or a Cheque Journey.
//   */
//  sealed trait BeforeTurn extends JourneyState
//
//  //TODO: make them marking traits for exhaustive pattern matching (those methods generate `match may not be exhaustive...`)
//
//  def isBeforeDoYouWantYourRefundViaBankTransfer(journeyState: JourneyState): Boolean = journeyState match {
//    case _ => Errors.notImplemented()
//  }
//
//  def isBeforeWeNeedYouToConfirmYourIdentity(journeyState: JourneyState): Boolean = journeyState match {
//    case _ => Errors.notImplemented()
//  }
//
//  def isBeforeWhatIsYourP800Reference(journeyState: JourneyState): Boolean = journeyState match {
//    case _ => Errors.notImplemented()
//  }
//
//  def isBeforeWhatIsYourNationalInsuranceNumber(journeyState: JourneyState): Boolean = journeyState match {
//    case _ => Errors.notImplemented()
//  }
//
//  def isBeforeWhatIsYourDateOfBirth(journeyState: JourneyState): Boolean = journeyState match {
//    case _ => Errors.notImplemented()
//  }
//
//  def isBeforeCheckYourAnswers(journeyState: JourneyState): Boolean = journeyState match {
//    case _ => Errors.notImplemented()
//  }
//
//  def isCheckYourAnswers(journeyState: JourneyState): Boolean = journeyState match {
//    case _ => Errors.notImplemented()
//  }
//
//  def isBeforeWeHaveConfirmedYourIdentity(journeyState: JourneyState): Boolean = journeyState match {
//    case _ => Errors.notImplemented()
//  }
//
//  //inclusive
//  def isInclusiveAfterWeHaveConfirmedYourIdentity(journeyState: JourneyState): Boolean = journeyState match {
//    case _ => Errors.notImplemented()
//  }
//
//  def isBeforeChooseAnotherWayToGetYourRefund(journeyState: JourneyState): Boolean = journeyState match {
//    case _ => Errors.notImplemented()
//  }
//
//  def isInclusiveAfterChooseAnotherWayToGetYourRefunds(journeyState: JourneyState): Boolean = journeyState match {
//    case _ => Errors.notImplemented()
//  }
//
//  def isBeforeCompleteYourRefundRequest(journeyState: JourneyState): Boolean = journeyState match {
//    case _ => Errors.notImplemented()
//  }
//
//  def isBeforeRequestReceived(journeyState: JourneyState): Boolean = journeyState match {
//    case _ => Errors.notImplemented()
//  }
//
//  def isBeforeWhatIsTheNameOfYourBankAccount(journeyState: JourneyState): Boolean = journeyState match {
//    case _ => Errors.notImplemented()
//  }
//
//  def isBeforeGiveYourConsent(journeyState: JourneyState): Boolean = journeyState match {
//    case _ => Errors.notImplemented()
//  }
//
//  def isBeforeWeAreVerifyingYourBankAccount(journeyState: JourneyState): Boolean = journeyState match {
//    case _ => Errors.notImplemented()
//  }
//
//  def isBeforeYourRefundRequestHasNotBeenSubmitted(journeyState: JourneyState): Boolean = journeyState match {
//    case _ => Errors.notImplemented()
//  }
//
//  def isTerminal(journeyState: JourneyState): Boolean = journeyState match {
//    case _ => Errors.notImplemented()
//  }
//
//  def isChequeJourney(journeyState: JourneyState): Boolean = journeyState match {
//    case _ => Errors.notImplemented()
//  }
//
//  def isBankTransferJourney(journeyState: JourneyState): Boolean = journeyState match {
//    case _ => Errors.notImplemented()
//  }
//
//}
