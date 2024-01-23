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

package controllers.testonly

import action.{Actions, JourneyIdKey}
import models.journeymodels.{HasFinished, Journey, JourneyId, JourneyType}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.JourneyService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.ViewsTestOnly

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TestOnlyController @Inject() (
    mcc:            MessagesControllerComponents,
    viewsTestOnly:  ViewsTestOnly,
    journeyService: JourneyService,
    as:             Actions
)(implicit ec: ExecutionContext) extends FrontendController(mcc) {

  val landing: Action[AnyContent] = as.default { implicit request =>
    Ok(viewsTestOnly.landingTestOnlyPage())
  }

  val govUkRouteIn: Action[AnyContent] = as.default { implicit request =>
    Ok(viewsTestOnly.govUkStubPage())
  }

  val ptaSignIn: Action[AnyContent] = as.default { implicit request =>
    Ok(viewsTestOnly.ptaSignInStubPage())
      .withNewSession //Pro TIP: this represents current behaviour of sign in page on the production...
  }

  val incomeTaxGeneralEnquiries: Action[AnyContent] = as.default { implicit request =>
    Ok(viewsTestOnly.incomeTaxGeneralEnquiriesStubPage())
  }

  val showJourney: Action[AnyContent] = as.default.async { implicit request =>
    request.session.get(JourneyIdKey.journeyIdKey).map(JourneyId.apply) match {
      case None            => Future.successful(Ok(s"No ${JourneyIdKey.journeyIdKey} in play session"))
      case Some(journeyId) => showJourney(journeyId)
    }
  }

  def showJourneyById(journeyId: JourneyId): Action[AnyContent] = as.default.async { _ =>
    showJourney(journeyId)
  }

  //TODO: remove it once we integrate with APIs
  val finishSucceedBankTransfer: Action[AnyContent] = as.journeyActionForTestOnly.async { implicit r =>
    journeyService.upsert(r.journey.copy(hasFinished = HasFinished.YesSucceeded)).map { _ =>
      Redirect(r.journey.getJourneyType match {
        case JourneyType.Cheque       => controllers.routes.RequestReceivedController.getCheque
        case JourneyType.BankTransfer => controllers.routes.RequestReceivedController.getBankTransfer
      })
    }

  }

  //TODO: remove it once we integrate with APIs
  val finishFailBankTransfer: Action[AnyContent] = as.journeyActionForTestOnly.async { implicit r =>
    journeyService.upsert(r.journey.copy(hasFinished = HasFinished.YesFailed)).map(_ =>
      Redirect(controllers.routes.YourRefundRequestHasNotBeenSubmittedController.get))
  }

  def addJourneyIdToSession(journeyId: JourneyId): Action[AnyContent] = as.default { implicit request =>
    Ok(s"${journeyId.value} added to session").addingToSession(JourneyIdKey.journeyIdKey -> journeyId.value)
  }

  private def showJourney(journeyId: JourneyId): Future[Result] = {
    for {
      maybeJourney: Option[Journey] <- journeyService.find(journeyId)
    } yield Ok(
      maybeJourney
        .map(journey => Json.prettyPrint(Json.toJson(journey)))
        .getOrElse(s"No Journey in mongo with journeyId: [${journeyId.value}]")
    )
  }
}
