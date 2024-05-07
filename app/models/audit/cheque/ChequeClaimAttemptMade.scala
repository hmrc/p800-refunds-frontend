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

package models.audit.cheque

import models.audit.{AuditDetail, IsSuccessful}
import models.{Nino, P800Reference}
import nps.models._
import play.api.libs.json.{Json, Writes}

final case class ChequeClaimAttemptMade(
    userEnteredDetails:   UserEnteredDetails,
    repaymentAmount:      RepaymentAmount,
    isSuccessful:         IsSuccessful,
    repaymentInformation: RepaymentInformation
) extends AuditDetail {
  val auditType: String = "ChequeClaimAttemptMade"
}

final case class UserEnteredDetails(p800Reference: P800Reference, nino: Nino)
final case class RepaymentAmount(value: BigDecimal) extends AnyVal
final case class RepaymentInformation(
    paymentNumber:            P800Reference,
    customerAccountNumber:    CustomerAccountNumber,
    associatedPayableNumber:  AssociatedPayableNumber,
    reconciliationIdentifier: Option[ReconciliationIdentifier],
    payeNumber:               Option[PayeNumber],
    taxDistrictNumber:        Option[TaxDistrictNumber]
)

object ChequeClaimAttemptMade {
  implicit val writes: Writes[ChequeClaimAttemptMade] = Json.writes[ChequeClaimAttemptMade]
}

object UserEnteredDetails {
  implicit val writes: Writes[UserEnteredDetails] = Json.writes[UserEnteredDetails]
}

object RepaymentAmount {
  implicit val writes: Writes[RepaymentAmount] = Json.valueWrites[RepaymentAmount]
}

object RepaymentInformation {
  implicit val writes: Writes[RepaymentInformation] = Json.writes[RepaymentInformation]
}
