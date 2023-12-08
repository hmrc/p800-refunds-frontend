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

class ChooseAnotherWayToReceiveYourRefundPage(baseUrl: String)(implicit webDriver: WebDriver) extends Page(
  baseUrl,
  path = "/get-an-income-tax-refund/choose-another-way-to-receive-your-refund"
) {

  override def expectedH1: String = "Choose another way to receive your refund"

  override def assertPageIsDisplayed(errors: ContentExpectation*): Unit = withPageClue {
    val contentExpectations: Seq[ContentExpectation] = Seq(ContentExpectation(
      atXpath       = PageUtil.Xpath.mainContent,
      expectedLines =
        """
          |Choose another way to receive your refund
          |Bank transfer via your personal tax account
          |Cheque
          |Continue
          |""".stripMargin
    )) ++ errors

    PageUtil.assertPage(
      baseUrl             = baseUrl,
      path                = path,
      h1                  = expectedH1,
      title               = PageUtil.standardTitle(expectedH1),
      contentExpectations = contentExpectations: _*
    )
  }

  def assertPageShowsWithErrors(): Unit = withPageClue {
    val errorContent: ContentExpectation = ContentExpectation(
      atXpath       = PageUtil.Xpath.mainContent,
      expectedLines =
        """
          |There is a problem
          |Select if you want to receive a bank transfer via your personal tax account, or a cheque
          |""".stripMargin
    )
    assertPageIsDisplayed(errorContent)
  }

  def clickBankTransferOption(): Unit = PageUtil.clickByIdOrName("way-to-get-refund")
  def clickChequeOption(): Unit = PageUtil.clickByIdOrName("way-to-get-refund-2")

}
