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

import edh._

trait TdEdh { dependencies: TdBase =>

  lazy val claimId: ClaimId = ClaimId("a3d35e50-ea17-4865-a2d2-38b0069b2665")

  lazy val getBankDetailsRiskResultRequest: GetBankDetailsRiskResultRequest = GetBankDetailsRiskResultRequest(
    header       = Header(
      transactionID = TransactionID(claimId.value),
      requesterID   = RequesterID("Repayment Service"),
      serviceID     = ServiceID("P800")
    ),
    paymentData  = Some(PaymentData(
      paymentAmount = Some(paymentAmount), //TODO: according to analysis this comes form repayment status API, but we don't call it.
      paymentNumber = Some(p800Reference.sanitiseReference.value.toInt) //TODO: according to analysis this comes form repayment status API, but we don't call it.
    )),
    employerData = None, //TODO: according to the analysis: confirm this is not needed

    riskData                     = List(RiskDataObject(
      personType  = PersonType.Customer,
      person      = Some(Person(
        //TODO: according to the analysis all those fields come from the CitisenDetails API, which we don't call
        surname                 = Surname(dependencies.surname),
        firstForenameOrInitial  = Some(FirstForenameOrInitial(dependencies.firstForename)),
        secondForenameOrInitial = None,
        nino                    = nino,
        dateOfBirth             = DateOfBirth(`dateOfBirthFormatted YYYY-MM-DD`),
        title                   = Some(Title(dependencies.title)),
        address                 = None
      )),
      bankDetails = Some(BankDetails(
        bankAccountNumber     = Some(BankAccountNumber(dependencies.bankAccountNumber)),
        bankSortCode          = Some(BankSortCode(dependencies.sortCode)),
        bankAccountName       = Some(BankAccountName(dependencies.bankAccountSummary.displayName.value)),
        buildingSocietyRef    = None, //TODO: this has not been analysed
        designatedAccountFlag = None, //TODO: according to the analysis: confirm this is not needed, Collected from user Journey, Is the same value as personType
        currency              = None //TODO: according to the analysis: confirm this is not needed, Always "GBP"
      ))
    )),
    bankValidationResults        = None, //as agreed we don't call BARS to obtain those data and won't pass anything here
    transactionMonitoringResults = None //TODO: this has not been analysed so don't know what needs to passed here
  )

  lazy val getBankDetailsRiskResultResponse: GetBankDetailsRiskResultResponse = GetBankDetailsRiskResultResponse(
    header                = Header(
      transactionID = TransactionID(claimId.value),
      requesterID   = RequesterID("Repayment Service"),
      serviceID     = ServiceID("P800")
    ),
    bankValidationResults = Some(BankValidationResults(
      accountExists      = Some(ValidationResult.Yes),
      nameMatches        = Some(ValidationResult.No),
      addressMatches     = Some(ValidationResult.Indeterminate),
      nonConsented       = Some(ValidationResult.Inapplicable),
      subjectHasDeceased = Some(ValidationResult.No)
    )),
    overallRiskResult     = OverallRiskResult(
      ruleScore  = 75,
      nextAction = NextAction.Pay
    ),
    riskResults           = Some(List(
      RuleResult(
        ruleId          = "Rule1",
        ruleInformation = Some("Additional information for Rule1"),
        ruleScore       = 80,
        nextAction      = NextAction.Pay
      ),
      RuleResult(
        ruleId          = "Rule2",
        ruleInformation = None,
        ruleScore       = 70,
        nextAction      = NextAction.Pay
      )
    ))
  )

  lazy val getBankDetailsRiskResultResponseDoNotPay: GetBankDetailsRiskResultResponse = getBankDetailsRiskResultResponse.copy(
    overallRiskResult = OverallRiskResult(
      ruleScore  = 45,
      nextAction = NextAction.DoNotPay
    )
  )

  lazy val clientUId: ClientUId = ClientUId(claimId.value)
  lazy val caseManagementRequest: CaseManagementRequest = CaseManagementRequest(
    clientUId             = clientUId,
    clientSystemId        = ClientSystemId("MDTP"),
    nino                  = nino,
    bankSortCode          = BankSortCode(sortCode),
    bankAccountNumber     = BankAccountNumber(bankAccountNumber),
    bankAccountName       = BankAccountName(bankAccountSummary.displayName.value),
    designatedAccountFlag = 1,
    contact               = List(
      CaseManagementContact(
        `type`    = PersonType.Customer,
        firstName = dependencies.firstForename,
        surname   = dependencies.surname,
        address   = List()
      )
    ),
    currency              = bankAccountSummary.currency,
    paymentAmount         = paymentAmount,
    overallRiskResult     = 45,
    ruleResults           = Some(List(
      CaseManagementRuleResult(
        ruleId          = Some("Rule1"),
        ruleInformation = Some("Additional information for Rule1"),
        ruleScore       = Some(80)
      ),
      CaseManagementRuleResult(
        ruleId          = Some("Rule2"),
        ruleInformation = None,
        ruleScore       = Some(70)
      )
    )),
    nameMatches           = None,
    addressMatches        = None,
    accountExists         = None,
    subjectHasDeceased    = None,
    nonConsented          = None,
    reconciliationId      = None,
    taxDistrictNumber     = None,
    payeNumber            = None
  )
}
