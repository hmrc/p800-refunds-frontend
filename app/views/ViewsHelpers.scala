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

package views

import language.{LangMessages, Language}
import requests.RequestSupport
import uk.gov.hmrc.govukfrontend.views.html.components._

import javax.inject.{Inject, Singleton}

/**
 * Put common components in here, so we only define them once
 */
@Singleton
class ViewsHelpers @Inject() (
    val requestSupport:    RequestSupport,
    val formWithCsrf:      FormWithCSRF,
    val govukBackLink:     GovukBackLink,
    val govukButton:       GovukButton,
    val govukCheckboxes:   GovukCheckboxes,
    val govukCookieBanner: GovukCookieBanner,
    val govukHint:         GovukHint,
    val govukRadios:       GovukRadios,
    val govukSelect:       GovukSelect,
    val govukInput:        GovukInput,
    val govukErrorSummary: GovukErrorSummary
)

object ViewsHelpers {
  def makeTitle(h1: String)(implicit language: Language): String =
    h1 + " - " + LangMessages.`Claim an income tax refund`.show
}
