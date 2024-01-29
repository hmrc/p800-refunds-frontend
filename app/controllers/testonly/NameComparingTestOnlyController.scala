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

package controllers.testonly

import action.Actions
import org.apache.commons.codec.language.Soundex
import play.api.data.Forms.{optional, text}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Request}
import uk.gov.hmrc.govukfrontend.views.Aliases.{HtmlContent, Key, Value}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.ViewsTestOnly
import services.{IdentityVerificationService, JourneyService}
import uk.gov.hmrc.govukfrontend.views.Aliases.{HtmlContent, Key, SummaryListRow, Value}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}

@Singleton
class NameComparingTestOnlyController @Inject() (
    mcc:           MessagesControllerComponents,
    viewsTestOnly: ViewsTestOnly,
    as:            Actions
) extends FrontendController(mcc) {

  def get(
      forename:      Option[String],
      middlename:    Option[String],
      surname:       Option[String],
      fullLegalName: Option[String]
  ): Action[AnyContent] = as.default { implicit request =>

    val nameComparing: NameComparing = NameComparing(
      nameAtNps               = NameAtNps(
        forename   = forename.getOrElse(""),
        middlename = middlename.getOrElse(""),
        surname    = surname.getOrElse("")
      ),
      fullLegalNameAtEcospend = fullLegalName.getOrElse("")
    )

    Ok(viewsTestOnly.nameComparingTestOnlyPage(
      form        = NameComparingTestOnlyControllerForm.form.fill(nameComparing),
      summaryList = buildSummaryList(nameComparing)
    ))
  }

  private def buildSummaryListRow(key: String, descriptionHtml: String)(implicit request: Request[_]): SummaryListRow = SummaryListRow(
    key     = Key(HtmlContent(s"""$key""")),
    value   = Value(HtmlContent(
      //language=HTML
      s"""
          <div class="govuk-grid-row">
              $descriptionHtml
          </div>
      """
    )),
    classes = ""
  )

  def buildSummaryList(comparing: NameComparing): SummaryList = {
    val a1 = comparing.fullLegalNameAtEcospend
    val b1 = comparing.nameAtNps.forename + " " + comparing.nameAtNps.middlename + " " + comparing.nameAtNps.surname

    SummaryList(rows = Seq(
      buildSummaryListRow(
        "Concatenation",
        s"""
           |<pre>$a1</pre>
           |<pre>$b1</pre>
           |""".stripMargin
      )
    ))
  }

  def trim(s: String): String = s.trim //Remove leading and trailing white spaces.
  def collapse(s: String): String = s.replaceAll("\\s+", " ") //Replace multiple consecutive white space characters with a single space.
  def caseNormalise(s: String): String = s.toUpperCase() //Convert all characters in the string to uppercase.
  def removePunctuationAndSpecialChars(s: String): String = s.replaceAll("[^A-Za-z0-9]", "")

  //diacritics to be stripped away, resulting in plain ASCII characters:
  def removeDiacritics(s: String): String = {
    import java.text.Normalizer
    import java.text.Normalizer.Form
    val nfdNormalizedString = Normalizer.normalize(s, Form.NFD)
    nfdNormalizedString.filterNot(ch => Character.getType(ch) == Character.NON_SPACING_MARK)
  }

  final case class LevensteinResult(distance: Int, maxLength: Int, similarityPercentage: Double)

  def levenshteinDistanceAnalysis(str1: String, str2: String): LevensteinResult = {
    import org.apache.commons.text.similarity.LevenshteinDistance
    val levenshteinDistance = new LevenshteinDistance()
    val distance = levenshteinDistance.apply(str1, str2)
    val maxLength: Int = math.max(str1.length, str2.length)

    val result = if (maxLength > 0) {
      (1 - distance.toDouble / maxLength) * 100
    } else {
      100.0
    }

    LevensteinResult(distance, maxLength, result)
  }

  private val soundex: Soundex = new Soundex()
  def encodePhonetically(s: String): String = soundex.encode(s)

  val post: Action[AnyContent] = as.default { implicit request =>
    NameComparingTestOnlyControllerForm.form.bindFromRequest().fold(
      formWithErrors =>
        BadRequest(viewsTestOnly.nameComparingTestOnlyPage(formWithErrors, SummaryList(rows = Seq()))),
      nameComparing =>
        Redirect(controllers.testonly.routes.NameComparingTestOnlyController.get(
          Some(nameComparing.nameAtNps.forename),
          Some(nameComparing.nameAtNps.middlename),
          Some(nameComparing.nameAtNps.surname),
          Some(nameComparing.fullLegalNameAtEcospend)
        ))
    )
  }

}

import play.api.data.Form
import play.api.data.Forms.mapping

final case class NameAtNps(
    forename:   String,
    middlename: String,
    surname:    String
)

final case class NameComparing(
    nameAtNps:               NameAtNps,
    fullLegalNameAtEcospend: String
)

object NameComparingTestOnlyControllerForm {
  val form: Form[NameComparing] = {
    Form(
      mapping = mapping(
        "nameAtNps.forename" -> optional(text),
        "nameAtNps.middlename" -> optional(text),
        "nameAtNps.surname" -> optional(text),
        "fullLegalNameAtEcospend" -> optional(text)
      )((a1, a2, a3, a4) => NameComparing(
          NameAtNps(
            a1.getOrElse(""),
            a2.getOrElse(""),
            a3.getOrElse("")
          ),
          a4.getOrElse("")
        ))(r =>
          Some((
            Some(r.nameAtNps.forename),
            Some(r.nameAtNps.middlename),
            Some(r.nameAtNps.surname),
            Some(r.fullLegalNameAtEcospend)
          )))
    )
  }
}
