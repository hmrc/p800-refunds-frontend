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
import connectors.P800RefundsBackendConnector
import language.Messages
import models.dateofbirth.DateOfBirth
import models.journeymodels._
import models.{Nino, UserEnteredP800Reference}
import nps.models.{TraceIndividualRequest, TracedIndividual, ValidateReferenceResult}
import play.api.mvc._
import requests.RequestSupport
import services.{AuditService, FailedVerificationAttemptService, JourneyService}
import uk.gov.hmrc.govukfrontend.views.Aliases.{HtmlContent, Key, SummaryListRow, Value}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import util.JourneyLogger
import util.SafeEquals._
import util.WelshDateUtil._
import views.Views

import java.time.format.DateTimeFormatter
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CheckYourAnswersController @Inject() (
    mcc:                              MessagesControllerComponents,
    requestSupport:                   RequestSupport,
    auditService:                     AuditService,
    journeyService:                   JourneyService,
    views:                            Views,
    actions:                          Actions,
    failedVerificationAttemptService: FailedVerificationAttemptService,
    p800RefundsBackendConnector:      P800RefundsBackendConnector
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
    Ok(views.checkYourAnswersPage(summaryList = summaryList))
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

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  def post: Action[AnyContent] = actions
    .journeyInProgress
    .andThen(validateP800Reference)
    .andThen(processFailedReferenceCheck)
    .async { implicit request =>
      val journey: Journey = request.journey
      for {
        maybeTraceIndividualResponse: Option[TracedIndividual] <- journey.getJourneyType match {
          case JourneyType.BankTransfer =>
            p800RefundsBackendConnector.traceIndividual(
              traceIndividualRequest = TraceIndividualRequest(
                identifier  = journey.getNino,
                dateOfBirth = journey.getDateOfBirth.`formatYYYY-MM-DD`
              ),
              correlationId          = journey.correlationId
            ).map(Some(_))
          case JourneyType.Cheque => Future.successful(None)
        }
        attemptInfo <- failedVerificationAttemptService.find()
        journey <- journeyService.upsert(journey.update(maybeTraceIndividualResponse = maybeTraceIndividualResponse))
      } yield {
        auditService.auditValidateUserDetails(journey      = journey, attemptInfo = attemptInfo, isSuccessful = true)(request, hc)
        Redirect(controllers.YourIdentityIsConfirmedController.redirectLocation(journey))
      }

    }

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  private val validateP800Reference: ActionRefiner[JourneyRequest, JourneyRequest] = new ActionRefiner[JourneyRequest, JourneyRequest] {
    override protected def refine[A](request: JourneyRequest[A]): Future[Either[Result, JourneyRequest[A]]] = {
      implicit val r: JourneyRequest[A] = request
      p800RefundsBackendConnector
        .validateP800Reference(r.journey.getNino, r.journey.getP800Reference.sanitiseReference, r.journey.correlationId)
        .map { referenceCheckResult: ValidateReferenceResult =>
          Right(new JourneyRequest[A](journey = r.journey.update(referenceCheckResult), request = r.request))
        }
    }

    override protected def executionContext: ExecutionContext = ec
  }

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  private val processFailedReferenceCheck: ActionFilter[JourneyRequest] = new ActionFilter[JourneyRequest] {
    override protected def filter[A](request: JourneyRequest[A]): Future[Option[Result]] = {
      implicit val r: JourneyRequest[A] = request
      r.journey.getReferenceCheckResult match {
        case _: ValidateReferenceResult.P800ReferenceChecked =>
          //All good, proceed to next action
          Future.successful(None)
        case ValidateReferenceResult.RefundAlreadyTaken =>
          journeyService
            .upsert(r.journey.update(HasFinished.YesRefundAlreadyTaken))
            .map { j =>
              JourneyLogger.info("NPS indicate that refund has already been claimed")
              auditService.auditValidateUserDetails(journey      = j, attemptInfo = None, isSuccessful = false)(request, hc)
              Some(Redirect(routes.ThereIsAProblemController.get))
            }
        case ValidateReferenceResult.ReferenceDidntMatchNino =>
          for {
            (shouldBeLockedOut, attemptInfo) <- failedVerificationAttemptService.updateNumberOfFailedAttempts()
            result <- if (shouldBeLockedOut) {
              journeyService
                .upsert(r.journey.copy(hasFinished = HasFinished.YesLockedOut))
                .map { j =>
                  auditService.auditValidateUserDetails(journey      = j, attemptInfo = Some(attemptInfo), isSuccessful = false)(request, hc)
                  Some(Redirect(controllers.NoMoreAttemptsLeftToConfirmYourIdentityController.redirectLocation(j)))
                }
            } else journeyService
              .upsert(r.journey)
              .map { j =>
                auditService.auditValidateUserDetails(journey      = j, attemptInfo = Some(attemptInfo), isSuccessful = false)(request, hc)
                Some(Redirect(controllers.CannotConfirmYourIdentityTryAgainController.redirectLocation(j)))
              }
          } yield result
      }
    }

    override protected def executionContext: ExecutionContext = ec
  }

  private def buildSummaryList(
      p800Reference:           UserEnteredP800Reference,
      nationalInsuranceNumber: Nino,
      dateOfBirth:             DateOfBirth
  )(implicit request: Request[_]): SummaryList = SummaryList(rows = Seq(
    p800ReferenceSummaryRow(p800Reference),
    ninoSummaryRow(nationalInsuranceNumber),
    dateOfBirthSummaryRow(dateOfBirth)
  ))

  private def buildSummaryList(
      p800Reference: UserEnteredP800Reference,
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

  private val `dd MMMM yyyy`: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")

  private def dateOfBirthSummaryRow(dateOfBirth: DateOfBirth)(implicit request: Request[_]): SummaryListRow = {
    buildSummaryListRow(
      Messages.CheckYourAnswersMessages.`Date of birth`.show,
      id    = "date-of-birth",
      value = if (requestSupport.language.code === "cy") dateOfBirth.format(`dd MMMM yyyy`).welshMonth else dateOfBirth.format(`dd MMMM yyyy`),
      call  = controllers.routes.CheckYourAnswersController.changeDateOfBirth
    )
  }

  private def p800ReferenceSummaryRow(p800Reference: UserEnteredP800Reference)(implicit request: Request[_]): SummaryListRow = {
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
                  <a class="govuk-link" href="${call.url}" id="change-$id">${Messages.CheckYourAnswersMessages.`Change`.show}
                  <span class="govuk-visually-hidden">$key</span></a>
              </div>
          </div>
      """
    )),
    classes = ""
  )

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
