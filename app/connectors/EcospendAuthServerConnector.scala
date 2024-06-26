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
import models.ecospend.EcospendAccessToken
import play.api.mvc.RequestHeader
import requests.RequestSupport
import uk.gov.hmrc.http.HttpReads.Implicits._
import util.JourneyLogger
import uk.gov.hmrc.http.StringContextOps
import uk.gov.hmrc.http.client.HttpClientV2

import java.time.Clock
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EcospendAuthServerConnector @Inject() (
    appConfig:      AppConfig,
    httpClient:     HttpClientV2,
    requestSupport: RequestSupport
)(implicit ec: ExecutionContext, clock: Clock) {

  import requestSupport._

  private val accessTokenUrl = appConfig.ExternalApiCalls.ecospendAuthServerUrl + "/connect/token"

  def accessToken(implicit request: RequestHeader): Future[EcospendAccessToken] = captureException {
    val body: Map[String, Seq[String]] = Map(
      "grant_type" -> "client_credentials",
      "client_id" -> appConfig.ExternalApiCalls.ecospendAuthClientId,
      "client_secret" -> appConfig.ExternalApiCalls.ecospendAuthClientSecret
    ).view.mapValues(Seq.apply(_)).toMap

    httpClient
      .post(url"$accessTokenUrl")
      .withProxy
      .withBody(body)
      .execute[EcospendAccessToken]
  }

  private def captureException[A](future: => Future[A])(implicit request: RequestHeader): Future[A] =
    future.recover {
      case ex =>
        JourneyLogger.warn(s"EcospendAuth call failed with exception: ${ex.toString}")
        throw ex
    }
}

