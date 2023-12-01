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

class PtaSignInPage(baseUrl: String)(implicit webDriver: WebDriver) extends Page(
  baseUrl,
  path = "/get-an-income-tax-refund/test-only/pta-sign-in"
) {

  override def expectedH1: String = "Personal tax account sign in"

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
            |Personal tax account sign in
            |I'm a stub representing Personal tax account sign in page.
            |""".stripMargin
      )
    )
    ()
  }
}
