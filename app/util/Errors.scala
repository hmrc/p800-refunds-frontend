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

package util

import play.api.mvc.{Request, RequestHeader}
import uk.gov.hmrc.http.UpstreamErrorResponse

import scala.concurrent.Future

object Errors {

  /**
   * Creates a requirement which has to pass in order to continue computation.
   * If it fails it will result in Upstream4xxResponse.
   */
  def require(requirement: Boolean, message: => String)(implicit request: RequestHeader): Unit = {
    if (!requirement) {
      JourneyLogger.error(s"Requirement failed: $message")
      throw UpstreamErrorResponse(message, play.mvc.Http.Status.BAD_REQUEST)
    } else ()
  }

  def requireF(requirement: Boolean, message: => String)(implicit request: Request[_]): Future[Unit] = {
    if (!requirement) {
      JourneyLogger.error(s"Requirement failed: $message")
      Future.failed(UpstreamErrorResponse(message, play.mvc.Http.Status.BAD_REQUEST))
    } else Future.successful(())
  }

  @inline def throwBadRequestException(message: => String)(implicit request: RequestHeader): Nothing = {
    JourneyLogger.error(message)
    throw UpstreamErrorResponse(
      message,
      play.mvc.Http.Status.BAD_REQUEST
    )
  }

  @inline def throwBadRequestExceptionF(message: => String)(implicit request: RequestHeader): Future[Nothing] = {
    JourneyLogger.error(message)
    Future.failed(UpstreamErrorResponse(
      message,
      play.mvc.Http.Status.BAD_REQUEST
    ))
  }

  @inline def throwNotFoundException(message: => String)(implicit request: RequestHeader): Nothing = {
    JourneyLogger.error(message)
    throw UpstreamErrorResponse(
      message,
      play.mvc.Http.Status.NOT_FOUND
    )
  }

  @inline def throwServerErrorException(message: => String)(implicit request: RequestHeader): Nothing = {
    JourneyLogger.error(message)
    throw UpstreamErrorResponse(
      message,
      play.mvc.Http.Status.INTERNAL_SERVER_ERROR
    )
  }

  /**
   * Call this to ensure that we don't do stupid things,
   * like make illegal transitions (eg. from Finished to New)
   */
  def sanityCheck(requirement: Boolean, message: => String)(implicit request: RequestHeader): Unit = {
    if (!requirement) {
      JourneyLogger.error(message)
      throw UpstreamErrorResponse(message, play.mvc.Http.Status.INTERNAL_SERVER_ERROR)
    } else ()
  }

  def notImplemented(message: => String = "")(implicit request: RequestHeader): Nothing = {
    val m = s"Unimplemented: $message"
    JourneyLogger.error(m)
    throw UpstreamErrorResponse(m, play.mvc.Http.Status.NOT_IMPLEMENTED)
  }

}
