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

package pagespecs

import models.dateofbirth.Month
import models.journeymodels.Journey
import testsupport.ItSpec

import java.time.{LocalDate, LocalDateTime}

class WhatIsYourDateOfBirthPageSpec extends ItSpec {

  override def beforeEach(): Unit = {
    super.beforeEach()
    addJourneyIdToSession(tdAll.journeyId)
  }

  "/enter-your-date-of-birth renders the what is your date of birth page" in {
    upsertJourneyToDatabase(tdAll.BankTransfer.journeyEnteredNino)
    pages.enterYourDateOfBirthPage.open()
    pages.enterYourDateOfBirthPage.assertPageIsDisplayed()
    getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeyEnteredNino
  }

  "Submitting form with valid data redirects user to checkYourAnswersPage" in {
    upsertJourneyToDatabase(tdAll.BankTransfer.journeyEnteredNino)
    pages.enterYourDateOfBirthPage.open()
    pages.enterYourDateOfBirthPage.assertPageIsDisplayed()
    pages.enterYourDateOfBirthPage.enterDayOfMonth("1")
    pages.enterYourDateOfBirthPage.enterMonth("1")
    pages.enterYourDateOfBirthPage.enterYear("2000")
    pages.enterYourDateOfBirthPage.clickSubmit()
    pages.checkYourAnswersBankTransferPage.assertPageIsDisplayedForBankTransfer(
      p800Reference           = tdAll.p800Reference,
      dateOfBirth             = tdAll.dateOfBirthFormatted,
      nationalInsuranceNumber = tdAll.nino
    )
    getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeyEnteredDateOfBirth
  }

  //todo when we support welsh, we'll need to update this to allow welsh months too.. I'll chuck them in as a comment for now :)
  "Allow month to be submitted in word form" - Seq(
    //Month x WhatUserEntered
    ("January", "January"), // "Ionawr"
    ("January", "january"), // "ionawr"
    ("February", "February"), // "Chwefror"
    ("March", "March"), // "Mawrth"
    ("April", "April"), // "Ebrill"
    ("May", "May"), // "Mai"
    ("June", "June"), // "Mehefin"
    ("July", "July"), // "Gorffennaf"
    ("August", "August"), // "Awst"
    ("September", "September"), // "Medi"
    ("October", "October"), // "Hydref"
    ("November", "November"), // "Tachwedd"
    ("December", "December"), // "Rhagfyr
    ("January", "Jan"), // not sure what the abbreviated welsh is, or if it exists.
    ("January", "jan"),
    ("February", "Feb"),
    ("March", "Mar"),
    ("April", "Apr"),
    //    "May", -- abbreviation is the same as full month name, just putting this here so we don't think we 'forgot'it
    ("June", "Jun"),
    ("July", "Jul"),
    ("August", "Aug"),
    ("September", "Sep"),
    ("October", "Oct"),
    ("November", "Nov"),
    ("December", "Dec")
  ).foreach {
      case (monthFormatted, whatUserEntered) =>
        s" '$whatUserEntered'" in {
          upsertJourneyToDatabase(tdAll.BankTransfer.journeyEnteredNino)
          pages.enterYourDateOfBirthPage.open()
          pages.enterYourDateOfBirthPage.assertPageIsDisplayed()
          pages.enterYourDateOfBirthPage.enterDayOfMonth("1")
          pages.enterYourDateOfBirthPage.enterMonth(whatUserEntered)
          pages.enterYourDateOfBirthPage.enterYear("2000")
          pages.enterYourDateOfBirthPage.clickSubmit()
          pages.checkYourAnswersBankTransferPage.assertPageIsDisplayedForBankTransfer(
            p800Reference           = tdAll.p800Reference,
            dateOfBirth             = s"01 $monthFormatted 2000",
            nationalInsuranceNumber = tdAll.nino
          )
          val expectedJourney: Journey = {
            val j = tdAll.BankTransfer.journeyEnteredDateOfBirth
            val dob = j.dateOfBirth.value
            j.copy(dateOfBirth = Some(j.dateOfBirth.value.copy(
              dayOfMonth = dob.dayOfMonth,
              month      = Month(whatUserEntered),
              year       = dob.year
            )))
          }
          getJourneyFromDatabase(tdAll.journeyId) shouldBeLike expectedJourney
        }
    }

