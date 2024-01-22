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

class WhatIsYourP800ReferencePage(baseUrl: String, pathForJourneyType: String)(implicit webDriver: WebDriver) extends Page(
  baseUrl,
  path = s"/get-an-income-tax-refund/$pathForJourneyType/what-is-your-p800-reference"
) {

  override def expectedH1: String = "What is your P800 reference?"

  private val p800ReferenceFieldId: String = "reference"

  def enterP800Reference(p800Reference: String): Unit = PageUtil.setTextFieldById(p800ReferenceFieldId, p800Reference)

  def clickPtaSignInLink(): Unit = PageUtil.clickByIdOrName("personal-tax-account-sign-in")

  def clickIncomeTaxGeneralEnquiriesLink(): String = {
    val handleBefore: String = webDriver.getWindowHandle
    PageUtil.clickByIdOrName("income-tax-general-enquiries")
    webDriver.getWindowHandles.asScala
      .diff(Set(handleBefore))
      .headOption
      .getOrElse(throw new Exception("Expecting at least one window handle"))
  }

  override def assertPageIsDisplayed(extraExpectations: ContentExpectation*): Unit = withPageClue {
    val contentExpectations: Seq[ContentExpectation] = Seq(
      ContentExpectation(
        atXpath       = PageUtil.Xpath.mainContent,
        expectedLines =
          """
            |It is on the letter HMRC sent you about your tax calculation, also known as a ‘P800’, and is up to 10 digits long.
            |For example, 1002033400.
            |If you do not know your P800 reference
            |""".stripMargin
      )
    ) ++ extraExpectations

    PageUtil.assertPage(
      baseUrl             = baseUrl,
      path                = path,
      h1                  = expectedH1,
      title               = PageUtil.standardTitle(expectedH1),
      contentExpectations = contentExpectations: _*
    )
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
  }
}
