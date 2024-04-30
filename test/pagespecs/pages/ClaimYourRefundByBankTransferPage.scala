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

class ClaimYourRefundByBankTransferPage(baseUrl: String)(implicit webDriver: WebDriver) extends Page(
  baseUrl,
  path = "/get-an-income-tax-refund/cheque/claim-your-refund-by-bank-transfer"
) {

  override def expectedH1: String = "Claim your refund by bank transfer"
  override def expectedWelshH1: String = "Hawliwch eich ad-daliad drwy drosglwyddiad banc"

  override def assertPageIsDisplayed(extraExpectations: ContentExpectation*): Unit = withPageClue {

    val contentExpectations: Seq[ContentExpectation] = Seq(ContentExpectation(
      atXpath       = PageUtil.Xpath.mainContent,
      expectedLines =
        """
          |Claim your refund by bank transfer
          |
          |Choose to sign in using your Government Gateway user ID to claim your refund and you will have fewer details to enter
          |Do you want to sign in?
          |Yes
          |No
          |Continue
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

  def assertPageDisplayedWithErrorMessage(): Unit = withPageClue {
    val contentExpectations: Seq[ContentExpectation] = Seq(
      ContentExpectation(
        atXpath       = PageUtil.Xpath.mainContent,
        expectedLines =
          """
            |Claim your refund by bank transfer
            |
            |Choose to sign in using your Government Gateway user ID to claim your refund and you will have fewer details to enter
            |Do you want to sign in?
            |Yes
            |No
            |Continue
            |""".stripMargin
      ),
      ContentExpectation(
        PageUtil.Xpath.errorSummary,
        """There is a problem
          |Select yes if you want to sign in to your tax account
          |""".stripMargin
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
    PageUtil.clickByIdOrName("sign-in")
  }

  def selectNo(): Unit = withPageClue {
    PageUtil.clickByIdOrName("sign-in-2")
  }

}
