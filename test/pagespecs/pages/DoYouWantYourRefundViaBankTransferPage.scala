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

class DoYouWantYourRefundViaBankTransferPage(baseUrl: String)(implicit webDriver: WebDriver) extends Page(
  baseUrl,
  path = "/get-an-income-tax-refund/do-you-want-your-refund-via-bank-transfer"
) {

  override def expectedH1: String = "Do you want your refund by bank transfer?"

  override def assertPageIsDisplayed(potentialErrors: ContentExpectation*): Unit = withPageClue {
    val contentExpectations: Seq[ContentExpectation] = Seq(
      ContentExpectation(
        atXpath       = PageUtil.Xpath.mainContent,
        expectedLines =
          """
            |Do you want your refund by bank transfer?
            |Bank transfers are faster, safer and better for the environment. You'll need to have your online or mobile banking details ready.
            |Yes
            |No, I want a cheque
            |Continue
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

  def assertPageShowsWithErrors(): Unit = assertPageIsDisplayed(
    ContentExpectation(
      atXpath       = PageUtil.Xpath.errorSummary,
      expectedLines =
        """
          |There is a problem
          |Select if you want to receive a bank transfer or a cheque
          |""".stripMargin
    ),
    ContentExpectation(
      atXpath       = PageUtil.Xpath.errorMessage,
      expectedLines = """Select if you want to receive a bank transfer or a cheque"""
    )
  )

  def selectYes(): Unit = withPageClue {
    PageUtil.clickByIdOrName("do-you-want-your-refund-via-bank-transfer")
  }

  def selectNo(): Unit = withPageClue {
    PageUtil.clickByIdOrName("do-you-want-your-refund-via-bank-transfer-2")
  }
}
