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

package config

import play.api.Configuration
import play.api.mvc.RequestHeader
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import java.net.URLEncoder
import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration.Infinite
import scala.concurrent.duration.FiniteDuration
import scala.util.Try

@Singleton
class AppConfig @Inject() (servicesConfig: ServicesConfig, configuration: Configuration) {

  object Timeout {
    val timeoutUrl: String = readConfigAsValidUrlString("timeoutUrl")
  }

  object JourneyRepo {
    val journeyRepoTtl: FiniteDuration = readFiniteDuration("mongodb.journey-repo.journey-repo-ttl")
  }

  object FailedAttemptRepo {
    val failedAttemptRepoTtl: FiniteDuration = readFiniteDuration("mongodb.failed-attempts.failed-attempt-repo-ttl")
    val failedAttemptRepoMaxAttempts: Int = configuration.get[Int]("mongodb.failed-attempts.failed-attempt-repo-max-attempts")
  }

  val platformFrontendHost: String = readConfigAsValidUrlString("urls.localhost")

  object Feedback {
    val feedbackFrontendUrl: String = readConfigAsValidUrlString("urls.feedback-frontend")

    def betaFeedbackUrl()(implicit requestHeader: RequestHeader): String = {
      val currentUrl: String = URLEncoder.encode(platformFrontendHost + requestHeader.uri, "utf-8")

      s"""$feedbackFrontendUrl/contact/beta-feedback?""" +
        s"""&service=p800-refunds-frontend""" +
        s"""&canOmitComments=true""" +
        s"""&backUrl=$currentUrl""" +
        s"""&referrerUrl=$currentUrl"""
    }
  }

  val govUkRouteIn: String = readConfigAsValidUrlString("urls.gov-uk.govuk-route-in")

  val contactHmrcChangeDetailsUrl: String = readConfigAsValidUrlString("urls.gov-uk.contact-hmrc-change-details")

  val generalEnquiriesUrl: String = readConfigAsValidUrlString("urls.gov-uk.general-enquiries")

  val personalTaxAccountSignInUrl: String = readConfigAsValidUrlString("urls.pta-sign-in")
  val nationalInsuranceNumberUrl: String = readConfigAsValidUrlString("urls.gov-uk.lost-national-insurance-number")

  object ExternalApiCalls {

    val ecospendApiVersionPath: String = "/api/v2.0"
    val ecospendAuthServerUrl: String = servicesConfig.baseUrl("ecospend-auth")
    val ecospendUrl: String = servicesConfig.baseUrl("ecospend") + "/api/v2.0"

    val ecospendAuthClientId: String = configuration.get[String]("ecospend.oauth.clientId")
    val ecospendAuthClientSecret: String = configuration.get[String]("ecospend.oauth.clientSecret")
  }

  object P800RefundsExternalApi {
    val p800RefundsExternalApiBaseUrl: String = servicesConfig.baseUrl("p800-refunds-external-api")
  }

  object P800RefundsBackend {
    val p800RefundsBackendBaseUrl: String = servicesConfig.baseUrl("p800-refunds-backend")
  }

  object DateCalculator {
    val dateCalculatorBaseUrl: String = servicesConfig.baseUrl("date-calculator")
  }

  object JourneyVariables {
    val bankTransferWorkingDaysToAdd: Int = configuration.get[Int]("journey-variables.bank-transfer-working-days-to-add")
    val chequeFutureDateAddition: Int = configuration.get[Int]("journey-variables.cheque-weeks-to-add")
  }

  /**
   * The application loads the configuration from the provided `configPath` and checks if it's a valid URL.
   * If it's not a valid URL, an exception is thrown.
   * This exception is triggered early during the application's startup to highlight a malformed configuration,
   * thus increasing the chances of it being rectified promptly.
   */
  private def readConfigAsValidUrlString(configPath: String): String = {
    val url: String = configuration.get[String](configPath)
    Try(new java.net.URI(url).toURL).fold[String](
      e => throw new RuntimeException(s"Invalid URL in config under [$configPath]", e),
      _ => url
    )
  }

  private def readFiniteDuration(configPath: String): FiniteDuration = {
    servicesConfig.getDuration(configPath) match {
      case d: FiniteDuration => d
      case _: Infinite       => throw new RuntimeException(s"Infinite Duration in config for the key [$configPath]")
    }
  }

}
