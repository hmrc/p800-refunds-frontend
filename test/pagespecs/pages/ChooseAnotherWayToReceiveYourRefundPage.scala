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

class ChooseAnotherWayToReceiveYourRefundPage(baseUrl: String, pathForJourneyType: String)(implicit webDriver: WebDriver) extends Page(
  baseUrl = baseUrl,
  path    = s"/get-an-income-tax-refund/$pathForJourneyType/choose-another-way-to-receive-your-refund"
) {

  override def expectedH1: String = "Choose another way to receive your refund"
  override def expectedTitleContent: String = "choose another way to receive your refund"

  override def assertPageIsDisplayed(errors: ContentExpectation*): Unit = sys.error("Use another variant for asserting page")

  def assertPageIsDisplayedPtaOrCheque(errors: ContentExpectation*): Unit = withPageClue {
    val contentExpectations: Seq[ContentExpectation] = Seq(ContentExpectation(
      atXpath       = PageUtil.Xpath.mainContent,
      expectedLines =
        """
          |Choose another way to receive your refund
          |You will have fewer details to enter if you sign in using your Government Gateway user ID.
          |Bank transfer using your Government Gateway user ID to sign in
          |Cheque
          |Continue
          |""".stripMargin
    )) ++ errors

    val expectedTitle =
      if (errors.isEmpty) PageUtil.standardTitleWithJourneyType(expectedTitleContent, JourneyType.BankTransfer)
      else PageUtil.standardErrorTitle(expectedTitleContent, JourneyType.BankTransfer)

    PageUtil.assertPage(
      baseUrl             = baseUrl,
      path                = path,
      h1                  = expectedH1,
      title               = expectedTitle,
      contentExpectations = contentExpectations: _*
    )
  }

  def assertPageIsDisplayedPtaOrBankTransfer(errors: ContentExpectation*): Unit = withPageClue {
    val contentExpectations: Seq[ContentExpectation] = Seq(ContentExpectation(
      atXpath       = PageUtil.Xpath.mainContent,
      expectedLines =
        """
          |Choose another way to receive your refund
          |Bank transfer using your Government Gateway user ID to sign in
          |Bank transfer logged out
          |Continue
          |""".stripMargin
    )) ++ errors

    val expectedTitle =
      if (errors.isEmpty) PageUtil.standardTitleWithJourneyType(expectedTitleContent, JourneyType.Cheque)
      else PageUtil.standardErrorTitle(expectedTitleContent, JourneyType.Cheque)

    PageUtil.assertPage(
      baseUrl             = baseUrl,
      path                = path,
      h1                  = expectedH1,
      title               = expectedTitle,
      contentExpectations = contentExpectations: _*
    )
  }

  def assertPtaOrChequePageShowsWithErrors(): Unit = withPageClue {
    val errorContent: ContentExpectation = ContentExpectation(
      atXpath       = PageUtil.Xpath.mainContent,
      expectedLines =
        """
          |There is a problem
          |Select the way you want to receive your refund
          |""".stripMargin
    )

    PageUtil.assertPage(
      baseUrl             = baseUrl,
      path                = path,
      h1                  = expectedH1,
      title               = PageUtil.standardTitleWithJourneyType(expectedTitleContent, JourneyType.BankTransfer),
      contentExpectations = errorContent
    )
  }

  object PtaOrCheque {
    def selectBankTransferViaPta(): Unit = PageUtil.clickByIdOrName("way-to-get-refund")
    def selectCheque(): Unit = PageUtil.clickByIdOrName("way-to-get-refund-2")
  }
  object PtaOrBankTransfer {
    def selectBankTransferViaPta(): Unit = PageUtil.clickByIdOrName("way-to-get-refund")
    def selectBankTransferLoggedOut(): Unit = PageUtil.clickByIdOrName("way-to-get-refund-2")
  }

}
