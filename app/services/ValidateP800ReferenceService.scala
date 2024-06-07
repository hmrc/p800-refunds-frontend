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

package services

import connectors.P800RefundsBackendConnector
import models.audit.ApiResponsibleForFailure
import models.journeymodels.Journey
import nps.models.ValidateReferenceResult
import play.api.mvc.Request
import requests.RequestSupport
import util.JourneyLogger

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ValidateP800ReferenceService @Inject() (
    auditService:                AuditService,
    p800RefundsBackendConnector: P800RefundsBackendConnector
)(implicit ec: ExecutionContext) {
  import RequestSupport._

  def validateP800Reference(journey: Journey)(implicit request: Request[_]): Future[ValidateReferenceResult] = {
    p800RefundsBackendConnector
      .validateP800Reference(journey.getNino, journey.getP800Reference.sanitiseReference, journey.correlationId)
      .recover {
        case ex =>
          JourneyLogger.warn(s"Call to P800 reference check failed with exception: ${ex.getMessage()}")
          auditService.auditValidateUserDetails(
            journey                  = journey,
            attemptInfo              = None,
            isSuccessful             = false,
            apiResponsibleForFailure = Some(ApiResponsibleForFailure.P800ReferenceCheck),
            failureReasons           = Some(Seq(
              ex.getMessage()
            ))
          )
          throw ex
      }
  }
}
