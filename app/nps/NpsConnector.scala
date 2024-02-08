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

import _root_.models.{Nino, P800Reference}
import config.AppConfig
import nps.models.{P800ReferenceCheckResultFailures, ReferenceCheckResult}
import play.api.http.Status
import play.api.mvc.RequestHeader
import requests.RequestSupport._
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.{HttpClient, HttpErrorFunctions, HttpReads, HttpResponse, UpstreamErrorResponse}
import util.SafeEquals._

import java.util.{Base64, UUID}
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

/**
 * This connector will call Nps endpoints (via HIP/IF).
 */
@Singleton
class NpsConnector @Inject() (
    appConfig:  AppConfig,
    httpClient: HttpClient
)(implicit ec: ExecutionContext) {

  private def p800ReferenceUrl(nino: Nino, p800Reference: P800Reference): String = appConfig.Nps.baseUrl +
    s"/nps-json-service/nps/v1/api/reconciliation/p800/${nino.value}/${p800Reference.value}"

  def p800ReferenceCheck(nino: Nino, p800Reference: P800Reference)(implicit requestHeader: RequestHeader): Future[ReferenceCheckResult] = {

    // A few notes:
    // 1) Constructing Awkward HttpReads[ReferenceCheckResult] is necessary for mapping HttpResponse
    // to ReferenceCheckResult. This mapping depends on the HTTP status codes and the content in the body.
    // The body content varies based on the HTTP status. For instance:
    //    - A 200 status code indicates a successful response, where the happy path JSON, if is valid, then it is transformed into P800ReferenceChecked.
    //    - A 422 status code, if it contains specific JSON and an error code in it, is considered a valid response.
    //    - A 404 status code is also treated as a valid case.
    // 2) The need to cast to `ReferenceCheckResult` arises because HttpReads[A] is invariant in A.
    // 3) To ensure compatibility with the error handling imposed by the Platform, HttpErrorFunctions are utilized.
    // This ensures that exceptions thrown by handleResponseEither are captured and managed as expected.

    implicit val reads: HttpReads[ReferenceCheckResult] = HttpReads.ask.flatMap {
      case (method, url, response) => response.status match {
        case Status.OK        => HttpReads[ReferenceCheckResult.P800ReferenceChecked].map((x: ReferenceCheckResult.P800ReferenceChecked) => x: ReferenceCheckResult)
        case Status.NOT_FOUND => HttpReads.pure[ReferenceCheckResult](ReferenceCheckResult.ReferenceDidntMatchNino)
        case Status.UNPROCESSABLE_ENTITY => HttpReads[P800ReferenceCheckResultFailures]
          .map((checkResultFailures: P800ReferenceCheckResultFailures) =>
            if (checkResultFailures.failures.exists(_.code === "TODO-refund-already-taken")) //TODO: agree with NPS the error code for this case
              ReferenceCheckResult.RefundAlreadyTaken: ReferenceCheckResult
            else
              HttpErrorFunctions.handleResponseEither(method, url)(response) match {
                case Left(err: UpstreamErrorResponse) => throw err
                //this case should not happen as we already know that the status is 4xx
                case Right(response: HttpResponse) =>

                  throw UpstreamErrorResponse(
                    message    = HttpErrorFunctions.upstreamResponseMessage(method, url, response.status, response.body),
                    statusCode = response.status,
                    reportAs   = Status.INTERNAL_SERVER_ERROR,
                    headers    = response.headers
                  )
              })
        case otherStatus => HttpErrorFunctions.handleResponseEither(method, url)(response) match {
          case Left(err: UpstreamErrorResponse) => throw err
          case Right(response: HttpResponse) =>
            throw UpstreamErrorResponse(
              message    = HttpErrorFunctions.upstreamResponseMessage(method, url, otherStatus, response.body),
              statusCode = otherStatus,
              reportAs   = Status.INTERNAL_SERVER_ERROR,
              headers    = response.headers
            )
        }
      }
    }

    httpClient
      .GET[ReferenceCheckResult](
        url     = p800ReferenceUrl(nino, p800Reference),
        headers = Seq(authorisationHeader, makeCorrelationIdHeader())
      )
  }

  private val authorisationHeader: (String, String) = {
      def encodeString(input: String): String = {
        val encoder: Base64.Encoder = Base64.getEncoder
        val encodedBytes = encoder.encode(input.getBytes("UTF-8"))
        new String(encodedBytes, "UTF-8")
      }
    val credentials = s"${appConfig.Nps.username}:${appConfig.Nps.password}"
    val credentialsEncoded = encodeString(credentials)
    "Authorization" -> s"Basic $credentialsEncoded"
  }

  private def makeCorrelationIdHeader(): (String, String) = {
    "CorrelationId" -> UUID.randomUUID().toString
  }
}
