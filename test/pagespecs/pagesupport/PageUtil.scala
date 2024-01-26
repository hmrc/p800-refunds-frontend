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

import models.journeymodels.JourneyType
import org.openqa.selenium.{By, WebDriver, WebElement}
import org.scalatestplus.selenium.WebBrowser
import org.scalatestplus.selenium.WebBrowser._
import testsupport.RichMatchers._

import scala.util.Try

object PageUtil {

  object Xpath {
    val mainContent: String = """//*[@id="main-content"]"""
    val errorSummary: String = """//*[@id="main-content"]//*[@class="govuk-error-summary"]"""
    val errorMessage: String = """//*[@class="govuk-error-message"]"""
    val header: String = """/html/body/div/header"""
    val h1: String = """//*[@id="main-content"]//h1"""
    val serviceName: String = """/html/body/header//a[contains(@class, "hmrc-header__service-name")]"""
  }

  def clickByIdOrName(id: String)(implicit webDriver: WebDriver): Unit = findElement(id).underlying.click()
  def clickByClassName(className: String)(implicit webDriver: WebDriver): Unit = findElementByClassName(className).underlying.click()
  def clickByXpath(xPath: String)(implicit webDriver: WebDriver): Unit = xpath(xPath).element.underlying.click()

  def setTextFieldById(idValue: String, content: String)(implicit webDriver: WebDriver): Unit = WebBrowser.textField(idValue).value = content
  def setEmailFieldById(idValue: String, content: String)(implicit webDriver: WebDriver): Unit = {
    val emailField: WebElement = WebBrowser.emailField(idValue).underlying
    emailField.clear()
    emailField.sendKeys(content)
  }
  def getContentByIdOrName(idOrName: String)(implicit webDriver: WebDriver): String = findElement(idOrName).text

  def setSelectByIdAndValue(id: String, value: String)(implicit webDriver: WebDriver): Unit =
    singleSel(id).value_=(value)

  /**
   * Gets the value attribute of element identified by id or name
   */
  def getValueById(idOrName: String)(implicit webDriver: WebDriver): String = {
    val e: WebBrowser.Element = findElement(idOrName)
    e
      .attribute("value")
      .getOrElse(s"Element with id=[$idOrName] didn't have attribute 'value' ${e.toString()}")
  }

  /**
   * Gets the target attribute of element identified by id or name
   */
  def getTargetById(idOrName: String)(implicit webDriver: WebDriver): String = {
    val e: WebBrowser.Element = findElement(idOrName)
    e
      .attribute("target")
      .getOrElse(s"Element with id=[$idOrName] didn't have attribute 'target' ${e.toString()}")
  }

  /**
   * Gets the href attribute of element identified by id or name
   */
  def getHrefById(idOrName: String)(implicit webDriver: WebDriver): String = {
    val e: WebBrowser.Element = findElement(idOrName)
    e
      .attribute("href")
      .getOrElse(s"Element with id=[$idOrName] didn't have attribute 'href' ${e.toString()}")
  }

  /**
   * Gets the 'href' attribute of element identified by 'xPath'
   */
  def getHrefByXpath(xPath: String)(implicit webDriver: WebDriver): String = {
    xpath(xPath)
      .element
      .attribute("href")
      .getOrElse(throw new RuntimeException(s"Could not find 'href' attribute of element identified by xpath: [$xPath]"))
  }

  def findElement(idOrName: String)(implicit webDriver: WebDriver): Element = {
    WebBrowser.find(idOrName).getOrElse(throw new RuntimeException(s"Could not find element by id or name: [$idOrName]"))
  }

  def findElementByClassName(className: String)(implicit webDriver: WebDriver): Element = {
    WebBrowser.find(ClassNameQuery(className)).getOrElse(throw new RuntimeException(s"Could not find element by classname: [$className]"))
  }

  def elementDisplayed(idOrName: String)(implicit webDriver: WebDriver): Boolean = {
    WebBrowser.find(idOrName).isDefined
  }

  def elementDisplayedByClassName(className: String)(implicit webDriver: WebDriver): Boolean = {
    WebBrowser.find(ClassNameQuery(className)).isDefined
  }

  /**
   * Gets the part of the current url after host and port.
   * In other words from
   * `http://localhost:2000/path?query=sialala#whatever`
   * it constructs
   * `/path?query=sialala#whatever`
   */
  def readPath()(implicit webDriver: WebDriver): String = {
    val currentUrl = webDriver.getCurrentUrl
    Try {
      val url = new java.net.URL(currentUrl)

      url.getPath + Option(url.getQuery).fold("")("?" + _) + Option(url.getRef).getOrElse("")
    }.getOrElse(s"Count not construct path from $currentUrl")
  }

  def assertContentByXpath(contentExpectations: ContentExpectation*)(implicit webDriver: WebDriver): Unit = contentExpectations.foreach(ce =>
    assertContentByXpath(ce.atXpath, ce.expectedLines))

