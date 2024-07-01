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

package controllers

import action.{Actions, JourneyRequest}
import casemanagement._
import config.AppConfig
import connectors.{P800RefundsBackendConnector, P800RefundsExternalApiConnector}
import edh._
import models.audit.{BankActionsOutcome, IsSuccessful, NameMatchOutcome, NameMatchingAudit, RawNpsName}
import models.ecospend.BankId
import models.ecospend.account.{BankAccountOwnerName, BankAccountSummary}
import models.ecospend.consent.{BankReferenceId, ConsentId, ConsentStatus}
import models.journeymodels._
import models.namematching.{NameMatchingResult, NameMatchingResponse}
import models.p800externalapi.EventValue
import nps.models.TraceIndividualResponse.TracedIndividual
import nps.models.ValidateReferenceResult.P800ReferenceChecked
import nps.models._
import play.api.mvc._
import services.{AuditService, EcospendService, GetBankDetailsRiskResultService, JourneyService, NameMatchingService}
import uk.gov.hmrc.http.{HttpException, UpstreamErrorResponse}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import util.SafeEquals.EqualsOps
import util.{Errors, JourneyLogger, NameParsingUtil}
import views.Views

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.math.BigDecimal.RoundingMode
import models.audit.ChosenBank
import models.ecospend.BankDescription

