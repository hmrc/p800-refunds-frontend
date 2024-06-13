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

import models.ecospend.consent.{BankReferenceId, ConsentId, ConsentStatus}
import org.openqa.selenium.WebDriver
import pagespecs.pagesupport.{ContentExpectation, Page, PageUtil}

class VerifyingBankAccountPage(baseUrl: String, consentStatus: ConsentStatus, consentId: ConsentId, bankReferenceId: BankReferenceId)(implicit webDriver: WebDriver) extends Page(
  baseUrl,
  path = s"/get-an-income-tax-refund/bank-transfer/verifying-your-bank-account?status=${consentStatus.toString}&consent_id=${consentId.value}&bank_reference_id=${bankReferenceId.value}"
) {

  override def expectedH1: String = "We are verifying your bank account"
  override def expectedWelshH1: String = "Rydym yn gwirio’ch cyfrif banc"

  override def assertPageIsDisplayed(extraExpectations: ContentExpectation*): Unit = withPageClue {

    val contentExpectations: Seq[ContentExpectation] = Seq(ContentExpectation(
      atXpath       = PageUtil.Xpath.mainContent,
      expectedLines =
        """
          |We are verifying your bank account
          |This can take up to a minute. You can refresh this page if it does not update automatically.
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

  def clickRefreshThisPageLink(): Unit = PageUtil.clickByIdOrName("refresh-this-page")

}
