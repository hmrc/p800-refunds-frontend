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

class IsYourAddressUpToDatePage(baseUrl: String)(implicit webDriver: WebDriver) extends Page(
  baseUrl,
  path = "/get-an-income-tax-refund/cheque/is-your-address-up-to-date"
) {

  override def expectedH1: String = "Is your address up to date?"
  override def expectedWelshH1: String = "A yw’ch cyfeiriad yn gyfredol?"

  override def assertPageIsDisplayed(extraExpectations: ContentExpectation*): Unit = withPageClue {

    val contentExpectations: Seq[ContentExpectation] = Seq(ContentExpectation(
      atXpath       = PageUtil.Xpath.mainContent,
      expectedLines =
        """
          |Is your address up to date?
          |Your cheque will be sent to the same address as your tax calculation letter.
          |Yes
          |No, I need to update it
          |Confirm and continue
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
  }

  def assertPageIsDisplayedWithError(): Unit = withPageClue {

    val contentExpectations: Seq[ContentExpectation] = Seq(
      ContentExpectation(
        atXpath       = PageUtil.Xpath.mainContent,
        expectedLines =
          """
            |Is your address up to date?
            |Your cheque will be sent to the same address as your tax calculation letter.
            |Yes
            |No, I need to update it
            |Confirm and continue
            |""".stripMargin
      ),
      ContentExpectation(
        atXpath       = PageUtil.Xpath.errorSummary,
        expectedLines =
          """
            |There is a problem
            |Select if your address is up to date
            |""".stripMargin
      ),
      ContentExpectation(
        atXpath       = PageUtil.Xpath.errorMessage,
        expectedLines = """Error: Select if your address is up to date"""
      )
    )

    PageUtil.assertPage(
      baseUrl             = baseUrl,
      path                = path,
      h1                  = expectedH1,
      title               = PageUtil.standardErrorTitle(expectedH1),
      welshTest           = false,
      contentExpectations = contentExpectations: _*
    )
  }

  def selectYes(): Unit = withPageClue {
    PageUtil.clickByIdOrName("address-up-to-date")
  }

  def selectNo(): Unit = withPageClue {
    PageUtil.clickByIdOrName("address-up-to-date-2")
  }

}
