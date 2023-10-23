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

import uk.gov.hmrc.http.UpstreamErrorResponse

import scala.concurrent.Future

object Errors {

  /**
   * Creates a requirement which has to pass in order to continue computation.
   * If it fails it will result in Upstream4xxResponse.
   */
  def require(requirement: Boolean, message: => String): Unit = {
    if (!requirement) throw UpstreamErrorResponse(message, play.mvc.Http.Status.BAD_REQUEST)
    else ()
  }

  def requireF(requirement: Boolean, message: => String): Future[Unit] = {
    if (!requirement) Future.failed(UpstreamErrorResponse(message, play.mvc.Http.Status.BAD_REQUEST))
    else Future.successful(())
  }

  @inline def throwBadRequestException(message: => String): Nothing = throw UpstreamErrorResponse(
    message,
    play.mvc.Http.Status.BAD_REQUEST
  )

  @inline def throwBadRequestExceptionF(message: => String): Future[Nothing] = Future.failed(UpstreamErrorResponse(
    message,
    play.mvc.Http.Status.BAD_REQUEST
  ))

  @inline def throwNotFoundException(message: => String): Nothing = throw UpstreamErrorResponse(
    message,
    play.mvc.Http.Status.NOT_FOUND
  )

  @inline def throwServerErrorException(message: => String): Nothing = throw UpstreamErrorResponse(
    message,
    play.mvc.Http.Status.INTERNAL_SERVER_ERROR
  )

  /**
   * Call this to ensure that we don't do stupid things,
   * like make illegal transitions (eg. from Finished to New)
   */
  def sanityCheck(requirement: Boolean, message: => String): Unit = {
    if (!requirement) throw UpstreamErrorResponse(message, play.mvc.Http.Status.INTERNAL_SERVER_ERROR)
    else ()
  }

  def notImplemented(message: => String = ""): Nothing = {
    throw UpstreamErrorResponse(s"Unimplemented: $message", play.mvc.Http.Status.NOT_IMPLEMENTED)
  }

}
