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

class DoYouWantToSignInPage(baseUrl: String)(implicit webDriver: WebDriver) extends Page(
  baseUrl,
  path = "/get-an-income-tax-refund/do-you-want-to-sign-in"
) {

  override def expectedH1: String = "Do you want to sign in?"
  override def expectedWelshH1 = "A ydych am fewngofnodi?"

  override def assertPageIsDisplayed(extraExpectations: ContentExpectation*): Unit = withPageClue {
    val contentExpectations: ContentExpectation = ContentExpectation(
      atXpath       = PageUtil.Xpath.mainContent,
      expectedLines =
        """
          |Do you want to sign in?
          |You’ll have fewer details to enter if you sign in using your Government Gateway user ID.
          |Yes, sign in
          |No, continue without signing in
          |Continue
          |""".stripMargin
    )

    PageUtil.assertPage(
      baseUrl             = baseUrl,
      path                = path,
      h1                  = expectedH1,
      title               = PageUtil.standardTitle(expectedH1),
      welshTest           = false,
      contentExpectations = contentExpectations
    )
  }

  def assertPageShowsWithErrors(): Unit = withPageClue {
    val errorContent: ContentExpectation = ContentExpectation(
      atXpath       = PageUtil.Xpath.mainContent,
      expectedLines =
        """
          |There is a problem
          |Select yes if you want to sign in to your tax account
          |Do you want to sign in?
          |You’ll have fewer details to enter if you sign in using your Government Gateway user ID.
          |Yes, sign in
          |No, continue without signing in
          |Continue
          |""".stripMargin
    )
    PageUtil.assertPage(
      baseUrl             = baseUrl,
      path                = path,
      h1                  = expectedH1,
      title               = PageUtil.standardErrorTitle(expectedH1),
      welshTest           = false,
      contentExpectations = errorContent
    )
  }

  def assertPageIsDisplayedInWelsh(): Unit = withPageClue {
    val contentExpectations: ContentExpectation = ContentExpectation(
      atXpath       = PageUtil.Xpath.mainContent,
      expectedLines =
        """
          |A ydych am fewngofnodi?
          |Bydd gennych lai o fanylion i’w nodi os byddwch yn mewngofnodi gan ddefnyddio eich Dynodydd Defnyddiwr (ID) ar gyfer Porth y Llywodraeth.
          |Iawn, mewngofnodi
          |Na, ewch yn eich blaen heb fewngofnodi
          |Yn eich blaen
          |""".stripMargin
    )

    PageUtil.assertPage(
      baseUrl             = baseUrl,
      path                = path,
      h1                  = expectedWelshH1,
      title               = PageUtil.standardTitleInWelsh(expectedWelshH1),
      welshTest           = true,
      contentExpectations = contentExpectations
    )
  }

  def assertPageShowsWithErrorsInWelsh(): Unit = withPageClue {
    val errorContent: ContentExpectation = ContentExpectation(
      atXpath       = PageUtil.Xpath.mainContent,
      expectedLines =
        """
          |A ydych am fewngofnodi?
          |Mae problem wedi codi
          |Dewiswch ‘Iawn’ os hoffech fewngofnodi i’ch cyfrif treth
          |Bydd gennych lai o fanylion i’w nodi os byddwch yn mewngofnodi gan ddefnyddio eich Dynodydd Defnyddiwr (ID) ar gyfer Porth y Llywodraeth.
          |Iawn, mewngofnodi
          |Na, ewch yn eich blaen heb fewngofnodi
          |Yn eich blaen
          |""".stripMargin
    )
    PageUtil.assertPage(
      baseUrl             = baseUrl,
      path                = path,
      h1                  = expectedWelshH1,
      title               = PageUtil.standardErrorTitleInWelsh(expectedWelshH1),
      welshTest           = true,
      contentExpectations = errorContent
    )
  }

  def selectYes(): Unit = withPageClue {
    PageUtil.clickByIdOrName("sign-in")
  }

  def selectNo(): Unit = withPageClue {
    PageUtil.clickByIdOrName("sign-in-2")
  }
}
