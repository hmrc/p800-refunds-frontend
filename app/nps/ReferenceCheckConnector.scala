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
import nps.models.{P800ReferenceCheckResultFailures, ReferenceCheckResult}
import play.api.Logger
import play.api.http.Status
import play.api.libs.json.{JsError, JsResult, JsSuccess}
import play.api.mvc.RequestHeader
import requests.RequestSupport._
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.{HttpClient, HttpErrorFunctions, HttpReads, HttpResponse, JsValidationException, UpstreamErrorResponse}
import util.SafeEquals._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

/**
 * This connector will call Nps' Check P800 Reference API endpoint.
 */
@Singleton
class ReferenceCheckConnector @Inject() (
    npsConfig:  NpsConfig,
    httpClient: HttpClient
)(implicit ec: ExecutionContext) {

  private def url(nino: Nino, p800Reference: P800Reference): String = npsConfig.baseUrl +
    s"/nps-json-service/nps/v1/api/reconciliation/p800/${nino.value}/${p800Reference.value}"

  def p800ReferenceCheck(nino: Nino, p800Reference: P800Reference)(implicit requestHeader: RequestHeader): Future[ReferenceCheckResult] = {
    implicit val reads: HttpReads[ReferenceCheckResult] = ReferenceCheckConnector.reads
    httpClient
      .GET[ReferenceCheckResult](
        url     = url(nino, p800Reference),
        headers = npsConfig.makeHeadersForNps()
      )
  }
}

object ReferenceCheckConnector {

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

  val reads: HttpReads[ReferenceCheckResult] = HttpReads.ask.flatMap {
    case (method, url, response) => response.status match {
      case Status.OK        => HttpReads[ReferenceCheckResult.P800ReferenceChecked].map((x: ReferenceCheckResult.P800ReferenceChecked) => x: ReferenceCheckResult)
      case Status.NOT_FOUND => HttpReads.pure[ReferenceCheckResult](ReferenceCheckResult.ReferenceDidntMatchNino)
      case Status.UNPROCESSABLE_ENTITY =>
        logger.info(s"UNPROCESSABLE_ENTITY1: ${response.body}")
        HttpReads[JsResult[P800ReferenceCheckResultFailures]]
          .flatMap {
            case JsSuccess(value: P800ReferenceCheckResultFailures, _) => HttpReads.pure(value)
            case JsError(errors) =>
              HttpReads.ask.map[P800ReferenceCheckResultFailures] {
                case (method, url, _) =>
                  throw new JsValidationException(method, url, this.getClass, errors.toString)
              }
          }.map { (checkResultFailures: P800ReferenceCheckResultFailures) =>
            logger.info(s"UNPROCESSABLE_ENTITY: ${checkResultFailures.toString}")
            if (checkResultFailures.failures.exists(_.code === NpsErrorCodes.`Overpayment has already been claimed`))
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
              }
          }
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

  private lazy val logger = Logger(this.getClass)

}
