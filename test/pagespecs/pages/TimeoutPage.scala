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
import testsupport.RichMatchers.convertToAnyShouldWrapper

class TimeoutPage(baseUrl: String, didUserDelete: Boolean)(implicit webDriver: WebDriver) extends Page(
  baseUrl,
  path = s"/get-an-income-tax-refund/timeout/${didUserDelete.toString}"
) {

  val expectedTitle = "Time out page - This page is going to time out"
  override def expectedH1: String = if (didUserDelete) "You deleted your answers" else "For your security, we deleted your answers"
  override def expectedWelshH1: String = if (didUserDelete) "Rydych wedi dileu’ch atebion" else "Er eich diogelwch, gwnaethom ddileu’ch atebion"

  override def assertPageIsDisplayed(extraExpectations: ContentExpectation*): Unit = withPageClue {

    val contentExpectations: Seq[ContentExpectation] = Seq(ContentExpectation(
      atXpath       = PageUtil.Xpath.mainContent,
      expectedLines =
        """
          |Start again
          |""".stripMargin
    )) ++ extraExpectations

    PageUtil.assertPage(
      baseUrl             = baseUrl,
      path                = path,
      h1                  = expectedH1,
      title               = PageUtil.standardTitle(expectedTitle),
      welshTest           = false,
      contentExpectations = contentExpectations: _*
    )

    PageUtil.elementDisplayedByClassName("govuk-back-link") shouldBe false
    ()
  }
}
