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

import models.NationalInsuranceNumber
import models.journeymodels.JourneyType
import org.openqa.selenium.WebDriver
import org.scalatest.Assertion
import pagespecs.pagesupport.{ContentExpectation, Page, PageUtil}
import testsupport.RichMatchers.convertToAnyShouldWrapper

class WhatIsYourNationalInsuranceNumberPage(baseUrl: String, pathForJourneyType: String)(implicit webDriver: WebDriver) extends Page(
  baseUrl,
  path = s"/get-an-income-tax-refund/$pathForJourneyType/what-is-your-national-insurance-number"
) {

  override def expectedH1: String = "What is your National Insurance number?"

  override def assertPageIsDisplayed(extraExpectations: ContentExpectation*): Unit = sys.error("Use another variant for asserting page")

  def enterNationalInsuranceNumber(nationalInsuranceNumber: NationalInsuranceNumber): Unit =
    PageUtil.setTextFieldById("nationalInsuranceNumber", nationalInsuranceNumber.value)

  val commonPageExpectations: Seq[ContentExpectation] = Seq(
    ContentExpectation(
      atXpath       = PageUtil.Xpath.mainContent,
      expectedLines =
        """
          |What is your National Insurance number?
          |It’s on your National Insurance card or letter, benefit letter, payslip or P60.
          |For example, ‘QQ 12 34 56 C’.
          |Continue
          |""".stripMargin
    ),
    ContentExpectation(
      atXpath       = """//*[@class="govuk-details__summary-text"]""",
      expectedLines =
        """
          |I do not know my National Insurance number
          |""".stripMargin
    ),
    ContentExpectation(
      atXpath       = """//*[@class="govuk-details__text"]""",
      expectedLines =
        """
          |You can get help to find a lost National Insurance number (opens in new tab)
          |""".stripMargin
    )
  )

  def assertPageIsDisplayed(journeyType: JourneyType, extraExpectations: ContentExpectation*): Unit = withPageClue {

    val contentExpectations: Seq[ContentExpectation] = commonPageExpectations ++ extraExpectations

    PageUtil.assertPage(
      baseUrl             = baseUrl,
      path                = path,
      h1                  = expectedH1,
      title               = PageUtil.standardTitleWithJourneyType(expectedH1, journeyType),
      contentExpectations = contentExpectations: _*
    )
    lostNationalInsuranceNumberHref() shouldBe "https://www.gov.uk/lost-national-insurance-number"
    ()
  }

  private def lostNationalInsuranceNumberHref(): String = PageUtil.getHrefById("lost-national-insurance-number-link")

  def assertPageShowsErrorEmptyInput(journeyType: JourneyType): Unit = withPageClue {
    val contentExpectations = Seq(
      ContentExpectation(
        atXpath       = PageUtil.Xpath.mainContent,
        expectedLines =
          """
            |There is a problem
            |Enter your National Insurance number
            |""".stripMargin
      )
    ) ++ commonPageExpectations

    PageUtil.assertPage(
      baseUrl             = baseUrl,
      path                = path,
      h1                  = expectedH1,
      title               = PageUtil.standardErrorTitle(expectedH1, journeyType),
      contentExpectations = contentExpectations: _*
    )
  }

  def assertPageShowsErrorInvalid(journeyType: JourneyType): Unit = withPageClue {
    val contentExpectations = Seq(
      ContentExpectation(
        atXpath       = PageUtil.Xpath.mainContent,
        expectedLines =
          """
            |There is a problem
            |Enter your National Insurance number in the correct format
            |""".stripMargin
      )
    ) ++ commonPageExpectations

    PageUtil.assertPage(
      baseUrl             = baseUrl,
      path                = path,
      h1                  = expectedH1,
      title               = PageUtil.standardErrorTitle(expectedH1, journeyType),
      contentExpectations = contentExpectations: _*
    )
  }

  def assertDataPrepopulated(nationalInsuranceNumber: NationalInsuranceNumber): Assertion = withPageClue {
    PageUtil.getValueById("nationalInsuranceNumber") shouldBe nationalInsuranceNumber.value
  }
}
