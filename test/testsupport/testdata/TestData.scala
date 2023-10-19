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

package testsupport.testdata

import models.journeymodels.{JourneyId, SessionId}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import testsupport.TdRequest.FakeRequestOps
import uk.gov.hmrc.http.Authorization

import java.time.{Instant, LocalDateTime, ZoneOffset}
import java.time.format.DateTimeFormatter

object TestData {
  lazy val dateString: String = "2059-11-25"
  lazy val timeString: String = s"${dateString}T16:33:51.880"
  lazy val localDateTime: LocalDateTime = {
    //the frozen time has to be in future otherwise the journeys will disappear from mongodb because of expiry index
    LocalDateTime.parse(timeString, DateTimeFormatter.ISO_DATE_TIME)
  }
  lazy val instant: Instant = localDateTime.toInstant(ZoneOffset.UTC)

  def journeyId: JourneyId = JourneyId("b6217497-ab5b-4e93-855a-afc9f9e933b6")
  def sessionId: SessionId = SessionId("session-2082fcd4-70f6-49cc-a4bf-845917981cd7")
  def authorization: Authorization = Authorization("Bearer xyz")

  def request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()
    .withSessionId()
    .withAuthToken()
    .withAkamaiReputationHeader()
    .withRequestId()
    .withTrueClientIp()
    .withTrueClientPort()
    .withDeviceId()
}
