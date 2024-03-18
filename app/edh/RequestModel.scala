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

package edh

import models.Nino
import play.api.libs.json._

import java.util.UUID
import scala.util.matching.Regex

final case class ClaimId(value: String) {
  def validate: Option[String] = if (ClaimId.regex.matches(value)) None else Some("Invalid 'ClaimId'")
  def asTransactionId: TransactionID = TransactionID(value)
}

object ClaimId {

  implicit val format: Format[ClaimId] = Json.valueFormat[ClaimId]

  private val regex: Regex = """^[A-Za-z0-9\- ]{1,255}$""".r

  def next(): ClaimId = ClaimId(UUID.randomUUID().toString)
}

final case class Header(
    transactionID: TransactionID,
    requesterID:   RequesterID,
    serviceID:     ServiceID
) {
  def validate: Option[String] =
    transactionID.validate
      .orElse(requesterID.validate)
      .orElse(serviceID.validate)
}

object Header {
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val format: OFormat[Header] = Json.format[Header]
}

final case class TransactionID(value: String) {
  def validate: Option[String] = if (TransactionID.regex.matches(value)) None else Some("Invalid 'TransactionID'")
}

object TransactionID {
  implicit val format: Format[TransactionID] = Json.valueFormat[TransactionID]
  val regex: Regex = """^[A-Za-z0-9\- ]*$""".r
}

final case class RequesterID(value: String) {
  def validate: Option[String] = if (RequesterID.regex.matches(value)) None else Some("Invalid 'RequesterID'")
}

object RequesterID {
  implicit val format: Format[RequesterID] = Json.valueFormat[RequesterID]
  val regex: Regex = """^[A-Za-z0-9\- ]*$""".r
}

final case class ServiceID(value: String) {
  def validate: Option[String] = if (ServiceID.regex.matches(value)) None else Some("Invalid 'ServiceID'")
}

object ServiceID {
  implicit val format: Format[ServiceID] = Json.valueFormat[ServiceID]
  val regex: Regex = """^[A-Za-z0-9\- ]*$""".r
}

final case class PaymentData(
    paymentAmount: Option[BigDecimal],
    paymentNumber: Option[Int]
)

object PaymentData {
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val format: OFormat[PaymentData] = Json.format[PaymentData]
}

final case class TaxDistrictNumber(value: String) {
  def validate: Option[String] = if (TaxDistrictNumber.regex.matches(value)) None else Some("Invalid 'TaxDistrictNumber")
}

object TaxDistrictNumber {
  implicit val format: Format[TaxDistrictNumber] = Json.valueFormat[TaxDistrictNumber]
  val regex: Regex = """^[0-9]{3}$""".r
}

final case class SchemeRef(value: String) {
  def validate: Option[String] = if (SchemeRef.regex.matches(value)) None else Some("Invalid 'SchemeRef'")
}

object SchemeRef {
  implicit val format: Format[SchemeRef] = Json.valueFormat[SchemeRef]
  val regex: Regex = """^[A-Z0-9\-/]*$""".r
}

final case class EmployerData(
    taxDistrictNumber: Option[TaxDistrictNumber],
    schemeRef:         Option[SchemeRef]
) {

  def validate: Option[String] =
    taxDistrictNumber.flatMap(_.validate).orElse(
      schemeRef.flatMap(_.validate)
    )
}

object EmployerData {
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val format: OFormat[EmployerData] = Json.format[EmployerData]
}

final case class Person(
    surname:                 Surname,
    firstForenameOrInitial:  Option[FirstForenameOrInitial],
    secondForenameOrInitial: Option[SecondForenameOrInitial],
    nino:                    Nino,
    dateOfBirth:             DateOfBirth,
    title:                   Option[Title],
    address:                 Option[List[Address]]
) {
  def validate: Option[String] =
    surname.validate
      .orElse(firstForenameOrInitial.flatMap(_.validate))
      .orElse(secondForenameOrInitial.flatMap(_.validate))
      //      .orElse(nino.validate) we don't validate nino as it should be validated by the form
      .orElse(dateOfBirth.validate)
      .orElse(title.flatMap(_.validate))
      .orElse(address.flatMap(_.collectFirst(_.validate).flatten))
}

object Person {
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val format: OFormat[Person] = Json.format[Person]
}

final case class Surname(value: String) {
  def validate: Option[String] = if (Surname.regex.matches(value)) None else Some("Invalid 'Surname'")
}

object Surname {
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val format: Format[Surname] = Json.valueFormat[Surname]
  val regex: Regex = """^[A-Za-z][A-Za-z\-' ]*$""".r
}

