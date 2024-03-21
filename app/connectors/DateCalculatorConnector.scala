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
import models.datecalculator.DateCalculatorRequest
import play.api.mvc.Request
import requests.RequestSupport
import uk.gov.hmrc.http.{HttpClient, HttpResponse}
import uk.gov.hmrc.http.HttpReads.Implicits._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DateCalculatorConnector @Inject() (
    appConfig:      AppConfig,
    httpClient:     HttpClient,
    requestSupport: RequestSupport
)(implicit executionContext: ExecutionContext) {

  import requestSupport.hc

  private val workingDaysUrl = s"${appConfig.DateCalculator.dateCalculatorBaseUrl}/date-calculator/add-working-days"

  def addWorkingDays(dateCalculatorRequest: DateCalculatorRequest)(implicit request: Request[_]): Future[HttpResponse] =
    httpClient.POST[DateCalculatorRequest, HttpResponse](workingDaysUrl, dateCalculatorRequest)

}
