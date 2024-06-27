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

package nps.models

import julienrf.json.derived
import models.P800Reference
import play.api.libs.json.{Json, OFormat}

sealed trait ValidateReferenceResult

object ValidateReferenceResult {

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val format: OFormat[ValidateReferenceResult] = derived.oformat[ValidateReferenceResult]()

  case object ReferenceDidntMatchNino extends ValidateReferenceResult

  case object RefundAlreadyTaken extends ValidateReferenceResult

  case object RefundNoLongerAvailable extends ValidateReferenceResult

  //TODO case object RefundSuspended extends ReferenceCheckResult

  final case class P800ReferenceChecked(
      reconciliationIdentifier: Option[ReconciliationIdentifier],
      paymentNumber:            P800Reference,
      payeNumber:               Option[PayeNumber],
      taxDistrictNumber:        Option[TaxDistrictNumber],
      paymentAmount:            BigDecimal,
      associatedPayableNumber:  AssociatedPayableNumber,
      customerAccountNumber:    CustomerAccountNumber,
      currentOptimisticLock:    CurrentOptimisticLock
  ) extends ValidateReferenceResult

  object P800ReferenceChecked {
    @SuppressWarnings(Array("org.wartremover.warts.Any"))
    implicit val format: OFormat[P800ReferenceChecked] = Json.format[P800ReferenceChecked]
  }

}
