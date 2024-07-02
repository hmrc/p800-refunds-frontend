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

class YourRefundRequestHasNotBeenSubmittedPage(baseUrl: String, consentStatus: ConsentStatus, consentId: ConsentId, bankReferenceId: BankReferenceId)(implicit webDriver: WebDriver) extends Page(
  baseUrl,
  path = s"/get-an-income-tax-refund/your-refund-request-has-not-been-submitted?status=${consentStatus.toString}&consent_id=${consentId.value}&bank_reference_id=${bankReferenceId.value}"
) {

  override def expectedH1: String = "Your refund request has not been submitted"
  override def expectedWelshH1: String = "Nid yw’ch cais am ad-daliad wedi’i gyflwyno"

  override def assertPageIsDisplayed(extraExpectations: ContentExpectation*): Unit = withPageClue {

    val contentExpectations: Seq[ContentExpectation] = Seq(ContentExpectation(
      atXpath       = PageUtil.Xpath.mainContent,
      expectedLines =
        """
          |There has been a technical issue, you can:
          |try again
          |choose another way to get your refund
          |return to tax calculation letter (P800) guidance and try again later
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

  def clickTryAgain()(implicit webDriver: WebDriver): Unit = PageUtil.clickByIdOrName("try-again")
  def clickChooseAnother()(implicit webDriver: WebDriver): Unit = PageUtil.clickByIdOrName("choose-another")
  def clickRefundGuidance()(implicit webDriver: WebDriver): Unit = PageUtil.clickByIdOrName("refund-guidance")
  def getGuidanceUrl: String = PageUtil.getHrefById("refund-guidance")
}
