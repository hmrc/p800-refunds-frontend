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

abstract class Page(
    baseUrl:           String,
    override val path: String
)(implicit protected val webDriver: WebDriver)
  extends Endpoint(baseUrl, path) {

  def expectedH1: String

  def clickEnglishLink()(implicit webDriver: WebDriver): Unit = click on xpath("""//a[@hreflang="en"]""")
  def clickWelshLink()(implicit webDriver: WebDriver): Unit = click on xpath("""//a[@hreflang="cy"]""")
  def clickBackButton()(implicit webDriver: WebDriver): Unit = click on xpath("""/html/body//a[@class="govuk-back-link"]""")
  def clickSignOut()(implicit webDriver: WebDriver): Unit = PageUtil.clickByClassName("hmrc-sign-out-nav__link")
  def clickSubmit()(implicit webDriver: WebDriver): Unit = PageUtil.clickByIdOrName("submit")

  protected def withPageClue[A](testF: => A)(implicit webDriver: WebDriver): A = PageUtil.withPageClue(path)(testF)

  def assertPageIsDisplayedWithTechnicalDifficultiesError(): Unit = withPageClue {
    PageUtil.assertPage(
      path  = path,
      h1    = "Sorry, there is a problem with the service",
      title = PageUtil.standardTitle("Sorry, there is a problem with the service"),
      ContentExpectation(
        atXpath       = PageUtil.Xpath.mainContent,
        expectedLines = """Try again later.
                          |
                          |Try again
                          |""".stripMargin
      )
    )
    ()
  }

  def assertPageIsDisplayedWithTechnicalDifficultiesErrorInWelsh(): Unit = withPageClue {
    PageUtil.assertPage(
      path  = path,
      h1    = "Mae’n ddrwg gennym, mae problem gyda’r gwasanaeth",
      title = PageUtil.standardTitleInWelsh("Mae’n ddrwg gennym, mae problem gyda’r gwasanaeth"),
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

  //Please don't add anything which is specific to only one page! Use specific Page for that.
  //Don't add public utility methods here. Use PageUtil for that.
}