@Singleton
class VerifyingYourBankAccountController @Inject() (
    appConfig:                       AppConfig,
    actions:                         Actions,
    auditService:                    AuditService,
    ecospendService:                 EcospendService,
    journeyService:                  JourneyService,
    mcc:                             MessagesControllerComponents,
    p800RefundsBackendConnector:     P800RefundsBackendConnector,
    p800RefundsExternalApiConnector: P800RefundsExternalApiConnector,
    getBankDetailsRiskResultService: GetBankDetailsRiskResultService,
    views:                           Views
)(implicit ec: ExecutionContext) extends FrontendController(mcc) {

  def get(status: Option[ConsentStatus], consent_id: Option[ConsentId], bank_reference_id: Option[BankReferenceId]): Action[AnyContent] = actions.journeyInProgress.async { implicit request: JourneyRequest[_] =>
    val journey: Journey = request.journey
    sanityChecks(consent_id, bank_reference_id, journey)

    val consentStatus: ConsentStatus =
      status.getOrElse(Errors.throwServerErrorException("This endpoint requires a status parameter."))

    for {
      bankAccountSummary: BankAccountSummary <- obtainBankAccountSummary(journey)
      result <- if (bankAccountSummary.accountIdentification.isDefined) process(journey, bankAccountSummary, consentStatus)
      else handleMissingAcccountIdentification(journey)
    } yield result
  }

  private def process(journey: Journey, bankAccountSummary: BankAccountSummary, consentStatus: ConsentStatus)(implicit request: JourneyRequest[_]): Future[Result] =
    for {
      isValidEventValue: EventValue <- obtainIsValid(journey, bankAccountSummary)
      bankDetailsRiskResultResponse: GetBankDetailsRiskResultResponse <- getBankDetailsRiskResultService.getBankDetailsRiskResult(journey, bankAccountSummary, isValidEventValue)
      didAnyNameMatch: Boolean = getNameMachingResult(journey.nameMatchingResult, journey.getTraceIndividualResponse, bankAccountSummary)
      newJourney = journey.update(isValidEventValue, bankAccountSummary, bankDetailsRiskResultResponse, NameMatchingResult(didAnyNameMatch))
      (result, newJourney) <- next(newJourney, isValidEventValue, bankDetailsRiskResultResponse, bankAccountSummary, consentStatus, didAnyNameMatch)
      _ <- journeyService.upsert(newJourney)
    } yield result

  private def handleMissingAcccountIdentification(journey: Journey)(implicit request: JourneyRequest[_]): Future[Result] = {
    for {
      _ <- journeyService.upsert(journey.copy(hasFinished = HasFinished.YesRefundNotSubmitted))
    } yield Redirect(routes.RefundRequestNotSubmittedController.get)
  }

  private def getNameMachingResult(
      maybeNameMatchingResult: Option[NameMatchingResult],
      individualResponse:      TracedIndividual,
      bankAccountSummary:      BankAccountSummary
  )(implicit request: JourneyRequest[_]): Boolean = {
    maybeNameMatchingResult match {
      case Some(nameMachingResult) => nameMachingResult.value
      case None                    => doAnyNamesFromPartiesListMatch(individualResponse, bankAccountSummary)
    }
  }

  private def doAnyNamesFromPartiesListMatch(
      individualResponse: TracedIndividual,
      bankAccountSummary: BankAccountSummary
  )(implicit request: JourneyRequest[_]): Boolean = {
    val bankId = bankAccountSummary.bankId.getOrElse(BankId("NoBankId")).value
    val isUsingSurnameFirstFormat: Boolean = appConfig.NameParsing.bankIdsUsingSurnameFirstFormat.contains(bankId)
    val isUsingPartiesName: Boolean = appConfig.NameParsing.bankIdsUsingPartyName.contains(bankId)

    val bankDescription: BankDescription = request.journey.getBankDescription

    (bankAccountSummary.parties.forall(_.isEmpty), bankAccountSummary.accountOwnerName.isEmpty, isUsingSurnameFirstFormat) match {
      case (true, true, _) =>
        JourneyLogger.warn("No parties list or account owner name present")

        val emptyListAudit = NameMatchingAudit(
          NameMatchOutcome(isSuccessful = false, "no names returned from bank"),
          RawNpsName(
            individualResponse.firstForename,
            individualResponse.secondForename,
            individualResponse.surname
          ),
          rawBankName         = None,
          transformedNpsName  = None,
          transformedBankName = None,
          chosenBank          = ChosenBank(
            bankId       = bankDescription.bankId,
            name         = bankDescription.name,
            friendlyName = bankDescription.friendlyName
          ),
          partiesArrayUsed    = false
        )
        auditService.auditNameMatching(emptyListAudit)
        false

      //No parties array, accountOwnerName not empty, isUsingSurnameFirstFormat true/false => fallback to accountOwnerName
      case (true, false, isUsingSurnameFirstFormat) =>
        handleNameMatchingFallback(bankAccountSummary, individualResponse, isUsingSurnameFirstFormat, bankDescription)

      //Parties array exists, accountOwnerName not empty, isUsingSurnameFirstFormat false.
      //Some banks are sending name instead of fullLegalName in parties array => fallback to accountOwnerName for when the name is missing.
      case (false, false, false) if isUsingPartiesName && bankAccountSummary.parties.getOrElse(List.empty).exists(_.name.isEmpty) =>
        handleNameMatchingFallback(bankAccountSummary, individualResponse, isUsingSurnameFirstFormat = false, bankDescription)

      //Parties array exists, accountOwnerName not empty, isUsingSurnameFirstFormat false.
      //This is fallback to accountOwnerName for when the fullLegalName in parties array is missing.
      case (false, false, false) if !isUsingPartiesName && bankAccountSummary.parties.getOrElse(List.empty).exists(_.fullLegalName.isEmpty) =>
        handleNameMatchingFallback(bankAccountSummary, individualResponse, isUsingSurnameFirstFormat = false, bankDescription)

      case (false, _, _) =>
        //There is a parties list to iterate through.
        val matchingResponseAndEcospendNameList: Seq[((NameMatchingResponse, NameMatchingAudit), String)] =
          bankAccountSummary
            .parties
            .getOrElse(List.empty)
            .map { party =>

              //Some banks do not send fullLegalName in the parties array, but name only.
              val ecospendFullName = if (isUsingPartiesName) {
                getEcospendPartyName(party.name.map(_.value), "party.name", bankId)
              } else {
                getEcospendPartyName(party.fullLegalName.map(_.value), "party.fullLegalName", bankId)
              }

              val ecospendNameWithoutTitle = NameParsingUtil.removeTitleFromName(appConfig.NameParsing.titles, ecospendFullName)

              (NameMatchingService.fuzzyNameMatching(
                individualResponse.firstForename,
                individualResponse.secondForename,
                individualResponse.surname,
                ecospendNameWithoutTitle,
                bankDescription
              ), ecospendFullName)
            }

        matchingResponseAndEcospendNameList.foreach{ response =>
          val (matchingResponse, ecoName) = response
          auditService.auditNameMatching(matchingResponse._2.copy(rawBankName      = Some(ecoName), partiesArrayUsed = true))
        }

        matchingResponseAndEcospendNameList.exists(matchingResponse => matchingResponse._1._1.isSuccess)
    }
  }

  private def getEcospendPartyName(maybeName: Option[String], fieldName: String, bankId: String)(implicit request: JourneyRequest[_]): String = {
    maybeName.getOrElse {
      Errors.throwServerErrorException(s"Expected $fieldName, but was None for bankId: $bankId")
    }
  }

  private def handleNameMatchingFallback(
      bankAccountSummary:        BankAccountSummary,
      individualResponse:        TracedIndividual,
      isUsingSurnameFirstFormat: Boolean,
      bankDescription:           BankDescription
  )(implicit request: JourneyRequest[_]): Boolean = {
    JourneyLogger.info(s"No parties list, falling back to account owner name - isUsingSurnameFirstFormat - ${isUsingSurnameFirstFormat.toString}")
    val accountName = bankAccountSummary.accountOwnerName.getOrElse(BankAccountOwnerName("fallback name error")).value
    val nameWithoutTitle = NameParsingUtil.removeTitleFromName(appConfig.NameParsing.titles, accountName)
    val names = if (isUsingSurnameFirstFormat)
      NameParsingUtil.bankIdBasedAccountNameParsing(nameWithoutTitle)
    else
      nameWithoutTitle.split(",").toSeq //For joint accounts, account owner names can be comma-delimited strings

    val matchingResponseList: Seq[(NameMatchingResponse, NameMatchingAudit)] = names.map { accountName =>
      NameMatchingService.fuzzyNameMatching(
        individualResponse.firstForename,
        individualResponse.secondForename,
        individualResponse.surname,
        accountName,
        bankDescription
      )
    }

    matchingResponseList.foreach(nameAudit => auditService.auditNameMatching(nameAudit._2.copy(rawBankName      = Some(accountName), partiesArrayUsed = false)))
    matchingResponseList.exists(_._1.isSuccess)
  }

  private def next(
      journey:                       Journey,
      isValidEventValue:             EventValue,
      bankDetailsRiskResultResponse: GetBankDetailsRiskResultResponse,
      bankAccountSummary:            BankAccountSummary,
      consentStatus:                 ConsentStatus,
      didAnyNameMatch:               Boolean
  )(implicit request: RequestHeader): Future[(Result, Journey)] = (isValidEventValue, consentStatus, didAnyNameMatch) match {
    case (_, ConsentStatus.Failed, _) => Future.successful {
      JourneyLogger.info(s"User failed consent flow. [ConsentStatus: Failed]")
      (
        Redirect(routes.RefundRequestNotSubmittedController.get),
        journey.copy(hasFinished = HasFinished.YesRefundNotSubmitted)
      )
    }
    case (_, ConsentStatus.Canceled, _) => Future.successful {
      JourneyLogger.info(s"User Canceled consent flow. [ConsentStatus: Canceled]")
      (
        Redirect(routes.RefundRequestNotSubmittedController.get),
        journey.copy(hasFinished = HasFinished.YesRefundNotSubmitted)
      )
    }
    case (_, _, false) => Future.successful {
      JourneyLogger.info(s"Ecospend names failed matching against NPS name")
      auditService.auditBankClaimAttempt(
        journey        = journey,
        actionsOutcome = BankActionsOutcome(
          ecospendFraudCheckIsSuccessful = isEcospendFraudCheckIsSuccessful(isValidEventValue),
          hmrcFraudCheckIsSuccessful     = isHmrcFraudCheckSuccessful(journey.bankDetailsRiskResultResponse),
          fuzzyNameMatchingIsSuccessful  = Some(IsSuccessful.no)
        ),
        failureReasons = Some(Seq("Ecospend names failed matching against NPS name"))
      )

      (
        Redirect(routes.RefundRequestNotSubmittedController.get),
        journey.copy(hasFinished = HasFinished.YesRefundNotSubmitted)
      )
    }
    case (EventValue.NotReceived, ConsentStatus.Authorised, _) => Future.successful(
      (
        Ok(views.verifyingYourBankAccountPage(Some(consentStatus), Some(journey.getBankConsent.id), Some(journey.getBankConsent.bankReferenceId))),
        journey
      )
    )
    case (EventValue.NotValid, ConsentStatus.Authorised, _) => Future.successful {
      JourneyLogger.info(s"Account assessment failed.")
      auditService.auditBankClaimAttempt(
        journey = journey,
        BankActionsOutcome(
          ecospendFraudCheckIsSuccessful = Some(IsSuccessful.no),
          hmrcFraudCheckIsSuccessful     = isHmrcFraudCheckSuccessful(journey.bankDetailsRiskResultResponse),
          fuzzyNameMatchingIsSuccessful  = Some(IsSuccessful(didAnyNameMatch))
        ),
        failureReasons = Some(Seq("Account assessment failed."))
      )

      (
        Redirect(routes.RefundRequestNotSubmittedController.get),
        journey.update(HasFinished.YesRefundNotSubmitted)
      )
    }
    case (EventValue.Valid, ConsentStatus.Authorised, true) =>
      JourneyLogger.info(s"Account assessment succeeded.")
      bankDetailsRiskResultResponse.overallRiskResult.nextAction match {
        case NextAction.DoNotPay => handleDoNotPay(journey)
        case NextAction.Pay      => handlePay(journey, bankAccountSummary)
      }
    case (_, consentStatus, _) =>
      Errors.throwServerErrorException(s"Unexpected [consentStatus: ${consentStatus.toString}]. Expected status to be one of [Authorised, Canceled, Failed].")
  }

  private def handleDoNotPay(journey: Journey)(implicit request: RequestHeader): Future[(Result, Journey)] = {
    journey.getBankAccountSummary.accountIdentification match {
      case Some(accountIdentification) => {
        val suspendOverpaymentRequest = SuspendOverpaymentRequest(
          paymentNumber            = journey.getP800Reference.sanitiseReference,
          currentOptimisticLock    = journey.getP800ReferenceChecked.currentOptimisticLock,
          reconciliationIdentifier = journey.getP800ReferenceChecked.reconciliationIdentifier,
          associatedPayableNumber  = journey.getP800ReferenceChecked.associatedPayableNumber,
          payeeBankAccountNumber   = accountIdentification.asPayeeBankAccountNumber,
          payeeBankSortCode        = accountIdentification.asPayeeBankSortCode,
          payeeBankAccountName     = PayeeBankAccountName(journey.getBankDescription.friendlyName.value).sanitisePayeeBankAccountName,
          designatedPayeeAccount   = DesignatedPayeeAccount(false)
        )

        for {
          _ <- if (appConfig.FeatureFlags.isCaseManagementEnabled) notifyCaseManagement(journey) else Future.successful(())
          _ <- p800RefundsBackendConnector.suspendOverpayment(journey.getNino, suspendOverpaymentRequest, journey.correlationId)
        } yield {
          auditService.auditBankClaimAttempt(
            journey        = journey,
            actionsOutcome = BankActionsOutcome(
              ecospendFraudCheckIsSuccessful = Some(IsSuccessful.yes),
              fuzzyNameMatchingIsSuccessful  = Some(IsSuccessful.yes),
              hmrcFraudCheckIsSuccessful     = Some(IsSuccessful.no)
            ),
            failureReasons = Some(Seq("EDH indicated DoNotPay"))
          )

          (
            Redirect(routes.RequestReceivedController.getBankTransfer),
            journey.update(hasFinished = HasFinished.YesSentToCaseManagement)
          )
        }
      }
      case None => {
        Future.successful(
          (
            Redirect(routes.RefundRequestNotSubmittedController.get),
            journey.update(hasFinished = HasFinished.YesRefundNotSubmitted)
          )
        )
      }
    }
  }

  private def handlePay(journey: Journey, bankAccountSummary: BankAccountSummary)(implicit request: RequestHeader): Future[(Result, Journey)] = {
    bankAccountSummary.accountIdentification match {
      case Some(_) =>
        makeBacsRepayment(journey, bankAccountSummary).map{
          case Right(_) =>
            auditService.auditBankClaimAttempt(journey, BankActionsOutcome(
              ecospendFraudCheckIsSuccessful = Some(IsSuccessful.yes),
              fuzzyNameMatchingIsSuccessful  = Some(IsSuccessful.yes),
              hmrcFraudCheckIsSuccessful     = Some(IsSuccessful.yes),
              claimOverpaymentIsSuccessful   = Some(IsSuccessful.yes)
            ))

            (
              Redirect(routes.RequestReceivedController.getBankTransfer),
              journey.update(hasFinished = HasFinished.YesSucceeded)
            )
          case Left(_) =>
            (
              Redirect(routes.RefundRequestNotSubmittedController.get),
              journey.update(hasFinished = HasFinished.YesRefundNotSubmitted)
            )
        }
      case None =>
        Future.successful(
          (
            Redirect(routes.RefundRequestNotSubmittedController.get),
            journey.update(hasFinished = HasFinished.YesRefundNotSubmitted)
          )
        )
    }
  }

  private def obtainIsValid(journey: Journey, bankAccountSummary: BankAccountSummary)(implicit request: Request[_]): Future[EventValue] = {
    JourneyLogger.debug("Obtaining EventValue ...")
    journey.isValidEventValue match {
      case None | Some(EventValue.NotReceived) =>
        p800RefundsExternalApiConnector.isValid(bankAccountSummary.id).map { ev =>
          JourneyLogger.info(s"Received EventValue from backend: [${ev.toString}]")
          ev
        }
      case Some(ev) => Future.successful {
        JourneyLogger.info(s"Got EventValue from journey: [${ev.toString}]")
        ev
      }
    }
  }

  private def obtainBankAccountSummary(journey: Journey)(implicit request: RequestHeader): Future[BankAccountSummary] = {
    JourneyLogger.debug("Obtaining BankAccountSummary ...")
    journey
      .bankAccountSummary
      .map(Future.successful)
      .getOrElse(ecospendService.getAccountSummary(journey))
  }

  private def notifyCaseManagement(journey: Journey)(implicit requestHeader: RequestHeader): Future[Unit] = {

    val bankAccountSummary: BankAccountSummary = journey.getBankAccountSummary
    val accountNumber: BankAccountNumber = bankAccountSummary.getAccountIdentification.asBankAccountNumber
    val sortCode: BankSortCode = bankAccountSummary.getAccountIdentification.asBankSortCode
    val bankAccountName: BankAccountName =
      BankAccountName(journey.getBankDescription.friendlyName.value)
        .sanitiseBankAccountName

    val bankDetailsRiskResult: GetBankDetailsRiskResultResponse = journey.getBankDetailsRiskResultResponse
    val clientUId: ClientUId = ClientUId(bankDetailsRiskResult.header.transactionID.value)

    val request: CaseManagementRequest = {
      val r: CaseManagementRequest = CaseManagementRequest(
        clientUId             = clientUId,
        clientSystemId        = ClientSystemId("MDTP"),
        nino                  = journey.getNino,
        bankSortCode          = sortCode,
        bankAccountNumber     = accountNumber,
        bankAccountName       = bankAccountName,
        designatedAccountFlag = 1, // TODO: Confirm what this should be
        contact               = List(
          CaseManagementContact(
            `type`    = PersonType.Customer,
            firstName = journey.getTraceIndividualResponse.firstForename.getOrElse(""),
            surname   = journey.getTraceIndividualResponse.surname.getOrElse(""),
            address   = List(
              CaseManagementAddress(
                `type`       = NPSAddress,
                addressLine1 = journey.getTraceIndividualResponse.addressLine1,
                addressLine2 = journey.getTraceIndividualResponse.addressLine2,
                postcode     = journey.getTraceIndividualResponse.addressPostcode
              )
            )
          )
        ),
        currency              = bankAccountSummary.currency,
        paymentAmount         = journey.getAmount.inPounds.setScale(2, RoundingMode.DOWN),
        overallRiskResult     = bankDetailsRiskResult.overallRiskResult.ruleScore,
        ruleResults           = Some(
          bankDetailsRiskResult.riskResults.getOrElse(List.empty).map(risk =>
            CaseManagementRuleResult(
              ruleId          = Some(risk.ruleId),
              ruleInformation = risk.ruleInformation,
              ruleScore       = Some(risk.ruleScore)
            ))
        ),
        nameMatches           = None,
        addressMatches        = None,
        accountExists         = None,
        subjectHasDeceased    = None,
        nonConsented          = None,
        reconciliationId      = journey.getP800ReferenceChecked.reconciliationIdentifier,
        taxDistrictNumber     = journey.getP800ReferenceChecked.taxDistrictNumber,
        payeNumber            = journey.getP800ReferenceChecked.payeNumber
      )

      r.validate.fold(())(validationProblem =>
        JourneyLogger.warn(s"Internal validation of CaseManagementRequest failed: [$validationProblem]"))
      r
    }

    p800RefundsBackendConnector
      .notifyCaseManagement(clientUId, request, journey.correlationId)
      .map(_ => ())
  }

  private def makeBacsRepayment(journey: Journey, bankAccountSummary: BankAccountSummary)(implicit request: RequestHeader): Future[Either[Unit, Unit]] = {
    val p800ReferenceCheckResult: P800ReferenceChecked = journey.getP800ReferenceChecked

    val makeBacsRepaymentRequest: MakeBacsRepaymentRequest = MakeBacsRepaymentRequest(
      paymentNumber            = journey.getP800Reference.sanitiseReference,
      currentOptimisticLock    = p800ReferenceCheckResult.currentOptimisticLock,
      reconciliationIdentifier = p800ReferenceCheckResult.reconciliationIdentifier,
      associatedPayableNumber  = p800ReferenceCheckResult.associatedPayableNumber,
      payeeBankAccountNumber   = bankAccountSummary.getAccountIdentification.asPayeeBankAccountNumber,
      payeeBankSortCode        = bankAccountSummary.getAccountIdentification.asPayeeBankSortCode,
      payeeBankAccountName     = PayeeBankAccountName(journey.getBankDescription.friendlyName.value).sanitisePayeeBankAccountName,
      designatedPayeeAccount   = DesignatedPayeeAccount(true)
    )

    p800RefundsBackendConnector
      .makeBacsRepayment(journey.getNino, makeBacsRepaymentRequest, journey.correlationId)
      .recover {
        case err @ (_: UpstreamErrorResponse | _: HttpException) =>
          auditService.auditBankClaimAttempt(
            journey        = journey,
            actionsOutcome = BankActionsOutcome(
              ecospendFraudCheckIsSuccessful = Some(IsSuccessful.yes),
              fuzzyNameMatchingIsSuccessful  = Some(IsSuccessful.yes),
              hmrcFraudCheckIsSuccessful     = Some(IsSuccessful.yes),
              claimOverpaymentIsSuccessful   = Some(IsSuccessful.no)
            ),
            failureReasons = Some(Seq(err.getMessage))
          )

          throw err
      }
      .map(_ => Right(()))
  }

  private def isEcospendFraudCheckIsSuccessful(eventValue: EventValue): Option[IsSuccessful] = eventValue match {
    case EventValue.Valid       => Some(IsSuccessful.yes)
    case EventValue.NotValid    => Some(IsSuccessful.no)
    case EventValue.NotReceived => None
  }

  private def isHmrcFraudCheckSuccessful(bankDetailsRiskResultResponse: Option[GetBankDetailsRiskResultResponse]): Option[IsSuccessful] =
    bankDetailsRiskResultResponse match {
      case Some(bankDetailsRiskResultResponse) => {
        bankDetailsRiskResultResponse.overallRiskResult.nextAction match {
          case NextAction.DoNotPay => Some(IsSuccessful.no)
          case NextAction.Pay      => Some(IsSuccessful.yes)
        }
      }
      case _ => None
    }

  @inline private def sanityChecks(consent_id: Option[ConsentId], bank_reference_id: Option[BankReferenceId], journey: Journey)(implicit request: RequestHeader): Unit = {
    Errors.require(
      journey.getJourneyType === JourneyType.BankTransfer,
      "This endpoint supports only BankTransfer journey"
    )

    consent_id.fold(
      Errors.throwBadRequestException("This endpoint requires a valid 'consent_id' query parameter")
    ){ consentId: ConsentId =>
        Errors.require(
          journey.getBankConsent.id === consentId,
          "The 'consent_id' supplied via the query parameter must match that stored in the journey. This should be investigated"
        )
      }

    bank_reference_id.fold(
      Errors.throwBadRequestException("This endpoint requires a valid 'bank_reference_id' query parameter")
    ) { bankReferenceId: BankReferenceId =>
        Errors.require(
          journey.getBankConsent.bankReferenceId === bankReferenceId,
          "The 'bank_reference_id' supplied via the query parameter must match that stored in the journey. This should be investigated"
        )
      }

  }

}
