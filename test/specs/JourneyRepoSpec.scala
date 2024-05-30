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

import models.journeymodels.Journey
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpecLike
import org.scalatest.matchers.should.Matchers
import repository.JourneyRepo
import testsupport.ItSpec
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository
import uk.gov.hmrc.mongo.test.DefaultPlayMongoRepositorySupport

import java.util.concurrent.TimeUnit

class JourneyRepoSpec extends AnyFreeSpecLike
  with DefaultPlayMongoRepositorySupport[Journey]
  with ItSpec
  with Matchers
  with ScalaFutures {

  override def configMap: Map[String, Any] = Map("mongodb.uri" -> mongoUri)

  override protected val repository: PlayMongoRepository[Journey] = app.injector.instanceOf[JourneyRepo]

  "JourneyRepo should have a index for lastUpdated with ttl of 30 minutes" in {
    repository.indexes.size shouldBe 1
    val indexOptions = repository.indexes.map(_.getOptions)
    indexOptions.find(_.getName === "lastUpdatedIdx").map(_.getExpireAfter(TimeUnit.MINUTES)) shouldBe Some(30)
  }

}
