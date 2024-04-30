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

class ThereIsAProblemPage(baseUrl: String)(implicit webDriver: WebDriver) extends Page(
  baseUrl,
  path = "/get-an-income-tax-refund/there-is-a-problem"
) {

  override def expectedH1: String = "There is a problem"
  override def expectedWelshH1: String = "Mae problem wedi codi"

  override def assertPageIsDisplayed(extraExpectations: ContentExpectation*): Unit = withPageClue {

    val contentExpectations: Seq[ContentExpectation] = Seq(ContentExpectation(
      atXpath       = PageUtil.Xpath.mainContent,
      expectedLines =
        """
          |There is a problem
          |We cannot process your refund request online, so we need you to contact us.
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

    PageUtil.elementDisplayedByClassName("govuk-back-link") shouldBe false
    contactHmrcHref() shouldBe "https://www.gov.uk/government/organisations/hm-revenue-customs/contact/income-tax-enquiries-for-individuals-pensioners-and-employees"
    ()
  }

  private def contactHmrcHref(): String = PageUtil.getHrefById("contact-hmrc-link")
}
