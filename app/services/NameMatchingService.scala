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

import models.namematching._
import nps.models.TraceIndividualResponse
import org.apache.commons.text.similarity.LevenshteinDetailedDistance
import play.api.Logger
import util.SafeEquals.EqualsOps

import java.text.Normalizer
import javax.inject.{Inject, Singleton}

@Singleton
class NameMatchingService @Inject() () extends {
  private val log: Logger = Logger("nameMatchingService")

  def buildNpsName(indiTrace: TraceIndividualResponse): String = {
    val surnameNoSpaces = indiTrace.surname.replaceAll(" +", "")
    s"${indiTrace.firstForename.getOrElse("")} ${indiTrace.secondForename.getOrElse("")} $surnameNoSpaces"
  }

  def fuzzyNameMatching(npsName: TraceIndividualResponse, ecospendName: String): NameMatchingResponse = {
    val (sanitisedNpsName, sanitisedEcospendName) = (sanitiseName(buildNpsName(npsName)), sanitiseName(ecospendName))

    val npsNamesList = sanitisedNpsName.split(' ')
    val ecoNamesList = sanitisedEcospendName.split(' ')
    val (npsListWithoutSurname, npsSurname) = npsNamesList.splitAt(npsNamesList.length - 1)
    val (ecoListWithoutSurname, ecoSurname) = ecoNamesList.splitAt(ecoNamesList.length - 1)

    val doSanitisedNamesMatch = sanitisedNpsName === sanitisedEcospendName
    val doSurnamesMatch = npsSurname.head === ecoSurname.head

    (doSanitisedNamesMatch, doSurnamesMatch) match {
      case (true, _) => BasicSuccessfulNameMatch
      case (false, false) =>
        log.info(s"Failed Surname Match")
        FailedNameMatch
      case (false, true) =>
        val comparisonResult = compareFirstAndMiddleNames(npsListWithoutSurname, ecoListWithoutSurname)
        if (comparisonResult.didNamesMatch) {
          log.info("Successful Initials Match")
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

  def sanitiseName(name: String): String = {
    val nameWithoutDiatrics = removeDiacritics(name)
    val nameWithoutHyphensOrSpaces = nameWithoutDiatrics.replace("-", "").trim.replaceAll(" +", " ")
    nameWithoutHyphensOrSpaces.toLowerCase
  }

  def removeDiacritics(name: String): String = {
    val normalised = Normalizer.normalize(name, Normalizer.Form.NFD)
    normalised.replaceAll("[^\\p{ASCII}]", "")
  }

  @SuppressWarnings(Array("org.wartremover.warts.AutoUnboxing"))
  def isWithinLevenshteinDistance(name: String, comparisonName: String): NameMatchingResponse = {
    val distance = LevenshteinDetailedDistance.getDefaultInstance.apply(name, comparisonName)
    if (distance.getDistance <= 1) {
      log.info("Successful Levenshtein Match")
      LevenshteinSuccessfulNameMatch
    } else {
      log.info("Failed Levenshtein Match")
      FailedNameMatch
    }
  }
}
