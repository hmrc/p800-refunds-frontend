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

class NoMoreAttemptsLeftToConfirmYourIdentityPage(baseUrl: String, pathForJourneyType: String)(implicit webDriver: WebDriver) extends Page(
  baseUrl,
  path = s"/get-an-income-tax-refund/$pathForJourneyType/no-more-attempts-left-to-confirm-your-identity"
) {

  override def expectedH1: String = "We cannot confirm your identity"
  override def expectedWelshH1: String = "Ni allwn gadarnhau pwy ydych"

  override def assertPageIsDisplayed(extraExpectations: ContentExpectation*): Unit = withPageClue {

    val contentExpectations: Seq[ContentExpectation] = Seq(ContentExpectation(
      atXpath       = PageUtil.Xpath.mainContent,
      expectedLines =
        """
          |We cannot confirm your identity
          |You have entered information that does not match our records too many times.
          |For security reasons, you must wait 24 hours and then try again.
          |Alternatively you can sign in to you HMRC online account to request your refund.
          |If you continue having problems with confirming your identity, you need to contact us.
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

    contactUsHref() shouldBe "https://www.gov.uk/government/organisations/hm-revenue-customs/contact/income-tax-enquiries-for-individuals-pensioners-and-employees"
    ()
  }

  def clickSignInToYourHmrcAccount(): Unit = PageUtil.clickByIdOrName("sign-in-to-you-hmrc-online-account")
  def contactUsHref(): String = PageUtil.getHrefById("contact-hmrc")

}
