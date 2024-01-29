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

  override def expectedH1: String = "Do you want to sign in?"
  override def expectedTitleContent: String = "add_me"

  override def assertPageIsDisplayed(extraExpectations: ContentExpectation*): Unit = withPageClue {
    val contentExpectations: ContentExpectation = ContentExpectation(
      atXpath       = PageUtil.Xpath.mainContent,
      expectedLines =
        """
          |Do you want to sign in?
          |You’ll have fewer details to enter if you sign in using your Government Gateway user ID.
          |""".stripMargin
    )

    PageUtil.assertPage(
      baseUrl             = baseUrl,
      path                = path,
      h1                  = expectedH1,
      title               = PageUtil.standardTitle(expectedH1),
      contentExpectations = contentExpectations
    )
  }

  def assertPageShowsWithErrors(): Unit = withPageClue {
    val errorContent: ContentExpectation = ContentExpectation(
      atXpath       = PageUtil.Xpath.mainContent,
      expectedLines =
        """
          |There is a problem
          |Select yes if you want to sign in to your tax account
          |Do you want to sign in?
          |You’ll have fewer details to enter if you sign in using your Government Gateway user ID.
          |""".stripMargin
    )
    PageUtil.assertPage(
      baseUrl             = baseUrl,
      path                = path,
      h1                  = expectedH1,
      title               = PageUtil.standardErrorTitle(expectedH1),
      contentExpectations = errorContent
    )
  }

  def selectYes(): Unit = withPageClue {
    PageUtil.clickByIdOrName("sign-in")
  }

  def selectNo(): Unit = withPageClue {
    PageUtil.clickByIdOrName("sign-in-2")
  }
}
