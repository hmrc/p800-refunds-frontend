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

package specs

import models.journeymodels.{Journey, Stage}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import services.JourneyService
import testsupport.ItSpec
import testsupport.testdata.TestData
import uk.gov.hmrc.http.{HeaderCarrier, SessionId, SessionKeys}

import java.util.UUID

class JourneyServiceSpec extends ItSpec {

  lazy val journeyService = app.injector.instanceOf[JourneyService]

  def makeHeaderCarrier(sessionId: SessionId): HeaderCarrier =
    HeaderCarrier(sessionId     = Some(sessionId), authorization = Some(TestData.authorization))

  val sessionId: SessionId = SessionId(s"session-${UUID.randomUUID().toString}")
  implicit val hc: HeaderCarrier = makeHeaderCarrier(sessionId)
  implicit val request: FakeRequest[AnyContentAsEmpty.type] = TestData.request.withSession(SessionKeys.sessionId -> sessionId.value)

  "JourneyService.newJourney should create a new journey" in {
    val createdJourney: Journey = journeyService.newJourney().futureValue
    createdJourney.stage shouldBe Stage.AfterStarted.Started
    createdJourney.sessionId should not be (sessionId)

  }

}
