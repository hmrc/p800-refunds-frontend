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

package pagespecs.pages.testonly

import org.openqa.selenium.WebDriver
import pagespecs.pagesupport.{ContentExpectation, Page, PageUtil}

class BankStubPage(baseUrl: String)(implicit webDriver: WebDriver) extends Page(
  baseUrl,
  path = "/get-an-income-tax-refund/test-only/bank-page"
) {

  override def expectedH1: String = "Bank Stub Page"
  override def expectedWelshH1: String = "ADD_ME"

  override def assertPageIsDisplayed(extraExpectations: ContentExpectation*): Unit = withPageClue {
    PageUtil.assertPage(
      path           = path,
      h1             = expectedH1,
      title          = PageUtil.standardTitleForTestOnlyPages,
      serviceName    = "Test Only - Get an Income Tax refund",
      serviceNameUrl = None,
      ContentExpectation(
        atXpath       = PageUtil.Xpath.mainContent,
        expectedLines =
          """
            |I'm the stub for the bank that the user selected on the 'What is the name of your bank?' page.
            |In production, this won't be here.
            |""".stripMargin
      )
    )
    ()
  }

  def selectAuthorised(): Unit = withPageClue {
    PageUtil.clickByIdOrName("bank-result")
  }

  def selectCanceled(): Unit = withPageClue {
    PageUtil.clickByIdOrName("bank-result-2")
  }

  def selectFailed(): Unit = withPageClue {
    PageUtil.clickByIdOrName("bank-result-3")
  }
}
