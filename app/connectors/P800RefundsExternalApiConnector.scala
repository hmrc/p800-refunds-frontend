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
import models.ecospend.consent.ConsentId
import models.p800externalapi.EventValue
import play.api.mvc.RequestHeader
import requests.RequestSupport
import uk.gov.hmrc.http.HttpClient
import uk.gov.hmrc.http.HttpReads.Implicits._
import util.JourneyLogger

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class P800RefundsExternalApiConnector @Inject() (
    appConfig:  AppConfig,
    httpClient: HttpClient
)(implicit executionContext: ExecutionContext) {

  import RequestSupport.hc

  private def isValidUrl(consentId: ConsentId): String =
    appConfig.P800RefundsExternalApi.p800RefundsExternalApiBaseUrl + s"/is-valid/${consentId.value}"

  def isValid(consentId: ConsentId)(implicit request: RequestHeader): Future[EventValue] = {
    JourneyLogger.debug(s"checking if isValid ... [url:${isValidUrl(consentId)}]")
    httpClient.GET[EventValue](isValidUrl(consentId))
  }

}
