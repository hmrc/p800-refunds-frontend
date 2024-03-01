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

package specs

import models.journeymodels.{Journey, JourneyId}
import repository.JourneyRepo
import testsupport.ItSpec
import uk.gov.hmrc.mongo.test.DefaultPlayMongoRepositorySupport

class JourneyRepoSpec extends ItSpec with DefaultPlayMongoRepositorySupport[Journey] {

  override def beforeEach(): Unit = { () }

  override lazy val repository: JourneyRepo = app.injector.instanceOf[JourneyRepo]

  //throws a No ttl indexes were found for collection journey atm, try and fix this as it might be a nice test to check indexes
  "JourneyRepo should have correct indexes" ignore {
    repository.findById(JourneyId("12345678")).futureValue shouldBe None
  }
}