final case class FirstForenameOrInitial(value: String) {
  def validate: Option[String] = if (FirstForenameOrInitial.regex.matches(value)) None else Some("Invalid 'FirstForenameOrInitial'")
}

object FirstForenameOrInitial {
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val format: Format[FirstForenameOrInitial] = Json.valueFormat[FirstForenameOrInitial]
  val regex: Regex = """^[A-Za-z][A-Za-z\-' ]*$""".r
}

final case class SecondForenameOrInitial(value: String) {
  def validate: Option[String] = if (SecondForenameOrInitial.regex.matches(value)) None else Some("Invalid 'SecondForenameOrInitial'")
}

object SecondForenameOrInitial {
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val format: Format[SecondForenameOrInitial] = Json.valueFormat[SecondForenameOrInitial]
  val regex: Regex = """^[A-Za-z][A-Za-z\-' ]*$""".r
}

final case class DateOfBirth(value: String) {
  def validate: Option[String] = if (DateOfBirth.regex.matches(value)) None else Some("Invalid 'DateOfBirth'")
}

object DateOfBirth {
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val format: Format[DateOfBirth] = Json.valueFormat[DateOfBirth]
  val regex: Regex = """^(((19|20)([2468][048]|[13579][26]|0[48])|2000)[-]02[-]29|((19|20)[0-9]{2}[-](0[469]|11)[-](0[1-9]|1[0-9]|2[0-9]|30)|(19|20)[0-9]{2}[-](0[13578]|1[02])[-](0[1-9]|[12][0-9]|3[01])|(19|20)[0-9]{2}[-]02[-](0[1-9]|1[0-9]|2[0-8])))$""".r
}

final case class Title(value: String) {
  def validate: Option[String] = if (Title.regex.matches(value)) None else Some("Invalid 'title'")
}

object Title {
  implicit val format: Format[Title] = Json.valueFormat[Title]
  val regex: Regex = """^[A-Z0-9\-' ]*$""".r
}

final case class Address(
    addressType: AddressType,
    line:        Option[List[String]],
    postcode:    Option[Postcode]
) {

  def validate: Option[String] = postcode.flatMap(_.validate)
}

object Address {
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val format: OFormat[Address] = Json.format[Address]
}

sealed trait AddressType

case object NPSAddress extends AddressType

case object EditedAddress extends AddressType

object AddressType {
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val format: Format[AddressType] = new Format[AddressType] {
    def reads(json: JsValue): JsResult[AddressType] = json match {
      case JsString("NPS Address")    => JsSuccess(NPSAddress)
      case JsString("Edited Address") => JsSuccess(EditedAddress)
      case _                          => JsError("Invalid AddressType")
    }

    def writes(addressType: AddressType): JsValue = addressType match {
      case NPSAddress    => JsString("NPS Address")
      case EditedAddress => JsString("Edited Address")
    }
  }
}

final case class Postcode(value: String) {
  def validate: Option[String] = if (Postcode.regex.matches(value)) None else Some("Invalid 'postcode'")
}

object Postcode {
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val format: Format[Postcode] = Json.valueFormat[Postcode]
  val regex: Regex = """^[A-Z]{1,2}[0-9][0-9A-Z]?\s?[0-9][A-Z]{2}|BFPO\s?[0-9]{1,10}$""".r
}

final case class BankDetails(
    bankAccountNumber:     Option[BankAccountNumber],
    bankSortCode:          Option[BankSortCode],
    bankAccountName:       Option[BankAccountName],
    buildingSocietyRef:    Option[BuildingSocietyRef],
    designatedAccountFlag: Option[DesignatedAccountFlag],
    currency:              Option[Currency]
) {
  def validate: Option[String] =
    bankAccountNumber.flatMap(_.validate)
      .orElse(bankSortCode.flatMap(_.validate))
      .orElse(buildingSocietyRef.flatMap(_.validate))
      .orElse(designatedAccountFlag.flatMap(_.validate))
      .orElse(currency.flatMap(_.validate))
}

object BankDetails {
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val format: OFormat[BankDetails] = Json.format[BankDetails]
}

final case class BankAccountNumber(value: String) {
  def validate: Option[String] = if (BankAccountNumber.regex.matches(value)) None else Some("Invalid 'BankAccountNumber'")
}

object BankAccountNumber {
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val format: Format[BankAccountNumber] = Json.valueFormat[BankAccountNumber]
  val regex: Regex = """^[0-9]*$""".r
}

final case class BankSortCode(value: String) {
  def validate: Option[String] = if (BankSortCode.regex.matches(value)) None else Some("Invalid 'BankSortCode'")
}

