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

package models

import uk.gov.hmrc.http.UpstreamErrorResponse

trait Api5xxError extends Exception {
  def message: String
}

final case class EcospendApi5xxError(override val message: String, cause: Option[UpstreamErrorResponse] = None) extends Api5xxError
final case class EdhApi5xxError(override val message: String, cause: Option[UpstreamErrorResponse] = None) extends Api5xxError
final case class ClaimOverpaymentApi5xxError(override val message: String, cause: Option[UpstreamErrorResponse] = None) extends Api5xxError
