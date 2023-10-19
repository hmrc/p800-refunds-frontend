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

package models.journeymodels

import julienrf.json.derived
import play.api.libs.json.OFormat

sealed trait State

object State {

  //  implicit val format: OFormat[State] = derived.oformat[State]()

  case object Started extends State

  case object JourneyDoYouWantToSignInYes extends State {
    @SuppressWarnings(Array("org.wartremover.warts.Any"))
    implicit val format: OFormat[JourneyDoYouWantToSignInYes.type] = derived.oformat[JourneyDoYouWantToSignInYes.type]()
  }

  sealed trait WhatIsYourP800ReferenceState extends State

  object WhatIsYourP800ReferenceState {
    @SuppressWarnings(Array("org.wartremover.warts.Any"))
    implicit val format: OFormat[WhatIsYourP800ReferenceState] = derived.oformat[WhatIsYourP800ReferenceState]()
    case object WhatIsYourP800Reference extends WhatIsYourP800ReferenceState
    case object CheckYourReference extends WhatIsYourP800ReferenceState
    case object WeCannotConfirmYourReference extends WhatIsYourP800ReferenceState
  }

  case object DoYouWantYourRefundViaBankTransfer extends State {
    @SuppressWarnings(Array("org.wartremover.warts.Any"))
    implicit val format: OFormat[DoYouWantYourRefundViaBankTransfer.type] = derived.oformat[DoYouWantYourRefundViaBankTransfer.type]()
  }
}
