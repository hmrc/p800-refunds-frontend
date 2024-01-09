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

package action

import play.api.mvc.{ActionBuilder, AnyContent, DefaultActionBuilder, Request}

import javax.inject.{Inject, Singleton}

@Singleton
class Actions @Inject() (
    actionBuilder:           DefaultActionBuilder,
    getJourneyActionRefiner: GetJourneyActionRefiner,
    ensureJourney:           EnsureJourney
) {

  val default: ActionBuilder[Request, AnyContent] = actionBuilder

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  def journeyFinished: ActionBuilder[JourneyRequest, AnyContent] =
    default
      .andThen(getJourneyActionRefiner)
      .andThen(
        ensureJourney.ensureJourney(
          _.hasFinished,
          controllers.routes.DoYouWantToSignInController.get, //or discover where to send it (actually there is no business requirement to handle such cases)
          "Journey is not finished"
        )
      )

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  def journeyInProgress: ActionBuilder[JourneyRequest, AnyContent] =
    default
      .andThen(getJourneyActionRefiner)
      .andThen(
        ensureJourney.ensureJourney(
          !_.hasFinished,
          controllers.routes.RequestReceivedController.get,
          "Journey is finished"
        )
      )

}
