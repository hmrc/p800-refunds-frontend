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

package crypto

import models.journeymodels.Journey

import javax.inject.Inject
import com.softwaremill.quicklens._
import nps.models.ValidateReferenceResult.P800ReferenceChecked

class JourneyCrypto @Inject() (crypto: Crypto) {

  def encryptJourney(journey: Journey): Journey = mapPii(journey)(crypto.encrypt)

  def decryptJourney(journey: Journey): Journey = mapPii(journey)(crypto.decrypt)

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  private def mapPii(journey: Journey)(f: String => String): Journey = journey
    .modify(_.p800Reference.each.value).using(f)
    .modify(_.nino.each.value).using(f)
    .modify(_.dateOfBirth.each.dayOfMonth.value).using(f)
    .modify(_.dateOfBirth.each.month.value).using(f)
    .modify(_.dateOfBirth.each.year.value).using(f)
    .modify(_.referenceCheckResult.each.when[P800ReferenceChecked].payeNumber.each.value).using(f)
    .modify(_.referenceCheckResult.each.when[P800ReferenceChecked].customerAccountNumber.value).using(f)
    .modify(_.traceIndividualResponse.each.firstForename.each).using(f)
    .modify(_.traceIndividualResponse.each.secondForename.each).using(f)
    .modify(_.traceIndividualResponse.each.surname.each).using(f)
    .modify(_.traceIndividualResponse.each.addressLine1.each).using(f)
    .modify(_.traceIndividualResponse.each.addressLine2.each).using(f)
    .modify(_.traceIndividualResponse.each.addressPostcode.each.value).using(f)
    .modify(_.bankAccountSummary.each.accountIdentification.value).using(f)
    .modify(_.bankAccountSummary.each.calculatedOwnerName.value).using(f)
    .modify(_.bankAccountSummary.each.accountOwnerName.each.value).using(f)
    .modify(_.bankAccountSummary.each.displayName.value).using(f)
    .modify(_.bankAccountSummary.each.parties.each.fullLegalName.value).using(f)
    .modify(_.bankAccountSummary.each.parties.each.name.value).using(f)
}
