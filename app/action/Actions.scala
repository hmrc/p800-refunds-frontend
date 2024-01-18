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

import models.journeymodels.{HasFinished, Journey}
import play.api.mvc.{ActionBuilder, AnyContent, Call, DefaultActionBuilder, Request}

import javax.inject.{Inject, Singleton}

@Singleton
class Actions @Inject() (
    actionBuilder:           DefaultActionBuilder,
    getJourneyActionRefiner: GetJourneyActionRefiner,
    ensureJourney:           EnsureJourney
) {

  val default: ActionBuilder[Request, AnyContent] = actionBuilder

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  val journeyActionForTestOnly: ActionBuilder[JourneyRequest, AnyContent] = default.andThen(getJourneyActionRefiner)

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  def journeyFinished: ActionBuilder[JourneyRequest, AnyContent] =
    default
      .andThen(getJourneyActionRefiner)
      .andThen(
        ensureJourney.ensureJourney(
          j => HasFinished.hasFinished(j.hasFinished),
          redirectWhenJourneyIsInProgress,
          "Journey is not finished"
        )
      )

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  def journeyInProgress: ActionBuilder[JourneyRequest, AnyContent] =
    default
      .andThen(getJourneyActionRefiner)
      .andThen(
        ensureJourney.ensureJourney(
          j => HasFinished.isInProgress(j.hasFinished),
          redirectWhenJourneyIsFinished,
          "Journey is finished"
        )
      )

  private def redirectWhenJourneyIsFinished(journey: Journey): Call = journey.hasFinished match {
    case HasFinished.YesSucceeded => controllers.routes.RequestReceivedController.get
    case HasFinished.YesFailed    => controllers.routes.YourRefundRequestHasNotBeenSubmittedController.get
    case HasFinished.No           => throw new RuntimeException(s"This case is not supported, journey should be already in one of finished states [${journey.id.toString}] [${journey.hasFinished.toString}]")
  }

  //TODO: this might need some extra refinement
  private def redirectWhenJourneyIsInProgress(journey: Journey): Call = journey.hasFinished match {
    case HasFinished.YesSucceeded => throw new RuntimeException(s"This case is not supported, journey should be in progress [${journey.id.toString}] [${journey.hasFinished.toString}]")
    case HasFinished.YesFailed    => throw new RuntimeException(s"This case is not supported, journey should be in progress [${journey.id.toString}] [${journey.hasFinished.toString}]")
    case HasFinished.No           => controllers.routes.DoYouWantToSignInController.get
  }

}
