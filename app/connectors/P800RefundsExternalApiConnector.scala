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
import models.p800externalapi.EventValue
import play.api.mvc.RequestHeader
import requests.RequestSupport
import uk.gov.hmrc.http.HttpReads.Implicits._
import util.JourneyLogger
import uk.gov.hmrc.http.StringContextOps
import uk.gov.hmrc.http.client.HttpClientV2

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class P800RefundsExternalApiConnector @Inject() (
    appConfig:  AppConfig,
    httpClient: HttpClientV2
)(implicit executionContext: ExecutionContext) {

  import RequestSupport.hc

  private def isValidUrl(recordId: UUID): String =
    appConfig.P800RefundsExternalApi.p800RefundsExternalApiBaseUrl + s"/is-valid/${recordId.toString}"

  def isValid(recordId: UUID)(implicit request: RequestHeader): Future[EventValue] = {
    val url = isValidUrl(recordId)
    JourneyLogger.debug(s"checking if isValid ... [url:${url}]")

    httpClient.get(url"$url").execute[EventValue]
  }

}
