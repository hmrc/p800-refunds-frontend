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

class BankTransferRequestReceivedPage(baseUrl: String)(implicit webDriver: WebDriver) extends Page(
  baseUrl,
  path = "/get-an-income-tax-refund/request-received"
) {

  override def expectedH1: String = "Request received"

  override def assertPageIsDisplayed(extraExpectations: ContentExpectation*): Unit = withPageClue {

    val contentExpectation: ContentExpectation = ContentExpectation(
      atXpath       = PageUtil.Xpath.mainContent,
      expectedLines =
        """
          |Request received
          |P800 reference
          |P800REFNO1
          |Your refund of £12.34 will now be paid by 1 December 2023.
          |
          |Print this page
          |
          |What happens next
          |If you don’t receive your refund you can call or write to the Income Tax helpline (opens in new tab). You will need your P800 reference.
          |What did you think of this service? (takes 30 seconds)
          |""".stripMargin
    )

    PageUtil.assertPage(
      baseUrl             = baseUrl,
      path                = path,
      h1                  = expectedH1,
      title               = PageUtil.standardTitle(expectedH1),
      contentExpectations = contentExpectation
    )

    generalEnquiriesHref() shouldBe "https://www.gov.uk/government/organisations/hm-revenue-customs/contact/income-tax-enquiries-for-individuals-pensioners-and-employees"
    generalEnquiriesTarget() shouldBe "_blank"
    PageUtil.elementDisplayedByClassName("govuk-back-link") shouldBe false
    ()
  }

  private def generalEnquiriesHref(): String = PageUtil.getHrefById("general-enquiries-link")
  private def generalEnquiriesTarget(): String = PageUtil.getTargetById("general-enquiries-link")

  def clickPrintThisPage(): Unit = PageUtil.clickByIdOrName("print-page")

}
