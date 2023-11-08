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
import config.AppConfig
import io.scalaland.chimney.dsl._
import models.forms.enumsforforms.{CheckYourReferenceFormValue, DoYouWantToSignInFormValue, DoYouWantYourRefundViaBankTransferFormValue}
import models.forms.{CheckYourReferenceForm, DoYouWantToSignInForm, DoYouWantYourRefundViaBankTransferForm, EnterP800ReferenceForm}
import models.journeymodels._
import models.{AmountInPence, P800Reference, ReferenceValidationResponse}
import play.api.mvc._
import requests.RequestSupport
import services.{JourneyService, ReferenceValidationService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import util.JourneyLogger
import views.Views

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class JourneyController @Inject() (
    mcc:                        MessagesControllerComponents,
    requestSupport:             RequestSupport,
    journeyService:             JourneyService,
    referenceValidationService: ReferenceValidationService,
    views:                      Views,
    actions:                    Actions,
    appConfig:                  AppConfig
)(implicit ec: ExecutionContext) extends FrontendController(mcc) {

  import requestSupport._

  val start: Action[AnyContent] = Action.async { implicit request =>
    journeyService
      .newJourney()
      .map(journey => Redirect(
        routes.JourneyController.doYouWantToSignIn
      ).addingToSession(JourneyController.journeyIdKey -> journey.id.value))
  }

  val doYouWantToSignIn: Action[AnyContent] = actions.journeyAction { implicit request =>
    Ok(views.doYouWantToSignInPage(
      form      = DoYouWantToSignInForm.form,
      submitUrl = controllers.routes.JourneyController.doYouWantToSignInSubmit
    ))
  }

  val doYouWantToSignInSubmit: Action[AnyContent] = actions.journeyAction.async { implicit request =>
    DoYouWantToSignInForm.form.bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(views.doYouWantToSignInPage(
        form      = formWithErrors,
        submitUrl = controllers.routes.JourneyController.doYouWantToSignInSubmit
      ))), {
        case DoYouWantToSignInFormValue.Yes =>
          journeyService
            .upsert(request.journey.transformInto[JourneyDoYouWantToSignInYes])
            .map(_ => Redirect(appConfig.ptaSignInUrl))
        case DoYouWantToSignInFormValue.No =>
          journeyService
            .upsert(request.journey.transformInto[JourneyDoYouWantToSignInNo])
            .map(_ => Redirect(controllers.routes.JourneyController.enterP800Reference))
      }
    )
  }

  val enterP800Reference: Action[AnyContent] = actions.journeyAction { implicit request =>
    Ok(views.enterP800ReferencePage(
      form      = EnterP800ReferenceForm.form,
      submitUrl = controllers.routes.JourneyController.enterP800ReferenceSubmit
    ))
  }

  val enterP800ReferenceSubmit: Action[AnyContent] = actions.journeyAction.async { implicit request =>
    EnterP800ReferenceForm.form.bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(views.enterP800ReferencePage(
        form      = formWithErrors,
        submitUrl = controllers.routes.JourneyController.enterP800ReferenceSubmit
      ))),
      p800Reference => {
        journeyService
          .upsert(journeyIntoWhatIsYourP800Reference(p800Reference))
          .map(_ => Redirect(controllers.routes.JourneyController.checkYourReference))
      }
    )
  }

  private def journeyIntoWhatIsYourP800Reference(p800Reference: P800Reference)(implicit request: JourneyRequest[AnyContent]): JourneyWhatIsYourP800Reference =
    request.journey.into[JourneyWhatIsYourP800Reference]
      .withFieldConst(_.p800Reference, p800Reference)
      .transform

  val checkYourReference: Action[AnyContent] = actions.journeyAction { implicit request =>
    request.journey match {
      case j: JourneyWhatIsYourP800Reference =>
        Ok(views.checkYourReferencePage(
          reference = j.p800Reference.value,
          form      = CheckYourReferenceForm.form,
          submitUrl = controllers.routes.JourneyController.checkYourReferenceSubmit
        ))
      case _ =>
        // TODO: Handle other cases more appropriately
        throw new Exception("Check your reference page with unexpected state")
    }
  }

  val checkYourReferenceSubmit: Action[AnyContent] = actions.journeyAction.async { implicit request =>
    request.journey match {
      case journey: JourneyWhatIsYourP800Reference =>
        bindCheckReferenceSubmitForm(journey)
      case _ =>
        // TODO: Handle other cases more appropriately
        throw new Exception("Check your reference page with unexpected state")
    }
  }

  private def bindCheckReferenceSubmitForm(journey: JourneyWhatIsYourP800Reference)(implicit request: JourneyRequest[AnyContent]): Future[Result] =
    CheckYourReferenceForm.form.bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(views.checkYourReferencePage(
        reference = journey.p800Reference.value, //TODO talk about type safety
        form      = formWithErrors,
        submitUrl = controllers.routes.JourneyController.checkYourReferenceSubmit
      ))),
      {
        case CheckYourReferenceFormValue.Yes => validateReference(journey)
        case CheckYourReferenceFormValue.No  => Future.successful(Redirect(controllers.routes.JourneyController.enterP800Reference))
      }
    )

  private def validateReference(journey: JourneyWhatIsYourP800Reference)(implicit request: Request[_]): Future[Result] =
    referenceValidationService.validateReference(journey.p800Reference).flatMap {
      case ReferenceValidationResponse(true) =>
        journeyService
          .upsert(
            journey.transformInto[JourneyCheckYourReferenceValid]
          )
          .map(_ => Redirect(controllers.routes.JourneyController.doYouWantYourRefundViaBankTransfer))
      case ReferenceValidationResponse(false) =>
        journeyService
          .upsert(
            journey.transformInto[JourneyCheckYourReferenceInvalid]
          )
          .map(_ => Redirect(controllers.routes.JourneyController.cannotConfirmReference))
    }

  val cannotConfirmReference: Action[AnyContent] = actions.default { implicit request =>
    Ok(views.cannotConfirmReferencePage())
  }

  val yourChequeWillBePostedToYou: Action[AnyContent] = actions.default { implicit request =>
    Ok(views.yourChequeWillBePostedToYouPage())
  }

  val chequeRequestReceived: Action[AnyContent] = actions.default { implicit request =>
    //todo get these from journey
    val (dummyP800Ref, refundAmountInPence) = P800Reference("P800REFNO1") -> AmountInPence(231.60)
    Ok(views.chequeRequestReceivedPage(dummyP800Ref, refundAmountInPence))
  }

  val weNeedYouToConfirmYourIdentity: Action[AnyContent] = actions.default { implicit request =>
    Ok(views.weNeedYouToConfirmYourIdentityPage())
  }

  val doYouWantYourRefundViaBankTransfer: Action[AnyContent] = actions.default { implicit request =>
    Ok(views.doYouWantYourRefundViaBankTransferPage(
      DoYouWantYourRefundViaBankTransferForm.form
    ))
  }

  val doYouWantYourRefundViaBankTransferSubmit: Action[AnyContent] = actions.journeyAction.async { implicit request =>
    request.journey match {
      case j: JourneyCheckYourReferenceValid =>
        DoYouWantYourRefundViaBankTransferForm.form.bindFromRequest().fold(
          formWithErrors => Future.successful(BadRequest(views.doYouWantYourRefundViaBankTransferPage(
            form = formWithErrors
          ))), {
            case DoYouWantYourRefundViaBankTransferFormValue.Yes =>
              journeyService
                .upsert(j.transformInto[JourneyDoYouWantYourRefundViaBankTransferYes])
                .map(_ => Redirect(controllers.routes.JourneyController.weNeedYouToConfirmYourIdentity))
            case DoYouWantYourRefundViaBankTransferFormValue.No =>
              journeyService
                .upsert(j.transformInto[JourneyDoYouWantYourRefundViaBankTransferNo])
                .map(_ => Redirect(controllers.routes.JourneyController.yourChequeWillBePostedToYou))
          }
        )
      case j =>
        JourneyLogger.error(s"Unsupported journey state ${j.name}, redirecting to corresponding page")
        // TODO: Handle other cases more appropriately
        throw new Exception("Check your reference page with unexpected state")
    }
  }

  //TODO: remove once we have all pages
  val underConstruction: Action[AnyContent] = actions.default { implicit request =>
    Ok(views.underConstructionPage())
  }
}

object JourneyController {
  val journeyIdKey: String = "p800-refunds-frontend.journeyId"
}
