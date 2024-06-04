/*
 * Copyright 2024 HM Revenue & Customs
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

object NameParsingUtil {

  val bankIdMatchingVariationList: Seq[String] = Seq(
    "obie-natwest-production",
    "obie-rbs-personal-production",
    "obie-natwest-bankline-production",
    "obie-natwest-international-production",
    "obie-natwest-international-eq-production",
    "obie-natwest-clearspend-production",
    "obie-rbs-natwest-one-production",
    "obie-rbs-one-production",
    "obie-rbs-virgin-production",
    "obie-rbs-bankline-production",
    "obie-rbs-international-production",
    "obie-rbs-international-eq-production",
    "obie-ulster-ni-production",
    "obie-ulster-ni-bankline-production"
  )

  @SuppressWarnings(Array("org.wartremover.warts.IterableOps"))
  def bankIdBasedAccountNameParsing(accountName: String): Seq[String] = {
    if (accountName.contains('&')) {
      val jointAccountNamesList = accountName.split('&')
      jointAccountNamesList.last.trim.length match {
        case 1 =>
          //Joint account same surname e.g. Rubens J & E
          val accountNameSeq = jointAccountNamesList.head.split(' ').toSeq
          val surname = accountNameSeq.head

          Seq(s"${accountNameSeq.last} $surname", s"${jointAccountNamesList.last.trim} $surname")
        case _ =>
          //Joint account different surnames e.g. Rubens J & Smith E
          jointAccountNamesList.map { fullName =>
            val fullNameAsList = fullName.split(' ')
            val firstName = fullNameAsList.last
            val surname = if (fullNameAsList.head.isEmpty) fullNameAsList(1) else fullNameAsList.head // the space after the & causes issues with split
            s"$firstName $surname"
          }.toSeq
      }

    } else {
      val singleAccountNameList = accountName.split(' ').toSeq
      singleAccountNameList.last.length match {
        case 1 =>
          //Single account name has has single initial e.g. Rubens J
          Seq(s"${singleAccountNameList.last} ${singleAccountNameList.head}")
        case _ =>
          //Single account name has has multiple initials e.g. Rubens JE
          val accountNameInitials = singleAccountNameList.last.split("").toSeq.mkString(" ") //JE -> J E
          Seq(s"$accountNameInitials ${singleAccountNameList.head}")
      }
    }
  }

}
