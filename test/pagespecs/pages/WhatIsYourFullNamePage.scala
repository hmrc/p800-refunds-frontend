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

class WhatIsYourFullNamePage(baseUrl: String)(implicit webDriver: WebDriver) extends Page(
  baseUrl,
  path = "/get-an-income-tax-refund/we-need-you-to-confirm-your-identity/what-is-your-full-name"
) {

  override def expectedH1: String = "What is your full name?"

  private val fullNameFieldId: String = "fullName"

  def enterFullName(fullName: String): Unit = PageUtil.setTextFieldById(fullNameFieldId, fullName)

  override def assertPageIsDisplayed(errors: ContentExpectation*): Unit = withPageClue {

    val contentExpectations: Seq[ContentExpectation] = Seq(ContentExpectation(
      atXpath       = PageUtil.Xpath.mainContent,
      expectedLines =
        """
          |What is your full name?
          |Enter your name as it appears on your tax calculation letter or ‘P800’.
          |""".stripMargin
    )) ++ errors

    PageUtil.assertPage(
      path                = path,
      h1                  = expectedH1,
      title               = PageUtil.standardTitle(expectedH1),
      contentExpectations = contentExpectations: _*
    )
  }

  def assertPageShowsErrorTooShort(): Unit = withPageClue {
    assertPageIsDisplayed(
      ContentExpectation(
        atXpath       = PageUtil.Xpath.mainContent,
        expectedLines =
          """
            |There is a problem
            |Full name must be 2 characters or more
            |""".stripMargin
      )
    )
  }

  def assertPageShowsErrorTooLong(): Unit = withPageClue {
    assertPageIsDisplayed(
      ContentExpectation(
        atXpath       = PageUtil.Xpath.mainContent,
        expectedLines =
          """
            |There is a problem
            |Full name must be 160 characters or less
            |""".stripMargin
      )
    )
  }

  def assertPageShowsInvalidCharacterError(expectedInvalidCharacters: String): Unit = withPageClue {
    assertPageIsDisplayed(
      ContentExpectation(
        atXpath       = PageUtil.Xpath.mainContent,
        expectedLines =
          s"""
            |There is a problem
            |Name must not include $expectedInvalidCharacters
            |""".stripMargin
      )
    )
  }

  def assertPageShowsTooManyInvalidCharacterError(): Unit = withPageClue {
    assertPageIsDisplayed(
      ContentExpectation(
        atXpath       = PageUtil.Xpath.mainContent,
        expectedLines =
          s"""
            |There is a problem
            |Full name must only include letters a to z, and special characters such as hyphens, spaces and apostrophes
            |""".stripMargin
      )
    )
  }

}
