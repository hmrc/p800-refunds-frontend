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
import models.{CorrelationId, Nino, P800Reference}
import nps.models._
import play.api.mvc.RequestHeader
import requests.RequestSupport.hc
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.{HttpClient, HttpReads, UpstreamErrorResponse, HttpResponse}
import util.{HttpResponseUtils, JourneyLogger}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class P800RefundsBackendConnector @Inject() (
    appConfig:  AppConfig,
    httpClient: HttpClient
)(implicit executionContext: ExecutionContext) {

  private val baseUrl: String = appConfig.P800RefundsBackend.p800RefundsBackendBaseUrl + "/p800-refunds-backend"

  private def makeHeaders(correlationId: CorrelationId): Seq[(String, String)] = Seq(
    P800RefundsBackendConnector.makeCorrelationIdHeader(correlationId)
  )

  def validateP800Reference(nino: Nino, p800Reference: P800Reference, correlationId: CorrelationId)(implicit requestHeader: RequestHeader): Future[ValidateReferenceResult] = {
    httpClient
      .POST[ValidateP800ReferenceRequest, ValidateReferenceResult](
        url     = s"$baseUrl/nps/validate-p800-reference",
        body    = ValidateP800ReferenceRequest(nino, p800Reference.sanitiseReference),
        headers = makeHeaders(correlationId)
      )
  }

  def traceIndividual(traceIndividualRequest: TraceIndividualRequest, correlationId: CorrelationId)(implicit requestHeader: RequestHeader): Future[TraceIndividualResponse] = {
    httpClient
      .POST[TraceIndividualRequest, TraceIndividualResponse](
        url     = s"$baseUrl/nps/trace-individual",
        body    = traceIndividualRequest,
        headers = makeHeaders(correlationId)
      )
  }

  def makeBacsRepayment(
      nino:                     Nino,
      makeBacsRepaymentRequest: MakeBacsRepaymentRequest,
      correlationId:            CorrelationId
  )(implicit requestHeader: RequestHeader): Future[MakeBacsRepaymentResponse] = {
    JourneyLogger.info("Making Bacs repayment (Claiming overpayment)")
    httpClient.POST[MakeBacsRepaymentRequest, MakeBacsRepaymentResponse](
      url     = s"$baseUrl/nps/make-bacs-repayment/${nino.value}",
      body    = makeBacsRepaymentRequest,
      headers = makeHeaders(correlationId)
    )
  }

  def suspendOverpayment(
      nino:                      Nino,
      suspendOverpaymentRequest: SuspendOverpaymentRequest,
      correlationId:             CorrelationId
  )(implicit requestHeader: RequestHeader): Future[Unit] = {
    JourneyLogger.info("Suspending Overpayment...")

    implicit val readUnit: HttpReads[Unit] = HttpResponseUtils.httpReadsUnit

    httpClient.POST[SuspendOverpaymentRequest, Unit](
      url     = s"$baseUrl/nps/suspend-overpayment/${nino.value}",
      body    = suspendOverpaymentRequest,
      headers = makeHeaders(correlationId)
    )
  }

  def issuePayableOrder(
      nino:                     Nino,
      p800Reference:            P800Reference,
      issuePayableOrderRequest: IssuePayableOrderRequest,
      correlationId:            CorrelationId
  )(implicit requestHeader: RequestHeader): Future[Unit] = {
    JourneyLogger.info("Issuing payable order")

    implicit val readUnit: HttpReads[Unit] = HttpResponseUtils.httpReadsUnit

    httpClient.POST[IssuePayableOrderRequest, Unit](
      url     = s"$baseUrl/nps/issue-payable-order/${nino.value}/${p800Reference.sanitiseReference.value}",
      body    = issuePayableOrderRequest,
      headers = makeHeaders(correlationId)
    )
  }

  private def getBankDetailsRiskResultUrl(claimId: ClaimId): String = baseUrl +
    s"/risking/claims/${claimId.value}/bank-details"

  def getBankDetailsRiskResult(claimId: ClaimId, request: GetBankDetailsRiskResultRequest, correlationId: CorrelationId)(implicit requestHeader: RequestHeader): Future[GetBankDetailsRiskResultResponse] = {
    JourneyLogger.info(s"calling EDH ${claimId.toString}...")
    httpClient.POST[GetBankDetailsRiskResultRequest, GetBankDetailsRiskResultResponse](
      url     = getBankDetailsRiskResultUrl(claimId),
      body    = request,
      headers = makeHeaders(correlationId)
    ).map { response: GetBankDetailsRiskResultResponse =>
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
      .POST[CaseManagementRequest, Either[UpstreamErrorResponse, HttpResponse]](
        url     = notifyCaseManagementUrl(clientUId),
        body    = request,
        headers = makeHeaders(correlationId)
      ).map {
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
