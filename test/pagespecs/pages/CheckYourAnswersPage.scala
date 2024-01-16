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

import models.journeymodels.JourneyType
import models.{NationalInsuranceNumber, P800Reference}
import org.openqa.selenium.WebDriver
import pagespecs.pagesupport.{ContentExpectation, Page, PageUtil}
import testsupport.RichMatchers.convertToAnyShouldWrapper

class CheckYourAnswersPage(baseUrl: String)(implicit webDriver: WebDriver) extends Page(
  baseUrl,
  path = "/get-an-income-tax-refund/check-your-answers"
) {

  override def expectedH1: String = "Check your answers"

  def assertPageIsDisplayedForBankTransfer(
      p800Reference:           P800Reference,
      dateOfBirth:             String,
      nationalInsuranceNumber: NationalInsuranceNumber
  ): Unit = withPageClue {

    val referenceExpectation: ContentExpectation = ContentExpectation(
      atXpath       = """//*[@id="reference"]""",
      expectedLines = p800Reference.value
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
      JourneyType.BankTransfer,
      referenceExpectation,
      dateOfBirthExpectation,
      nationalInsuranceNumberExpectation
    )
  }

  def assertPageIsDisplayedForCheque(
      p800Reference:           P800Reference,
      nationalInsuranceNumber: NationalInsuranceNumber
  ): Unit = withPageClue {

    val referenceExpectation: ContentExpectation = ContentExpectation(
      atXpath       = """//*[@id="reference"]""",
      expectedLines = p800Reference.value
    )

    val nationalInsuranceNumberExpectation: ContentExpectation = ContentExpectation(
      atXpath       = """//*[@id="national-insurance-number"]""",
      expectedLines = nationalInsuranceNumber.value
    )

    assertPageIsDisplayed(
      JourneyType.Cheque,
      referenceExpectation,
      nationalInsuranceNumberExpectation
    )
  }

  override def assertPageIsDisplayed(extraExpectations: ContentExpectation*): Unit = sys.error("Use 'assertPageIsDisplayedForCheque' or 'assertPageIsDisplayedForBankTransfer' or other variants")

  private def assertPageIsDisplayed(journeyType: JourneyType, extraExpectations: ContentExpectation*): Unit = withPageClue {

    val contentExpectation: ContentExpectation = journeyType match {
      case JourneyType.BankTransfer =>
        ContentExpectation(
          atXpath       = PageUtil.Xpath.mainContent,
          expectedLines =
            """
              |Check your answers
              |Reference
              |Change
              |National insurance number
              |Change
              |Date of birth
              |Change
              |Continue
              |""".stripMargin
        )
      case JourneyType.Cheque =>
        ContentExpectation(
          atXpath       = PageUtil.Xpath.mainContent,
          expectedLines =
            """
              |Check your answers
              |Reference
              |Change
              |National insurance number
              |Change
              |Continue
              |""".stripMargin
        )
    }

    val contentExpectations = contentExpectation :: extraExpectations.toList

    PageUtil.assertPage(
      baseUrl             = baseUrl,
      path                = path,
      h1                  = expectedH1,
      title               = PageUtil.standardTitle(expectedH1),
      contentExpectations = contentExpectations: _*
    )

    PageUtil.elementDisplayedByClassName("govuk-back-link") shouldBe true
    ()
  }

  def clickChangeReference(): Unit = PageUtil.clickByIdOrName("change-reference")
  def clickChangeDateOfBirth(): Unit = PageUtil.clickByIdOrName("change-date-of-birth")
  def clickChangeNationalInsuranceNumber(): Unit = PageUtil.clickByIdOrName("change-national-insurance-number")

}
