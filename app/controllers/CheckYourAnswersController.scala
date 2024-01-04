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
import language.Messages
import models.dateofbirth.DateOfBirth
import models.journeymodels._
import models.{P800Reference, IdentityVerificationResponse, IdentityVerified, NationalInsuranceNumber}
import play.api.mvc._
import requests.RequestSupport
import services.{IdentityVerificationService, JourneyService}
import uk.gov.hmrc.govukfrontend.views.Aliases.{HtmlContent, Key, SummaryListRow, Value}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import util.JourneyLogger
import views.Views

import java.time.format.DateTimeFormatter
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

@Singleton
class CheckYourAnswersController @Inject() (
    mcc:                         MessagesControllerComponents,
    requestSupport:              RequestSupport,
    journeyService:              JourneyService,
    views:                       Views,
    actions:                     Actions,
    identityVerificationService: IdentityVerificationService
)(implicit ec: ExecutionContext) extends FrontendController(mcc) {

  import requestSupport._

  val get: Action[AnyContent] = actions.journeyAction { implicit request =>
    request.journey match {
      case j: JTerminal                                   => JourneyRouter.handleFinalJourneyOnNonFinalPage(j)
      case j: JBeforeWhatIsYourNationalInsuranceNumber    => JourneyRouter.sendToCorrespondingPage(j)
      case j: JourneyWhatIsYourNationalInsuranceNumber    => getResult(j.p800Reference, j.dateOfBirth, j.nationalInsuranceNumber)
      case j: JAfterWhatIsYourNationalInsuranceNumber     => getResult(j.p800Reference, j.dateOfBirth, j.nationalInsuranceNumber)
      case j: JourneyDoYouWantYourRefundViaBankTransferNo => JourneyRouter.sendToCorrespondingPage(j)
    }
  }

  //Warn: These have to be `def`s otherwise it won't work.
  //If those actions are eagerly evaluated, play framework (or actually its reverse routes mechanism)
  //won't add `/get-an-income-tax-refund` path in the generated URLs.
  def changeP800Reference: Action[AnyContent] = change(controllers.routes.EnterP800ReferenceController.get)
  def changeDateOfBirth: Action[AnyContent] = change(controllers.routes.WhatIsYourDateOfBirthController.get)
  def changeNationalInsuranceNumber: Action[AnyContent] = change(controllers.routes.WhatIsYourNationalInsuranceNumberController.get)

  /**
   * When user decides to change details ([[FullName]], [[NationalInsuranceNumber]] or [[DateOfBirth]])
   * he is send to the relevant page. When submitting edits, he needs to be navigate back to the  CheckYourAnswersPage.
   * This is achieved by introducing [[JourneyCheckYourAnswersChange]] journey state.
   *
   * This creates an action which changes journey state to [[JourneyCheckYourAnswersChange]] and redirects to user to proper page
   * (defined using `call` parameter) where he can enter new details.
   */
  private def change(call: Call): Action[AnyContent] = actions.journeyAction.async { implicit request =>
    request.journey match {
      case j: JTerminal                                => JourneyRouter.handleFinalJourneyOnNonFinalPageF(j)
      case j: JBeforeWhatIsYourNationalInsuranceNumber => JourneyRouter.sendToCorrespondingPageF(j)
      case j: JourneyWhatIsYourNationalInsuranceNumber =>
        journeyService
          .upsert(j.into[JourneyCheckYourAnswersChange].transform)
          .map(_ => Redirect(call))
      case j: JAfterWhatIsYourNationalInsuranceNumber =>
        journeyService
          .upsert(j.into[JourneyCheckYourAnswersChange].enableInheritedAccessors.transform)
          .map(_ => Redirect(call))
      case j: JourneyDoYouWantYourRefundViaBankTransferNo => JourneyRouter.sendToCorrespondingPageF(j)
    }
  }

  private def getResult(
      p800Reference:           P800Reference,
      dateOfBirth:             DateOfBirth,
      nationalInsuranceNumber: NationalInsuranceNumber
  )(implicit request: Request[_]) = Ok(views.checkYourAnswersPage(
    summaryList = buildSummaryList(p800Reference, dateOfBirth, nationalInsuranceNumber)
  ))

  val post: Action[AnyContent] = actions.journeyAction.async { implicit request =>
    request.journey match {
      case j: JTerminal                                   => JourneyRouter.handleFinalJourneyOnNonFinalPageF(j)
      case j: JBeforeWhatIsYourNationalInsuranceNumber    => JourneyRouter.sendToCorrespondingPageF(j)
      case j: JourneyWhatIsYourNationalInsuranceNumber    => processForm(j)
      case j: JAfterWhatIsYourNationalInsuranceNumber     => processForm(j.into[JourneyWhatIsYourNationalInsuranceNumber].enableInheritedAccessors.transform)
      case j: JourneyDoYouWantYourRefundViaBankTransferNo => JourneyRouter.sendToCorrespondingPageF(j)
    }
  }

  private def processForm(journey: JourneyWhatIsYourNationalInsuranceNumber)(implicit request: JourneyRequest[AnyContent]): Future[Result] = {
    for {
      identityVerificationResponse <- identityVerificationService.verifyIdentity(journey.nationalInsuranceNumber)
      (newJourney, redirect) = identityVerificationResponse match {
        case idVerifiedResponse @ IdentityVerificationResponse(IdentityVerified(true), _) =>
          val j: JourneyIdentityVerified = journey
            .into[JourneyIdentityVerified]
            .withFieldConst(_.identityVerificationResponse, idVerifiedResponse)
            .transform
          j -> controllers.routes.WeHaveConfirmedYourIdentityController.get
        case idVerifiedResponse @ IdentityVerificationResponse(IdentityVerified(false), _) =>
          val j: JourneyIdentityNotVerified = journey
            .into[JourneyIdentityNotVerified]
            .withFieldConst(_.identityVerificationResponse, idVerifiedResponse)
            .transform
          j -> controllers.routes.WeCannotConfirmYourIdentityController.get
      }
      _ <- journeyService.upsert(newJourney)
    } yield Redirect(redirect)
  }

  def buildSummaryList(
      p800Reference:           P800Reference,
      dateOfBirth:             DateOfBirth,
      nationalInsuranceNumber: NationalInsuranceNumber
  )(implicit request: Request[_]): SummaryList = {

    SummaryList(rows = Seq(
      buildSummaryListRow(
        Messages.CheckYourAnswersMessages.`P800 reference`.show,
        id = "p800-reference",
        p800Reference.value,
        controllers.routes.CheckYourAnswersController.changeP800Reference
      ),
      buildSummaryListRow(
        Messages.CheckYourAnswersMessages.`National Insurance Number`.show,
        id = "national-insurance-number",
        s"""${nationalInsuranceNumber.value}""",
        controllers.routes.CheckYourAnswersController.changeNationalInsuranceNumber
      ),
      buildSummaryListRow(
        Messages.CheckYourAnswersMessages.`Date of birth`.show,
        id = "date-of-birth",
        formatDateOfBirth(dateOfBirth),
        controllers.routes.CheckYourAnswersController.changeDateOfBirth
      )
    ))
  }

  private def buildSummaryListRow(key: String, id: String, value: String, call: Call)(implicit request: Request[_]): SummaryListRow = SummaryListRow(
    key     = Key(HtmlContent(s"""$key""")),
    value   = Value(HtmlContent(
      //language=HTML
      s"""
          <div class="govuk-grid-row">
              <div id="$id" class="govuk-grid-column-one-half">$value</div>
              <div class="govuk-grid-column-one-half govuk-!-text-align-right">
                  <a href="${call.url}" id="change-$id">${Messages.CheckYourAnswersMessages.`Change`.show}
                  <span class="govuk-visually-hidden">$key</span></a>
              </div>
          </div>
      """
    )),
    classes = ""
  )

  private val dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")

  private def formatDateOfBirth(dateOfBirth: DateOfBirth)(implicit request: Request[_]): String = {
    Try(DateOfBirth.asLocalDate(dateOfBirth)) match {
      case Success(date) => date.format(dateFormatter)
      case Failure(ex) =>
        JourneyLogger.error(s"Error formatting date, investigate (the journey was not interrupted)", ex)
        s"${dateOfBirth.dayOfMonth.value} ${dateOfBirth.month.value} ${dateOfBirth.year.value} "
    }
  }
}

