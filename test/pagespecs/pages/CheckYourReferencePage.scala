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

class CheckYourReferencePage(baseUrl: String)(implicit webDriver: WebDriver) extends Page(
  baseUrl,
  path = "/get-an-income-tax-refund/check-your-reference"
) {

  override def expectedH1: String = "Check your reference"

  def assertPageIsDisplayed(potentialErrors: ContentExpectation*): Unit = withPageClue {
    val contentExpectations: Seq[ContentExpectation] = Seq(
      ContentExpectation(
        atXpath       = PageUtil.Xpath.mainContent,
        expectedLines =
          """
            |Check your reference
            |You entered P800REFNO1
            |Is this correct?
            |""".stripMargin
      )
    ) ++ potentialErrors

    PageUtil.assertPage(
      path                = path,
      h1                  = expectedH1,
      title               = PageUtil.standardTitle(expectedH1),
      contentExpectations = contentExpectations: _*
    )
  }

  def assertPageShowsWithErrors(): Unit = withPageClue {
    assertPageIsDisplayed(
      ContentExpectation(
        atXpath       = PageUtil.Xpath.mainContent,
        expectedLines =
          """
            |There is a problem
            |Select yes if you entered the correct reference
            |""".stripMargin
      )
    )
  }

  def selectYes(): Unit = withPageClue {
    PageUtil.clickByIdOrName("reference-check")
  }

  def selectNo(): Unit = withPageClue {
    PageUtil.clickByIdOrName("reference-check-2")
  }
}
