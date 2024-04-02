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

import com.google.inject.{Inject, Singleton}
import config.AppConfig
import models.{Nino, P800Reference}
import nps.models.{ReferenceCheckResult, TraceIndividualRequest, TraceIndividualResponse}
import play.api.mvc.RequestHeader
import requests.RequestSupport.hc
import uk.gov.hmrc.http.HttpClient
import uk.gov.hmrc.http.HttpReads.Implicits._

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class P800RefundsBackendConnector @Inject() (
    appConfig:  AppConfig,
    httpClient: HttpClient
)(implicit executionContext: ExecutionContext) {

  private def referenceCheckUrl(nino: Nino, p800Reference: P800Reference): String = appConfig.P800RefundsBackend.p800RefundsBackendBaseUrl +
    s"/nps-json-service/nps/v1/api/reconciliation/p800/${nino.value}/${p800Reference.value}"

  def p800ReferenceCheck(nino: Nino, p800Reference: P800Reference)(implicit requestHeader: RequestHeader): Future[ReferenceCheckResult] = {
    val sanitisedP800Reference = p800Reference.sanitiseReference
    httpClient
      .GET[ReferenceCheckResult](
        url     = referenceCheckUrl(nino, sanitisedP800Reference),
        headers = Seq(P800RefundsBackendConnector.makeCorrelationIdHeader())
      )
  }

  private val traceIndividualUrl: String = appConfig.P800RefundsBackend.p800RefundsBackendBaseUrl +
    "/nps-json-service/nps/v1/api/individual/trace-individual?exactMatch=true&returnRealName=true"

  def traceIndividual(traceIndividualRequest: TraceIndividualRequest)(implicit requestHeader: RequestHeader): Future[TraceIndividualResponse] = {
    httpClient
      .POST[TraceIndividualRequest, TraceIndividualResponse](
        url     = traceIndividualUrl,
        body    = traceIndividualRequest,
        headers = Seq(P800RefundsBackendConnector.makeCorrelationIdHeader())
      )
  }

}

object P800RefundsBackendConnector {
  //TODO: update this to use correlationId from journey object when we've done that ticket (OPS-11777)
  private def makeCorrelationIdHeader(): (String, String) = {
    "CorrelationId" -> UUID.randomUUID().toString
  }
}
