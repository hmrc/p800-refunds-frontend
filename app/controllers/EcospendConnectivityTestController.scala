/*
 * Copyright 2024 HM Revenue & Customs
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

import action.{Actions, JourneyIdKey}
import edh.Postcode
import models.dateofbirth._
import models.journeymodels.{HasFinished, IsChanging, Journey, JourneyType}
import models.{CorrelationId, Nino, P800Reference, UserEnteredP800Reference}
import nps.models._
import play.api.mvc._
import services.{JourneyIdGenerator, JourneyService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import util.JourneyLogger

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

//todo remove this controller when we remove the super secret connectivity test endpoint
@Singleton
class EcospendConnectivityTestController @Inject() (
    mcc:                MessagesControllerComponents,
    journeyService:     JourneyService,
    journeyIdGenerator: JourneyIdGenerator,
    actions:            Actions
)(implicit ec: ExecutionContext) extends FrontendController(mcc) {

  def setupJourneyForEcospendConnectivityTest: Action[AnyContent] = actions.default.async { implicit request =>
    val tempTestJourneyForConnectivityTest = Journey(
      _id                           = journeyIdGenerator.nextJourneyId(),
      createdAt                     = java.time.Instant.now(),
      correlationId                 = CorrelationId(java.util.UUID.fromString("5eda7a62-7b15-4960-927c-41a67be208e8")),
      hasFinished                   = HasFinished.No,
      journeyType                   = Some(JourneyType.BankTransfer),
      p800Reference                 = Some(UserEnteredP800Reference("Dontcarewontbeused")),
      nino                          = Some(Nino("Dontcarewontbeused")),
      isChanging                    = IsChanging.No,
      dateOfBirth                   = Some(DateOfBirth(DayOfMonth("1"), Month("1"), Year("2000"))),
      referenceCheckResult          = Some(ValidateReferenceResult.P800ReferenceChecked(Some(ReconciliationIdentifier(0)), P800Reference(0), Some(PayeNumber("Dontcarewontbeused")), Some(TaxDistrictNumber(717)), BigDecimal(12.34), AssociatedPayableNumber(0), CustomerAccountNumber("customerAccountNumber-1234"), CurrentOptimisticLock(15))),
      traceIndividualResponse       = Some(TracedIndividual(Some("Sir"), Some("madeup"), None, Some("person"), Some("Flat 1 Rose House"), Some("Worthing"), Some(Postcode("BN12 4XL")))),
      bankDescription               = None,
      bankConsentResponse           = None,
      bankAccountSummary            = None,
      isValidEventValue             = None,
      bankDetailsRiskResultResponse = None
    )
    journeyService
      .upsert(tempTestJourneyForConnectivityTest)
      .map { upsertedJourney =>
        JourneyLogger.info("Upserting journey using the super secret connectivity test endpoint")
        Redirect(routes.YourIdentityIsConfirmedController.getBankTransfer)
          .addingToSession(JourneyIdKey.journeyIdKey -> upsertedJourney.journeyId.value)
      }
  }

}
