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

package models

import julienrf.json.derived
import play.api.libs.json.OFormat

/**
 * P800 reference is going to be validated in external system
 * Those cases represent in what state that reference is.
 */
sealed trait P800ReferenceValidation

object P800ReferenceValidation {
  case object NotValidatedYet extends P800ReferenceValidation
  case object Invalid extends P800ReferenceValidation
  //  case object Valid extends P800ReferenceValidation no need for that, the journey itself will be promoted to next state

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val format: OFormat[P800ReferenceValidation] = derived.oformat[P800ReferenceValidation]()
}
