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

package testdata

import play.api.test.FakeRequest
import uk.gov.hmrc.http.{HeaderNames, SessionKeys}

import java.time.LocalDate

object TdSupport {

  implicit class FakeRequestOps[T](r: FakeRequest[T]) {

    def withAuthToken(authToken: String = TdAll.tdAll.authToken): FakeRequest[T] = r.withSession((SessionKeys.authToken, authToken))

    def withAkamaiReputationHeader(
        akamaiReputationValue: String = TdAll.tdAll.akamaiReputationValue
    ): FakeRequest[T] = r.withHeaders(
      HeaderNames.akamaiReputation -> akamaiReputationValue
    )

    def withRequestId(requestId: String = TdAll.tdAll.requestId): FakeRequest[T] = r.withHeaders(
      HeaderNames.xRequestId -> requestId
    )

    def withTrueClientIp(ip: String = TdAll.tdAll.trueClientIpString): FakeRequest[T] = r.withHeaders(
      HeaderNames.trueClientIp -> ip
    )

    def withTrueClientPort(port: String = TdAll.tdAll.trueClientPort): FakeRequest[T] = r.withHeaders(
      HeaderNames.trueClientPort -> port
    )

    def withDeviceId(deviceId: String = TdAll.tdAll.deviceIdInRequest): FakeRequest[T] = r.withHeaders(
      HeaderNames.deviceID -> deviceId
    )
  }

  implicit def toSome[T](t: T): Option[T] = Some(t)
  implicit def toLocalDate(s: String): LocalDate = LocalDate.parse(s)
  implicit def toOptionLocalDate(s: String): Option[LocalDate] = Some(LocalDate.parse(s))
}
