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

import models.audit.{NameMatchOutcome, NameMatchingAudit, RawNpsName}
import models.namematching._
import org.apache.commons.text.similarity.LevenshteinDetailedDistance
import play.api.mvc.RequestHeader
import util.JourneyLogger
import util.SafeEquals.EqualsOps
import models.ecospend.BankDescription

import java.text.Normalizer
import models.audit.ChosenBank

object NameMatchingService {

  def fuzzyNameMatching(
      npsOptFirstName:  Option[String],
      npsOptSecondName: Option[String],
      npsOptSurname:    Option[String],
      ecospendName:     String,
      bankDescription:  BankDescription
  )(implicit request: RequestHeader): (NameMatchingResponse, NameMatchingAudit) = {

    val npsSurname = npsOptSurname.getOrElse("")
    val sanitisedNpsSurname = sanitiseFullName(npsSurname)
    val sanitisedNpsName = sanitiseFullName(s"${npsOptFirstName.getOrElse("")} ${npsOptSecondName.getOrElse("")} $npsSurname")
    val sanitisedEcospendName = sanitiseFullName(ecospendName)

    val npsNamesList = sanitisedNpsName.split(' ').toSeq
    val ecoNamesList = sanitisedEcospendName.split(' ').toSeq
    val (npsListWithoutSurname, _) = npsNamesList.splitAt(npsNamesList.length - getSurnameLength(npsSurname))
    val (ecoListWithoutSurname, ecoSurname) = splitEcospendSurname(npsSurname, ecoNamesList)

    val fullEcospendName = s"${ecoListWithoutSurname.mkString(" ")} $ecoSurname"

    val doSanitisedNamesMatch = sanitisedNpsName === fullEcospendName
    val doSurnamesMatch = sanitisedNpsSurname === ecoSurname && sanitisedNpsSurname.nonEmpty && ecoSurname.nonEmpty

    val chosenBank: ChosenBank = ChosenBank(
      bankId       = bankDescription.bankId,
      name         = bankDescription.name,
      friendlyName = bankDescription.friendlyName
    )

    (doSanitisedNamesMatch, doSurnamesMatch) match {
      case (true, _) => (
        BasicSuccessfulNameMatch,
        NameMatchingAudit(
          outcome             = NameMatchOutcome(isSuccessful = true, BasicSuccessfulNameMatch.auditString),
          rawNpsName          = RawNpsName(npsOptFirstName, npsOptSecondName, npsOptSurname),
          rawBankName         = Some(ecospendName),
          transformedNpsName  = Some(sanitisedNpsName),
          transformedBankName = Some(fullEcospendName),
          chosenBank          = ChosenBank(
            bankId       = bankDescription.bankId,
            name         = bankDescription.name,
            friendlyName = bankDescription.friendlyName
          )
        )
      )
      case (false, false) =>
        JourneyLogger.info(s"Failed Surname Match")
        (
          FailedSurnameMatch,
          NameMatchingAudit(
            outcome             = NameMatchOutcome(isSuccessful = false, FailedSurnameMatch.auditString),
            rawNpsName          = RawNpsName(npsOptFirstName, npsOptSecondName, npsOptSurname),
            rawBankName         = Some(ecospendName),
            transformedNpsName  = Some(sanitisedNpsName),
            transformedBankName = Some(fullEcospendName),
            chosenBank          = chosenBank
          )
        )
      case (false, true) =>
        val comparisonResult = compareFirstAndMiddleNames(npsListWithoutSurname, ecoListWithoutSurname)
        if (comparisonResult.didNamesMatch) {
          JourneyLogger.info(s"Successful First and Middle name Match")
          (
            FirstAndMiddleNameSuccessfulNameMatch,
            NameMatchingAudit(
              outcome             = NameMatchOutcome(isSuccessful = true, FirstAndMiddleNameSuccessfulNameMatch.auditString),
              rawNpsName          = RawNpsName(npsOptFirstName, npsOptSecondName, npsOptSurname),
              rawBankName         = Some(ecospendName),
              transformedNpsName  = Some(s"${comparisonResult.npsNameWithInitials} $sanitisedNpsSurname"),
              transformedBankName = Some(s"${comparisonResult.ecospendNameWithInitials} $ecoSurname"),
              chosenBank          = chosenBank
            )
          )
        } else if (comparisonResult.validForLevenshtein) {
          val (matchingResponse, levenshteinDistance, isSuccessful) = isWithinLevenshteinDistance(comparisonResult.npsNameWithInitials, comparisonResult.ecospendNameWithInitials)
          (
            matchingResponse,
            NameMatchingAudit(
              outcome             = NameMatchOutcome(isSuccessful = isSuccessful, matchingResponse.auditString),
              rawNpsName          = RawNpsName(npsOptFirstName, npsOptSecondName, npsOptSurname),
              rawBankName         = Some(ecospendName),
              transformedNpsName  = Some(s"${comparisonResult.npsNameWithInitials} $sanitisedNpsSurname"),
              transformedBankName = Some(s"${comparisonResult.ecospendNameWithInitials} $ecoSurname"),
              chosenBank          = chosenBank,
              levenshteinDistance = Some(levenshteinDistance)
            )
          )
        } else {
          JourneyLogger.info(s"Failed First and Middle name Match")
          (
            FailedFirstAndMiddleNameMatch,
            NameMatchingAudit(
              outcome             = NameMatchOutcome(isSuccessful = false, FailedFirstAndMiddleNameMatch.auditString),
              rawNpsName          = RawNpsName(npsOptFirstName, npsOptSecondName, npsOptSurname),
              rawBankName         = Some(ecospendName),
              transformedNpsName  = Some(s"${comparisonResult.npsNameWithInitials} $sanitisedNpsSurname"),
              transformedBankName = Some(s"${comparisonResult.ecospendNameWithInitials} $ecoSurname"),
              chosenBank          = chosenBank
            )
          )
        }
    }
  }

