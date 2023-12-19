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

package controllers

import action.{Actions, JourneyRequest}
import io.scalaland.chimney.dsl._
import models.ecospend.verification.{BankVerification, VerificationStatus}
import models.journeymodels._
import play.api.mvc._
import services.{EcospendService, JourneyService}
import uk.gov.hmrc.http.UpstreamErrorResponse
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.Views
import util.SafeEquals.EqualsOps

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class VerifyBankAccountController @Inject() (
    mcc:             MessagesControllerComponents,
    views:           Views,
    actions:         Actions,
    ecospendService: EcospendService,
    journeyService:  JourneyService
)(implicit ec: ExecutionContext) extends FrontendController(mcc) {

  val get: Action[AnyContent] = actions.journeyAction.async { implicit request =>
    request.journey match {
      case j: JTerminal                             => JourneyRouter.handleFinalJourneyOnNonFinalPageF(j)
      case j: JBeforeWhatIsTheNameOfYourBankAccount => JourneyRouter.sendToCorrespondingPageF(j)
      case j: JourneyWhatIsTheNameOfYourBankAccount => JourneyRouter.sendToCorrespondingPageF(j)
      case j: JourneyRefundConsentGiven             => pageAction(j)
    }
  }

  private def pageAction(journey: JourneyRefundConsentGiven)(implicit journeyRequest: JourneyRequest[_]): Future[Result] = {
    for {
      maybeBankVerification: BankVerification <- ecospendService.validate(journey)
      _ <- maybeBankVerification.verificationStatus match {
        case VerificationStatus.Successful   => journeyService.upsert(journey.into[JourneyApprovedRefund].transform).map(_ => ())
        case VerificationStatus.UnSuccessful => journeyService.upsert(journey.into[JourneyNotApprovedRefund].transform).map(_ => ())
      }
    } yield maybeBankVerification
  }.map(bankVerification => bankVerification.verificationStatus match {
    case VerificationStatus.Successful   => Redirect(routes.RequestReceivedController.bankTransferGet)
    case VerificationStatus.UnSuccessful => Redirect(routes.RequestNotSubmittedController.get)
  }).recover {
    case e: UpstreamErrorResponse if e.statusCode === PAYMENT_REQUIRED => Ok(views.verifyBankAccountPage())
  }

}
