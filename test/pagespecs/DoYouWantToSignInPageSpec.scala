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

class DoYouWantToSignInPageSpec extends ItSpec {

  "navigating to /start redirects to /do-you-want-to-sign-in" in {
    pages.startEndpoint.open()
    pages.doYouWantToSignInPage.assertPageIsDisplayed()
  }

  "Selecting 'Yes, sign in' redirects to personal tax account" in {
    pages.startEndpoint.open()
    pages.doYouWantToSignInPage.assertPageIsDisplayed()
    pages.doYouWantToSignInPage.selectRadioItemAndContinue("radio-sign-in")
    pages.doYouWantToSignInPage.urlShouldBe("https://www.gov.uk/personal-tax-account/prove-identity")
  }

  "Selecting 'No, continue without signing in' redirects to 'What is your P800 Reference' page" in {
    pages.startEndpoint.open()
    pages.doYouWantToSignInPage.assertPageIsDisplayed()
    pages.doYouWantToSignInPage.selectRadioItemAndContinue("radio-do-not-sign-in")
    pages.doYouWantToSignInPage.pathShouldBe("/get-an-income-tax-refund/what-is-your-p800-reference")
  }

  "Clicking 'Back' redirects back to start page" in {
    pages.startEndpoint.open()
    pages.doYouWantToSignInPage.assertPageIsDisplayed()
    pages.doYouWantToSignInPage.clickBackButton()
    pages.doYouWantToSignInPage.pathShouldBe("/get-an-income-tax-refund/test-only")
  }

  "Selecting nothing and clicking continue shows error" in {
    pages.startEndpoint.open()
    pages.doYouWantToSignInPage.assertPageIsDisplayed()
    pages.doYouWantToSignInPage.clickContinue()
    pages.doYouWantToSignInPage.assertPageShowsError()
  }

  // BONUS: validate database journey is valid JourneyDoNotSignIn
}
