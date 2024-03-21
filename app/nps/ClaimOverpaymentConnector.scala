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

package nps

import _root_.models.{Nino, P800Reference}
import nps.models.{ClaimOverpaymentRequest, ClaimOverpaymentResponse}
import play.api.mvc.RequestHeader
import requests.RequestSupport._
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.HttpClient
import util.JourneyLogger

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ClaimOverpaymentConnector @Inject() (
    npsConfig:  NpsConfig,
    httpClient: HttpClient
)(implicit ec: ExecutionContext) {

  private def url(nino: Nino, p800Reference: P800Reference): String = npsConfig.baseUrl +
    s"/nps-json-service/nps/v1/api/accounting/claim-overpayment/${nino.value}/${p800Reference.sanitiseReference.value}"

  def claimOverpayment(
      nino:                    Nino,
      p800Reference:           P800Reference,
      claimOverpaymentRequest: ClaimOverpaymentRequest
  )(implicit requestHeader: RequestHeader): Future[ClaimOverpaymentResponse] = {

    JourneyLogger.info("Claiming overpayment")

    val sanitisedP800Reference = p800Reference.sanitiseReference

    httpClient
      .PUT[ClaimOverpaymentRequest, ClaimOverpaymentResponse](
        url     = url(nino, sanitisedP800Reference),
        body    = claimOverpaymentRequest,
        headers = npsConfig.makeHeadersForNps()
      )
  }
}
