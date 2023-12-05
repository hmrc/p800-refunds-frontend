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
import models.IdentityVerificationRequest
import play.api.mvc.RequestHeader
import requests.RequestSupport._
import uk.gov.hmrc.http.{HttpClient, HttpResponse}
import uk.gov.hmrc.http.HttpReads.Implicits._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

//todo maybe rename this to NPS connector or something?
@Singleton
class IdentityVerificationConnector @Inject() (
    appConfig:  AppConfig,
    httpClient: HttpClient
)(implicit ec: ExecutionContext) {

  //todo update the url once we know what it is from the spec.
  private val identityVerificationUrl: String = appConfig.ExternalApiCalls.npsBaseUrl + "/verify-identity"

  def verifyIdentity(identityVerificationRequest: IdentityVerificationRequest)(implicit requestHeader: RequestHeader): Future[HttpResponse] = {
    httpClient.POST[IdentityVerificationRequest, HttpResponse](identityVerificationUrl, identityVerificationRequest)
  }

}