object BankSortCode {
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val format: Format[BankSortCode] = Json.valueFormat[BankSortCode]
  val regex: Regex = """^[0-9]*$""".r
}

final case class BankAccountName(value: String)

object BankAccountName {
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val format: Format[BankAccountName] = Json.valueFormat[BankAccountName]
}

final case class BuildingSocietyRef(value: String) {
  def validate: Option[String] = if (BuildingSocietyRef.regex.matches(value)) None else Some("Invalid 'BuildingSocietyRef'")
}

object BuildingSocietyRef {
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val format: Format[BuildingSocietyRef] = Json.valueFormat[BuildingSocietyRef]
  val regex: Regex = """^[A-Z0-9\-/]*$""".r
}

final case class DesignatedAccountFlag(value: Int) {
  def validate: Option[String] = if (value >= 0 && value <= 1) None else Some("Invalid 'DesignatedAccountFlag'")
}

object DesignatedAccountFlag {
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val format: Format[DesignatedAccountFlag] = Json.valueFormat[DesignatedAccountFlag]
}

final case class Currency(value: String) {
  def validate: Option[String] = if (Currency.regex.matches(value)) None else Some("Invalid 'Currency'")
}

object Currency {
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val format: Format[Currency] = Json.valueFormat[Currency]
  val regex: Regex = """^[A-Za-z0-9\-' ]*$""".r
}

sealed trait PersonType

object PersonType {
  case object Customer extends PersonType
  case object Designate extends PersonType

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val format: Format[PersonType] = new Format[PersonType] {
    def reads(json: JsValue): JsResult[PersonType] = json match {
      case JsString("Customer")  => JsSuccess(Customer)
      case JsString("Designate") => JsSuccess(Designate)
      case _                     => JsError("Invalid PersonType")
    }

    def writes(personType: PersonType): JsValue = personType match {
      case Customer  => JsString("Customer")
      case Designate => JsString("Designate")
    }
  }
}

final case class RiskDataObject(
    personType:  PersonType,
    person:      Option[Person],
    bankDetails: Option[BankDetails]
) {
  def validate: Option[String] = person.flatMap(_.validate).orElse(bankDetails.flatMap(_.validate))
}

object RiskDataObject {
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val format: OFormat[RiskDataObject] = Json.format[RiskDataObject]
}

sealed trait ValidationResult

case object Yes extends ValidationResult

case object No extends ValidationResult

case object Indeterminate extends ValidationResult

case object Inapplicable extends ValidationResult

case object Error extends ValidationResult

object ValidationResult {
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val format: Format[ValidationResult] = new Format[ValidationResult] {
    def reads(json: JsValue): JsResult[ValidationResult] = json match {
      case JsString("yes")           => JsSuccess(Yes)
      case JsString("no")            => JsSuccess(No)
      case JsString("indeterminate") => JsSuccess(Indeterminate)
      case JsString("inapplicable")  => JsSuccess(Inapplicable)
      case JsString("error")         => JsSuccess(Error)
      case _                         => JsError("Invalid ValidationResult")
    }

    def writes(validationResult: ValidationResult): JsValue = validationResult match {
      case Yes           => JsString("yes")
      case No            => JsString("no")
      case Indeterminate => JsString("indeterminate")
      case Inapplicable  => JsString("inapplicable")
      case Error         => JsString("error")
    }
  }
}

final case class BankValidationResults(
    accountExists:      Option[ValidationResult],
    nameMatches:        Option[ValidationResult],
    addressMatches:     Option[ValidationResult],
    nonConsented:       Option[ValidationResult],
    subjectHasDeceased: Option[ValidationResult]
)

object BankValidationResults {
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val format: OFormat[BankValidationResults] = Json.format[BankValidationResults]
}

final case class TransactionMonitoringResults(
    transactionMonitoringScore: Option[BigDecimal]
)

object TransactionMonitoringResults {
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val format: OFormat[TransactionMonitoringResults] = Json.format[TransactionMonitoringResults]
}

final case class GetBankDetailsRiskResultRequest(
    header:                       Header,
    paymentData:                  Option[PaymentData],
    employerData:                 Option[EmployerData],
    riskData:                     List[RiskDataObject],
    bankValidationResults:        Option[BankValidationResults],
    transactionMonitoringResults: Option[TransactionMonitoringResults]
) {
  def validate: Option[String] =
    header.validate
      .orElse(employerData.flatMap(_.validate))
      .orElse(riskData.collectFirst(_.validate).flatten)
}

object GetBankDetailsRiskResultRequest {
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val format: OFormat[GetBankDetailsRiskResultRequest] = Json.format[GetBankDetailsRiskResultRequest]
}
