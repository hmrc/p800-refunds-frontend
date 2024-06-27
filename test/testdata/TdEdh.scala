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
      paymentNumber = Some(userEnteredP800Reference.sanitiseReference.value) //TODO: according to analysis this comes form repayment status API, but we don't call it.
    )),
    employerData = None, //TODO: according to the analysis: confirm this is not needed

    riskData                     = List(RiskDataObject(
      personType  = PersonType.Customer,
      person      = Some(Person(
        //TODO: according to the analysis all those fields come from the CitisenDetails API, which we don't call
        surname                 = Some(Surname(dependencies.surnameGreg)),
        firstForenameOrInitial  = Some(FirstForenameOrInitial(dependencies.firstNameGreg)),
        secondForenameOrInitial = Some(SecondForenameOrInitial(dependencies.secondForenameGreggson)),
        nino                    = nino,
        dateOfBirth             = DateOfBirth(`dateOfBirthFormatted YYYY-MM-DD`),
        title                   = Some(Title(dependencies.title)),
        address                 = None
      )),
      bankDetails = Some(BankDetails(
        bankAccountNumber     = Some(BankAccountNumber(dependencies.bankAccountNumber)),
        bankSortCode          = Some(BankSortCode(dependencies.sortCode)),
        bankAccountName       = None, // Note: Check the spec as this field is limited to 28 characters & has a regex
        buildingSocietyRef    = None,
        designatedAccountFlag = None,
        currency              = None
      ))
    )),
    bankValidationResults        = None, // NOTE: As we have agreed to not call BARS we can't pass anything here
    transactionMonitoringResults = None
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
}
