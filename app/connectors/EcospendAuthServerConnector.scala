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

package connectors

import config.AppConfig
import uk.gov.hmrc.http.HttpReads.Implicits._
import models.ecospend.EcospendAccessToken
import action.JourneyRequest
import requests.RequestSupport
import util.JourneyLogger

import java.time.Clock
import javax.inject.{Inject, Singleton}
import scala.concurrent.{Future, ExecutionContext}

@Singleton
class EcospendAuthServerConnector @Inject() (
    appConfig:      AppConfig,
    httpClient:     ProxyClient,
    requestSupport: RequestSupport
)(implicit ec: ExecutionContext, clock: Clock) {

  import requestSupport.hc

  private val accessTokenUrl = appConfig.ExternalApiCalls.ecospendAuthServerUrl + "/connect/token"

  def accessToken(implicit request: JourneyRequest[_]): Future[EcospendAccessToken] = captureException {
    val body: Map[String, Seq[String]] = Map(
      "grant_type" -> "client_credentials",
      "client_id" -> appConfig.ExternalApiCalls.ecospendAuthClientId,
      "client_secret" -> appConfig.ExternalApiCalls.ecospendAuthClientSecret,
      "scope" -> appConfig.ExternalApiCalls.ecospendAuthScope
    ).view.mapValues(Seq.apply(_)).toMap

    httpClient.POSTForm[EcospendAccessToken](accessTokenUrl, body)
  }

  private def captureException[A](future: => Future[A])(implicit request: JourneyRequest[_]): Future[A] =
    future.recover {
      case ex =>
        JourneyLogger.warn(s"EcospendAuth call failed with exception: ${ex.toString}")
        throw ex
    }
}

