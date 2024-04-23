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

package services

import action.JourneyRequest
import models.namematching._
import org.apache.commons.text.similarity.LevenshteinDetailedDistance
import util.JourneyLogger
import util.SafeEquals.EqualsOps

import java.text.Normalizer

class NameMatchingService extends {

  def fuzzyNameMatching(
      npsOptFirstName:  Option[String],
      npsOptSecondName: Option[String],
      npsSurname:       String,
      ecospendName:     String
  )(implicit request: JourneyRequest[_]): NameMatchingResponse = {

    val sanitisedNpsName = sanitiseFullName(s"${removeSpacesAndHyphens(npsOptFirstName.getOrElse(""))} ${npsOptSecondName.getOrElse("")} ${removeSpacesAndHyphens(npsSurname)}")
    val sanitisedEcospendName = sanitiseFullName(ecospendName)

    val npsNamesList = sanitisedNpsName.split(' ')
    val ecoNamesList = sanitisedEcospendName.split(' ')
    val (npsListWithoutSurname, npsSurnameFromArray) = npsNamesList.splitAt(npsNamesList.length - 1)
    val (ecoListWithoutSurname, ecoSurname) = ecoNamesList.splitAt(ecoNamesList.length - 1)

    val doSanitisedNamesMatch = sanitisedNpsName === sanitisedEcospendName
    val doSurnamesMatch = npsSurnameFromArray.head === ecoSurname.head

    (doSanitisedNamesMatch, doSurnamesMatch) match {
      case (true, _) => BasicSuccessfulNameMatch
      case (false, false) =>
        JourneyLogger.info(s"Failed Surname Match")
        FailedNameMatch
      case (false, true) =>
        val comparisonResult = compareFirstAndMiddleNames(npsListWithoutSurname, ecoListWithoutSurname)
        if (comparisonResult.didNamesMatch) {
          JourneyLogger.info(s"Successful Initials Match")
          InitialsSuccessfulNameMatch
        } else {
          isWithinLevenshteinDistance(comparisonResult.npsNameWithInitials, comparisonResult.ecospendNameWithInitials)
        }
    }
  }

  def compareFirstAndMiddleNames(npsNamesList: Array[String], ecospendNamesList: Array[String]): ComparisonResult = {
    val pairedList: Array[(String, String)] = npsNamesList.zip(ecospendNamesList).map{ nameTuple =>
      val (npsPart, ecoPart) = nameTuple
      val isAnyPartAnInitial = (npsPart.length === 1) || (ecoPart.length === 1)

      (npsPart === ecoPart, isAnyPartAnInitial) match {
        case (false, true)  => (npsPart.head.toString, ecoPart.head.toString)
        case (false, false) => nameTuple
        case (true, _)      => nameTuple
      }
    }

    val npsNameWithInitials = pairedList.map(_._1).mkString(" ")
    val ecospendNameWithInitials = pairedList.map(_._2).mkString(" ")
    val namesWithInitialsMatch = npsNameWithInitials === ecospendNameWithInitials

    if (namesWithInitialsMatch) {
      ComparisonResult(didNamesMatch = true, npsNameWithInitials, ecospendNameWithInitials)
    } else {
      ComparisonResult(didNamesMatch = false, npsNameWithInitials, ecospendNameWithInitials)
    }
  }

  def removeSpacesAndHyphens(name: String): String = {
    name.trim
      .replace("-", "")
      .replaceAll("\\s", "")
  }

  def sanitiseFullName(name: String): String = {
    val nameWithoutDiatrics = removeDiacritics(name)
    val nameWithoutHyphensOrSpaces = nameWithoutDiatrics
      .replace("-", "")
      .trim
      .replaceAll(" +", " ")
      .replaceAll("\\s", " ")

    nameWithoutHyphensOrSpaces.toLowerCase
  }

  def removeDiacritics(name: String): String = {
    val normalised = Normalizer.normalize(name, Normalizer.Form.NFD)
    normalised.replaceAll("[^\\p{ASCII}]", "")
  }

  @SuppressWarnings(Array("org.wartremover.warts.AutoUnboxing"))
  def isWithinLevenshteinDistance(name: String, comparisonName: String)(implicit request: JourneyRequest[_]): NameMatchingResponse = {
    val distance = LevenshteinDetailedDistance.getDefaultInstance.apply(name, comparisonName)
    if (distance.getDistance <= 1) {
      JourneyLogger.info("Successful Levenshtein Match")
      LevenshteinSuccessfulNameMatch
    } else {
      JourneyLogger.info("Failed Levenshtein Match")
      FailedNameMatch
    }
  }
}