  def compareFirstAndMiddleNames(npsNamesList: Seq[String], ecospendNamesList: Seq[String]): ComparisonResult = {
    //Excess middle names are intentionally dropped & we only convert to initials if one already exists
    val pairedList: Seq[(String, String)] = npsNamesList.zip(ecospendNamesList).map{ nameTuple =>
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
    val nameIsLongerThanOneCharacter = npsNameWithInitials.length > 1 && ecospendNameWithInitials.length > 1
    val namesAreEmptyStrings = npsNameWithInitials.isEmpty && ecospendNameWithInitials.isEmpty

    if (namesWithInitialsMatch && !namesAreEmptyStrings) {
      //Names matched
      ComparisonResult(didNamesMatch       = true, validForLevenshtein = false, npsNameWithInitials, ecospendNameWithInitials)
    } else if (nameIsLongerThanOneCharacter) {
      //Names didn't match and there is more than just an initial (Valid for Levenshtein)
      ComparisonResult(didNamesMatch       = false, validForLevenshtein = true, npsNameWithInitials, ecospendNameWithInitials)
    } else {
      //Didn't match and only an initial (Invalid for Levenshtein)
      ComparisonResult(didNamesMatch       = false, validForLevenshtein = false, npsNameWithInitials, ecospendNameWithInitials)
    }
  }

  private def getSurnameLength(surname: String) = {
    processSpacesApostrophesAndHyphens(surname)
      .split(" ")
      .toSeq
      .length
  }

  def splitEcospendSurname(npsSurname: String, ecospendNamesList: Seq[String]): (Seq[String], String) = {
    //By looking at the structure of the NPS surname, we can identify the structure of the Ecospend name to learn where the first/middle name ends and the surname begins.
    //This is necessary for surnames split by spaces not hyphens.
    val npsSurnameLength = getSurnameLength(npsSurname)

    val (firstMiddleList, surnameList) = ecospendNamesList.splitAt(ecospendNamesList.length - npsSurnameLength)
    val sanitisedSurname = processSpacesApostrophesAndHyphens(surnameList.mkString(" "))
    (firstMiddleList, sanitisedSurname)
  }

  def sanitiseFullName(name: String): String = {
    val nameWithoutHyphensOrSpaces = processSpacesApostrophesAndHyphens(name)
    val sanitisedName = removeDiacritics(nameWithoutHyphensOrSpaces)
    sanitisedName.toLowerCase
  }

  def processSpacesApostrophesAndHyphens(name: String): String = {
    name
      .trim
      .replaceAll("['.]", "") //removes apostrophes and fullstops
      .replaceAll("[‒‑—–-]", " ") //turns different hyphen types into spaces
      .replaceAll("\\s", " ") //turns tabs into single spaces
      .replaceAll(" +", " ") //turns multiple spaces into single space, (must be done after tab conversion)
  }

  def removeDiacritics(name: String): String = {
    val normalised = Normalizer.normalize(name, Normalizer.Form.NFD)
    normalised.replaceAll("[^\\p{ASCII}]", "")
  }

  @SuppressWarnings(Array("org.wartremover.warts.AutoUnboxing"))
  def isWithinLevenshteinDistance(name: String, comparisonName: String)(implicit request: RequestHeader): (NameMatchingResponse, Int, Boolean) = {
    val distance = LevenshteinDetailedDistance.getDefaultInstance.apply(name, comparisonName)
    if (distance.getDistance <= 1) {
      JourneyLogger.info("Successful Levenshtein Match")
      (LevenshteinSuccessfulNameMatch, distance.getDistance, true)
    } else {
      JourneyLogger.info("Failed Levenshtein Match")
      (FailedComprehensiveNameMatch, distance.getDistance, false)
    }
  }
}
