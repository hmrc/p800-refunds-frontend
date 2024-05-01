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

import models.Nino
import org.openqa.selenium.WebDriver
import org.scalatest.Assertion
import pagespecs.pagesupport.{ContentExpectation, Page, PageUtil}
import testsupport.RichMatchers.convertToAnyShouldWrapper

class EnterYourNationalInsuranceNumberPage(baseUrl: String, pathForJourneyType: String)(implicit webDriver: WebDriver) extends Page(
  baseUrl,
  path = s"/get-an-income-tax-refund/$pathForJourneyType/enter-your-national-insurance-number"
) {

  override def expectedH1: String = "What is your National Insurance number?"
  override def expectedWelshH1: String = "Beth yw eich rhif Yswiriant Gwladol?"

  def enterNationalInsuranceNumber(nationalInsuranceNumber: Nino): Unit =
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
    )
  )

  override def assertPageIsDisplayed(extraExpectations: ContentExpectation*): Unit = withPageClue {

    val contentExpectations: Seq[ContentExpectation] = commonPageExpectations ++ extraExpectations

    PageUtil.assertPage(
      baseUrl             = baseUrl,
      path                = path,
      h1                  = expectedH1,
      title               = PageUtil.standardTitle(expectedH1),
      welshTest           = false,
      contentExpectations = contentExpectations: _*
    )

  }

  def assertPageShowsErrorEmptyInput(): Unit = withPageClue {
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
      title               = PageUtil.standardErrorTitle(expectedH1),
      welshTest           = false,
      contentExpectations = contentExpectations: _*
    )
  }

  def assertPageShowsErrorInvalid(): Unit = withPageClue {
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
      title               = PageUtil.standardErrorTitle(expectedH1),
      welshTest           = false,
      contentExpectations = contentExpectations: _*
    )
  }

  def assertDataPrepopulated(nationalInsuranceNumber: Nino): Assertion = withPageClue {
    PageUtil.getValueById("nationalInsuranceNumber") shouldBe nationalInsuranceNumber.value
  }
}
