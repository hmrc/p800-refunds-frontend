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

package action

import models.journeymodels.Journey
import play.api.mvc.Results.Redirect
import play.api.mvc.{ActionFilter, Call, Result}
import util.JourneyLogger

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EnsureJourney @Inject() ()(implicit ec: ExecutionContext) {

  /**
   * Check if journey matches predicate. If it doesn't, it will send the Redirect.
   */
  def ensureJourney(predicate: Journey => Boolean, redirectF: Journey => Call, hintWhyRedirecting: String): ActionFilter[JourneyRequest] = new ActionFilter[JourneyRequest] {
    override def filter[A](request: JourneyRequest[A]): Future[Option[Result]] = {
      implicit val r: JourneyRequest[A] = request
      val journey = request.journey
      val result: Option[Result] =
        if (predicate(journey)) None
        else {
          val call = redirectF(journey)
          JourneyLogger.warn(s"Can't display page in that journey ($hintWhyRedirecting), redirecting to [${call.url}]")
          Some(Redirect(call))
        }
      Future.successful(result)
    }

    override protected def executionContext: ExecutionContext = ec
  }
}
