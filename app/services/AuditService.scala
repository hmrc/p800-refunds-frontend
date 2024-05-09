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

import models.AmountInPence
import models.audit._
import models.journeymodels.Journey
import nps.models.{TracedIndividual, ValidateReferenceResult}
import models.audit.{AuditDetail, IpAddressLockedout, Login, NameMatchingAudit, UserLoginSelection}
import play.api.libs.json.{Json, Writes}
import play.api.mvc.RequestHeader
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.AuditExtensions._
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.audit.model.ExtendedDataEvent
import models.attemptmodels.{AttemptInfo, NumberOfAttempts}
import config.AppConfig
import models.audit.cheque.ChequeClaimAttemptMade

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext
import models.journeymodels.JourneyType

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

  def auditValidateUserDetails(journey: Journey, attemptInfo: Option[AttemptInfo], isSuccessful: Boolean)(implicit requestHeader: RequestHeader, hc: HeaderCarrier): Unit =
    audit(toValidateUserDetails(journey, attemptInfo, IsSuccessful(isSuccessful)))

  def auditChequeClaimAttemptMade(journey: Journey, isSuccessful: IsSuccessful)(implicit requestHeader: RequestHeader, hc: HeaderCarrier): Unit = {
    audit(toChequeClaimAttemptMade(journey, isSuccessful))
  }

  private def toValidateUserDetails(journey: Journey, attemptInfo: Option[AttemptInfo], isSuccessful: IsSuccessful)(implicit requestHeader: RequestHeader): ValidateUserDetails =
    ValidateUserDetails(
      outcome              = toOutcome(journey, attemptInfo, isSuccessful),
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

  private def toOutcome(journey: Journey, maybeAttemptInfo: Option[AttemptInfo], isSuccessful: IsSuccessful)(implicit requestHeader: RequestHeader): Outcome = {
    val p800ReferenceCheck: String = "p800 reference check"
    val traceIndividual: String = "trace indvidual"
    val lockedOut = LockedOut(maybeAttemptInfo.fold(false)(AttemptInfo.shouldBeLockedOut(_, appConfig.FailedAttemptRepo.failedAttemptRepoMaxAttempts)))

    Outcome(
      isSuccessful             = isSuccessful,
      attemptsOnRecord         = maybeAttemptInfo.fold[Option[NumberOfAttempts]](None) { attemptInfo => Some(attemptInfo.numberOfFailedAttempts) },
      lockout                  = lockedOut,
      apiResponsibleForFailure =
        if (isSuccessful.value) {
          None
        } else {
          journey.getJourneyType match {
            case JourneyType.Cheque => Some(p800ReferenceCheck)
            case JourneyType.BankTransfer =>
              Some(journey.referenceCheckResult.fold[String](p800ReferenceCheck) {
                case _: ValidateReferenceResult.P800ReferenceChecked => traceIndividual
                case _ => p800ReferenceCheck
              })
          }
        },
      reasons                  = Seq()
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
