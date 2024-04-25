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
import testsupport.RichMatchers.convertToAnyShouldWrapper

class WeNeedYouToConfirmYourIdentityPage(baseUrl: String, pathForJourneyType: String)(implicit webDriver: WebDriver) extends Page(
  baseUrl,
  path = s"/get-an-income-tax-refund/$pathForJourneyType/confirm-your-identity"
) {

  override def expectedH1: String = "We need you to confirm your identity"

  override def expectedTitleContent: String = "add_me"

  def assertPageIsDisplayedForChequeJourney(): Unit =
    assertPageIsDisplayed(JourneyType.Cheque)

  def assertPageIsDisplayedForBankTransferJourney(): Unit =
    assertPageIsDisplayed(
      JourneyType.BankTransfer,
      ContentExpectation(
        atXpath       = PageUtil.Xpath.mainContent,
        expectedLines = "date of birth"
      )
    )

  override def assertPageIsDisplayed(extraExpectations: ContentExpectation*): Unit = sys.error("Use another variant for asserting page")

  def assertPageIsDisplayed(journeyType: JourneyType, extraExpectations: ContentExpectation*): Unit = withPageClue {

    val contentExpectations: Seq[ContentExpectation] = Seq(ContentExpectation(
      atXpath       = PageUtil.Xpath.mainContent,
      expectedLines =
        """
          |We need you to confirm your identity
          |Before we pay your refund, we need to ask you some security questions to confirm your identity.
          |We will need to ask you for your:
          |reference
          |national insurance number
          |We do this to protect your security.
          |If you do not know your P800 reference, Sign in using your Government Gateway user ID to claim your refund.
          |If you do not know your National Insurance number, you can get help to find it (opens in new tab).
          |""".stripMargin
    )) ++ extraExpectations

    PageUtil.assertPage(
      baseUrl             = baseUrl,
      path                = path,
      h1                  = expectedH1,
      title               = PageUtil.standardTitleWithJourneyType("confirm your identity", journeyType),
      contentExpectations = contentExpectations: _*
    )

    PageUtil.getHrefById("lost-national-insurance-number-link") shouldBe "https://www.gov.uk/lost-national-insurance-number"

    ()

  }

}
