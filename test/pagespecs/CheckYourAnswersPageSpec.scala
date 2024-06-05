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

package pagespecs

import models.dateofbirth.{DateOfBirth, DayOfMonth, Month, Year}
import models.journeymodels.{Journey, JourneyType}
import models.{Nino, P800Reference, UserEnteredP800Reference}
import nps.models.TraceIndividualRequest
import play.api.libs.json.{Json, JsObject}
import testsupport.ItSpec
import testsupport.stubs.AuditConnectorStub
import testsupport.stubs.{TraceIndividualStub, VerifyP800ReferenceStub}

class CheckYourAnswersPageSpec extends ItSpec {

  "page renders correctly" - {
    "bank transfer" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.journeyEnteredDateOfBirth)
      pages.checkYourAnswersBankTransferPage.open()
      pages.checkYourAnswersBankTransferPage.assertPageIsDisplayedForBankTransfer(
        tdAll.userEnteredP800Reference,
        tdAll.dateOfBirthFormatted,
        tdAll.nino
      )
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeyEnteredDateOfBirth
    }
    "cheque" in {
      upsertJourneyToDatabase(tdAll.Cheque.journeyEnteredNino)
      pages.checkYourAnswersChequePage.open()
      pages.checkYourAnswersChequePage.assertPageIsDisplayedForCheque(
        tdAll.userEnteredP800Reference,
        tdAll.nino
      )
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.Cheque.journeyEnteredNino
    }
  }

  "changing reference" - {
    "bank transfer" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.journeyEnteredDateOfBirth)
      pages.checkYourAnswersBankTransferPage.open()
      pages.checkYourAnswersBankTransferPage.assertPageIsDisplayedForBankTransfer(
        tdAll.userEnteredP800Reference,
        tdAll.dateOfBirthFormatted,
        tdAll.nino
      )
      pages.checkYourAnswersBankTransferPage.clickChangeReference()
      pages.whatIsYourP800ReferenceBankTransferPage.assertPageIsDisplayed()
      val newReference = UserEnteredP800Reference("123455")
      pages.whatIsYourP800ReferenceBankTransferPage.enterP800Reference(newReference.value)
      pages.whatIsYourP800ReferenceBankTransferPage.clickSubmit()
      pages.checkYourAnswersBankTransferPage.assertPageIsDisplayedForBankTransfer(
        newReference,
        tdAll.dateOfBirthFormatted,
        tdAll.nino
      )
      val expectedJourney = tdAll.BankTransfer.journeyEnteredDateOfBirth.copy(
        p800Reference = Some(newReference)
      )
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike expectedJourney
    }
    "cheque" in {
      upsertJourneyToDatabase(tdAll.Cheque.journeyEnteredNino)
      pages.checkYourAnswersChequePage.open()
      pages.checkYourAnswersChequePage.clickChangeReference()
      pages.whatIsYourP800ReferenceChequePage.assertPageIsDisplayed()
      val newReference = UserEnteredP800Reference("123456")
      pages.whatIsYourP800ReferenceChequePage.enterP800Reference(newReference.value)
      pages.whatIsYourP800ReferenceChequePage.clickSubmit()
      pages.checkYourAnswersChequePage.assertPageIsDisplayedForCheque(
        newReference,
        tdAll.nino
      )
      val expectedJourney = tdAll.Cheque.journeyEnteredNino.copy(
        p800Reference = Some(newReference)
      )
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike expectedJourney
    }
  }

  "changing date of birth" - {
    "bank transfer" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.journeyEnteredDateOfBirth)
      pages.checkYourAnswersBankTransferPage.open()
      pages.checkYourAnswersBankTransferPage.clickChangeDateOfBirth()
      pages.enterYourDateOfBirthPage.assertPageIsDisplayed()
      val newDob = DateOfBirth(DayOfMonth("19"), Month("Feb"), Year("2001"))
      pages.enterYourDateOfBirthPage.enterYear(newDob.year.value)
      pages.enterYourDateOfBirthPage.enterMonth(newDob.month.value)
      pages.enterYourDateOfBirthPage.enterDayOfMonth(newDob.dayOfMonth.value)

      pages.enterYourDateOfBirthPage.clickSubmit()
      pages.checkYourAnswersBankTransferPage.assertPageIsDisplayedForBankTransfer(
        tdAll.userEnteredP800Reference,
        "19 February 2001",
        tdAll.nino
      )
      val expectedJourney = tdAll.BankTransfer.journeyEnteredDateOfBirth.copy(
        dateOfBirth = Some(newDob)
      )
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike expectedJourney
    }
  }

  "changing national insurance number" - {
    "bank transfer" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.journeyEnteredDateOfBirth)
      pages.checkYourAnswersBankTransferPage.open()
      pages.checkYourAnswersBankTransferPage.clickChangeNationalInsuranceNumber()
      pages.enterYourNationalInsuranceNumberBankTransferPage.assertPageIsDisplayed()
      val newNino = Nino("AB123123C")
      pages.enterYourNationalInsuranceNumberBankTransferPage.enterNationalInsuranceNumber(newNino)
      pages.enterYourNationalInsuranceNumberBankTransferPage.clickSubmit()
      pages.checkYourAnswersBankTransferPage.assertPageIsDisplayedForBankTransfer(
        tdAll.userEnteredP800Reference,
        tdAll.dateOfBirthFormatted,
        newNino
      )
      val expectedJourney = tdAll.BankTransfer.journeyEnteredDateOfBirth.copy(
        nino = Some(newNino)
      )
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike expectedJourney
    }
    "cheque" in {
      upsertJourneyToDatabase(tdAll.Cheque.journeyEnteredNino)
      pages.checkYourAnswersChequePage.open()
      pages.checkYourAnswersChequePage.clickChangeNationalInsuranceNumber()
      val newNino = Nino("AB123123C")
      pages.enterYourNationalInsuranceNumberChequePage.enterNationalInsuranceNumber(newNino)
      pages.enterYourNationalInsuranceNumberChequePage.clickSubmit()
      pages.checkYourAnswersChequePage.assertPageIsDisplayedForCheque(
        tdAll.userEnteredP800Reference,
        newNino
      )
      val expectedJourney = tdAll.Cheque.journeyEnteredNino.copy(
        nino = Some(newNino)
      )
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike expectedJourney
    }
  }

  "clicking submit redirects to 'Your identity is confirmed' if response from NPS indicates identity verification is successful" - {
    "bank transfer" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.journeyEnteredDateOfBirth)
      pages.checkYourAnswersBankTransferPage.open()
      val j = tdAll.BankTransfer.journeyAfterTracedIndividual
      VerifyP800ReferenceStub.p800ReferenceChecked(j.nino.value, tdAll.p800Reference, j.getP800ReferenceChecked(request = tdAll.fakeRequest))
      TraceIndividualStub.traceIndividual(
        request  = TraceIndividualRequest(j.nino.value, tdAll.`dateOfBirthFormatted YYYY-MM-DD`),
        response = j.traceIndividualResponse.value
      )

      pages.checkYourAnswersBankTransferPage.clickSubmit()
      pages.yourIdentityIsConfirmedBankTransferPage.assertPageIsDisplayed()
      VerifyP800ReferenceStub.verify(tdAll.correlationId)
      TraceIndividualStub.verifyTraceIndividual(tdAll.correlationId)
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike j

      AuditConnectorStub.verifyEventAudited(
        AuditConnectorStub.validateUserDetailsAuditType,
        Json.parse(
          //format=JSON
          """
          {
            "outcome": {
              "isSuccessful": true,
              "lockout": false,
              "reasons": []
            },
            "userEnteredDetails": {
              "repaymentMethod": "bank",
              "p800Reference": 12345678,
              "nino": "LM001014C",
              "dob": "2000-01-01"
            },
            "repaymentAmount": 12.34,
            "repaymentInformation": {
              "reconciliationIdentifier": 123,
              "paymentNumber": 12345678,
              "payeNumber": "PayeNumber-123",
              "taxDistrictNumber": 717,
              "associatedPayableNumber": 1234,
              "customerAccountNumber": "customerAccountNumber-1234",
              "currentOptimisticLock": 15
            }
          }
          """.stripMargin
        ).as[JsObject]
      )
    }
    "cheque" in {
      upsertJourneyToDatabase(tdAll.Cheque.journeyEnteredNino)
      pages.checkYourAnswersChequePage.open()
      val j = tdAll.Cheque.AfterReferenceCheck.journeyReferenceChecked
      VerifyP800ReferenceStub.p800ReferenceChecked(j.nino.value, tdAll.p800Reference, j.getP800ReferenceChecked(request = tdAll.fakeRequest))
      pages.checkYourAnswersChequePage.clickSubmit()
      pages.yourIdentityIsConfirmedChequePage.assertPageIsDisplayed()
      VerifyP800ReferenceStub.verify(tdAll.correlationId)
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike j

      AuditConnectorStub.verifyEventAudited(
        AuditConnectorStub.validateUserDetailsAuditType,
        Json.parse(
          //format=JSON
          """
          {
            "outcome": {
              "isSuccessful": true,
              "lockout": false,
              "reasons": []
            },
            "userEnteredDetails": {
              "repaymentMethod": "cheque",
              "p800Reference": 12345678,
              "nino": "LM001014C"
            },
            "repaymentAmount": 12.34,
            "repaymentInformation": {
              "reconciliationIdentifier": 123,
              "paymentNumber": 12345678,
              "payeNumber": "PayeNumber-123",
              "taxDistrictNumber": 717,
              "associatedPayableNumber": 1234,
              "customerAccountNumber": "customerAccountNumber-1234",
              "currentOptimisticLock": 15
            }
          }
          """.stripMargin
        ).as[JsObject]
      )
    }

    "with p800 reference getting sanitised before being sent, if user decides to put leading 0's, or any of the other bizarre 'allowed' characters" in {
      val p800ReferenceThatShouldBeSent = P800Reference(1234560)
      val expectedCheckResult = Some(tdAll.p800ReferenceChecked.copy(paymentNumber = p800ReferenceThatShouldBeSent))
      val journey = tdAll.BankTransfer.journeyEnteredDateOfBirth.copy(p800Reference = Some(UserEnteredP800Reference("00123,- 4560")))
      upsertJourneyToDatabase(journey)
      pages.checkYourAnswersBankTransferPage.open()
      val j = tdAll.BankTransfer.journeyAfterTracedIndividual.copy(
        p800Reference        = Some(UserEnteredP800Reference("00123,- 4560")),
        referenceCheckResult = expectedCheckResult
      )
      VerifyP800ReferenceStub.p800ReferenceChecked(j.nino.value, p800ReferenceThatShouldBeSent, j.getP800ReferenceChecked(request = tdAll.fakeRequest).copy(paymentNumber = p800ReferenceThatShouldBeSent))
      TraceIndividualStub.traceIndividual(
        request  = TraceIndividualRequest(j.nino.value, tdAll.`dateOfBirthFormatted YYYY-MM-DD`),
        response = j.traceIndividualResponse.value
      )

      pages.checkYourAnswersBankTransferPage.clickSubmit()
      pages.yourIdentityIsConfirmedBankTransferPage.assertPageIsDisplayed()
      VerifyP800ReferenceStub.verify(tdAll.correlationId)
      TraceIndividualStub.verifyTraceIndividual(tdAll.correlationId)
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike j
    }
  }

  "clicking submit redirects to 'We cannot confirm your identity' if response from NPS indicates identity verification failed" - {

    "bank transfer - failed attempts repo is empty" in {
      val j = tdAll.BankTransfer.journeyEnteredDateOfBirth
      upsertJourneyToDatabase(j)
      VerifyP800ReferenceStub.checkReferenceReferenceDidntMatchNino(j.nino.value, tdAll.p800Reference)
      getFailedAttemptCount() shouldBe None
      //No need for TraceIndividual stub as it won't be called

      pages.checkYourAnswersBankTransferPage.open()
      pages.checkYourAnswersBankTransferPage.assertPageIsDisplayedForBankTransfer(
        tdAll.userEnteredP800Reference,
        tdAll.dateOfBirthFormatted,
        tdAll.nino
      )
      pages.checkYourAnswersBankTransferPage.clickSubmit()
      pages.cannotConfirmYourIdentityTryAgainBankTransferPage.assertPageIsDisplayed(JourneyType.BankTransfer)
      VerifyP800ReferenceStub.verify(tdAll.correlationId)
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.AfterReferenceCheck.journeyReferenceDidntMatchNino
      getFailedAttemptCount() shouldBe Some(1)

      AuditConnectorStub.verifyEventAudited(
        AuditConnectorStub.validateUserDetailsAuditType,
        Json.parse(
          //format=JSON
          """
          {
            "outcome": {
              "isSuccessful": false,
              "attemptsOnRecord": 1,
              "lockout": false,
              "apiResponsibleForFailure": "p800 reference check",
              "reasons": [
                "NPS indicated that the reference did not match the NINO"
              ]
            },
            "userEnteredDetails": {
              "repaymentMethod": "bank",
              "p800Reference": 12345678,
              "nino": "LM001014C",
              "dob": "2000-01-01"
            }
          }
          """.stripMargin
        ).as[JsObject]
      )
    }

    "cheque - failed attempts repo is empty" in {
      val j = tdAll.Cheque.journeyEnteredNino
      upsertJourneyToDatabase(j)
      VerifyP800ReferenceStub.checkReferenceReferenceDidntMatchNino(j.nino.value, tdAll.p800Reference)
      getFailedAttemptCount() shouldBe None

      pages.checkYourAnswersChequePage.open()
      pages.checkYourAnswersChequePage.clickSubmit()
      pages.cannotConfirmYourIdentityTryAgainChequePage.assertPageIsDisplayed(JourneyType.Cheque)

      VerifyP800ReferenceStub.verify(tdAll.correlationId)
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.Cheque.AfterReferenceCheck.journeyReferenceDidntMatchNino
      getFailedAttemptCount() shouldBe Some(1)

      AuditConnectorStub.verifyEventAudited(
        AuditConnectorStub.validateUserDetailsAuditType,
        Json.parse(
          //format=JSON
          """
          {
            "outcome": {
              "isSuccessful": false,
              "attemptsOnRecord": 1,
              "lockout": false,
              "apiResponsibleForFailure": "p800 reference check",
              "reasons": [
                "NPS indicated that the reference did not match the NINO"
              ]
            },
            "userEnteredDetails": {
              "repaymentMethod": "cheque",
              "p800Reference": 12345678,
              "nino": "LM001014C"
            }
          }
          """.stripMargin
        ).as[JsObject]
      )
    }

    "bank transfer - user already failed 1 time" in {
      val j = tdAll.BankTransfer.AfterReferenceCheck.journeyReferenceDidntMatchNino
      VerifyP800ReferenceStub.checkReferenceReferenceDidntMatchNino(j.nino.value, tdAll.p800Reference)

      upsertJourneyToDatabase(j)
      upsertFailedAttemptToDatabase(tdAll.attemptInfo(failedAttempts = 1))

      pages.checkYourAnswersBankTransferPage.open()
      pages.checkYourAnswersBankTransferPage.clickSubmit()
      pages.cannotConfirmYourIdentityTryAgainBankTransferPage.assertPageIsDisplayed(JourneyType.BankTransfer)

      VerifyP800ReferenceStub.verify(tdAll.correlationId)
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike j
      getFailedAttemptCount() shouldBe Some(2)
    }

    "cheque - user already failed 1 time" in {
      val j = tdAll.Cheque.AfterReferenceCheck.journeyReferenceDidntMatchNino
      VerifyP800ReferenceStub.checkReferenceReferenceDidntMatchNino(j.nino.value, tdAll.p800Reference)
      upsertJourneyToDatabase(j)
      upsertFailedAttemptToDatabase(tdAll.attemptInfo(failedAttempts = 1))

      pages.checkYourAnswersChequePage.open()
      pages.checkYourAnswersChequePage.clickSubmit()
      pages.cannotConfirmYourIdentityTryAgainChequePage.assertPageIsDisplayed(JourneyType.Cheque)

      VerifyP800ReferenceStub.verify(tdAll.correlationId)
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike j
      getFailedAttemptCount() shouldBe Some(2)
    }
  }

  "clicking submit, resulting in failing verification too many times, redirects to 'No more attempts left' page" - {

    "bank transfer - when user has 2 existing failed attempts, 3rd attempt also fails" in {
      val j: Journey = tdAll.BankTransfer.AfterReferenceCheck.journeyReferenceDidntMatchNino
      VerifyP800ReferenceStub.checkReferenceReferenceDidntMatchNino(j.nino.value, tdAll.p800Reference)

      upsertJourneyToDatabase(j)
      upsertFailedAttemptToDatabase(tdAll.attemptInfo(2))
      test(JourneyType.BankTransfer)

      pages.noMoreAttemptsLeftToConfirmYourIdentityBankTransferPage.assertPageIsDisplayed()
      VerifyP800ReferenceStub.verify(tdAll.correlationId)
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeyLockedOutFromFailedAttempts
      getFailedAttemptCount() shouldBe Some(3)

      AuditConnectorStub.verifyEventAudited(
        AuditConnectorStub.validateUserDetailsAuditType,
        Json.parse(
          //format=JSON
          """
          {
            "outcome": {
              "isSuccessful": false,
              "attemptsOnRecord": 3,
              "lockout": true,
              "apiResponsibleForFailure": "p800 reference check",
              "reasons": [
                "Max attempts reached. User is locked out.",
                "NPS indicated that the reference did not match the NINO"
              ]
            },
            "userEnteredDetails": {
              "repaymentMethod": "bank",
              "p800Reference": 12345678,
              "nino": "LM001014C",
              "dob": "2000-01-01"
            }
          }
          """.stripMargin
        ).as[JsObject]
      )
    }

    "cheque - when user has 2 existing failed attempts, 3rd attempt also fails" in {
      val j: Journey = tdAll.Cheque.AfterReferenceCheck.journeyReferenceDidntMatchNino
      VerifyP800ReferenceStub.checkReferenceReferenceDidntMatchNino(j.nino.value, tdAll.p800Reference)

      upsertJourneyToDatabase(j)
      upsertFailedAttemptToDatabase(tdAll.attemptInfo(2))
      test(JourneyType.Cheque)

      pages.noMoreAttemptsLeftToConfirmYourIdentityChequePage.assertPageIsDisplayed()
      VerifyP800ReferenceStub.verify(tdAll.correlationId)
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.Cheque.journeyLockedOutFromFailedAttempts
      getFailedAttemptCount() shouldBe Some(3)

      AuditConnectorStub.verifyEventAudited(
        AuditConnectorStub.validateUserDetailsAuditType,
        Json.parse(
          //format=JSON
          """
          {
            "outcome": {
              "isSuccessful": false,
              "attemptsOnRecord": 3,
              "lockout": true,
              "apiResponsibleForFailure": "p800 reference check",
              "reasons": [
                "Max attempts reached. User is locked out.",
                "NPS indicated that the reference did not match the NINO"
              ]
            },
            "userEnteredDetails": {
              "repaymentMethod": "cheque",
              "p800Reference": 12345678,
              "nino": "LM001014C"
            }
          }
          """.stripMargin
        ).as[JsObject]
      )
    }

      def test(journeyType: JourneyType): Unit = {
        val startPage = journeyType match {
          case JourneyType.Cheque       => pages.checkYourAnswersChequePage
          case JourneyType.BankTransfer => pages.checkYourAnswersBankTransferPage
        }
        startPage.open()
        startPage.clickSubmit()
      }
  }

  "clicking submit, resulting in Nps indicating that refund has already been claimed, redirects to 'There is a problem' page" - {
    "bank transfer" in {
      val j: Journey = tdAll.BankTransfer.journeyEnteredDateOfBirth
      VerifyP800ReferenceStub.refundAlreadyTaken(j.nino.value, tdAll.p800Reference)
      upsertJourneyToDatabase(j)

      test(JourneyType.BankTransfer)

      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.AfterReferenceCheck.journeyRefundAlreadyTaken
      VerifyP800ReferenceStub.verify(tdAll.correlationId)
      TraceIndividualStub.verifyNoneTraceIndividual()

      AuditConnectorStub.verifyEventAudited(
        AuditConnectorStub.validateUserDetailsAuditType,
        Json.parse(
          //format=JSON
          """
          {
            "outcome": {
              "isSuccessful": false,
              "lockout": false,
              "apiResponsibleForFailure": "p800 reference check",
              "reasons": [
                "NPS indicated that refund has already been claimed"
              ]
            },
            "userEnteredDetails": {
              "repaymentMethod": "bank",
              "p800Reference": 12345678,
              "nino": "LM001014C",
              "dob": "2000-01-01"
            }
          }
          """.stripMargin
        ).as[JsObject]
      )
    }
    "cheque" in {
      val j: Journey = tdAll.Cheque.journeyEnteredNino
      VerifyP800ReferenceStub.refundAlreadyTaken(j.nino.value, tdAll.p800Reference)
      upsertJourneyToDatabase(j)

      test(JourneyType.Cheque)

      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.Cheque.AfterReferenceCheck.journeyRefundAlreadyTaken
      VerifyP800ReferenceStub.verify(tdAll.correlationId)
      TraceIndividualStub.verifyNoneTraceIndividual()

      AuditConnectorStub.verifyEventAudited(
        AuditConnectorStub.validateUserDetailsAuditType,
        Json.parse(
          //format=JSON
          """
          {
            "outcome": {
              "isSuccessful": false,
              "lockout": false,
              "apiResponsibleForFailure": "p800 reference check",
              "reasons": [
                "NPS indicated that refund has already been claimed"
              ]
            },
            "userEnteredDetails": {
              "repaymentMethod": "cheque",
              "p800Reference": 12345678,
              "nino": "LM001014C"
            }
          }
          """.stripMargin
        ).as[JsObject]
      )
    }

      def test(journeyType: JourneyType): Unit = {
        val startPage = journeyType match {
          case JourneyType.Cheque       => pages.checkYourAnswersChequePage
          case JourneyType.BankTransfer => pages.checkYourAnswersBankTransferPage
        }
        startPage.open()
        startPage.clickSubmit()
        pages.thereIsAProblemPage.assertPageIsDisplayed()
      }
  }

  "clicking submit redirects to 'We cannot confirm your identity' if P800 ref is outside of NPS spec number bounds (greater than 2147483646), but doesn't actually call NPS" in {
    val p800RefOutOfBounds = UserEnteredP800Reference("2147483647")
    val j = tdAll.BankTransfer.journeyEnteredDateOfBirth.copy(p800Reference = Some(p800RefOutOfBounds))
    upsertJourneyToDatabase(j)
    getFailedAttemptCount() shouldBe None
    //No need for TraceIndividual stub as it won't be called

    pages.checkYourAnswersBankTransferPage.open()
    pages.checkYourAnswersBankTransferPage.assertPageIsDisplayedForBankTransfer(
      p800RefOutOfBounds,
      tdAll.dateOfBirthFormatted,
      tdAll.nino
    )
    pages.checkYourAnswersBankTransferPage.clickSubmit()
    pages.cannotConfirmYourIdentityTryAgainBankTransferPage.assertPageIsDisplayed(JourneyType.BankTransfer)

    VerifyP800ReferenceStub.verifyNone()
    getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.AfterReferenceCheck.journeyReferenceDidntMatchNino.copy(p800Reference = Some(p800RefOutOfBounds))
    getFailedAttemptCount() shouldBe Some(1)

    AuditConnectorStub.verifyEventAudited(
      AuditConnectorStub.validateUserDetailsAuditType,
      Json.parse(
        //format=JSON
        s"""
          {
            "outcome": {
              "isSuccessful": false,
              "attemptsOnRecord": 1,
              "lockout": false,
              "apiResponsibleForFailure": "p800 reference check",
              "reasons": [
                "NPS indicated that the reference did not match the NINO"
              ]
            },
            "userEnteredDetails": {
              "repaymentMethod": "bank",
              "p800Reference": ${p800RefOutOfBounds.value},
              "nino": "LM001014C",
              "dob": "2000-01-01"
            }
          }
          """.stripMargin
      ).as[JsObject]
    )
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    addJourneyIdToSession(tdAll.journeyId)
  }
}
