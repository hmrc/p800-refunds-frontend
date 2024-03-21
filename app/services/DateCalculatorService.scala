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

package services

import com.google.inject.{Inject, Singleton}
import config.AppConfig
import connectors.DateCalculatorConnector
import models.datecalculator.{DateCalculatorRequest, DateCalculatorResponse, Region}
import play.api.libs.json.Json
import play.api.mvc.Request

import java.time.{Clock, LocalDate}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DateCalculatorService @Inject() (
    appConfig:               AppConfig,
    clock:                   Clock,
    dateCalculatorConnector: DateCalculatorConnector
)(implicit executionContext: ExecutionContext) {

  private val workingDaysToAdd = appConfig.JourneyVariables.bankTransferWorkingDaysToAdd
  private val dateCalculatorRequest = DateCalculatorRequest(
    date                     = LocalDate.now(),
    numberOfWorkingDaysToAdd = workingDaysToAdd,
    regions                  = Region.values.toSet
  )

  def getFutureDate()(implicit request: Request[_]): Future[LocalDate] = {
    dateCalculatorConnector
      .addWorkingDays(dateCalculatorRequest)
      .map { response =>
        Json.fromJson[DateCalculatorResponse](response.json).asOpt match {
          case Some(validJson) => validJson.result
          case None            => LocalDate.now(clock).plusDays(7)
        }
      }.recover { case _ => LocalDate.now(clock).plusDays(7) }
  }
}
