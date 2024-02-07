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

import action.JourneyRequest
import config.AppConfig
import models.ecospend.verification.{BankVerification, BankVerificationRequest}
import models.ecospend.{EcospendAccessToken, EcospendGetBanksResponse}
import models.ecospend.consent.{BankConsentRequest, BankConsentResponse}
import requests.RequestSupport
import uk.gov.hmrc.http.HttpClient
import uk.gov.hmrc.http.HttpReads.Implicits._
import util.JourneyLogger

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EcospendConnector @Inject() (
    appConfig:      AppConfig,
    httpClient:     HttpClient,
    requestSupport: RequestSupport
)(implicit ec: ExecutionContext) {

  import requestSupport._

  private val banksUrl: String = appConfig.ExternalApiCalls.ecospendUrl + "/banks"

  def getListOfAvailableBanks(accessToken: EcospendAccessToken)(implicit request: JourneyRequest[_]): Future[EcospendGetBanksResponse] = captureException {
    httpClient.GET[EcospendGetBanksResponse](
      url     = banksUrl,
      headers = authorizationHeader(accessToken)
    )
  }

  private val validateUrl: String = appConfig.ExternalApiCalls.ecospendUrl + "/validate"

  def validate(
      accessToken:             EcospendAccessToken,
      bankVerificationRequest: BankVerificationRequest
  )(implicit journeyRequest: JourneyRequest[_]): Future[BankVerification] = captureException {
    httpClient.POST[BankVerificationRequest, BankVerification](
      url     = validateUrl,
      body    = bankVerificationRequest,
      headers = authorizationHeader(accessToken)
    )
  }

  private val createConsentUrl: String = appConfig.ExternalApiCalls.ecospendUrl + "/consents"

  def createConsent(
      accessToken:        EcospendAccessToken,
      bankConsentRequest: BankConsentRequest
  )(implicit request: JourneyRequest[_]): Future[BankConsentResponse] = captureException {
    httpClient.POST[BankConsentRequest, BankConsentResponse](
      url     = createConsentUrl,
      body    = bankConsentRequest,
      headers = authorizationHeader(accessToken)
    )
  }

  private def captureException[A](future: => Future[A])(implicit request: JourneyRequest[_]): Future[A] =
    future.recover {
      case ex =>
        JourneyLogger.warn(s"Ecospend call failed with exception: ${ex.toString}")
        throw ex
    }

  private def authorizationHeader(accessToken: EcospendAccessToken) =
    Seq(("Authorization", s"Bearer ${accessToken.token}"))
}

