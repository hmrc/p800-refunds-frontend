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

package testdata

import _root_.nps.models._
import org.apache.pekko.http.scaladsl.model.Uri
import models._
import models.attemptmodels.{AttemptId, AttemptInfo, IpAddress, NumberOfAttempts}
import models.dateofbirth.{DateOfBirth, DayOfMonth, Month, Year}
import models.ecospend._
import models.ecospend.account._
import models.ecospend.consent._
import models.p800externalapi.EventValue
import nps.models.ReferenceCheckResult.P800ReferenceChecked
import testsupport.ItSpec

import java.time.format.DateTimeFormatter
import java.time.{Instant, LocalDateTime, ZoneOffset}
import java.util.UUID
import java.util.Currency

trait TdBase {

  lazy val dateString: String = "2059-11-25"
  lazy val timeString: String = s"${dateString}T16:33:51.880"
  lazy val localDateTime: LocalDateTime = {
    //the frozen time has to be in future otherwise the journeys will disappear from mongodb because of expiry index
    LocalDateTime.parse(timeString, DateTimeFormatter.ISO_DATE_TIME)
  }
  lazy val instant: Instant = localDateTime.toInstant(ZoneOffset.UTC)
  lazy val newInstant: Instant = instant.plusSeconds(20) //used when a new journey is created from existing one

  lazy val p800Reference: P800Reference = P800Reference("12345678")

  lazy val gdsDateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMMM Y")

  lazy val dayOfMonth: DayOfMonth = DayOfMonth("1")
  lazy val month: Month = Month("1")
  lazy val year: Year = Year("2000")
  lazy val dateOfBirth: DateOfBirth = DateOfBirth(dayOfMonth, month, year)
  lazy val dateOfBirthFormatted: String = "01 January 2000"
  lazy val `dateOfBirthFormatted YYYY-MM-DD`: String = "2000-01-01"

  lazy val nino: Nino = Nino("LM001014C")

  lazy val p800ReferenceChecked: P800ReferenceChecked = ReferenceCheckResult.P800ReferenceChecked(
    reconciliationIdentifier = ReconciliationIdentifier(123),
    paymentNumber            = p800Reference,
    payeNumber               = PayeNumber("PayeNumber-123"),
    taxDistrictNumber        = TaxDistrictNumber("EAST LONDON AREA (SERVICE) (717)"),
    paymentAmount            = BigDecimal("12.34"),
    associatedPayableNumber  = AssociatedPayableNumber(1234),
    customerAccountNumber    = CustomerAccountNumber("customerAccountNumber-1234"),
    currentOptimisticLock    = CurrentOptimisticLock(15)
  )

  lazy val traceIndividualResponse = TraceIndividualResponse(
    title          = Some("Sir"),
    firstForename  = Some("Accolon"),
    secondForename = None,
    surname        = "of Gaul"
  )

  lazy val bankId: BankId = BankId("obie-barclays-personal")
  lazy val bankDescription: BankDescription = BankDescription(
    bankId       = bankId,
    name         = BankName("Barclays Personal"),
    friendlyName = BankFriendlyName("Barclays Personal"),
    logoUrl      = Uri("https://logo.com"),
    iconUrl      = Uri("https://public.ecospend.com/images/banks/Barclays_icon.svg")
  )

  def attemptInfo(failedAttempts: Int): AttemptInfo = AttemptInfo(
    _id                    = AttemptId(UUID.fromString("52e31cd7-23ec-42f9-99d6-e159b6242aa3")),
    createdAt              = instant,
    ipAddress              = IpAddress("127.0.0.1"),
    numberOfFailedAttempts = NumberOfAttempts(failedAttempts)
  )

  lazy val consentId: ConsentId = ConsentId("00000000-0000-0000-0000-000000000000")
  lazy val bankReferenceId: BankReferenceId = BankReferenceId("MyBank-129781876126")
  lazy val bankConsentUrl: Uri = Uri(s"http://localhost:${ItSpec.testServerPort.toString}/get-an-income-tax-refund/test-only/bank-page")
  lazy val redirectUrl: Uri = Uri(s"http://localhost:${ItSpec.testServerPort.toString}/get-an-income-tax-refund/bank-transfer/verifying-your-bank-account")
  lazy val permissions: List[ConsentPermission] = List(
    ConsentPermission.Account,
    ConsentPermission.Balance,
    ConsentPermission.Transactions,
    ConsentPermission.DirectDebits,
    ConsentPermission.StandingOrders,
    ConsentPermission.Parties
  )

  lazy val bankConsent: BankConsentResponse = BankConsentResponse(
    id                = consentId,
    bankReferenceId   = bankReferenceId,
    bankConsentUrl    = bankConsentUrl,
    bankId            = bankId,
    status            = ConsentStatus.AwaitingAuthorization,
    redirectUrl       = redirectUrl,
    consentEndDate    = localDateTime,
    consentExpiryDate = localDateTime,
    permissions       = permissions
  )

  lazy val accountId: UUID = UUID.fromString("00000000-0000-0000-0000-000000000000")
  lazy val bankAccountSummary: BankAccountSummary = BankAccountSummary(
    id                    = accountId,
    bankId                = bankId,
    merchantId            = None,
    merchantUserId        = None,
    ttype                 = BankAccountType.Personal,
    subType               = BankAccountSubType.CurrentAccount,
    currency              = Currency.getInstance("GBP"),
    accountFormat         = BankAccountFormat.SortCode,
    accountIdentification = BankAccountIdentification("44556610002333"),
    calculatedOwnerName   = CalculatedOwnerName("Greg Greggson"),
    accountOwnerName      = BankAccountOwnerName("Greg Greggson"),
    displayName           = BankAccountDisplayName("bank account display name"),
    balance               = 123.7,
    lastUpdateTime        = localDateTime,
    parties               = List(BankAccountParty(
      name          = BankPartyName("Greg Greggson"),
      fullLegalName = BankPartyFullLegalName("Greg Greggory Greggson")
    ))
  )

  lazy val isValidEventValueValid: EventValue = EventValue.Valid
  lazy val isValidEventValueNotValid: EventValue = EventValue.NotValid
  lazy val isValidEventValueNotReceived: EventValue = EventValue.NotReceived

  lazy val claimOverpaymentRequest: ClaimOverpaymentRequest = ClaimOverpaymentRequest(
    currentOptimisticLock    = CurrentOptimisticLock(15),
    reconciliationIdentifier = ReconciliationIdentifier(123),
    associatedPayableNumber  = AssociatedPayableNumber(1234),
    payeeBankAccountNumber   = PayeeBankAccountNumber("10002333"),
    payeeBankSortCode        = PayeeBankSortCode("445566"),
    payeeBankAccountName     = PayeeBankAccountName("bank account display name"),
    designatedPayeeAccount   = DesignatedPayeeAccount(true)
  )
  lazy val claimOverpaymentResponse: ClaimOverpaymentResult.ClaimOverpaymentResponse = ClaimOverpaymentResult.ClaimOverpaymentResponse(
    identifer             = nino,
    currentOptimisticLock = CurrentOptimisticLock(15)
  )
}
