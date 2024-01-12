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

class GiveYourPermissionPage(baseUrl: String)(implicit webDriver: WebDriver) extends Page(
  baseUrl,
  path = "/get-an-income-tax-refund/give-your-permission"
) {

  override def expectedH1: String = "Give your permission"

  override def assertPageIsDisplayed(errors: ContentExpectation*): Unit = withPageClue {
    val contentExpectations: Seq[ContentExpectation] = Seq(ContentExpectation(
      atXpath       = PageUtil.Xpath.mainContent,
      expectedLines =
        """
          |By choosing approve, you will be redirected to Barclays Personal to securely log in and approve your refund of £12.34. Change my bank.
          |This is a service provided by Ecospend, an authorised payment institution regulated by the Financial Conduct Authority (FCA), which will initiate a refund directly from HMRC to your bank.
          |Ecospend will have one-off access to:
          |the name on your account
          |your account number and sort code
          |your transactions (this is just to confirm that your bank account is real and protect your security).
          |Ecospend will not store or share any of your data.
          |HMRC cannot see your transactions or online bank account.
          |Approve this refund
          |Choose another way to get my money
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

  def clickChangeBank(): Unit = PageUtil.clickByIdOrName("change-bank")
  def clickApproveThisRefund(): Unit = PageUtil.clickByIdOrName("approve-this-refund")
  def clickChooseAnotherWayToGetMyMoney(): Unit = PageUtil.clickByIdOrName("choose-another-way")

}
