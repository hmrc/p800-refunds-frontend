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

import models.journeymodels.Journey
import play.api.libs.json.Json
import testsupport.ItSpec

class StartEndpointPageSpec extends ItSpec {

  "navigating to /start redirects to /do-you-want-to-sign-in" in {
    pages.startEndpoint.open()
    pages.doYouWantToSignInPage.assertPageIsDisplayed()
    goToViaPath("/get-an-income-tax-refund/test-only/show-journey")
    val journey = Json.parse(webDriver.getPageSource).as[Journey]
    // relaxed assertion, it has new journeyId when this endpoint is called - we mainly care that it's started
    val expectedJourney = tdAll.journeyStarted.copy(_id           = journey.id, createdAt = journey.createdAt, correlationId = journey.correlationId)
    getJourneyFromDatabase(journey._id) shouldBeLike expectedJourney
  }
}
