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

import testsupport.ItSpec

import java.time.{LocalDate, LocalDateTime}

class WhatIsYourDateOfBirthPageSpec extends ItSpec {

  override def beforeEach(): Unit = {
    super.beforeEach()
    addJourneyIdToSession(tdAll.journeyId)
    //TODO    upsertJourneyToDatabase(tdAll.journeyWhatIsYourFullName)
  }

  "/what-is-your-date-of-birth renders the what is your date of birth page" in {
    pages.whatIsYourDateOfBirthPage.open()
    pages.whatIsYourDateOfBirthPage.assertPageIsDisplayed()
  }

  "Submitting form with valid data redirects user to 'What is your national insurance number' page" in {
    pages.whatIsYourDateOfBirthPage.open()
    pages.whatIsYourDateOfBirthPage.assertPageIsDisplayed()
    pages.whatIsYourDateOfBirthPage.enterDayOfMonth("1")
    pages.whatIsYourDateOfBirthPage.enterMonth("1")
    pages.whatIsYourDateOfBirthPage.enterYear("2000")
    pages.whatIsYourDateOfBirthPage.clickSubmit()
    pages.whatIsYourNationalInsuranceNumberPage.assertPageIsDisplayed()
  }

  //todo when we support welsh, we'll need to update this to allow welsh months too.. I'll chuck them in as a comment for now :)
  Seq(
    "January", // "Ionawr"
    "january", // "ionawr"
    "February", // "Chwefror"
    "March", // "Mawrth"
    "April", // "Ebrill"
    "May", // "Mai"
    "June", // "Mehefin"
    "July", // "Gorffennaf"
    "August", // "Awst"
    "September", // "Medi"
    "October", // "Hydref"
    "November", // "Tachwedd"
    "December", // "Rhagfyr
    "Jan", // not sure what the abbreviated welsh is, or if it exists.
    "jan",
    "Feb",
    "Mar",
    "Apr",
    //"May", -- abbreviation is the same as full month name, just putting this here so we don't think we 'forgot'it
    "Jun",
    "Jul",
    "Aug",
    "Sep",
    "Oct",
    "Nov",
    "Dec"
  ).foreach { monthAsWord =>
      s"Allow month to be submitted in word form (i.e. '$monthAsWord')" in {
        pages.whatIsYourDateOfBirthPage.open()
        pages.whatIsYourDateOfBirthPage.assertPageIsDisplayed()
        pages.whatIsYourDateOfBirthPage.enterDayOfMonth("1")
        pages.whatIsYourDateOfBirthPage.enterMonth(monthAsWord)
        pages.whatIsYourDateOfBirthPage.enterYear("2000")
        pages.whatIsYourDateOfBirthPage.clickSubmit()
        pages.whatIsYourNationalInsuranceNumberPage.assertPageIsDisplayed()
      }
    }

  "Clicking 'Back' sends user to 'What is your name' page" in {
    pages.whatIsYourDateOfBirthPage.open()
    pages.whatIsYourDateOfBirthPage.assertPageIsDisplayed()
    pages.whatIsYourDateOfBirthPage.clickBackButton()
    pages.whatIsYourFullNamePage.assertPageIsDisplayed()
  }

  "Prepopulate the form if the user has already entered it" in {
    //TODO   upsertJourneyToDatabase(tdAll.journeyWhatIsYourDateOfBirth)
    pages.whatIsYourDateOfBirthPage.open()
    pages.whatIsYourDateOfBirthPage.assertDataPrepopulated("1", "1", "2000")
  }

  "Submitting form without entering anything shows error message" in {
    pages.whatIsYourDateOfBirthPage.open()
    pages.whatIsYourDateOfBirthPage.assertPageIsDisplayed()
    pages.whatIsYourDateOfBirthPage.clickSubmit()
    pages.whatIsYourDateOfBirthPage.assertPageShowsErrorAllEmpty()
  }

  "Submitting form without day of month shows error message" in {
    pages.whatIsYourDateOfBirthPage.open()
    pages.whatIsYourDateOfBirthPage.assertPageIsDisplayed()
    pages.whatIsYourDateOfBirthPage.enterMonth("1")
    pages.whatIsYourDateOfBirthPage.enterYear("2000")
    pages.whatIsYourDateOfBirthPage.clickSubmit()
    pages.whatIsYourDateOfBirthPage.assertPageShowsErrorDayMissing()
  }

  "Submitting form without month shows error message" in {
    pages.whatIsYourDateOfBirthPage.open()
    pages.whatIsYourDateOfBirthPage.assertPageIsDisplayed()
    pages.whatIsYourDateOfBirthPage.enterDayOfMonth("1")
    pages.whatIsYourDateOfBirthPage.enterYear("2000")
    pages.whatIsYourDateOfBirthPage.clickSubmit()
    pages.whatIsYourDateOfBirthPage.assertPageShowsErrorMonthMissing()
  }