  def assertContentByXpath(atXpath: String, expectedLines: String)(implicit webDriver: WebDriver): Unit = {
    val expectedLinesProcessed: Seq[String] = expectedLines.stripSpaces().split("\n").toIndexedSeq
    val contentAtXpath: String = xpath(atXpath)
      .element
      .text
      .stripSpaces()
      //we replace `\n` with spaces so the tests can run both in intellij and in sbt.
      //for some reasons webDeriver's `getText` returns text with extra new lines if you run it from intellij.
      .replaceAll("\n", " ")

    expectedLinesProcessed.foreach { expectedLine: String =>
      contentAtXpath should include(expectedLine) withClue s"""\nThe text page, at xpath '$atXpath' didn't include '$expectedLine', the content was:\n$contentAtXpath\n"""
    }
  }

  /**
   * Utility method to help implement assertPageIsDisplayed* related methods in `Page` subclasses.
   * It asserts various elements like path, h1 and content making a good quality page assertion
   */
  def assertPage(
      path:                String,
      h1:                  String,
      title:               String,
      serviceName:         String,
      serviceNameUrl:      Option[String],
      contentExpectations: ContentExpectation*
  )(implicit webDriver: WebDriver): Unit = withPageClue(path) {
    readPath() shouldBe path withClue "path"
    readH1() shouldBe h1 withClue "h1"
    pageTitle shouldBe title withClue "pageTitle"
    readPageServiceName() shouldBe serviceName withClue "serviceName"
    serviceNameUrl.foreach(serviceNameUrl =>
      readPageServiceNameUrl() shouldBe serviceNameUrl withClue "serviceNameUrl")
    assertContentByXpath(contentExpectations: _*)
  }

  /**
   * Utility method to help implement assertPageIsDisplayed* related methods in `Page` subclasses.
   * It asserts various elements like path, h1 and content making a good quality page assertion
   */
  def assertPage(
      baseUrl:             String,
      path:                String,
      h1:                  String,
      title:               String,
      contentExpectations: ContentExpectation*
  )(implicit webDriver: WebDriver): Unit = assertPage(
    path                = path,
    h1                  = h1,
    title               = title,
    serviceName         = "Get an Income Tax refund",
    serviceNameUrl      = Some(s"$baseUrl/get-an-income-tax-refund/test-only/gov-uk-route-in"),
    contentExpectations = contentExpectations: _*
  )

  private def readH1()(implicit webDriver: WebDriver): String = xpath("""//*[@id="main-content"]//h1""").element.text.stripSpaces()

  private def readPageServiceName()(implicit webDriver: WebDriver): String = xpath(Xpath.serviceName).element.text
  private def readPageServiceNameUrl()(implicit webDriver: WebDriver): String = PageUtil.getHrefByXpath(PageUtil.Xpath.serviceName)

  /**
   * Runs test using `testF` and reports page related information if the test fails.
   * Handy if debugging failing page specs.
   */
  def withPageClue[A](pathHint: String, showPageSource: Boolean = true)(testF: => A)(implicit webDriver: WebDriver): A = //eventually(testF).
    testF.withClue {
      s"""
         |>>>the page: ${this.getClass.getSimpleName}
         |>>>url was: ${webDriver.getCurrentUrl}
         |>>>path is supposed to be: $pathHint
         |>>>serviceName: ${Try(readPageServiceName()).fold(_.toString, identity)}
         |>>>h1: ${Try(readH1()).fold(_.toString, identity)}
         |>>>title was: ${Try(pageTitle).fold(_.toString, identity)}
         |>>>page body was:
         |${Try(webDriver.findElement(By.tagName("body")).getText).fold(_ => webDriver.getPageSource, identity)}
         |${if (showPageSource) ">>>page source was:\n" + webDriver.getPageSource else ""}
         |""".stripMargin
    }

  def standardTitle(h1: String): String = s"$h1 - Get an Income Tax refund - GOV.UK"

  def standardTitleWithJourneyType(titleContent: String, journeyType: JourneyType): String =
    s"${journeyTypeAsStringContent(journeyType)} - $titleContent - Get an Income Tax refund - GOV.UK"

  val standardTitleForTestOnlyPages: String = s"Test Only - Get an Income Tax refund - GOV.UK"

  def standardTitleInWelsh(h1: String): String = s"$h1 - Get an Income Tax refund - GOV.UK"

  def standardErrorTitle(titleContent: String, journeyType: JourneyType): String =
    s"Error: ${journeyTypeAsStringContent(journeyType)} - $titleContent - Get an Income Tax refund - GOV.UK"

  private val journeyTypeAsStringContent: JourneyType => String = {
    case JourneyType.Cheque       => "Cheque"
    case JourneyType.BankTransfer => "Bank transfer"
  }

  val standardHeader: String =
    """
      |GOV.UK
      |Telephone Payment Service
      |Sign out"""
      .stripMargin

  val standardHeaderInWelsh: String =
    """
      |GOV.UK
      |Gwasanaeth Talu Dros y Ffôn
      |Allgofnodi"""
      .stripMargin

  implicit class StringOps(s: String) {
    /**
     * Transforms string so it's easier it to compare.
     * It also replaces `unchecked`
     */
    def stripSpaces(): String = s
      .replaceAll("unchecked", "") //when you run tests from intellij webdriver.getText adds extra 'unchecked' around selection
      .replaceAll("[^\\S\\r\\n]+", " ") //replace many consecutive white-spaces (but not new lines) with one space
      .replaceAll("[\r\n]+", "\n") //replace many consecutive new lines with one new line
      .split("\n").map(_.trim) //trim each line
      .filterNot(_ == "") //remove any empty lines
      .mkString("\n")
  }

}

