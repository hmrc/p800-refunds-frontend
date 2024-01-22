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

import models.ecospend.BankId
import models.journeymodels.JourneyType
import org.openqa.selenium.WebDriver
import pagespecs.pagesupport.{ContentExpectation, Page, PageUtil}

class WhatIsTheNameOfYourBankAccountPage(baseUrl: String)(implicit webDriver: WebDriver) extends Page(
  baseUrl,
  path = "/get-an-income-tax-refund/bank-transfer/what-is-the-name-of-your-bank-account"
) {

  override def expectedH1: String = "What is the name of your bank?"

  override def clickSubmit()(implicit webDriver: WebDriver): Unit =
    PageUtil.clickByIdOrName("continue")

  def clickMyAccountIsNotListed()(implicit webDriver: WebDriver): Unit =
    PageUtil.clickByIdOrName("myAccountIsNotListed")

  def selectBankAccount(bankId: BankId)(implicit webDriver: WebDriver): Unit =
    PageUtil.setSelectByIdAndValue("selectedBankId", bankId.value)

  override def assertPageIsDisplayed(extraExpectations: ContentExpectation*): Unit = withPageClue {

    val contentExpectations: Seq[ContentExpectation] = Seq(ContentExpectation(
      atXpath       = PageUtil.Xpath.mainContent,
      expectedLines =
        """
          |What is the name of your bank?
          |Start typing the name of a UK bank that you want your refund to be sent to.
          |Continue
          |My bank is not listed
          |""".stripMargin
    )) ++ extraExpectations

    PageUtil.assertPage(
      baseUrl             = baseUrl,
      path                = path,
      h1                  = expectedH1,
      title               = PageUtil.standardTitle(expectedH1),
      contentExpectations = contentExpectations: _*
    )
  }

  def assertPageShowsError(): Unit = withPageClue {

    val contentExpectations: Seq[ContentExpectation] = Seq(
      ContentExpectation(
        atXpath       = PageUtil.Xpath.mainContent,
        expectedLines =
          """
            |What is the name of your bank?
            |Start typing the name of a UK bank that you want your refund to be sent to.
            |Continue
            |My bank is not listed
            |""".stripMargin
      ),
      ContentExpectation(
        atXpath       = PageUtil.Xpath.mainContent,
        expectedLines =
          """
            |There is a problem
            |Select a bank from the list
            |""".stripMargin
      )
    )

    PageUtil.assertPage(
      baseUrl             = baseUrl,
      path                = path,
      h1                  = expectedH1,
      title               = PageUtil.standardErrorTitle(expectedH1, JourneyType.BankTransfer),
      contentExpectations = contentExpectations: _*
    )
  }

}
