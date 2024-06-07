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
import models.ecospend.account.BankAccountSummaryResponse
import models.ecospend.consent.{BankConsentRequest, BankConsentResponse, ConsentId}
import models.ecospend.verification.{BankVerification, BankVerificationRequest}
import models.ecospend.{EcospendAccessToken, EcospendGetBanksResponse}
import play.api.mvc.RequestHeader
import requests.RequestSupport
import uk.gov.hmrc.http.HeaderNames
import uk.gov.hmrc.http.HttpReads.Implicits._
import util.JourneyLogger
import uk.gov.hmrc.http.StringContextOps
import uk.gov.hmrc.http.client.HttpClientV2
import play.api.libs.json.Json

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EcospendConnector @Inject() (
    appConfig:      AppConfig,
    httpClient:     HttpClientV2,
    requestSupport: RequestSupport
)(implicit ec: ExecutionContext) {

  import requestSupport._

  private val banksUrl: String = appConfig.ExternalApiCalls.ecospendUrl + "/banks"

  def getListOfAvailableBanks(accessToken: EcospendAccessToken)(implicit request: JourneyRequest[_]): Future[EcospendGetBanksResponse] = captureException {
    httpClient
      .get(url"$banksUrl")
      .withProxy
      .setHeader(authorizationHeader(accessToken))
      .execute[EcospendGetBanksResponse]
  }

  private val validateUrl: String = appConfig.ExternalApiCalls.ecospendUrl + "/validate"

  def validate(
      accessToken:             EcospendAccessToken,
      bankVerificationRequest: BankVerificationRequest
  )(implicit journeyRequest: JourneyRequest[_]): Future[BankVerification] = captureException {
    httpClient
      .post(url"$validateUrl")
      .withProxy
      .setHeader(authorizationHeader(accessToken))
      .withBody(Json.toJson(bankVerificationRequest))
      .execute[BankVerification]
  }

  private val createConsentUrl: String = appConfig.ExternalApiCalls.ecospendUrl + "/consents"

  def createConsent(
      accessToken:        EcospendAccessToken,
      bankConsentRequest: BankConsentRequest
  )(implicit request: JourneyRequest[_]): Future[BankConsentResponse] = captureException {
    httpClient
      .post(url"$createConsentUrl")
      .withProxy
      .setHeader(authorizationHeader(accessToken))
      .withBody(Json.toJson(bankConsentRequest))
      .execute[BankConsentResponse]
  }

  private val accountSummaryUrl: String = appConfig.ExternalApiCalls.ecospendUrl + "/accounts/summary"

  def getAccountSummary(
      accessToken: EcospendAccessToken,
      consentId:   ConsentId
  )(implicit request: RequestHeader): Future[BankAccountSummaryResponse] = captureException {
    httpClient
      .get(url"$accountSummaryUrl")
      .withProxy
      .setHeader(authorizationHeader(accessToken))
      .setHeader(consentIdHeader(consentId): _*)
      .execute[BankAccountSummaryResponse]
  }

  private def authorizationHeader(accessToken: EcospendAccessToken): (String, String) =
    HeaderNames.authorisation -> s"Bearer ${accessToken.token}"

  private val consentIdHeaderKey: String = "consent_id"
  private val developmentConsentIdHeaderKey: String = "consent-id"

  /**
   * Platform drops HTTP headers containing underscore characters.
   * As a workaround for testing a duplicate header is sent without an underscore.
   * Outbound requests should not be affected.
   */
  private def consentIdHeader(consentId: ConsentId): Seq[(String, String)] = {
    val consentIdValue: String = consentId.value
    Seq(
      consentIdHeaderKey -> consentIdValue,
      developmentConsentIdHeaderKey -> consentIdValue
    )
  }

  private def captureException[A](future: => Future[A])(implicit request: RequestHeader): Future[A] =
    future.recover {
      case ex =>
        JourneyLogger.warn(s"Ecospend call failed with exception: ${ex.toString}")
        throw ex
    }
}

