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

package edh

import play.api.mvc.RequestHeader
import requests.RequestSupport._
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.HttpClient
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import util.JourneyLogger

import java.util.UUID
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EdhConnector @Inject() (
    httpClient:     HttpClient,
    servicesConfig: ServicesConfig
)(implicit ec: ExecutionContext) {

  private val baseUrl: String = servicesConfig.baseUrl("edh")

  private def url(claimId: ClaimId): String = baseUrl +
    s"/risking/claims/${claimId.value}/bank-details"

  def getBankDetailsRiskResult(claimId: ClaimId, request: GetBankDetailsRiskResultRequest)(implicit requestHeader: RequestHeader): Future[GetBankDetailsRiskResultResponse] = {
    JourneyLogger.info(s"calling EDH ${claimId.toString}...")
    httpClient.POST[GetBankDetailsRiskResultRequest, GetBankDetailsRiskResultResponse](
      url     = url(claimId),
      body    = request,
      headers = makeHeaders()

    ).map { response: GetBankDetailsRiskResultResponse =>
        JourneyLogger.info(s"calling EDH ${claimId.toString} succeeded: [NextAction=${response.overallRiskResult.nextAction.toString}]")
        response
      }

  }

  private val bearerToken: String = servicesConfig.getString("microservice.services.edh.bearerToken")
  private val environment: String = servicesConfig.getString("microservice.services.edh.environment")

  private def makeHeaders(): Seq[(String, String)] = Seq(
    "Authorization" -> s"Bearer $bearerToken",
    "CorrelationId" -> UUID.randomUUID().toString,
    "Environment" -> environment,
    "RequesterId" -> "Repayment Service"
  )
}
