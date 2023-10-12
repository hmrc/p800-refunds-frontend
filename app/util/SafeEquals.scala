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

/**
 * Simple safe equals so we don't have to import cats
 */
object SafeEquals {

  @SuppressWarnings(Array("org.wartremover.warts.Equals"))
  implicit class EqualsOps[A](v: A) {
    def ===(other: A): Boolean = v == other
    def =!=(other: A): Boolean = v != other
  }
}
