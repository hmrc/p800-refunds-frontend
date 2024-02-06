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

class YouCannotConfirmYourSecurityDetailsYetPage(baseUrl: String)(implicit webDriver: WebDriver) extends Page(
  baseUrl,
  path = "/get-an-income-tax-refund/you-cannot-confirm-your-security-details-yet"
) {

  override def expectedH1: String = "You cannot confirm your security details yet"
  override def expectedTitleContent: String = "You cannot confirm your security details yet"

  def staticJourney: String = ""
  override def assertPageIsDisplayed(extraExpectations: ContentExpectation*): Unit = withPageClue {

    val contentExpectations: Seq[ContentExpectation] = Seq(ContentExpectation(
      atXpath       = PageUtil.Xpath.mainContent,
      expectedLines =
        """
          |You cannot confirm your security details yet
          |You have previously entered information that does not match our records too many times. For security reasons you have been locked out. You can try again after *A DATE*.
          |Alternatively you can sign in to your HMRC online account to request your refund. If you continue having problems with confirming your identity, you need to contact us.
          |""".stripMargin
    )) ++ extraExpectations

    PageUtil.assertPage(
      baseUrl             = baseUrl,
      path                = path,
      h1                  = expectedH1,
      title               = PageUtil.standardTitle(expectedTitleContent),
      contentExpectations = contentExpectations: _*
    )
  }

}
