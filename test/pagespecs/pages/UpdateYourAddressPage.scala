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

class UpdateYourAddressPage(baseUrl: String)(implicit webDriver: WebDriver) extends Page(
  baseUrl,
  path = "/get-an-income-tax-refund/cheque/update-your-address"
) {

  override def expectedH1: String = "Update your address"
  override def expectedWelshH1: String = "Diweddaru’ch cyfeiriad"

  override def assertPageIsDisplayed(extraExpectations: ContentExpectation*): Unit = withPageClue {

    val contentExpectations: Seq[ContentExpectation] = Seq(ContentExpectation(
      atXpath       = PageUtil.Xpath.mainContent,
      expectedLines =
        """
          |Update your address
          |To update your address you need to:
          |Contact HMRC to tell us that you have changed address.
          |Wait 2 days for HMRC to update your details.
          |Restart your refund request.
          |""".stripMargin
    )) ++ extraExpectations

    PageUtil.assertPage(
      baseUrl             = baseUrl,
      path                = path,
      h1                  = expectedH1,
      title               = PageUtil.standardTitle(expectedH1),
      welshTest           = false,
      contentExpectations = contentExpectations: _*
    )

    contactHmrcHref() shouldBe "https://www.gov.uk/government/organisations/hm-revenue-customs/contact/change-your-personal-details"
    ()
  }

  private def contactHmrcHref(): String = PageUtil.getHrefById("contact-hmrc-link")

}
