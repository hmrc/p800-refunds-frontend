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

package config

import com.typesafe.config.ConfigFactory
import play.api.Configuration
import play.api.mvc.Request
import play.api.test.FakeRequest
import testsupport.UnitSpec
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

class AppConfigSpec extends UnitSpec {

  "beta feedback url renders correctly" in {

    val appConfig = {
      val configuration: Configuration = new Configuration(ConfigFactory.load())
      val servicesConfig = new ServicesConfig(configuration)
      new AppConfig(servicesConfig, configuration)
    }

    implicit val request: Request[_] = FakeRequest(method = "GET", path = "/get-an-income-tax-refund/any-page")

    appConfig.Feedback.betaFeedbackUrl() shouldBe "http://localhost:9514/contact/beta-feedback?" +
      "&service=p800-refunds-frontend" +
      "&canOmitComments=true" +
      "&backUrl=http%3A%2F%2Flocalhost%3A10150%2Fget-an-income-tax-refund%2Fany-page" +
      "&referrerUrl=http%3A%2F%2Flocalhost%3A10150%2Fget-an-income-tax-refund%2Fany-page"
  }

}
