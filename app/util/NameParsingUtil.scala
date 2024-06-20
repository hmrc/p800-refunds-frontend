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

  @SuppressWarnings(Array("org.wartremover.warts.IterableOps"))
  def removeTitleFromName(titles: Seq[String], ecospendName: String): String = {
    val titlesSet = (titles ++ titles.map(_ + ".")).map(_.toLowerCase).toSet
    val fullNameList = ecospendName.split(',').map(_.toLowerCase).toSeq //split by comma first, to handle joint accounts

    val namesList: Seq[String] = fullNameList.map { fullName =>
      val singleAccountNameList = fullName.split(' ')
      val singleAccountNameTitle = if (singleAccountNameList.head.isEmpty) singleAccountNameList(1) else singleAccountNameList.head //skipping empty space after comma

      if (titlesSet.contains(singleAccountNameTitle)) {
        fullName.replace(singleAccountNameTitle, "").trim
      } else {
        fullName
      }
    }
    namesList.mkString(", ")
  }

  @SuppressWarnings(Array("org.wartremover.warts.IterableOps"))
  def bankIdBasedAccountNameParsing(accountName: String): Seq[String] = {
    if (accountName.contains('&')) {
      val jointAccountNamesList = accountName.split('&').map(_.trim)

      val secondSurname = jointAccountNamesList
        .last
        .splitAt(jointAccountNamesList.head.indexOf(' '))
        ._1
        .trim
        .replaceAll("\\s+", "")

        def sanitizeInitial(initials: String): String =
          initials
            .trim
            .replaceAll("\\s+", "")
            .toCharArray
            .mkString(" ")

      secondSurname.length match {
        case secondSurnameLength if secondSurnameLength <= 2 =>
          //Joint account same surname e.g. 'Rubens J & E' OR 'Rubens JI & ER' OR 'Rubens J I & E R'
          val (surname, party1InitialsUnsanitized) = // ('Rubens', 'J') or ('Rubens', 'JI') or ('Rubens', 'J I')
            jointAccountNamesList
              .head
              .splitAt(jointAccountNamesList.head.indexOf(' '))

          val party1Initials = sanitizeInitial(party1InitialsUnsanitized)
          val party2Initials = sanitizeInitial(jointAccountNamesList.last)

          Seq(s"$party1Initials $surname", s"$party2Initials $surname")
        case _ =>
          //Joint account different surnames e.g. 'Rubens J & Smith E' OR 'Rubens JI & Smith ER'
          jointAccountNamesList.map { fullName =>
            val fullNameAsList = fullName.split(' ').map(_.trim)
            val firstName = sanitizeInitial(fullNameAsList.tail.mkString)
            val surname = fullNameAsList.head
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
