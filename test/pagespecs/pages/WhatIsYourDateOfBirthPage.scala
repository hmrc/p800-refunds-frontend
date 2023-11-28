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
import org.scalatest.Assertion
import pagespecs.pagesupport.{ContentExpectation, Page, PageUtil}
import testsupport.RichMatchers.convertToAnyShouldWrapper

class WhatIsYourDateOfBirthPage(baseUrl: String)(implicit webDriver: WebDriver) extends Page(
  baseUrl,
  path = "/get-an-income-tax-refund/what-is-your-date-of-birth"
) {

  override def expectedH1: String = "What is your date of birth?"

  override def assertPageIsDisplayed(extraExpectations: ContentExpectation*): Unit = withPageClue {

    val contentExpectations: Seq[ContentExpectation] = Seq(ContentExpectation(
      atXpath       = PageUtil.Xpath.mainContent,
      expectedLines =
        """
          |What is your date of birth?
          |For example, 27 3 2007
          |Day Month Year
          |Continue
          |""".stripMargin
    )) ++ extraExpectations

    PageUtil.assertPage(
      path                = path,
      h1                  = expectedH1,
      title               = PageUtil.standardTitle(expectedH1),
      contentExpectations = contentExpectations: _*
    )
  }

  def assertErrorMessages(expectedErrorMessage: String): Unit = withPageClue {
    assertPageIsDisplayed(
      ContentExpectation(
        atXpath       = PageUtil.Xpath.errorSummary,
        expectedLines =
          s"""
            |There is a problem
            |$expectedErrorMessage
            |""".stripMargin
      ),
      ContentExpectation(
        atXpath       = PageUtil.Xpath.errorMessage,
        expectedLines = s"""$expectedErrorMessage"""
      )
    )
  }

  def assertPageShowsErrorAllEmpty(): Unit = withPageClue {
    assertErrorMessages("""Enter your date of birth""")
  }

  def assertPageShowsErrorDayMissing(): Unit = withPageClue {
    assertErrorMessages("""Date of birth must include a day""")
  }

  def assertPageShowsErrorMonthMissing(): Unit = withPageClue {
    assertErrorMessages("""Date of birth must include a month""")
  }

  def assertPageShowsErrorYearMissing(): Unit = withPageClue {
    assertErrorMessages("""Date of birth must include a year""")
  }

  def assertPageShowsErrorMonthTooLarge(): Unit = withPageClue {
    assertErrorMessages("""You must enter a real date""")
  }

  def assertPageShowsErrorDateInTheFuture(): Unit = withPageClue {
    assertErrorMessages("""Date of birth must be in the past""")
  }

  def assertPageShowsErrorDateTooYoung(expectedDate: String): Unit = withPageClue {
    assertErrorMessages(s"""Date of birth must be on or before $expectedDate""")
  }

  def assertPageShowsErrorDateTooOld(expectedDate: String): Unit = withPageClue {
    assertErrorMessages(s"""Date of birth must be on or after $expectedDate""")
  }

  def assertPageShowsErrorYearLength(): Unit = withPageClue {
    assertErrorMessages("""Enter a year which contains 4 numbers""".stripMargin)
  }

  def enterDayOfMonth(day: String): Unit = PageUtil.setTextFieldById("date.day", day)
  def enterMonth(month: String): Unit = PageUtil.setTextFieldById("date.month", month)
  def enterYear(year: String): Unit = PageUtil.setTextFieldById("date.year", year)

  def assertDataPrepopulated(day: String, month: String, year: String): Assertion = withPageClue {
    PageUtil.getValueById("date.day") shouldBe day
    PageUtil.getValueById("date.month") shouldBe month
    PageUtil.getValueById("date.year") shouldBe year
  }

}
