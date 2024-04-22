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

import models.journeymodels.JourneyType
import org.openqa.selenium.WebDriver
import pagespecs.pagesupport.{ContentExpectation, Page, PageUtil}

class WeCannotConfirmYourIdentityPage(baseUrl: String, pathForJourneyType: String)(implicit webDriver: WebDriver) extends Page(
  baseUrl,
  path = s"/get-an-income-tax-refund/$pathForJourneyType/cannot-confirm-your-identity-try-again"
) {

  override def expectedH1: String = "We cannot confirm your identity"
  override def expectedTitleContent: String = "cannot confirm your identity try again"

  override def assertPageIsDisplayed(extraExpectations: ContentExpectation*): Unit = sys.error("Use another variant for asserting page")

  def assertPageIsDisplayed(journeyType: JourneyType, extraExpectations: ContentExpectation*): Unit = withPageClue {

    val chooseAnotherMethodLinkText: String = journeyType match {
      case JourneyType.Cheque       => "Claim your refund by bank transfer"
      case JourneyType.BankTransfer => "Choose another way to get my refund"
    }

    val contentExpectations: Seq[ContentExpectation] = Seq(ContentExpectation(
      atXpath       = PageUtil.Xpath.mainContent,
      expectedLines =
        s"""
          |We cannot confirm your identity
          |The information you have provided does not match our records.
          |Try again
          |$chooseAnotherMethodLinkText
          |""".stripMargin
    )) ++ extraExpectations

    PageUtil.assertPage(
      path                = path,
      h1                  = expectedH1,
      title               = PageUtil.standardTitleWithJourneyType(expectedTitleContent, journeyType),
      contentExpectations = contentExpectations: _*,
      baseUrl             = baseUrl
    )
  }

  def clickTryAgain(): Unit = PageUtil.clickByIdOrName("try-again")
  def clickChooseAnotherWay(): Unit = PageUtil.clickByIdOrName("choose-another-method-link")

}
