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

package pagespecs.pages

import org.openqa.selenium.WebDriver
import pagespecs.pagesupport.{ContentExpectation, Page, PageUtil}

class DoYouWantToSignInPage(baseUrl: String)(implicit webDriver: WebDriver) extends Page(
  baseUrl,
  path = "/get-an-income-tax-refund/do-you-want-to-sign-in"
) {

  def assertPageIsDisplayed(): Unit = withPageClue {
    val h1: String = "Do you want to sign in?"
    PageUtil.assertPage(
      path  = path,
      h1    = h1,
      title = PageUtil.standardTitle(h1),
      ContentExpectation(
        atXpath       = PageUtil.Xpath.mainContent,
        expectedLines =
          """
            |Do you want to sign in?
            |Sign in with your Government Gateway user ID. You'll have fewer details to enter this way.
            |""".stripMargin
      ),
    )
    ()
  }

  def assertPageShowsError(): Unit = withPageClue {
    val h1: String = "Do you want to sign in?"
    PageUtil.assertPage(
      path  = path,
      h1    = h1,
      title = PageUtil.standardTitle(h1),
      ContentExpectation(
        atXpath       = PageUtil.Xpath.mainContent,
        expectedLines =
          """
            |There is a problem
            |Select yes if you want to sign in to your tax account
            |""".stripMargin
      ),
    )
    ()
  }

  def selectRadioItemAndContinue(radioItemId: String): Unit = withPageClue {
    PageUtil.clickByIdOrName(radioItemId)
    clickContinue()
    ()
  }
}