  "Prepopulate the form if the user has already entered it" in {
    upsertJourneyToDatabase(tdAll.BankTransfer.journeyEnteredDateOfBirth)
    pages.enterYourDateOfBirthPage.open()
    pages.enterYourDateOfBirthPage.assertDataPrepopulated("1", "1", "2000")
    getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeyEnteredDateOfBirth
  }

  "Submitting form without entering anything shows error message" in {
    upsertJourneyToDatabase(tdAll.BankTransfer.journeyEnteredNino)
    pages.enterYourDateOfBirthPage.open()
    pages.enterYourDateOfBirthPage.assertPageIsDisplayed()
    pages.enterYourDateOfBirthPage.clickSubmit()
    pages.enterYourDateOfBirthPage.assertPageShowsErrorAllEmpty()
    getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeyEnteredNino
  }

  "Submitting form without day of month shows error message" in {
    upsertJourneyToDatabase(tdAll.BankTransfer.journeyEnteredNino)
    pages.enterYourDateOfBirthPage.open()
    pages.enterYourDateOfBirthPage.assertPageIsDisplayed()
    pages.enterYourDateOfBirthPage.enterMonth("1")
    pages.enterYourDateOfBirthPage.enterYear("2000")
    pages.enterYourDateOfBirthPage.clickSubmit()
    pages.enterYourDateOfBirthPage.assertPageShowsErrorDayMissing()
    getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeyEnteredNino
  }

  "Submitting form without month shows error message" in {
    upsertJourneyToDatabase(tdAll.BankTransfer.journeyEnteredNino)
    pages.enterYourDateOfBirthPage.open()
    pages.enterYourDateOfBirthPage.assertPageIsDisplayed()
    pages.enterYourDateOfBirthPage.enterDayOfMonth("1")
    pages.enterYourDateOfBirthPage.enterYear("2000")
    pages.enterYourDateOfBirthPage.clickSubmit()
    pages.enterYourDateOfBirthPage.assertPageShowsErrorMonthMissing()
    getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeyEnteredNino
  }

  "Submitting form without year shows error message" in {
    upsertJourneyToDatabase(tdAll.BankTransfer.journeyEnteredNino)
    pages.enterYourDateOfBirthPage.open()
    pages.enterYourDateOfBirthPage.assertPageIsDisplayed()
    pages.enterYourDateOfBirthPage.enterDayOfMonth("1")
    pages.enterYourDateOfBirthPage.enterMonth("1")
    pages.enterYourDateOfBirthPage.clickSubmit()
    pages.enterYourDateOfBirthPage.assertPageShowsErrorYearMissing()
    getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeyEnteredNino
  }

  "Submitting form where month is greater than 12" in {
    upsertJourneyToDatabase(tdAll.BankTransfer.journeyEnteredNino)
    pages.enterYourDateOfBirthPage.open()
    pages.enterYourDateOfBirthPage.assertPageIsDisplayed()
    pages.enterYourDateOfBirthPage.enterDayOfMonth("1")
    pages.enterYourDateOfBirthPage.enterMonth("13")
    pages.enterYourDateOfBirthPage.enterYear("2000")
    pages.enterYourDateOfBirthPage.clickSubmit()
    pages.enterYourDateOfBirthPage.assertPageShowsErrorMonthTooLarge()
    getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeyEnteredNino
  }

  "Submitting form where month is greater than day is greater than 31" in {
    upsertJourneyToDatabase(tdAll.BankTransfer.journeyEnteredNino)
    pages.enterYourDateOfBirthPage.open()
    pages.enterYourDateOfBirthPage.assertPageIsDisplayed()
    pages.enterYourDateOfBirthPage.enterDayOfMonth("32")
    pages.enterYourDateOfBirthPage.enterMonth("1")
    pages.enterYourDateOfBirthPage.enterYear("2000")
    pages.enterYourDateOfBirthPage.clickSubmit()
    pages.enterYourDateOfBirthPage.assertPageShowsErrorMonthTooLarge()
    getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeyEnteredNino
  }

