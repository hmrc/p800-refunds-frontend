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

package nps

import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import java.util.{Base64, UUID}
import javax.inject.Inject

class NpsConfig @Inject() (servicesConfig: ServicesConfig) {

  val baseUrl: String = servicesConfig.baseUrl("nps")

  //TODO: we should probably just move all of this into backend once all NPS APIs migrated
  def makeHeadersForNps(): Seq[(String, String)] = Seq(
    authorisationHeader,
    makeCorrelationIdHeader(),
    makeOriginatorIdHeader()
  )

  private val username: String = servicesConfig.getString("microservice.services.nps.username")
  private val password: String = servicesConfig.getString("microservice.services.nps.password")

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

  //TODO: update this to use correlationId from journey object when we've done that ticket (OPS-11777)
  private def makeCorrelationIdHeader(): (String, String) = {
    "CorrelationId" -> UUID.randomUUID().toString
  }

  private def makeOriginatorIdHeader(): (String, String) = {
    "gov-uk-originator-id" -> "DA2_MRA_DIGITAL"
  }

}
