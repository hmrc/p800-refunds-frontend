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

package services

import config.AppConfig
import models.AmountInPence
import models.attemptmodels.{AttemptInfo, NumberOfAttempts}
import models.audit._
import models.audit.cheque.ChequeClaimAttemptMade
import models.audit.{AuditDetail, IpAddressLockedout, Login, NameMatchingAudit, UserLoginSelection}
import models.ecospend.BankFriendlyName
import models.journeymodels.Journey
import nps.models.{TracedIndividual, ValidateReferenceResult}
import play.api.libs.json.{Json, Writes}
import play.api.mvc.RequestHeader
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.AuditExtensions._
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.audit.model.ExtendedDataEvent

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class AuditService @Inject() (
    auditConnector: AuditConnector,
    appConfig:      AppConfig
)(implicit ec: ExecutionContext) {

  private val auditSource: String = "p800-refunds-frontend"

  private def audit[A <: AuditDetail: Writes](a: A)(implicit hc: HeaderCarrier): Unit = {
    val _ = auditConnector.sendExtendedEvent(
      ExtendedDataEvent(
        auditSource = auditSource,
        auditType   = a.auditType,
        tags        = hc.toAuditTags(),
        detail      = Json.toJson(a)
      )
    )
  }

  def auditUserLoginSelection(login: Login, ipAddressLockedout: IpAddressLockedout)(implicit hc: HeaderCarrier): Unit =
    audit(UserLoginSelection(
      login              = login,
      ipAddressLockedout = ipAddressLockedout
    ))

  def auditValidateUserDetails(journey: Journey, attemptInfo: Option[AttemptInfo], isSuccessful: Boolean, apiResponsibleForFailure: Option[ApiResponsibleForFailure] = None, failureReasons: Option[Seq[String]] = None)(implicit requestHeader: RequestHeader, hc: HeaderCarrier): Unit =
    audit(toValidateUserDetails(journey, attemptInfo, IsSuccessful(isSuccessful), apiResponsibleForFailure, failureReasons))

  def auditChequeClaimAttemptMade(journey: Journey, isSuccessful: IsSuccessful)(implicit requestHeader: RequestHeader, hc: HeaderCarrier): Unit = {
    audit(toChequeClaimAttemptMade(journey, isSuccessful))
  }

  def auditBankClaimAttempt(journey: Journey, actionsOutcome: BankActionsOutcome, failureReasons: Option[Seq[String]] = None)(implicit requestHeader: RequestHeader, hc: HeaderCarrier): Unit =
    audit(toBankClaimAttempt(journey, actionsOutcome, failureReasons))

  private def toValidateUserDetails(journey: Journey, attemptInfo: Option[AttemptInfo], isSuccessful: IsSuccessful, apiResponsibleForFailure: Option[ApiResponsibleForFailure], failureReasons: Option[Seq[String]])(implicit requestHeader: RequestHeader): ValidateUserDetails =
    ValidateUserDetails(
      outcome              = toOutcome(attemptInfo, isSuccessful, apiResponsibleForFailure, failureReasons.getOrElse(Seq.empty)),
      userEnteredDetails   = toUserEnteredDetails(journey),
      repaymentAmount      = journey.referenceCheckResult.fold[Option[AmountInPence]](None) {
        case p800ReferenceChecked: ValidateReferenceResult.P800ReferenceChecked => Some(AmountInPence(p800ReferenceChecked.paymentAmount))
        case _ => None
      },
      repaymentInformation = journey.referenceCheckResult.fold[Option[RepaymentInformation]](None) {
        case p800ReferenceChecked: ValidateReferenceResult.P800ReferenceChecked => Some(toRepaymentInformation(p800ReferenceChecked))
        case _ => None
      },
      name                 = toName(journey.traceIndividualResponse),
      address              = toAddress(journey.traceIndividualResponse)
    )

  private def toOutcome(maybeAttemptInfo: Option[AttemptInfo], isSuccessful: IsSuccessful, apiResponsibleForFailure: Option[ApiResponsibleForFailure], reasons: Seq[String]): Outcome = {
    val lockedOut = LockedOut(maybeAttemptInfo.fold(false)(AttemptInfo.shouldBeLockedOut(_, appConfig.FailedAttemptRepo.failedAttemptRepoMaxAttempts)))

    Outcome(
      isSuccessful             = isSuccessful,
      attemptsOnRecord         = maybeAttemptInfo.fold[Option[NumberOfAttempts]](None) { attemptInfo => Some(attemptInfo.numberOfFailedAttempts) },
      lockout                  = lockedOut,
      apiResponsibleForFailure = apiResponsibleForFailure,
      reasons                  = reasons
    )
  }

  private def toUserEnteredDetails(journey: Journey)(implicit requestHeader: RequestHeader): UserEnteredDetails =
    UserEnteredDetails(
      repaymentMethod = journey.getJourneyType,
      p800Reference   = journey.getP800Reference.sanitiseReference,
      nino            = journey.getNino,
      dob             = journey.dateOfBirth
    )

  private def toRepaymentInformation(p800ReferenceChecked: ValidateReferenceResult.P800ReferenceChecked): RepaymentInformation =
    RepaymentInformation(
      reconciliationIdentifier = p800ReferenceChecked.reconciliationIdentifier,
      paymentNumber            = p800ReferenceChecked.paymentNumber,
      payeNumber               = p800ReferenceChecked.payeNumber,
      taxDistrictNumber        = p800ReferenceChecked.taxDistrictNumber,
      associatedPayableNumber  = p800ReferenceChecked.associatedPayableNumber,
      customerAccountNumber    = p800ReferenceChecked.customerAccountNumber,
      currentOptimisticLock    = p800ReferenceChecked.currentOptimisticLock
    )

  private def toBankClaimAttempt(journey: Journey, actionsOutcome: BankActionsOutcome, failureReasons: Option[Seq[String]])(implicit requestHeader: RequestHeader): BankClaimAttempt =
    BankClaimAttempt(
      outcome              = toBankClaimOutcome(actionsOutcome, failureReasons),
      userEnteredDetails   = toBankClaimUserEnteredDetails(journey),
      repaymentAmount      = journey.referenceCheckResult.fold[Option[AmountInPence]](None) {
        case p800ReferenceChecked: ValidateReferenceResult.P800ReferenceChecked => Some(AmountInPence(p800ReferenceChecked.paymentAmount))
        case _ => None
      },
      repaymentInformation = journey.referenceCheckResult.fold[Option[RepaymentInformation]](None) {
        case p800ReferenceChecked: ValidateReferenceResult.P800ReferenceChecked => Some(toRepaymentInformation(p800ReferenceChecked))
        case _ => None
      },
      name                 = toName(journey.traceIndividualResponse),
      address              = toAddress(journey.traceIndividualResponse)
    )

  private def toBankClaimOutcome(actionsOutcome: BankActionsOutcome, failureReasons: Option[Seq[String]]): BankClaimOutcome =
    BankClaimOutcome(
      isSuccessful   = actionsOutcome.overallResult,
      actionsOutcome = actionsOutcome,
      failureReasons = failureReasons
    )

  private def toBankClaimUserEnteredDetails(journey: Journey)(implicit requestHeader: RequestHeader): BankClaimUserEnteredDetails =
    BankClaimUserEnteredDetails(
      repaymentMethod = journey.getJourneyType,
      chosenBank      = journey.bankDescription.fold[Option[BankFriendlyName]](None){ bankDescription =>
        Some(bankDescription.friendlyName)
      },
      p800Reference   = journey.getP800Reference.sanitiseReference,
      nino            = journey.getNino,
      dob             = journey.dateOfBirth
    )

  private def toName(maybeTracedIndividual: Option[TracedIndividual]): Option[Name] =
    maybeTracedIndividual.fold[Option[Name]](None) { traceIndividualResponse =>
      Some(Name(
        title          = traceIndividualResponse.title,
        firstForename  = traceIndividualResponse.firstForename,
        secondForename = traceIndividualResponse.secondForename,
        surname        = traceIndividualResponse.surname
      ))
    }

  private def toAddress(maybeTracedIndividual: Option[TracedIndividual]): Option[Address] =
    maybeTracedIndividual.fold[Option[Address]](None) { traceIndividualResponse =>
      Some(Address(
        addressLine1    = traceIndividualResponse.addressLine1,
        addressLine2    = traceIndividualResponse.addressLine2,
        addressPostcode = traceIndividualResponse.addressPostcode
      ))
    }

  private def toChequeClaimAttemptMade(journey: Journey, isSuccessful: IsSuccessful)(implicit requestHeader: RequestHeader): ChequeClaimAttemptMade = {
    val referenceCheckResult: ValidateReferenceResult.P800ReferenceChecked = journey.getP800ReferenceChecked
    ChequeClaimAttemptMade(
      userEnteredDetails   = models.audit.cheque.UserEnteredDetails(
        p800Reference = referenceCheckResult.paymentNumber,
        nino          = journey.getNino
      ),
      repaymentAmount      = models.audit.cheque.RepaymentAmount(referenceCheckResult.paymentAmount),
      isSuccessful         = isSuccessful,
      repaymentInformation = models.audit.cheque.RepaymentInformation(
        paymentNumber            = referenceCheckResult.paymentNumber,
        customerAccountNumber    = referenceCheckResult.customerAccountNumber,
        associatedPayableNumber  = referenceCheckResult.associatedPayableNumber,
        reconciliationIdentifier = referenceCheckResult.reconciliationIdentifier,
        payeNumber               = referenceCheckResult.payeNumber,
        taxDistrictNumber        = referenceCheckResult.taxDistrictNumber
      )
    )
  }

  def auditNameMatching(nameMatchingAudit: NameMatchingAudit)(implicit hc: HeaderCarrier): Unit = {
    audit(nameMatchingAudit)
  }
}
