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
import models.ReferenceValidationResponse
import models.forms.CheckYourReferenceForm
import models.forms.enumsforforms.CheckYourReferenceFormValue
import models.journeymodels._
import play.api.mvc._
import requests.RequestSupport
import services.{JourneyService, ReferenceValidationService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import util.{Errors, JourneyLogger}
import views.Views

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CheckYourReferenceController @Inject() (
    mcc:                        MessagesControllerComponents,
    requestSupport:             RequestSupport,
    journeyService:             JourneyService,
    referenceValidationService: ReferenceValidationService,
    views:                      Views,
    actions:                    Actions
)(implicit ec: ExecutionContext) extends FrontendController(mcc) {

  import requestSupport._

  val get: Action[AnyContent] = actions.journeyAction.async { implicit request =>
    request.journey match {
      case _: JTerminal                      => JourneyController.handleFinalJourneyOnNonFinalPage()
      case j: JBeforeWhatIsYourP800Reference => JourneyController.sendToCorrespondingPageF(j)
      case j: JourneyWhatIsYourP800Reference => Future.successful(getResult(j))
      case j: JAfterWhatIsYourP800Reference =>
        journeyService
          .upsert(
            j
              .into[JourneyWhatIsYourP800Reference]
              .enableInheritedAccessors
              .transform
          )
          .map(j => getResult(j))
    }
  }

  private def getResult(journey: JourneyWhatIsYourP800Reference)(implicit request: Request[_]) = Ok(views.checkYourReferencePage(
    reference = journey.p800Reference,
    form      = CheckYourReferenceForm.form
  ))

  val post: Action[AnyContent] = actions.journeyAction.async { implicit request =>
    request.journey match {
      case _: JTerminal                      => JourneyController.handleFinalJourneyOnNonFinalPage()
      case j: JBeforeWhatIsYourP800Reference => JourneyController.sendToCorrespondingPageF(j)
      case j: JourneyWhatIsYourP800Reference => processForm(j)
      case _: JAfterWhatIsYourP800Reference =>
        Errors.throwServerErrorException(s"This endpoint supports only ${classOf[JourneyWhatIsYourP800Reference].toString}")
      //TODO: discuss alternative approach
      //val journey = j
      //              .into[JourneyWhatIsYourP800Reference]
      //              .enableInheritedAccessors
      //              .transform
      //processForm(j)
    }
  }

  private def processForm(journey: JourneyWhatIsYourP800Reference)(implicit request: JourneyRequest[AnyContent]): Future[Result] =
    CheckYourReferenceForm.form.bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(views.checkYourReferencePage(
        reference = journey.p800Reference,
        form      = formWithErrors
      ))),
      {
        case CheckYourReferenceFormValue.Yes => validateReference(journey)
        case CheckYourReferenceFormValue.No  => Future.successful(Redirect(controllers.routes.EnterP800ReferenceController.get))
      }
    )

  private def validateReference(journey: JourneyWhatIsYourP800Reference)(implicit request: Request[_]): Future[Result] =
    referenceValidationService.validateReference(journey.p800Reference).flatMap {
      case ReferenceValidationResponse(true) =>
        journeyService
          .upsert(
            journey.transformInto[JourneyCheckYourReferenceValid]
          )
          .map(_ => Redirect(controllers.routes.DoYouWantYourRefundViaBankTransferController.get))
      case ReferenceValidationResponse(false) =>
        JourneyLogger.info("P800 reference is invalid")
        Future.successful(Redirect(controllers.routes.CannotConfirmReferenceController.get))
    }

}
