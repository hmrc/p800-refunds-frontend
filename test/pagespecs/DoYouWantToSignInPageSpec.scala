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

import testsupport.ItSpec
import testsupport.stubs.AuditConnectorStub
import play.api.libs.json.{Json, JsObject}

class DoYouWantToSignInPageSpec extends ItSpec {

  override def beforeEach(): Unit = {
    super.beforeEach()
    addJourneyIdToSession(tdAll.journeyId)
    upsertJourneyToDatabase(tdAll.journeyStarted)
  }

  "Selecting 'Yes, sign in' redirects to personal tax account" in {
    pages.doYouWantToSignInPage.open()
    pages.doYouWantToSignInPage.assertPageIsDisplayed()
    pages.doYouWantToSignInPage.selectYes()
    pages.doYouWantToSignInPage.clickSubmit()
    pages.ptaSignInPage.assertPageIsDisplayed()
    getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.journeyStarted

    AuditConnectorStub.verifyEventAudited(
      AuditConnectorStub.userLoginSelectionAuditType,
      Json.parse(
        //format=JSON
        """
         {
           "login": true,
           "ipAddressLockedout": false
         }
         """.stripMargin
      ).as[JsObject]
    )
  }

  "Selecting 'No, continue without signing in' redirects to 'Do you want your refund by bank transfer' page" in {
    pages.doYouWantToSignInPage.open()
    pages.doYouWantToSignInPage.assertPageIsDisplayed()
    pages.doYouWantToSignInPage.selectNo()
    pages.doYouWantToSignInPage.clickSubmit()
    pages.doYouWantYourRefundViaBankTransferPage.assertPageIsDisplayed()
    getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.journeyStarted

    AuditConnectorStub.verifyEventAudited(
      AuditConnectorStub.userLoginSelectionAuditType,
      Json.parse(
        //format=JSON
        """
         {
           "login": false,
           "ipAddressLockedout": false
         }
         """.stripMargin
      ).as[JsObject]
    )
  }

  "Selecting 'No, continue without signing in' but the user has been locked out, it redirects to 'YouCannotConfirmYourIdentityDetailsYet' page" in {
    upsertFailedAttemptToDatabase(tdAll.attemptInfo(failedAttempts = 3))
    pages.doYouWantToSignInPage.open()
    pages.doYouWantToSignInPage.assertPageIsDisplayed()
    pages.doYouWantToSignInPage.selectNo()
    pages.doYouWantToSignInPage.clickSubmit()
    pages.youCannotConfirmYourIdentityYetSpec.assertPageIsDisplayed()

    AuditConnectorStub.verifyEventAudited(
      AuditConnectorStub.userLoginSelectionAuditType,
      Json.parse(
        //format=JSON
        """
         {
           "login": false,
           "ipAddressLockedout": true
         }
         """.stripMargin
      ).as[JsObject]
    )
  }

  "Selecting nothing and clicking continue shows error" in {
    pages.doYouWantToSignInPage.open()
    pages.doYouWantToSignInPage.assertPageIsDisplayed()
    pages.doYouWantToSignInPage.clickSubmit()
    pages.doYouWantToSignInPage.assertPageShowsWithErrors()
    getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.journeyStarted

    AuditConnectorStub.verifyNoAuditEvent(AuditConnectorStub.userLoginSelectionAuditType)
  }

  "Render page in Welsh" in {
    pages.doYouWantToSignInPage.open()
    pages.doYouWantToSignInPage.assertPageIsDisplayed()
    pages.doYouWantToSignInPage.clickWelshLink()
    pages.doYouWantToSignInPage.assertPageIsDisplayedInWelsh()
  }

  "Display error messages in welsh" in {
    pages.doYouWantToSignInPage.open()
    pages.doYouWantToSignInPage.assertPageIsDisplayed()
    pages.doYouWantToSignInPage.clickWelshLink()
    pages.doYouWantToSignInPage.clickSubmit()
    pages.doYouWantToSignInPage.assertPageShowsWithErrorsInWelsh()
    getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.journeyStarted

    AuditConnectorStub.verifyNoAuditEvent(AuditConnectorStub.userLoginSelectionAuditType)
  }

}
