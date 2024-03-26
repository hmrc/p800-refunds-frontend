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

package nps

import _root_.models.{Nino, P800Reference}
import nps.models.IssuePayableOrderRequest
import play.api.http.Status.{BAD_GATEWAY, INTERNAL_SERVER_ERROR}
import play.api.mvc.RequestHeader
import requests.RequestSupport._
import uk.gov.hmrc.http.{HttpClient, HttpErrorFunctions, HttpReads, UpstreamErrorResponse}
import util.JourneyLogger

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

/**
 * This connector will call Nps' Issue Payable Order API endpoint.
 */
@Singleton
class IssuePayableOrderConnector @Inject() (
    npsConfig:  NpsConfig,
    httpClient: HttpClient
)(implicit ec: ExecutionContext) {

  private def url(nino: Nino, p800Reference: P800Reference): String = npsConfig.baseUrl +
    s"/nps-json-service/nps/v1/api/accounting/issue-payable-order/${nino.value}/${p800Reference.sanitiseReference.value}"

  def issuePayableOrder(nino: Nino, p800Reference: P800Reference, issuePayableOrderRequest: IssuePayableOrderRequest)(implicit requestHeader: RequestHeader): Future[Unit] = {
    JourneyLogger.info("Issuing payable order")

    implicit val readUnit: HttpReads[Unit] = HttpReads.ask.map {
      case (method, url, response) => response.status match {
        case 200 => ()
        case other => throw UpstreamErrorResponse(
          message    = HttpErrorFunctions.upstreamResponseMessage(method, url, other, response.body),
          statusCode = response.status,
          reportAs   = if (HttpErrorFunctions.is4xx(response.status)) INTERNAL_SERVER_ERROR else BAD_GATEWAY,
          headers    = response.headers
        )
      }
    }

    httpClient.PUT[IssuePayableOrderRequest, Unit](
      url     = url(nino, p800Reference),
      body    = issuePayableOrderRequest,
      headers = npsConfig.makeHeadersForNps()
    )
  }
}
