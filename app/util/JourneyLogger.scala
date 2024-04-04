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

import action.JourneyRequest
import play.api.Logger
import play.api.mvc.RequestHeader
import uk.gov.hmrc.http.CookieNames
import util.SafeEquals._
import requests.RequestSupport._

/**
 * Journey Logger is a contextual logger. It will append to the message some extra bits of information
 * like journeyId origin, path method, etc.
 * Use it everywhere
 */
object JourneyLogger {

  private val log: Logger = Logger("journey")

  def debug(message: => String)(implicit request: RequestHeader): Unit = logMessage(message, Debug)

  def info(message: => String)(implicit request: RequestHeader): Unit = logMessage(message, Info)

  def warn(message: => String)(implicit request: RequestHeader): Unit = logMessage(message, Warn)

  def error(message: => String)(implicit request: RequestHeader): Unit = logMessage(message, Error)

  def debug(message: => String, ex: Throwable)(implicit request: RequestHeader): Unit = logMessage(message, ex, Debug)

  def info(message: => String, ex: Throwable)(implicit request: RequestHeader): Unit = logMessage(message, ex, Info)

  def warn(message: => String, ex: Throwable)(implicit request: RequestHeader): Unit = logMessage(message, ex, Warn)

  def error(message: => String, ex: Throwable)(implicit request: RequestHeader): Unit = logMessage(message, ex, Error)

  private def context(implicit request: RequestHeader) = s"[context: ${request.method} ${request.path}] $sessionId $requestId $referer $deviceId"

  private def sessionId(implicit request: RequestHeader) = s"[${hc.sessionId.toString}]"

  private def requestId(implicit request: RequestHeader) = s"[${hc.requestId.toString}]"

  private def referer(implicit r: RequestHeader) = s"[Referer: ${r.headers.headers.find(_._1 === "Referer").map(_._2).getOrElse("")}]"

  private def deviceId(implicit r: RequestHeader) = s"[deviceId: ${r.cookies.find(_.name === CookieNames.deviceID).map(_.value).getOrElse("")}]"

  private def journeyId(implicit r: JourneyRequest[_]) = s"[${r.journey.id.toString}]"

  private def correlationId(implicit r: JourneyRequest[_]) = s"[correlationId:${r.journey.correlationId.toString}]"

  private def consentId(implicit r: JourneyRequest[_]) = s"[consentId:${r.journey.bankConsentResponse.map(_.id.value).getOrElse("")}]"

  private def selectedBank(implicit r: JourneyRequest[_]) = s"[bankName:${r.journey.bankDescription.map(_.friendlyName.value).getOrElse("")}]"

  private def isValidEventValue(implicit r: JourneyRequest[_]) = s"[isValid:${r.journey.isValidEventValue.map(_.toString).getOrElse("")}]"

  private def journeyType(implicit r: JourneyRequest[_]) = s"[journeyType:${r.journey.journeyType.toString}]"

  private def makeRichMessage(message: String)(implicit request: RequestHeader): String = {
    request match {
      case r: JourneyRequest[_] =>
        implicit val req: JourneyRequest[_] = r
        //Warn, don't log whole journey as it might contain sensitive data (PII)
        s"$message $journeyType $journeyId $correlationId $selectedBank $consentId $isValidEventValue $context"
      case _ =>
        s"$message $context "
    }
  }

  private sealed trait LogLevel

  private case object Debug extends LogLevel

  private case object Info extends LogLevel

  private case object Warn extends LogLevel

  private case object Error extends LogLevel

  private def logMessage(message: => String, level: LogLevel)(implicit request: RequestHeader): Unit = {
    lazy val richMessage = makeRichMessage(message)
    level match {
      case Debug => log.debug(richMessage)
      case Info  => log.info(richMessage)
      case Warn  => log.warn(richMessage)
      case Error => log.error(richMessage)
    }
  }

  private def logMessage(message: => String, ex: Throwable, level: LogLevel)(implicit request: RequestHeader): Unit = {
    lazy val richMessage = makeRichMessage(message)
    level match {
      case Debug => log.debug(richMessage, ex)
      case Info  => log.info(richMessage, ex)
      case Warn  => log.warn(richMessage, ex)
      case Error => log.error(richMessage, ex)
    }
  }

}
