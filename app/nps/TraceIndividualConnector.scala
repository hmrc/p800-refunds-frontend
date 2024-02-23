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

package nps

import nps.models.{TraceIndividualRequest, TraceIndividualResponse}
import play.api.mvc.RequestHeader
import requests.RequestSupport._
import uk.gov.hmrc.http.HttpClient
import uk.gov.hmrc.http.HttpReads.Implicits._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

/**
 * This connector will call Nps' Trace Individual API endpoint.
 */
@Singleton
class TraceIndividualConnector @Inject() (
    npsConfig:  NpsConfig,
    httpClient: HttpClient
)(implicit ec: ExecutionContext) {

  private val url: String = npsConfig.baseUrl +
    "/nps-json-service/nps/v1/api/individual/trace-individual?exactMatch=true&returnRealName=true"

  def traceIndividual(traceIndividualRequest: TraceIndividualRequest)(implicit requestHeader: RequestHeader): Future[TraceIndividualResponse] = {
    httpClient
      .POST[TraceIndividualRequest, List[TraceIndividualResponse]](
        url     = url,
        body    = traceIndividualRequest,
        headers = npsConfig.makeHeadersForNps()
      ).map{
          case traceIndividualResponse :: Nil => traceIndividualResponse
          case l                              => throw new RuntimeException(s"Unexpected response from NPS. Expecting only one record in the list, got ${l.size.toString} items")
        }
  }

}