  Seq("20oo", "94", "20001").foreach { yearInput: String =>
    s"Submitting form incorrect year format: [ $yearInput ]" in {
      upsertJourneyToDatabase(tdAll.BankTransfer.journeyEnteredNino)
      pages.enterYourDateOfBirthPage.open()
      pages.enterYourDateOfBirthPage.assertPageIsDisplayed()
      pages.enterYourDateOfBirthPage.enterDayOfMonth("1")
      pages.enterYourDateOfBirthPage.enterMonth("1")
      pages.enterYourDateOfBirthPage.enterYear(yearInput)
      pages.enterYourDateOfBirthPage.clickSubmit()
      pages.enterYourDateOfBirthPage.assertPageShowsErrorYearLength()
      getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeyEnteredNino
    }
  }

  "Submitting form date in the future" in {
    upsertJourneyToDatabase(tdAll.BankTransfer.journeyEnteredNino)
    val dateInFuture: LocalDateTime = LocalDateTime.now(clock).plusDays(1L)
    pages.enterYourDateOfBirthPage.open()
    pages.enterYourDateOfBirthPage.assertPageIsDisplayed()
    pages.enterYourDateOfBirthPage.enterDayOfMonth(dateInFuture.getDayOfMonth.toString)
    pages.enterYourDateOfBirthPage.enterMonth(dateInFuture.getMonthValue.toString)
    pages.enterYourDateOfBirthPage.enterYear(dateInFuture.getYear.toString)
    pages.enterYourDateOfBirthPage.clickSubmit()
    pages.enterYourDateOfBirthPage.assertPageShowsErrorDateInTheFuture()
    getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeyEnteredNino
  }

  "Submitting a DOB that is below the minimum allowed age of 16 years (e.g. 15 years and 364 days and below)" in {
    upsertJourneyToDatabase(tdAll.BankTransfer.journeyEnteredNino)
    val `not quite 16 years ago` = LocalDate.now().minusYears(16L).plusDays(1L)
    // i.e. format
    val `16 years ago expected date message` = LocalDate.now().minusYears(16L).format(tdAll.gdsDateTimeFormatter)
    pages.enterYourDateOfBirthPage.open()
    pages.enterYourDateOfBirthPage.assertPageIsDisplayed()
    pages.enterYourDateOfBirthPage.enterDayOfMonth(`not quite 16 years ago`.getDayOfMonth.toString)
    pages.enterYourDateOfBirthPage.enterMonth(`not quite 16 years ago`.getMonthValue.toString)
    pages.enterYourDateOfBirthPage.enterYear(`not quite 16 years ago`.getYear.toString)
    pages.enterYourDateOfBirthPage.clickSubmit()
    pages.enterYourDateOfBirthPage.assertPageShowsErrorDateTooYoung(`16 years ago expected date message`)
    getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeyEnteredNino
  }

  "Submitting a DOB that is above the maximum allowed age of 110 years" in {
    upsertJourneyToDatabase(tdAll.BankTransfer.journeyEnteredNino)
    val `110 Years and one day ago` = LocalDate.now().minusYears(110L).minusDays(1L)
    val `110 years ago expected date message` = LocalDate.now().minusYears(110L).format(tdAll.gdsDateTimeFormatter)
    pages.enterYourDateOfBirthPage.open()
    pages.enterYourDateOfBirthPage.assertPageIsDisplayed()
    pages.enterYourDateOfBirthPage.enterDayOfMonth(`110 Years and one day ago`.getDayOfMonth.toString)
    pages.enterYourDateOfBirthPage.enterMonth(`110 Years and one day ago`.getMonthValue.toString)
    pages.enterYourDateOfBirthPage.enterYear(`110 Years and one day ago`.getYear.toString)
    pages.enterYourDateOfBirthPage.clickSubmit()
    pages.enterYourDateOfBirthPage.assertPageShowsErrorDateTooOld(`110 years ago expected date message`)
    getJourneyFromDatabase(tdAll.journeyId) shouldBeLike tdAll.BankTransfer.journeyEnteredNino
  }

}
