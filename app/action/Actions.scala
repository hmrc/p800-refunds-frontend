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

import models.journeymodels.{HasFinished, Journey, JourneyType}
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
    case HasFinished.YesSucceeded =>
      journey.journeyType match {
        case Some(JourneyType.Cheque)       => controllers.routes.RequestReceivedController.getCheque
        case Some(JourneyType.BankTransfer) => controllers.routes.RequestReceivedController.getBankTransfer
        case None                           => throw new RuntimeException(s"This case is not supported for YesSucceeded, journey should have a journeyType [${journey.id.toString}] [${journey.hasFinished.toString}]")
      }
    case HasFinished.YesRefundNotSubmitted => controllers.routes.RefundRequestNotSubmittedController.get
    case HasFinished.YesLockedOut => journey.journeyType match {
      case Some(JourneyType.Cheque)       => controllers.routes.NoMoreAttemptsLeftToConfirmYourIdentityController.getCheque
      case Some(JourneyType.BankTransfer) => controllers.routes.NoMoreAttemptsLeftToConfirmYourIdentityController.getBankTransfer
      case None                           => throw new RuntimeException(s"This case is not supported for LockedOut, journey should have a journeyType [${journey.id.toString}] [${journey.hasFinished.toString}]")
    }
    case HasFinished.No => throw new RuntimeException(s"This case is not supported, journey should be already in one of finished states [${journey.id.toString}] [${journey.hasFinished.toString}]")
  }

  //TODO: this might need some extra refinement
  private def redirectWhenJourneyIsInProgress(journey: Journey): Call = journey.hasFinished match {
    case HasFinished.YesSucceeded          => throw new RuntimeException(s"This case is not supported [YesSucceeded], journey should be in progress [${journey.id.toString}] [${journey.hasFinished.toString}]")
    case HasFinished.YesRefundNotSubmitted => throw new RuntimeException(s"This case is not supported [RefundNotSubmitted], journey should be in progress [${journey.id.toString}] [${journey.hasFinished.toString}]")
    case HasFinished.YesLockedOut          => throw new RuntimeException(s"This case is not supported [LockedOut], journey should be in progress [${journey.id.toString}] [${journey.hasFinished.toString}]")
    case HasFinished.No                    => controllers.routes.DoYouWantToSignInController.get
  }

}
