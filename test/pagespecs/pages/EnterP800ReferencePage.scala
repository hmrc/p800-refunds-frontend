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

import scala.jdk.CollectionConverters._

class EnterP800ReferencePage(baseUrl: String)(implicit webDriver: WebDriver) extends Page(
  baseUrl,
  path = "/get-an-income-tax-refund/enter-P800-reference"
) {

  def enterInvalidRef(): Unit = withPageClue {
    PageUtil.setTextFieldById("reference", "this is a really long and invalid reference")
  }

  def clickPtaSignInLink(): Unit = PageUtil.clickByIdOrName("personal-tax-account-sign-in")

  def clickIncomeTaxGeneralEnquiriesLink(): String = {
    val handleBefore = webDriver.getWindowHandle
    PageUtil.clickByIdOrName("income-tax-general-enquiries")
    (webDriver.getWindowHandles.asScala diff Set(handleBefore)).headOption
      .getOrElse(throw new Exception("Expecting at least one window handle"))
  }

  def assertPageIsDisplayed(errors: ContentExpectation*): Unit = withPageClue {
    val h1: String = "What is your P800 reference?"
    val contentExpectations = Seq(
      ContentExpectation(
        atXpath       = PageUtil.Xpath.mainContent,
        expectedLines =
          """
            |It’s on the letter HMRC sent you about your tax calculation, also known as a ‘P800’.
            |For example, ‘P800REFNO1’.
            |If you do not know your P800 reference
            |""".stripMargin
      )
    ) ++ errors

    PageUtil.assertPage(
      path                = path,
      h1                  = h1,
      title               = PageUtil.standardTitle(h1),
      contentExpectations = contentExpectations: _*
    )
    ()
  }

  def assertPageShowsErrorRequired(): Unit = withPageClue {
    assertPageIsDisplayed(
      ContentExpectation(
        atXpath       = PageUtil.Xpath.mainContent,
        expectedLines =
          """
            |There is a problem
            |Enter your P800 reference
            |""".stripMargin
      )
    )
    ()
  }

  def assertPageShowsErrorReferenceFormat(): Unit = withPageClue {
    assertPageIsDisplayed(
      ContentExpectation(
        atXpath       = PageUtil.Xpath.mainContent,
        expectedLines =
          """
            |There is a problem
            |Enter your P800 reference in the correct format
            |""".stripMargin
      )
    )
    ()
  }
}
