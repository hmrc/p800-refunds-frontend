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

class HelloWorldExamplePage(baseUrl: String)(implicit webDriver: WebDriver) extends Page(
  baseUrl,
  path = "/get-an-income-tax-refund"
) {

  def assertPageIsDisplayed(): Unit = withPageClue {
    val h1: String = "Example message"
    PageUtil.assertPage(
      path  = path,
      h1    = h1,
      title = PageUtil.standardTitle(h1),
      ContentExpectation(
        atXpath       = PageUtil.Xpath.mainContent,
        expectedLines =
          // here only text parts which don't change when items added/removed from the basket
          //varying parts in another ContentExpectation which comes from the method argument
          """
            |Example message
            |Another example message
            |Final example message
            |""".stripMargin
      ),
      ContentExpectation(
        atXpath       = """//*[@id="example-id"]""",
        expectedLines = "Final example message"
      )
    )
    ()
  }

  def assertPageIsDisplayedInWelsh(): Unit = {
    val h1: String = "Something in welsh"
    PageUtil.assertPage(
      path  = path,
      h1    = h1,
      title = PageUtil.standardTitleInWelsh(h1),
      ContentExpectation(
        atXpath       = PageUtil.Xpath.mainContent,
        expectedLines =
          // here only text parts which don't change when items added/removed from the basket
          //varying parts in another ContentExpectation which comes from the method argument
          """
            |some welsh
            |""".stripMargin
      ),
      ContentExpectation(
        atXpath       = """//*[@id="example-id"]""",
        expectedLines = "some welsh"
      )
    )
    ()
  }

}
