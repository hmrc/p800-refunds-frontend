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

package models.journeymodels
import julienrf.json.derived
import play.api.libs.json.OFormat

/**
 * Flag which is set on change check-your-answers page when user clicks "change" link
 */
sealed trait IsChanging

object IsChanging {
  case object Yes extends IsChanging
  case object No extends IsChanging

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val format: OFormat[IsChanging] = derived.oformat[IsChanging]()
}

