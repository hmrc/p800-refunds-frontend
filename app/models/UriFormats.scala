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

package models

import org.apache.pekko.http.scaladsl.model.Uri
import play.api.libs.functional.syntax._
import play.api.libs.json._

import scala.util.{Try, Success, Failure}

object UriFormats {
  implicit class ReadsOps[A](val r: Reads[A]) extends AnyVal {
    def jsrFlatMap[B](f: A => JsResult[B]): Reads[B] = Reads[B](json =>
      r.reads(json).flatMap(a => f(a)))
  }

  private val reads: Reads[Uri] = Reads.StringReads.jsrFlatMap(s => parseUrl(s)) andKeep Reads.StringReads.map(_.trim).map(Uri(_))
  private val writes: Writes[Uri] = implicitly[Format[String]].inmap[Uri](Uri(_), _.toString)

  private def parseUrl(str: String): JsResult[Uri] = Try(Uri(str)) match {
    case Success(value) => JsSuccess(value)
    case Failure(_)     => JsError(s"'$str' is not an url")
  }

  implicit val uriJsonFormat: Format[Uri] = Format(reads, writes)
}

