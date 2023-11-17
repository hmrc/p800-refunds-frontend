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

import testdata.TdAll
import testsupport.ItSpec

import java.time.LocalDateTime

class WhatIsYourDateOfBirthPageSpec extends ItSpec {

  override def beforeEach(): Unit = {
    super.beforeEach()
    addJourneyIdToSession(TdAll.journeyId)
    upsertJourneyToDatabase(TdAll.journeyDoYouWantYourRefundViaBankTransferYes)
  }

  "/what-is-your-date-of-birth renders the what is your date of birth page" in {
    pages.whatIsYourDateOfBirthPage.open()
    pages.whatIsYourDateOfBirthPage.assertPageIsDisplayed()
  }

  "Submitting form with valid data redirects user to 'What is your national insurance number' page" in {
    pages.whatIsYourDateOfBirthPage.open()
    pages.whatIsYourDateOfBirthPage.assertPageIsDisplayed()
    pages.whatIsYourDateOfBirthPage.enterDayOfMonth("")
    pages.whatIsYourDateOfBirthPage.enterMonth("")
    pages.whatIsYourDateOfBirthPage.enterYear("")
    pages.whatIsYourDateOfBirthPage.clickSubmit()
    pages.whatIsYourNationalInsuranceNumberPage.assertPageIsDisplayed()
  }

  "Clicking 'Back' sends user to 'What is your name' page" in {
    pages.whatIsYourDateOfBirthPage.open()
    pages.whatIsYourDateOfBirthPage.assertPageIsDisplayed()
    pages.whatIsYourDateOfBirthPage.clickBackButton()
    pages.whatIsYourFullNamePage.assertPageIsDisplayed()
  }

  "Prepopulate the form if the user has already entered it" ignore {
    //todo work out how to prepopulate the form in template
    upsertJourneyToDatabase(TdAll.journeyWhatIsYourDateOfBirth)
    pages.whatIsYourDateOfBirthPage.open()
    pages.whatIsYourDateOfBirthPage.assertPageIsDisplayed()
    //todo add method to assert fields prepopulated
  }

  "Submitting form without entering anything shows error message" in {
    pages.whatIsYourDateOfBirthPage.open()
    pages.whatIsYourDateOfBirthPage.assertPageIsDisplayed()
    pages.whatIsYourDateOfBirthPage.clickSubmit()
    //todo assert errors shown
    //    Date of birth must include a day, month and year
  }

  "Submitting form without day of month shows error message" in {
    pages.whatIsYourDateOfBirthPage.open()
    pages.whatIsYourDateOfBirthPage.assertPageIsDisplayed()
    pages.whatIsYourDateOfBirthPage.clickSubmit()
    //todo assert errors shown
    //    Date of birth must include a day
  }

  "Submitting form without month shows error message" in {
    pages.whatIsYourDateOfBirthPage.open()
    pages.whatIsYourDateOfBirthPage.assertPageIsDisplayed()
    pages.whatIsYourDateOfBirthPage.clickSubmit()
    //todo assert errors shown
    //    Date of birth must include a month
  }

  "Submitting form without year shows error message" in {
    pages.whatIsYourDateOfBirthPage.open()
    pages.whatIsYourDateOfBirthPage.assertPageIsDisplayed()
    pages.whatIsYourDateOfBirthPage.clickSubmit()
    //todo assert errors shown
    //    Date of birth must include a year
  }

  "Submitting form where month is greater than 12" in {
    pages.whatIsYourDateOfBirthPage.open()
    pages.whatIsYourDateOfBirthPage.assertPageIsDisplayed()
    pages.whatIsYourDateOfBirthPage.enterDayOfMonth("1")
    pages.whatIsYourDateOfBirthPage.enterMonth("13")
    pages.whatIsYourDateOfBirthPage.enterYear("2000")
    pages.whatIsYourDateOfBirthPage.clickSubmit()
    //todo assert errors shown
    //You must enter a real date
  }

  "Submitting form where month is greater than day is greater than 31" in {
    pages.whatIsYourDateOfBirthPage.open()
    pages.whatIsYourDateOfBirthPage.assertPageIsDisplayed()
    pages.whatIsYourDateOfBirthPage.enterDayOfMonth("32")
    pages.whatIsYourDateOfBirthPage.enterMonth("1")
    pages.whatIsYourDateOfBirthPage.enterYear("2000")
    pages.whatIsYourDateOfBirthPage.clickSubmit()
    //todo assert errors shown
    //You must enter a real date
  }

  "Submitting form incorrect year format" in {
    pages.whatIsYourDateOfBirthPage.open()
    pages.whatIsYourDateOfBirthPage.assertPageIsDisplayed()
    pages.whatIsYourDateOfBirthPage.enterDayOfMonth("1")
    pages.whatIsYourDateOfBirthPage.enterMonth("1")
    pages.whatIsYourDateOfBirthPage.enterYear("20oo")
    pages.whatIsYourDateOfBirthPage.clickSubmit()
    //todo assert errors shown
    // Enter a year which contains 4 numbers
  }

  "Submitting form date in the future" in {
    val dateInFuture: LocalDateTime = LocalDateTime.now().plusMonths(1L)
    pages.whatIsYourDateOfBirthPage.open()
    pages.whatIsYourDateOfBirthPage.assertPageIsDisplayed()
    pages.whatIsYourDateOfBirthPage.enterDayOfMonth(dateInFuture.getDayOfMonth.toString)
    pages.whatIsYourDateOfBirthPage.enterMonth(dateInFuture.getMonthValue.toString)
    pages.whatIsYourDateOfBirthPage.enterYear(dateInFuture.getYear.toString)
    pages.whatIsYourDateOfBirthPage.clickSubmit()
    //todo assert errors shown
    // Date of birth must be in the past
  }

  "Submitting a DOB that is below the minimum allowed age of 16 years (e.g. 15 years and 364 days and below)" in {
    val `16YearsAgo` = LocalDateTime.now.minusYears(16L) //todo check if I need to minus a day also
    pages.whatIsYourDateOfBirthPage.open()
    pages.whatIsYourDateOfBirthPage.assertPageIsDisplayed()
    pages.whatIsYourDateOfBirthPage.enterDayOfMonth(`16YearsAgo`.getDayOfMonth.toString)
    pages.whatIsYourDateOfBirthPage.enterMonth(`16YearsAgo`.getMonthValue.toString)
    pages.whatIsYourDateOfBirthPage.enterYear(`16YearsAgo`.getYear.toString)
    pages.whatIsYourDateOfBirthPage.clickSubmit()
    //todo assert errors shown
    // Date of birth must be on or before [date]
  }

  "Submitting a DOB that is above the maximum allowed age of 110 years" in {
    val `110YearsAgo` = LocalDateTime.now.minusYears(110L) //todo check if I need to minus a day also
    pages.whatIsYourDateOfBirthPage.open()
    pages.whatIsYourDateOfBirthPage.assertPageIsDisplayed()
    pages.whatIsYourDateOfBirthPage.enterDayOfMonth(`110YearsAgo`.getDayOfMonth.toString)
    pages.whatIsYourDateOfBirthPage.enterMonth(`110YearsAgo`.getMonthValue.toString)
    pages.whatIsYourDateOfBirthPage.enterYear(`110YearsAgo`.getYear.toString)
    pages.whatIsYourDateOfBirthPage.clickSubmit()
    //todo assert errors shown
    // Date of birth must be on or after [date]
  }

}
