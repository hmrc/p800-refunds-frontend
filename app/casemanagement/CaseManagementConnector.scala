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

package casemanagement

import play.api.mvc.RequestHeader
import requests.RequestSupport._
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.{HttpClient, HttpResponse, UpstreamErrorResponse}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import util.JourneyLogger

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import java.util.{Base64, UUID}

@Singleton
class CaseManagementConnector @Inject() (
    httpClient:     HttpClient,
    servicesConfig: ServicesConfig
)(implicit ec: ExecutionContext) {

  private val baseUrl: String = servicesConfig.baseUrl("casemanagement")
  private def notifyCaseManagementUrl(clientUId: ClientUId): String = baseUrl +
    s"/risking/exceptions/${clientUId.value}"

  def notifyCaseManagement(
      clientUId: ClientUId,
      request:   CaseManagementRequest
  )(implicit requestHeader: RequestHeader): Future[Unit] = {

    JourneyLogger.info(s"Notifying case management: ${clientUId.value}")

    httpClient
      .POST[CaseManagementRequest, Either[UpstreamErrorResponse, HttpResponse]](
        url     = notifyCaseManagementUrl(clientUId),
        body    = request,
        headers = makeHeaders()
      ).map {
          case Right(_)    => ()
          case Left(error) => throw error
        }
  }

  private val username: String = servicesConfig.getString("microservice.services.casemanagement.username")
  private val password: String = servicesConfig.getString("microservice.services.casemanagement.password")

  private val authorisationHeader: (String, String) = {
      def encodeString(input: String): String = {
        val encoder: Base64.Encoder = Base64.getEncoder
        val encodedBytes = encoder.encode(input.getBytes("UTF-8"))
        new String(encodedBytes, "UTF-8")
      }
    val credentials = s"${username}:${password}"
    val credentialsEncoded = encodeString(credentials)
    "Authorization" -> s"Basic $credentialsEncoded"
  }
  private val environment: String = servicesConfig.getString("microservice.services.casemanagement.environment")

  private def makeHeaders(): Seq[(String, String)] = Seq(
    authorisationHeader,
    "CorrelationId" -> UUID.randomUUID().toString,
    "Environment" -> environment
  )

}
