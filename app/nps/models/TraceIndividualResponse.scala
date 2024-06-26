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

package nps.models

import edh.Postcode
import julienrf.json.derived
import play.api.http.Status
import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.http.{HttpErrorFunctions, HttpReads, HttpResponse, UpstreamErrorResponse}
import uk.gov.hmrc.http.HttpReads.Implicits._

sealed trait TraceIndividualResponse

object TraceIndividualResponse {

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val format: OFormat[TraceIndividualResponse] = derived.oformat[TraceIndividualResponse]()

  case object TraceIndividualNotFound extends TraceIndividualResponse

  implicit val traceIndividualReads: HttpReads[TraceIndividualResponse] = HttpReads.ask.flatMap {
    case (method, url, response) =>
      response.status match {
        case Status.OK        => HttpReads[TracedIndividual].map((x: TraceIndividualResponse.TracedIndividual) => x: TracedIndividual)
        case Status.NOT_FOUND => HttpReads.pure[TraceIndividualResponse](TraceIndividualResponse.TraceIndividualNotFound)
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

  /**
   * We're storing only few fields which will be actually used
   */
  final case class TracedIndividual(
      title:           Option[String],
      firstForename:   Option[String],
      secondForename:  Option[String],
      surname:         Option[String],
      addressLine1:    Option[String],
      addressLine2:    Option[String],
      addressPostcode: Option[Postcode]
  ) extends TraceIndividualResponse

  object TracedIndividual {
    @SuppressWarnings(Array("org.wartremover.warts.Any"))
    implicit val format: OFormat[TracedIndividual] = Json.format[TracedIndividual]
  }
}

