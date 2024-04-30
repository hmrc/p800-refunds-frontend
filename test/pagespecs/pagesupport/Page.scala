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

package pagespecs.pagesupport

import org.openqa.selenium.WebDriver
import org.scalatestplus.selenium.WebBrowser._
import pagespecs.pagesupport.PageUtil.Xpath

abstract class Page(
    baseUrl:           String,
    override val path: String
)(implicit protected val webDriver: WebDriver)
  extends Endpoint(baseUrl, path) {

  def expectedH1: String
  def expectedWelshH1: String
  def clickEnglishLink()(implicit webDriver: WebDriver): Unit = click on xpath("""//a[@hreflang="en"]""")
  def clickWelshLink()(implicit webDriver: WebDriver): Unit = click on xpath("""//a[@hreflang="cy"]""")
  def clickBackButton()(implicit webDriver: WebDriver): Unit = click on xpath("""/html/body//a[@class="govuk-back-link"]""")
  def clickBackButtonInBrowser()(implicit webDriver: WebDriver): Unit = webDriver.navigate().back()
  def clickSignOut()(implicit webDriver: WebDriver): Unit = PageUtil.clickByClassName("hmrc-sign-out-nav__link")
  def clickSubmit()(implicit webDriver: WebDriver): Unit = PageUtil.clickByIdOrName("submit")
  def clickServiceName()(implicit webDriver: WebDriver): Unit = PageUtil.clickByXpath(Xpath.serviceName)

  protected def withPageClue[A](testF: => A)(implicit webDriver: WebDriver): A = PageUtil.withPageClue(path)(testF)

  def assertPageIsDisplayedWithTechnicalDifficultiesError(): Unit = withPageClue {
    PageUtil.assertPage(
      baseUrl   = baseUrl,
      path      = path,
      h1        = "Sorry, we’re experiencing technical difficulties",
      title     = PageUtil.standardTitle("Sorry, we are experiencing technical difficulties - 500"),
      welshTest = false,
      ContentExpectation(
        atXpath       = PageUtil.Xpath.mainContent,
        expectedLines = """
                          |Sorry, we’re experiencing technical difficulties
                          |Please try again in a few minutes.
                          |""".stripMargin
      )
    )
    ()
  }

  def assertPageIsDisplayedWithTechnicalDifficultiesErrorInWelsh(): Unit = withPageClue {
    PageUtil.assertPage(
      baseUrl   = baseUrl,
      path      = path,
      h1        = "Mae’n ddrwg gennym, mae problem gyda’r gwasanaeth",
      title     = PageUtil.standardTitleInWelsh("Mae’n ddrwg gennym, mae problem gyda’r gwasanaeth"),
      welshTest = true,
      ContentExpectation(
        atXpath       = PageUtil.Xpath.mainContent,
        expectedLines = """Rhowch gynnig arall arni yn nes ymlaen.
                          |
                          |Rhowch gynnig arall arni
                          |""".stripMargin
      )
    )
    ()
  }

  def assertPageIsDisplayed(extraExpectations: ContentExpectation*): Unit

  //Please don't add anything which is specific to only one page! Use specific Page for that.
  //Don't add public utility methods here. Use PageUtil for that.
}
