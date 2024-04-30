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
import testsupport.RichMatchers.convertToAnyShouldWrapper

class RequestReceivedPage(baseUrl: String, pathForJourneyType: String)(implicit webDriver: WebDriver) extends Page(
  baseUrl,
  path = s"/get-an-income-tax-refund/$pathForJourneyType/request-received"
) {

  override def expectedH1: String = "Request received"
  override def expectedWelshH1: String = "ADD_ME"

  val expectedBankTransferH1: String = "Bank transfer request received"
  val expectedChequeH1: String = "Cheque request received"

  override def assertPageIsDisplayed(extraExpectations: ContentExpectation*): Unit =
    sys.error("Use 'assertPageIsDisplayedForCheque' or 'assertPageIsDisplayedForBankTransfer' or other variants")

  def assertPageIsDisplayedForBankTransfer(failedDateCalculatorScenario: Boolean = false): Unit = {
    val dateToDisplay = if (failedDateCalculatorScenario) "2 December 2059" else "30 November 2059"
    val contentExpectation: ContentExpectation = ContentExpectation(
      atXpath       = PageUtil.Xpath.mainContent,
      expectedLines =
        s"""
          |Bank transfer request received
          |Your P800 reference:
          |12345678
          |Your refund of £12.34 will now be paid by $dateToDisplay.
          |
          |Print this page
          |
          |What happens next
          |If you do not receive your refund you can write to us or call the Income Tax helpline (opens in new tab). You will need your P800 reference.
          |What did you think of this service? (takes 30 seconds)
          |""".stripMargin
    )

    PageUtil.assertPage(
      baseUrl             = baseUrl,
      path                = path,
      h1                  = expectedBankTransferH1,
      title               = PageUtil.standardTitle(expectedBankTransferH1),
      welshTest           = false,
      contentExpectations = contentExpectation
    )

    generalEnquiriesHref() shouldBe "https://www.gov.uk/government/organisations/hm-revenue-customs/contact/income-tax-enquiries-for-individuals-pensioners-and-employees"
    generalEnquiriesTarget() shouldBe "_blank"
    PageUtil.elementDisplayedByClassName("govuk-back-link") shouldBe false
    ()
  }

  def assertPageIsDisplayedForCheque(): Unit = {
    val contentExpectation: ContentExpectation = ContentExpectation(
      atXpath       = PageUtil.Xpath.mainContent,
      expectedLines =
        """
          |Cheque request received
          |Your P800 reference:
          |12345678
          |Your refund of £12.34 will arrive in the post by 6 January 2060.
          |
          |Print this page
          |
          |What happens next
          |If you do not receive your refund you can write to us or call the Income Tax helpline (opens in new tab). You will need your P800 reference.
          |What did you think of this service? (takes 30 seconds)
          |""".stripMargin
    )

    PageUtil.assertPage(
      baseUrl             = baseUrl,
      path                = path,
      h1                  = expectedChequeH1,
      title               = PageUtil.standardTitle(expectedChequeH1),
      welshTest           = false,
      contentExpectations = contentExpectation
    )

    generalEnquiriesHref() shouldBe "https://www.gov.uk/government/organisations/hm-revenue-customs/contact/income-tax-enquiries-for-individuals-pensioners-and-employees"
    PageUtil.elementDisplayedByClassName("govuk-back-link") shouldBe false
    ()
  }

  private def generalEnquiriesHref(): String = PageUtil.getHrefById("general-enquiries-link")

  private def generalEnquiriesTarget(): String = PageUtil.getTargetById("general-enquiries-link")

  def clickPrintThisPage(): Unit = PageUtil.clickByIdOrName("print-page")

  def clickFeedbackLink(): Unit = PageUtil.clickByIdOrName("survey-link")

}
