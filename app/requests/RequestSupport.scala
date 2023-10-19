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

package requests

import language.Language
import models.journeymodels.SessionId
import play.api.i18n._
import play.api.mvc.{Request, RequestHeader}
import uk.gov.hmrc.http.{HeaderCarrier, SessionKeys}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendHeaderCarrierProvider
import util.Errors

import javax.inject.Inject
import scala.concurrent.Future

/**
 * I'm repeating a pattern which was brought originally by play-framework
 * and putting some more data which can be derived from a request
 *
 * Use it to provide HeaderCarrier, Lang, or Messages
 */
class RequestSupport @Inject() (i18nSupport: I18nSupport) {

  def lang(implicit requestHeader: RequestHeader): Lang = i18nSupport.request2Messages(requestHeader).lang

  implicit def language(implicit requestHeader: RequestHeader): Language = {
    val lang: Lang = i18nSupport.request2Messages(requestHeader).lang
    Language(lang)
  }

  implicit def legacyMessages(implicit requestHeader: RequestHeader): Messages = {
    i18nSupport.request2Messages(requestHeader)
  }

  implicit def hc(implicit request: Request[_]): HeaderCarrier = RequestSupport.hc
}

object RequestSupport {

  implicit def hc(implicit request: RequestHeader): HeaderCarrier = HcProvider.headerCarrier

  /**
   * Naive way of checking if user is logged in. Use it in views only.
   * For more real check see auth.AuthService
   */
  def isLoggedIn(implicit request: Request[_]): Boolean = request.session.get(SessionKeys.authToken).isDefined

  /**
   * This is because we want to give responsibility of creation of HeaderCarrier to the platform code.
   * If they refactor how hc is created our code will pick it up automatically.
   */
  private object HcProvider extends FrontendHeaderCarrierProvider {
    def headerCarrier(implicit request: RequestHeader): HeaderCarrier = hc(request)
  }

  def getSessionId()(implicit request: Request[_]): Future[SessionId] = Future.successful {
    hc
      .sessionId
      .map(s => SessionId(s.value))
      .getOrElse(Errors.throwBadRequestException("Session id must be provided"))
  }

}

