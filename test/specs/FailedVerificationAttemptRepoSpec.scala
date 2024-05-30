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

import models.attemptmodels.AttemptInfo
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpecLike
import org.scalatest.matchers.should.Matchers
import repository.FailedVerificationAttemptRepo
import testsupport.ItSpec
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository
import uk.gov.hmrc.mongo.test.DefaultPlayMongoRepositorySupport

import java.util.concurrent.TimeUnit

class FailedVerificationAttemptRepoSpec extends AnyFreeSpecLike
  with DefaultPlayMongoRepositorySupport[AttemptInfo]
  with ItSpec
  with Matchers
  with ScalaFutures {

  override def beforeEach(): Unit = {
    upsertFailedAttemptToDatabase(tdAll.attemptInfo(1))
  }

  override def configMap: Map[String, Any] = Map("mongodb.uri" -> mongoUri)

  override protected val repository: PlayMongoRepository[AttemptInfo] = app.injector.instanceOf[FailedVerificationAttemptRepo]

  "FailedVerificationAttemptRepo" - {
    "should have a indexes for lastUpdated with ttl of 24 hours and also an index for ipAddress" in {
      repository.indexes.size shouldBe 2
      val indexOptions = repository.indexes.map(_.getOptions)
      indexOptions.find(_.getName === "lastUpdatedIdx").map(_.getExpireAfter(TimeUnit.HOURS)) shouldBe Some(24) withClue "TTL should be 24 hours"
      indexOptions.exists(_.getName === "ipAddressIdx") shouldBe true
      indexOptions.find(_.getName === "ipAddressIdx").map(_.isUnique) shouldBe Some(true) withClue "ipAddressIdx should be unique"

      val fieldNames = tdAll.attemptInfo(1).productElementNames.toList
      fieldNames.exists(_ === "ipAddress") shouldBe true withClue "ipAddress fieldName must exist in AttemptInfo, be careful if you're refactoring it..."
    }
  }

}
