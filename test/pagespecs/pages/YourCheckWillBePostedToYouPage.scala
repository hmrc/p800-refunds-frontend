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

class YourCheckWillBePostedToYouPage(baseUrl: String)(implicit webDriver: WebDriver) extends Page(
  baseUrl,
  path = "/get-an-income-tax-refund/your-cheque-will-be-posted-to-you"
) {

  override def expectedH1: String = "Your cheque will be posted to you"

  def assertPageIsDisplayed(): Unit = withPageClue {
    val contentExpectations: Seq[ContentExpectation] = Seq(
      ContentExpectation(
        atXpath       = PageUtil.Xpath.mainContent,
        expectedLines =
          """
                  |Your cheque will be posted to you
                  |Your cheque will be sent to the same address as your p800 letter.
                  |My cheque needs to go to a different address
                  |Submit refund request
                  |""".stripMargin
      ),
      ContentExpectation(
        atXpath       = """//*[@class="govuk-details__summary-text"]""",
        expectedLines =
          """
            |My cheque needs to go to a different address
            |""".stripMargin
      ),
      ContentExpectation(
        atXpath       = """//*[@class="govuk-details__text"]""",
        expectedLines =
          """
            |To update your address you need to:
            |Contact HMRC to tell us that you have changed address.
            |Wait two days for HMRC to update your details.
            |Restart your refund request.
            |""".stripMargin
      )
    )

    PageUtil.assertPage(
      path                = path,
      h1                  = expectedH1,
      title               = PageUtil.standardTitle(expectedH1),
      contentExpectations = contentExpectations: _*
    )
    contactHmrcHref() shouldBe "https://www.gov.uk/government/organisations/hm-revenue-customs/contact/change-your-personal-details"
    ()
  }

  private def contactHmrcHref(): String = PageUtil.getHrefById("contact-hmrc-link")

  def clickSubmitRefundRequest(): Unit = PageUtil.clickByIdOrName("submit-refund-request")

}
