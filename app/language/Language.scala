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

package language

import enumeratum.{Enum, EnumEntry}
import play.api.i18n.Lang
import play.api.libs.json.Format
import play.api.mvc.PathBindable
import Languages.{English, Welsh}
import util.EnumFormat
import util.ValueClassBinder.valueClassBinder

import scala.collection.immutable

sealed trait Language extends EnumEntry with Product with Serializable {
  val toPlayLang: Lang = Lang(code)

  def code: String

  def label: String
}

object Language {

  implicit val format: Format[Language] = EnumFormat(Languages)
  implicit val languagePathBinder: PathBindable[Language] = valueClassBinder(_.toString)

  def apply(lang: Lang): Language = lang.code match {
    case "en" => English
    case "cy" => Welsh
    case _    => English //default language is English
  }
}

object Languages extends Enum[Language] {

  val availableLanguages: List[Language] = List(English, Welsh)

  override def values: immutable.IndexedSeq[Language] = findValues

  case object English extends Language {
    override def code: String = "en"

    override def label: String = "English"
  }

  case object Welsh extends Language {
    override def code: String = "cy"

    override def label: String = "Cymraeg"
  }
}