  "Submitting form without year shows error message" in {
    pages.whatIsYourDateOfBirthPage.open()
    pages.whatIsYourDateOfBirthPage.assertPageIsDisplayed()
    pages.whatIsYourDateOfBirthPage.enterDayOfMonth("1")
    pages.whatIsYourDateOfBirthPage.enterMonth("1")
    pages.whatIsYourDateOfBirthPage.clickSubmit()
    pages.whatIsYourDateOfBirthPage.assertPageShowsErrorYearMissing()
  }

  "Submitting form where month is greater than 12" in {
    pages.whatIsYourDateOfBirthPage.open()
    pages.whatIsYourDateOfBirthPage.assertPageIsDisplayed()
    pages.whatIsYourDateOfBirthPage.enterDayOfMonth("1")
    pages.whatIsYourDateOfBirthPage.enterMonth("13")
    pages.whatIsYourDateOfBirthPage.enterYear("2000")
    pages.whatIsYourDateOfBirthPage.clickSubmit()
    pages.whatIsYourDateOfBirthPage.assertPageShowsErrorMonthTooLarge()
  }

  "Submitting form where month is greater than day is greater than 31" in {
    pages.whatIsYourDateOfBirthPage.open()
    pages.whatIsYourDateOfBirthPage.assertPageIsDisplayed()
    pages.whatIsYourDateOfBirthPage.enterDayOfMonth("32")
    pages.whatIsYourDateOfBirthPage.enterMonth("1")
    pages.whatIsYourDateOfBirthPage.enterYear("2000")
    pages.whatIsYourDateOfBirthPage.clickSubmit()
    pages.whatIsYourDateOfBirthPage.assertPageShowsErrorMonthTooLarge()
  }

  Seq("20oo", "94", "20001").foreach { yearInput: String =>
    s"Submitting form incorrect year format: [ $yearInput ]" in {
      pages.whatIsYourDateOfBirthPage.open()
      pages.whatIsYourDateOfBirthPage.assertPageIsDisplayed()
      pages.whatIsYourDateOfBirthPage.enterDayOfMonth("1")
      pages.whatIsYourDateOfBirthPage.enterMonth("1")
      pages.whatIsYourDateOfBirthPage.enterYear(yearInput)
      pages.whatIsYourDateOfBirthPage.clickSubmit()
      pages.whatIsYourDateOfBirthPage.assertPageShowsErrorYearLength()
    }
  }

  "Submitting form date in the future" in {
    val dateInFuture: LocalDateTime = LocalDateTime.now(clock).plusDays(1L)
    pages.whatIsYourDateOfBirthPage.open()
    pages.whatIsYourDateOfBirthPage.assertPageIsDisplayed()
    pages.whatIsYourDateOfBirthPage.enterDayOfMonth(dateInFuture.getDayOfMonth.toString)
    pages.whatIsYourDateOfBirthPage.enterMonth(dateInFuture.getMonthValue.toString)
    pages.whatIsYourDateOfBirthPage.enterYear(dateInFuture.getYear.toString)
    pages.whatIsYourDateOfBirthPage.clickSubmit()
    pages.whatIsYourDateOfBirthPage.assertPageShowsErrorDateInTheFuture()
  }

  "Submitting a DOB that is below the minimum allowed age of 16 years (e.g. 15 years and 364 days and below)" in {
    val `not quite 16 years ago` = LocalDate.now().minusYears(16L).plusDays(1L)
    // i.e. format
    val `16 years ago expected date message` = LocalDate.now().minusYears(16L).format(tdAll.gdsDateTimeFormatter)
    pages.whatIsYourDateOfBirthPage.open()
    pages.whatIsYourDateOfBirthPage.assertPageIsDisplayed()
    pages.whatIsYourDateOfBirthPage.enterDayOfMonth(`not quite 16 years ago`.getDayOfMonth.toString)
    pages.whatIsYourDateOfBirthPage.enterMonth(`not quite 16 years ago`.getMonthValue.toString)
    pages.whatIsYourDateOfBirthPage.enterYear(`not quite 16 years ago`.getYear.toString)
    pages.whatIsYourDateOfBirthPage.clickSubmit()
    pages.whatIsYourDateOfBirthPage.assertPageShowsErrorDateTooYoung(`16 years ago expected date message`)
  }

  "Submitting a DOB that is above the maximum allowed age of 110 years" in {
    val `110 Years and one day ago` = LocalDate.now().minusYears(110L).minusDays(1L)
    val `110 years ago expected date message` = LocalDate.now().minusYears(110L).format(tdAll.gdsDateTimeFormatter)
    pages.whatIsYourDateOfBirthPage.open()
    pages.whatIsYourDateOfBirthPage.assertPageIsDisplayed()
    pages.whatIsYourDateOfBirthPage.enterDayOfMonth(`110 Years and one day ago`.getDayOfMonth.toString)
    pages.whatIsYourDateOfBirthPage.enterMonth(`110 Years and one day ago`.getMonthValue.toString)
    pages.whatIsYourDateOfBirthPage.enterYear(`110 Years and one day ago`.getYear.toString)
    pages.whatIsYourDateOfBirthPage.clickSubmit()
    pages.whatIsYourDateOfBirthPage.assertPageShowsErrorDateTooOld(`110 years ago expected date message`)
  }

}
