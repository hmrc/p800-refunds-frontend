/*
 * Copyright 2024 HM Revenue & Customs
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

package pagespecs.pages.testonly

import org.openqa.selenium.WebDriver
import pagespecs.pagesupport.{ContentExpectation, Page, PageUtil}

class FeedbackFrontendStubPage(baseUrl: String, pathForJourneyType: String)(implicit webDriver: WebDriver) extends Page(
  baseUrl,
  path = s"/get-an-income-tax-refund/test-only/feedback/p800-refunds-${pathForJourneyType}"
) {
  override def expectedH1: String = "Feedback Service Stub"
  override def expectedWelshH1: String = "ADD_ME"

  override def assertPageIsDisplayed(extraExpectations: ContentExpectation*): Unit =
    sys.error("Use 'assertPageIsDisplayedForCheque' or 'assertPageIsDisplayedForBankTransfer' or other variants")

  def assertPageIsDisplayedForBankTransfer(): Unit = {

    PageUtil.assertPage(
      path                = path,
      h1                  = expectedH1,
      title               = PageUtil.standardTitleForTestOnlyPages,
      serviceName         = "Test Only - Get an Income Tax refund",
      serviceNameUrl      = None,
      contentExpectations = ContentExpectation(
        atXpath       = PageUtil.Xpath.mainContent,
        expectedLines =
          s"""
            |Feedback Service Stub
            |I'm a stub representing the Feedback frontend service page.
            |Service name: p800-refunds-bank-transfer
            |""".stripMargin
      )
    )
  }

  def assertPageIsDisplayedForCheque(): Unit = {

    PageUtil.assertPage(
      path                = path,
      h1                  = expectedH1,
      title               = PageUtil.standardTitleForTestOnlyPages,
      serviceName         = "Test Only - Get an Income Tax refund",
      serviceNameUrl      = None,
      contentExpectations = ContentExpectation(
        atXpath       = PageUtil.Xpath.mainContent,
        expectedLines =
          """
            |Feedback Service Stub
            |I'm a stub representing the Feedback frontend service page.
            |Service name: p800-refunds-cheque
            |""".stripMargin
      )
    )
  }
}
