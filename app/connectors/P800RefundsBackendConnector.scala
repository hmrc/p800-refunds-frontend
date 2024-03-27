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
import nps.models.ReferenceCheckResult
import play.api.mvc.RequestHeader
import requests.RequestSupport
import uk.gov.hmrc.http.HttpClient
import uk.gov.hmrc.http.HttpReads.Implicits._

import java.util.{Base64, UUID}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class P800RefundsBackendConnector @Inject() (
    appConfig:  AppConfig,
    httpClient: HttpClient
)(implicit executionContext: ExecutionContext) {

  import RequestSupport.hc

  private def url(nino: Nino, p800Reference: P800Reference): String = appConfig.P800RefundsBackend.p800RefundsBackendBaseUrl +
    s"/nps-json-service/nps/v1/api/reconciliation/p800/${nino.value}/${p800Reference.value}"

  def p800ReferenceCheck(nino: Nino, p800Reference: P800Reference)(implicit requestHeader: RequestHeader): Future[ReferenceCheckResult] = {
    val sanitisedP800Reference = p800Reference.sanitiseReference
    httpClient
      .GET[ReferenceCheckResult](
        url     = url(nino, sanitisedP800Reference),
        headers = makeHeadersForNps()
      )
  }

  //TODO: we should probably just move all of this into backend once all NPS APIs migrated
  def makeHeadersForNps(): Seq[(String, String)] = Seq(
    authorisationHeader,
    makeCorrelationIdHeader(),
    makeOriginatorIdHeader()
  )

  //this should probably just live in backend, we don't really need it here TODO move it/delete soon
  private val authorisationHeader: (String, String) = {
      def encodeString(input: String): String = {
        val encoder: Base64.Encoder = Base64.getEncoder
        val encodedBytes = encoder.encode(input.getBytes("UTF-8"))
        new String(encodedBytes, "UTF-8")
      }
    val credentials = s"${appConfig.P800RefundsBackend.npsUsername}:${appConfig.P800RefundsBackend.npsPassword}"
    val credentialsEncoded = encodeString(credentials)
    "Authorization" -> s"Basic $credentialsEncoded"
  }

  //TODO: update this to use correlationId from journey object when we've done that ticket (OPS-11777)
  private def makeCorrelationIdHeader(): (String, String) = {
    "CorrelationId" -> UUID.randomUUID().toString
  }

  //this should probably just live in backend, we don't really need it here TODO move it/delete soon
  private def makeOriginatorIdHeader(): (String, String) = {
    "gov-uk-originator-id" -> "DA2_MRA_DIGITAL"
  }

}
