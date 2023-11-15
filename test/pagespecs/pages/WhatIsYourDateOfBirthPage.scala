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

class WhatIsYourDateOfBirthPage(baseUrl: String)(implicit webDriver: WebDriver) extends Page(
  baseUrl,
  path = "/get-an-income-tax-refund/we-need-you-to-confirm-your-identity/what-is-your-date-of-birth"
) {

  override def expectedH1: String = "What is your date of birth?"

  def assertPageIsDisplayed(errors: ContentExpectation*): Unit = withPageClue {

    val contentExpectations: Seq[ContentExpectation] = Seq(ContentExpectation(
      atXpath       = PageUtil.Xpath.mainContent,
      expectedLines =
        """
          |What is your date of birth?
          |For example, 27 3 2007
          |Day Month Year
          |Continue
          |""".stripMargin
    )) ++ errors

    PageUtil.assertPage(
      path                = path,
      h1                  = expectedH1,
      title               = PageUtil.standardTitle(expectedH1),
      contentExpectations = contentExpectations: _*
    )
  }

}
