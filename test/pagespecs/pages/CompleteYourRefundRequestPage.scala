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
import org.openqa.selenium.WebDriver
import pagespecs.pagesupport.{ContentExpectation, Page, PageUtil}
import testsupport.RichMatchers.convertToAnyShouldWrapper

class CompleteYourRefundRequestPage(baseUrl: String)(implicit webDriver: WebDriver) extends Page(
  baseUrl,
  path = "/get-an-income-tax-refund/cheque/complete-refund-request"
) {

  override def expectedH1: String = "Complete your refund request to get your cheque"
  override def expectedTitleContent: String = "complete refund request"

  override def assertPageIsDisplayed(extraExpectations: ContentExpectation*): Unit = withPageClue {
    val contentExpectations: Seq[ContentExpectation] = Seq(
      ContentExpectation(
        atXpath       = PageUtil.Xpath.mainContent,
        expectedLines =
          """
                  |Complete your refund request to get your cheque
                  |Your cheque will be sent to the same address as your tax calculation letter.
                  |My cheque needs to go to a different address
                  |Complete your refund request
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
      baseUrl             = baseUrl,
      path                = path,
      h1                  = expectedH1,
      title               = PageUtil.standardTitleWithJourneyType(expectedTitleContent, JourneyType.Cheque),
      contentExpectations = contentExpectations: _*
    )
    contactHmrcHref() shouldBe "https://www.gov.uk/government/organisations/hm-revenue-customs/contact/change-your-personal-details"
    ()
  }

  private def contactHmrcHref(): String = PageUtil.getHrefById("contact-hmrc-link")

  def clickSubmitRefundRequest(): Unit = PageUtil.clickByIdOrName("submit-refund-request")

}
