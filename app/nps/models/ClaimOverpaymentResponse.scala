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
import _root_.models.Nino
import play.api.libs.json.{OFormat, Json}

sealed trait ClaimOverpaymentResult

object ClaimOverpaymentResult {

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val format: OFormat[ClaimOverpaymentResult] = derived.oformat[ClaimOverpaymentResult]()

  case object RefundAlreadyTaken extends ClaimOverpaymentResult

  case object RefundSuspended extends ClaimOverpaymentResult

  final case class ClaimOverpaymentResponse(
      identifer:             Nino,
      currentOptimisticLock: CurrentOptimisticLock
  ) extends ClaimOverpaymentResult

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  object ClaimOverpaymentResponse {
    implicit val format: OFormat[ClaimOverpaymentResponse] = Json.format[ClaimOverpaymentResponse]
  }
}

