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

import models.ecospend.EcospendAccessToken
import play.api.libs.json.{JsSuccess, Json, Reads}
import testdata.TdAll
import testsupport.UnitSpec

import java.time.{Clock, Instant, ZoneId, LocalDateTime}

class EcospendAccessTokenSpec extends UnitSpec {
  lazy val tdAll: TdAll = TdAll()
  val frozenInstant: Instant = tdAll.instant
  implicit val clock: Clock = Clock.fixed(frozenInstant, ZoneId.of("UTC"))

  "Deserialise from Ecospend example JSON" in {
    val testJson = Json.parse(
      """
      |{
      |  "access_token": "1234567890",
      |  "expires_in": 300,
      |  "token_type": "client_credentials",
      |  "scope": "px01.ecospend.pis.sandbox"
      |}
      """.stripMargin
    )

    val expectedAccessToken =
      EcospendAccessToken(
        token  = "1234567890",
        expiry = LocalDateTime.ofInstant(frozenInstant, ZoneId.of("UTC")).plusSeconds(300)
      )

    implicitly[Reads[EcospendAccessToken]].reads(testJson) shouldBe JsSuccess(expectedAccessToken)
  }

}
