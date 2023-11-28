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

import models.{FullName, NationalInsuranceNumber}
import org.openqa.selenium.WebDriver
import pagespecs.pagesupport.{ContentExpectation, Page, PageUtil}
import testsupport.RichMatchers.convertToAnyShouldWrapper

class CheckYourAnswersPage(baseUrl: String)(implicit webDriver: WebDriver) extends Page(
  baseUrl,
  path = "/get-an-income-tax-refund/check-your-answers"
) {

  override def expectedH1: String = "Check your answers"

  def assertPageIsDisplayed(
      fullName:                FullName,
      dateOfBirth:             String,
      nationalInsuranceNumber: NationalInsuranceNumber
  ): Unit = withPageClue {

    val nameExpectation: ContentExpectation = ContentExpectation(
      atXpath       = """//*[@id="full-name"]""",
      expectedLines = fullName.value
    )

    val dateOfBirthExpectation: ContentExpectation = ContentExpectation(
      atXpath       = """//*[@id="date-of-birth"]""",
      expectedLines = dateOfBirth
    )

    val nationalInsuranceNumberExpectation: ContentExpectation = ContentExpectation(
      atXpath       = """//*[@id="national-insurance-number"]""",
      expectedLines = nationalInsuranceNumber.value
    )

    assertPageIsDisplayed(
      nameExpectation,
      dateOfBirthExpectation,
      nationalInsuranceNumberExpectation
    )
  }

  def assertPageIsDisplayed(extraExpectations: ContentExpectation*): Unit = withPageClue {

    val contentExpectation: ContentExpectation = ContentExpectation(
      atXpath       = PageUtil.Xpath.mainContent,
      expectedLines =
        """
          |Check your answers
          |Full name
          |
          |Change
          |Date of birth
          |
          |Change
          |National Insurance number
          |
          |Change
          |Continue
          |""".stripMargin
    )

    val contentExpectations = contentExpectation :: extraExpectations.toList

    PageUtil.assertPage(
      path                = path,
      h1                  = expectedH1,
      title               = PageUtil.standardTitle(expectedH1),
      contentExpectations = contentExpectations: _*
    )

    PageUtil.elementDisplayedByClassName("govuk-back-link") shouldBe true
    ()
  }

  def clickChangeName(): Unit = PageUtil.clickByIdOrName("change-full-name")
  def clickChangeDateOfBirth(): Unit = PageUtil.clickByIdOrName("change-date-of-birth")
  def clickChangeNationalInsuranceNumber(): Unit = PageUtil.clickByIdOrName("change-national-insurance-number")
}
