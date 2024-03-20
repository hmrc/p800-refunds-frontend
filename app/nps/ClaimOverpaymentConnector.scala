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

import _root_.models.{Nino, P800Reference}
import nps.models.{ClaimOverpaymentRequest, ClaimOverpaymentResult, ClaimOverpaymentResultFailures}
import play.api.Logger
import play.api.http.Status
import play.api.libs.json.{JsError, JsResult, JsSuccess}
import play.api.mvc.RequestHeader
import requests.RequestSupport._
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.{HttpClient, HttpErrorFunctions, HttpReads, HttpResponse, JsValidationException, UpstreamErrorResponse}
import util.JourneyLogger
import util.SafeEquals._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ClaimOverpaymentConnector @Inject() (
    npsConfig:  NpsConfig,
    httpClient: HttpClient
)(implicit ec: ExecutionContext) {

  private def url(nino: Nino, p800Reference: P800Reference): String = npsConfig.baseUrl +
    s"/nps-json-service/nps/v1/api/accounting/claim-overpayment/${nino.value}/${p800Reference.sanitiseReference.value}"

  def claimOverpayment(
      nino:                    Nino,
      p800Reference:           P800Reference,
      claimOverpaymentRequest: ClaimOverpaymentRequest
  )(implicit requestHeader: RequestHeader): Future[ClaimOverpaymentResult] = {

    JourneyLogger.info("Claiming overpayment")

    val sanitisedP800Reference = p800Reference.sanitiseReference
    implicit val reads: HttpReads[ClaimOverpaymentResult] = ClaimOverpaymentConnector.reads

    httpClient
      .PUT[ClaimOverpaymentRequest, ClaimOverpaymentResult](
        url     = url(nino, sanitisedP800Reference),
        body    = claimOverpaymentRequest,
        headers = npsConfig.makeHeadersForNps()
      ).map {
          case claimOverpaymentResult: ClaimOverpaymentResult => claimOverpaymentResult
        }
  }
}

object ClaimOverpaymentConnector {

  /**
   * Reads handles one of four possible Scenarios
   *
   * 1. 200 status code with happy path JSON ClaimOverpaymentResponse
   * 2. 422 status code, with a JSON failure code mapping to 'Overpayment already taken'
   * 3. 422 status code, with a JSON failure code mapping to 'Overpayment suspended'
   * 4. Any other error code status.
   *
   * Cases 2, 3 should be handled gracefully.
   * Case 4 results in 'Technical difficulties' page.
   */
  val reads: HttpReads[ClaimOverpaymentResult] = HttpReads.ask.flatMap {
    case (method, url, response) => response.status match {
      case Status.OK => HttpReads[ClaimOverpaymentResult.ClaimOverpaymentResponse].map((x: ClaimOverpaymentResult.ClaimOverpaymentResponse) => x: ClaimOverpaymentResult)
      case Status.UNPROCESSABLE_ENTITY =>
        logger.info(s"UNPROCESSABLE_ENTITY: ${response.body}")
        HttpReads[JsResult[ClaimOverpaymentResultFailures]]
          .flatMap {
            case JsSuccess(value: ClaimOverpaymentResultFailures, _) => HttpReads.pure(value)
            case JsError(errors) =>
              HttpReads.ask.map[ClaimOverpaymentResultFailures] {
                case (method, url, _) =>
                  throw new JsValidationException(method, url, this.getClass, errors.toString)
              }
          }.map { (claimOverpaymentResultFailures: ClaimOverpaymentResultFailures) =>
            logger.info(s"UNPROCESSABLE_ENTITY: ${claimOverpaymentResultFailures.toString}")
            if (claimOverpaymentResultFailures.failures.exists(_.code === NpsErrorCodes.`Overpayment has already been claimed`))
              ClaimOverpaymentResult.RefundAlreadyTaken
            else if (claimOverpaymentResultFailures.failures.exists(_.code === NpsErrorCodes.`Overpayment is suspended`))
              ClaimOverpaymentResult.RefundSuspended
            else
              HttpErrorFunctions.handleResponseEither(method, url)(response) match {
                case Left(err: UpstreamErrorResponse) => throw err
                // This case should not happen as we already know that the status is 4xx
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
