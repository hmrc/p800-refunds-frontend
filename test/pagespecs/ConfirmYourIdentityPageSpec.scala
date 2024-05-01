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

import models.journeymodels.JourneyType
import testsupport.ItSpec

class ConfirmYourIdentityPageSpec extends ItSpec {

  private val journeyBankTransfer = tdAll.BankTransfer.journeySelectedType
  private val journeyCheque = tdAll.Cheque.journeySelectedType

  override def beforeEach(): Unit = {
    super.beforeEach()
    addJourneyIdToSession(tdAll.journeyId)
  }

  "render weNeedYouToConfirmYourIdentityPage" - {
    "bank transfer" in {
      upsertJourneyToDatabase(journeyBankTransfer)
      test(JourneyType.BankTransfer)
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike journeyBankTransfer
    }
    "cheque" in {
      upsertJourneyToDatabase(journeyCheque)
      test(JourneyType.Cheque)
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike journeyCheque
    }

      def test(journeyType: JourneyType): Unit = {
        val page = journeyType match {
          case JourneyType.BankTransfer => pages.weNeedYouToConfirmYourIdentityChequePage
          case JourneyType.Cheque       => pages.weNeedYouToConfirmYourIdentityBankTransferPage
        }
        page.open()
        page.assertPageIsDisplayed()
      }
  }

  "'Continue' button sends user to whatIsYourP800ReferencePage" - {
    "bank transfer" in {
      upsertJourneyToDatabase(journeyBankTransfer)
      test(JourneyType.BankTransfer)
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike journeyBankTransfer
    }
    "cheque" in {
      upsertJourneyToDatabase(journeyCheque)
      test(JourneyType.Cheque)
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike journeyCheque
    }

      def test(journeyType: JourneyType): Unit = {
        val (startPage, endPage) = journeyType match {
          case JourneyType.BankTransfer => pages.weNeedYouToConfirmYourIdentityBankTransferPage -> pages.whatIsYourP800ReferenceBankTransferPage
          case JourneyType.Cheque       => pages.weNeedYouToConfirmYourIdentityChequePage -> pages.whatIsYourP800ReferenceChequePage
        }
        startPage.open()
        startPage.assertPageIsDisplayed()
        startPage.clickSubmit()
        endPage.assertPageIsDisplayed()
      }
  }

  //Pawel TODO
  //  forAll(Table(
  //    ("journeyState", "expectedPage"),
  //    (tdAll.journeyDoYouWantYourRefundViaBankTransferNo, pages.completeYourRefundRequestPage),
  //    (tdAll.journeyStarted, pages.doYouWantToSignInPage),
  //    (tdAll.journeyDoYouWantToSignInNo, pages.enterP800ReferencePage),
  //    (tdAll.journeyWhatIsYourP800Reference, pages.checkYourReferencePage),
  //    (tdAll.journeyCheckYourReferenceValid, pages.doYouWantYourRefundViaBankTransferPage),
  //    (tdAll.journeyYourChequeWillBePostedToYou, pages.chequeRequestReceivedPage),
  //    (tdAll.journeyWhatIsYourFullName, pages.weNeedYouToConfirmYourIdentityPage),
  //    (tdAll.journeyWhatIsYourDateOfBirth, pages.weNeedYouToConfirmYourIdentityPage)
  //  )) { (journeyState: Journey, expectedPage: Page) =>
  //    s"JourneyState: [${journeyState.name}] should redirect accordingly when state is before this page" in {
  //      upsertJourneyToDatabase(journeyState)
  //      pages.weNeedYouToConfirmYourIdentityPage.open()
  //      expectedPage.assertPageIsDisplayed()
  //    }
  //  }

}
