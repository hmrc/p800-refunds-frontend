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
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration.Infinite
import scala.concurrent.duration.FiniteDuration
import scala.util.Try

@Singleton
class AppConfig @Inject() (servicesConfig: ServicesConfig, configuration: Configuration) {

  object JourneyRepo {
    val journeyRepoTtl: FiniteDuration = readFiniteDuration("mongodb.journey-repo.journey-repo-ttl")
  }

  object FailedAttemptRepo {
    val failedAttemptRepoTtl: FiniteDuration = readFiniteDuration("mongodb.failed-attempts.failed-attempt-repo-ttl")
    val failedAttemptRepoMaxAttempts: Int = configuration.get[Int]("mongodb.failed-attempts.failed-attempt-repo-max-attempts")
  }

  object Nps {
    val baseUrl: String = servicesConfig.baseUrl("nps")
    val username: String = servicesConfig.getString("microservice.services.nps.username")
    val password: String = servicesConfig.getString("microservice.services.nps.password")
  }

  val govUkRouteIn: String = readConfigAsValidUrlString("urls.gov-uk.govuk-route-in")

  val incomeTaxGeneralEnquiriesUrl: String = readConfigAsValidUrlString("urls.gov-uk.income-tax-general-enquiries")

  val contactHmrcChangeDetailsUrl: String = readConfigAsValidUrlString("urls.gov-uk.contact-hmrc-change-details")

  val generalEnquiriesUrl: String = readConfigAsValidUrlString("urls.gov-uk.general-enquiries")

  val lostNationalInsuranceNumberUrl: String = readConfigAsValidUrlString("urls.gov-uk.lost-national-insurance-number")

  object PersonalTaxAccountUrls {
    val personalTaxAccountSignInUrl: String = readConfigAsValidUrlString("urls.pta-sign-in")
  }

  object ExternalApiCalls {
    val p800ReferenceValidationBaseUrl: String = servicesConfig.baseUrl("p800-reference-validation")

    val ecospendApiVersionPath: String = "/api/v2.0"
    val ecospendAuthServerUrl: String = servicesConfig.baseUrl("ecospend-auth")
    val ecospendUrl: String = servicesConfig.baseUrl("ecospend") + "/api/v2.0"

    val ecospendAuthClientId: String = configuration.get[String]("ecospend.oauth.clientId")
    val ecospendAuthClientSecret: String = configuration.get[String]("ecospend.oauth.clientSecret")
    val ecospendAuthScope: String = configuration.get[String]("ecospend.oauth.scope")
  }

  /**
   * The application loads the configuration from the provided `configPath` and checks if it's a valid URL.
   * If it's not a valid URL, an exception is thrown.
   * This exception is triggered early during the application's startup to highlight a malformed configuration,
   * thus increasing the chances of it being rectified promptly.
   */
  private def readConfigAsValidUrlString(configPath: String): String = {
    val url: String = configuration.get[String](configPath)
    Try(new java.net.URL(url)).fold[String](
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
