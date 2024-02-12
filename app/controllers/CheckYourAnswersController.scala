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
import language.Messages
import models.attemptmodels.AttemptInfo
import models.dateofbirth.DateOfBirth
import models.journeymodels._
import models.{Nino, P800Reference}
import nps.NpsConnector
import nps.models.ReferenceCheckResult
import play.api.mvc._
import requests.RequestSupport
import services.{FailedVerificationAttemptService, JourneyService}
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
    mcc:                              MessagesControllerComponents,
    requestSupport:                   RequestSupport,
    journeyService:                   JourneyService,
    views:                            Views,
    actions:                          Actions,
    failedVerificationAttemptService: FailedVerificationAttemptService,
    appConfig:                        AppConfig,
    npsConnector:                     NpsConnector
)(implicit ec: ExecutionContext) extends FrontendController(mcc) {

  import requestSupport._

  def getBankTransfer: Action[AnyContent] = actions.journeyInProgress { implicit request: JourneyRequest[_] => getResult }

  def getCheque: Action[AnyContent] = actions.journeyInProgress { implicit request: JourneyRequest[_] => getResult }

  private def getResult(implicit request: JourneyRequest[_]) = {
    val journey: Journey = request.journey

    val summaryList = journey.getJourneyType match {
      case JourneyType.BankTransfer => buildSummaryList(
        p800Reference           = journey.getP800Reference,
        nationalInsuranceNumber = journey.getNino,
        dateOfBirth             = journey.getDateOfBirth
      )
      case JourneyType.Cheque => buildSummaryList(
        p800Reference = journey.getP800Reference,
        nino          = journey.getNino
      )
    }
    Ok(views.checkYourAnswersPage(
      summaryList = summaryList,
      journeyType = request.journey.getJourneyType
    ))
  }

  def changeNationalInsuranceNumber: Action[AnyContent] = actions.journeyInProgress.async { implicit request =>
    val journey = request.journey
    journeyService
      .upsert(journey.copy(isChanging = IsChanging.Yes))
      .map(_ =>
        Redirect(EnterYourNationalInsuranceNumberController.redirectLocation(journey)))
  }

  def changeDateOfBirth: Action[AnyContent] = actions.journeyInProgress { _ =>
    Redirect(controllers.routes.EnterYourDateOfBirthController.get)
  }

  def changeP800Reference: Action[AnyContent] = actions.journeyInProgress.async { implicit request =>
    val journey = request.journey
    journeyService
      .upsert(journey.copy(isChanging = IsChanging.Yes))
      .map(_ => Redirect(EnterYourP800ReferenceController.redirectLocation(journey)))
  }

  def post: Action[AnyContent] = actions.journeyInProgress.async { implicit request =>
    val journey: Journey = request.journey
    processForm(journey)
  }

  private def processForm(journey: Journey)(implicit request: JourneyRequest[AnyContent]): Future[Result] = {
    //TODO: discuss refactoring options, this becomes a monster function...
    val updateLogicResultingInRedirect: Future[Call] =
      failedVerificationAttemptService
        .find()
        .flatMap { attemptInfoExists: Option[AttemptInfo] =>
          // if failed count is already at 3, ensure journey is HasFinished.LockedOut, redirect to no more attempts
          if (AttemptInfo.shouldBeLockedOut(attemptInfoExists, appConfig.FailedAttemptRepo.failedAttemptRepoMaxAttempts)) {
            journeyService.upsert(journey.copy(hasFinished = HasFinished.LockedOut))
              .map(_ => controllers.NoMoreAttemptsLeftToConfirmYourIdentityController.redirectLocation(journey))
          } else {
            // otherwise call identity verification
            npsConnector
              .p800ReferenceCheck(journey.getNino, journey.getP800Reference)
              .flatMap { referenceCheckResult: ReferenceCheckResult =>
                // if verification is successful, reflect this in journey, redirect to confirmed identity
                referenceCheckResult match {
                  case _: ReferenceCheckResult.P800ReferenceChecked => journeyService
                    .upsert(journey.update(referenceCheckResult = referenceCheckResult))
                    .map(_ => controllers.WeHaveConfirmedYourIdentityController.redirectLocation(journey))
                  case ReferenceCheckResult.RefundAlreadyTaken => journeyService
                    .upsert(journey.update(referenceCheckResult = referenceCheckResult))
                    .map(_ => controllers.CannotConfirmYourIdentityTryAgainController.redirectLocation(journey))
                  case ReferenceCheckResult.ReferenceDidntMatchNino =>
                    {
                      // otherwise (verification failed) update the number of failed attempts
                      failedVerificationAttemptService
                        .upsertRecordIfVerificationFails
                        .flatMap { (maybeAttemptInfo: Option[AttemptInfo]) =>
                          // if newly updated failed attempt count is over the threshold, update journey to HasFinished.LockedOut, redirecting to no more attempts
                          if (AttemptInfo.shouldBeLockedOut(maybeAttemptInfo, appConfig.FailedAttemptRepo.failedAttemptRepoMaxAttempts)) {
                            journeyService
                              .upsert(journey.update(referenceCheckResult = referenceCheckResult).copy(hasFinished = HasFinished.LockedOut))
                              .map(_ => controllers.NoMoreAttemptsLeftToConfirmYourIdentityController.redirectLocation(journey))
                          } else {
                            // otherwise attempts is not over threshold, redirect to cannot confirm your identity page
                            journeyService
                              .upsert(journey.update(referenceCheckResult = referenceCheckResult))
                              .map(_ => controllers.CannotConfirmYourIdentityTryAgainController.redirectLocation(journey))
                          }
                        }
                    }

                }
              }
          }
        }

    updateLogicResultingInRedirect
      .map(redirect => Redirect(redirect))
  }

  private def buildSummaryList(
      p800Reference:           P800Reference,
      nationalInsuranceNumber: Nino,
      dateOfBirth:             DateOfBirth
  )(implicit request: Request[_]): SummaryList = SummaryList(rows = Seq(
    p800ReferenceSummaryRow(p800Reference),
    ninoSummaryRow(nationalInsuranceNumber),
    dateOfBirthSummaryRow(dateOfBirth)
  ))

  private def buildSummaryList(
      p800Reference: P800Reference,
      nino:          Nino
  )(implicit request: Request[_]): SummaryList = SummaryList(rows = Seq(
    p800ReferenceSummaryRow(p800Reference),
    ninoSummaryRow(nino)
  ))

  private def ninoSummaryRow(nino: Nino)(implicit request: Request[_]): SummaryListRow = {
    buildSummaryListRow(
      Messages.CheckYourAnswersMessages.`National insurance number`.show,
      id    = "national-insurance-number",
      value = s"""${nino.value}""",
      call  = controllers.routes.CheckYourAnswersController.changeNationalInsuranceNumber
    )
  }

  private def dateOfBirthSummaryRow(dateOfBirth: DateOfBirth)(implicit request: Request[_]): SummaryListRow = {
    buildSummaryListRow(
      Messages.CheckYourAnswersMessages.`Date of birth`.show,
      id    = "date-of-birth",
      value = formatDateOfBirth(dateOfBirth),
      call  = controllers.routes.CheckYourAnswersController.changeDateOfBirth
    )
  }

  private def p800ReferenceSummaryRow(p800Reference: P800Reference)(implicit request: Request[_]): SummaryListRow = {
    buildSummaryListRow(
      Messages.CheckYourAnswersMessages.`P800 reference`.show,
      id    = "reference",
      value = p800Reference.value,
      call  = controllers.routes.CheckYourAnswersController.changeP800Reference
    )
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

object CheckYourAnswersController {

  private val key: String = "p800-refunds-frontend.changing-from-check-your-answers-page"

  def isChanging(implicit request: Request[_]): Boolean = request.flash.get(key).isDefined

  implicit class ResultOps(r: Result) {
    def makeChanging(): Result = {
      r.flashing(key -> "changing")
    }

    def continueChanging()(implicit request: Request[_]): Result = {
      if (isChanging)
        r.makeChanging()
      else
        r
    }
  }

  def redirectLocation(journey: Journey)(implicit request: Request[_]): Call = Journey.deriveRedirectByJourneyType(
    journeyType           = journey.getJourneyType,
    chequeJourneyRedirect = controllers.routes.CheckYourAnswersController.getCheque,
    bankJourneyRedirect   = controllers.routes.CheckYourAnswersController.getBankTransfer
  )

}
