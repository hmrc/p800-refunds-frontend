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

package models.forms.testonly

import enumeratum.Enum
import models.forms.testonly.WebhookSimulationForm.RecordType
import models.p800externalapi.EventValue
import play.api.data.{FieldMapping, Form, Forms}
import play.api.data.Forms.mapping
import util.EnumFormatter

final case class WebhookSimulationForm(recordType: RecordType, recordId: String, eventValue: EventValue)

object WebhookSimulationForm {

  //todo these should probably live in models directory, but do that when we wire up properly to external api in journey
  sealed trait RecordType extends enumeratum.EnumEntry

  object RecordType extends Enum[RecordType] {
    case object AccountAssessment extends RecordType
    //I don't think we need these, but it would stop ecospend requests failing if they did send it for whatever reason.
    case object ConsentTransaction extends RecordType
    case object Account extends RecordType
    case object AccountTransaction extends RecordType
    case object StandingOrder extends RecordType
    case object ScheduledPayment extends RecordType
    case object DirectDebit extends RecordType
    case object Consent extends RecordType
    override def values: IndexedSeq[RecordType] = findValues
  }

  private val eventValueMapping: FieldMapping[EventValue] = Forms.of(EnumFormatter.format(
    `enum`                  = EventValue,
    errorMessageIfMissing   = "Select an option",
    errorMessageIfEnumError = "Select an option"
  ))

  private val recordTypeMapping: FieldMapping[RecordType] = Forms.of(EnumFormatter.format(
    `enum`                  = RecordType,
    errorMessageIfMissing   = "Select an option",
    errorMessageIfEnumError = "Select an option"
  ))

  def form: Form[WebhookSimulationForm] = Form[WebhookSimulationForm](
    mapping(
      "recordType" -> recordTypeMapping,
      "recordId" -> Forms.nonEmptyText,
      "eventValue" -> eventValueMapping
    )(WebhookSimulationForm.apply)(WebhookSimulationForm.unapply)
  )
}
