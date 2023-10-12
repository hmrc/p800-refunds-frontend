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

package util

import enumeratum.{Enum, EnumEntry}
import play.api.libs.json._

object EnumFormat {

  final case class Transformation(writesTransformation: String => String, readsTransformation: String => String)

  object Transformation {
    val identityTransformation: Transformation = Transformation(identity, identity)
  }

  def apply[T <: EnumEntry](e: Enum[T], transformation: Transformation = Transformation.identityTransformation): Format[T] = Format(
    Reads {
      case JsString(value) =>
        e.withNameOption(transformation.readsTransformation(value))
          .map[JsResult[T]](JsSuccess(_))
          .getOrElse(JsError(s"Unknown ${e.getClass.getSimpleName} value: $value"))

      case _ =>
        JsError("Can only parse String")
    },
    Writes(v => JsString(transformation.writesTransformation(v.entryName)))
  )

}
