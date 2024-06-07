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

package connectors

import casemanagement.{CaseManagementRequest, ClientUId}
import com.google.inject.{Inject, Singleton}
import config.AppConfig
import edh.{ClaimId, GetBankDetailsRiskResultRequest, GetBankDetailsRiskResultResponse}
import models.audit.IsSuccessful
import models.journeymodels.Journey
import models.{CorrelationId, Nino, P800Reference}
import nps.models._
import play.api.mvc.RequestHeader
import requests.RequestSupport.hc
import services.AuditService
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.{HttpReads, HttpResponse, UpstreamErrorResponse}
import util.{HttpResponseUtils, JourneyLogger}
import uk.gov.hmrc.http.StringContextOps
import uk.gov.hmrc.http.client.HttpClientV2
import play.api.libs.json.Json

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class P800RefundsBackendConnector @Inject() (
    appConfig:    AppConfig,
    auditService: AuditService,
    httpClient:   HttpClientV2
)(implicit executionContext: ExecutionContext) {

  private val baseUrl: String = appConfig.P800RefundsBackend.p800RefundsBackendBaseUrl + "/p800-refunds-backend"

  private def makeHeaders(correlationId: CorrelationId): Seq[(String, String)] = Seq(
    P800RefundsBackendConnector.makeCorrelationIdHeader(correlationId)
  )

  def validateP800Reference(nino: Nino, p800Reference: P800Reference, correlationId: CorrelationId)(implicit requestHeader: RequestHeader): Future[ValidateReferenceResult] = {
    httpClient
      .post(url"$baseUrl/nps/validate-p800-reference")
      .setHeader(makeHeaders(correlationId): _*)
      .withBody(Json.toJson(ValidateP800ReferenceRequest(nino, p800Reference)))
      .execute[ValidateReferenceResult]
  }

  def traceIndividual(traceIndividualRequest: TraceIndividualRequest, correlationId: CorrelationId)(implicit requestHeader: RequestHeader): Future[TracedIndividual] = {
    httpClient
      .post(url"$baseUrl/nps/trace-individual")
      .setHeader(makeHeaders(correlationId): _*)
      .withBody(Json.toJson(traceIndividualRequest))
      .execute[TracedIndividual]
  }

  def makeBacsRepayment(
      nino:                     Nino,
      makeBacsRepaymentRequest: MakeBacsRepaymentRequest,
      correlationId:            CorrelationId
  )(implicit requestHeader: RequestHeader): Future[MakeBacsRepaymentResponse] = {
    JourneyLogger.info("Making Bacs repayment (Claiming overpayment)")

    httpClient
      .post(url"$baseUrl/nps/make-bacs-repayment/${nino.value}")
      .setHeader(makeHeaders(correlationId): _*)
      .withBody(Json.toJson(makeBacsRepaymentRequest))
      .execute[MakeBacsRepaymentResponse]
  }

  def suspendOverpayment(
      nino:                      Nino,
      suspendOverpaymentRequest: SuspendOverpaymentRequest,
      correlationId:             CorrelationId
  )(implicit requestHeader: RequestHeader): Future[Unit] = {
    JourneyLogger.info("Suspending Overpayment...")

    implicit val readUnit: HttpReads[Unit] = HttpResponseUtils.httpReadsUnit

    httpClient
      .post(url"$baseUrl/nps/suspend-overpayment/${nino.value}")
      .setHeader(makeHeaders(correlationId): _*)
      .withBody(Json.toJson(suspendOverpaymentRequest))
      .execute[Unit]
  }

  def issuePayableOrder(
      nino:                     Nino,
      p800Reference:            P800Reference,
      issuePayableOrderRequest: IssuePayableOrderRequest,
      correlationId:            CorrelationId
  )(journey: Journey)(implicit requestHeader: RequestHeader): Future[Unit] = {
    JourneyLogger.info("Issuing payable order")

    implicit val readUnit: HttpReads[Unit] = HttpResponseUtils.httpReadsUnit

    httpClient
      .post(url"$baseUrl/nps/issue-payable-order/${nino.value}/${p800Reference.value.toString}")
      .setHeader(makeHeaders(correlationId): _*)
      .withBody(Json.toJson(issuePayableOrderRequest))
      .execute[Unit]
      .recoverWith { _ =>
        auditService.auditChequeClaimAttemptMade(journey, isSuccessful = IsSuccessful(false))(requestHeader, hc)
        throw new RuntimeException("Error when submitting Cheque issue payable order")
      }
  }

  private def getBankDetailsRiskResultUrl(claimId: ClaimId): String = baseUrl +
    s"/risking/claims/${claimId.value}/bank-details"

  def getBankDetailsRiskResult(claimId: ClaimId, request: GetBankDetailsRiskResultRequest, correlationId: CorrelationId)(implicit requestHeader: RequestHeader): Future[GetBankDetailsRiskResultResponse] = {
    JourneyLogger.info(s"calling EDH ${claimId.toString}...")
    httpClient
      .post(url"${getBankDetailsRiskResultUrl(claimId)}")
      .setHeader(makeHeaders(correlationId): _*)
      .withBody(Json.toJson(request))
      .execute[GetBankDetailsRiskResultResponse]
      .map { response: GetBankDetailsRiskResultResponse =>
        JourneyLogger.info(s"got BankDetailsRiskResult from EDH ${claimId.toString} succeeded: [NextAction=${response.overallRiskResult.nextAction.toString}]")
        response
      }
  }

  private def notifyCaseManagementUrl(clientUId: ClientUId): String = baseUrl +
    s"/risking/exceptions/${clientUId.value}"

  def notifyCaseManagement(
      clientUId:     ClientUId,
      request:       CaseManagementRequest,
      correlationId: CorrelationId
  )(implicit requestHeader: RequestHeader): Future[Unit] = {
    JourneyLogger.info(s"Notifying case management: ${clientUId.value}")

    httpClient
      .post(url"${notifyCaseManagementUrl(clientUId)}")
      .setHeader(makeHeaders(correlationId): _*)
      .withBody(Json.toJson(request))
      .execute[Either[UpstreamErrorResponse, HttpResponse]]
      .map {
        case Right(_)    => ()
        case Left(error) => throw error
      }
  }
}

object P800RefundsBackendConnector {
  private def makeCorrelationIdHeader(correlationId: CorrelationId): (String, String) = {
    "CorrelationId" -> correlationId.value.toString
  }
}
