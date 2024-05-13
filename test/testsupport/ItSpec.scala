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

package testsupport

import com.google.inject.AbstractModule
import crypto.JourneyCrypto
import edh.{ClaimId, ClaimIdGenerator}
import models.attemptmodels.{AttemptInfo, IpAddress}
import models.journeymodels.{Journey, JourneyId}
import org.openqa.selenium.WebDriver
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.scalatest.freespec.AnyFreeSpecLike
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.{Assertion, BeforeAndAfterEach}
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import pagespecs.pagesupport.Pages
import play.api.inject.guice.{GuiceApplicationBuilder, GuiceableModule}
import play.api.test.{DefaultTestServerFactory, RunningServer}
import play.api.{Application, Mode}
import play.core.server.ServerConfig
import repository.{FailedVerificationAttemptRepo, JourneyRepo}
import testdata.TdAll
import testsupport.wiremock.WireMockSupport

import java.time.{Clock, Instant, ZoneId}

trait ItSpec extends AnyFreeSpecLike
  with RichMatchers
  with BeforeAndAfterEach
  with GuiceOneServerPerSuite
  with WireMockSupport
  with Matchers {

  override implicit val patienceConfig: PatienceConfig = PatienceConfig(timeout  = scaled(Span(3, Seconds)), interval = scaled(Span(300, Millis)))
  private val testServerPort = ItSpec.testServerPort
  private val baseUrl: String = s"http://localhost:${testServerPort.toString}"
  private val databaseName: String = "p800-refunds-frontend-it"
  lazy val webdriverUrl: String = s"http://localhost:${port.toString}"

  lazy val tdAll: TdAll = TdAll()
  lazy val frozenInstant: Instant = tdAll.instant
  lazy val clock: Clock = Clock.fixed(frozenInstant, ZoneId.of("UTC"))

  protected implicit val webDriver: WebDriver = new HtmlUnitDriver()

  protected lazy val configMap: Map[String, Any] = Map[String, Any](
    "mongodb.uri" -> s"mongodb://localhost:27017/$databaseName",
    "play.http.router" -> "testOnlyDoNotUseInAppConf.Routes",
    "auditing.consumer.baseUri.port" -> WireMockSupport.port,
    "auditing.enabled" -> true,
    "auditing.traceRequests" -> false,
    "urls.gov-uk.govuk-route-in" -> s"http://localhost:${testServerPort.toString}/get-an-income-tax-refund/test-only/gov-uk-route-in",
    "urls.pta-sign-in" -> s"http://localhost:${testServerPort.toString}/get-an-income-tax-refund/test-only/pta-sign-in",
    "urls.gov-uk.income-tax-general-enquiries" -> s"http://localhost:${testServerPort.toString}/get-an-income-tax-refund/test-only/income-tax-general-enquiries",
    "urls.feedback-frontend" -> s"http://localhost:${testServerPort.toString}/get-an-income-tax-refund/test-only",
    "microservice.services.p800-reference-validation.port" -> WireMockSupport.port,
    "microservice.services.ecospend-auth.port" -> WireMockSupport.port,
    "microservice.services.ecospend.port" -> WireMockSupport.port,
    "microservice.services.p800-refunds-external-api.port" -> WireMockSupport.port,
    "microservice.services.p800-refunds-backend.port" -> WireMockSupport.port,
    "microservice.services.date-calculator.port" -> WireMockSupport.port
  )

  lazy val overridingsModule: AbstractModule = new AbstractModule {
    override def configure(): Unit = {
      bind(classOf[Clock]).toInstance(clock)
      bind(classOf[ClaimIdGenerator]).toInstance(new ClaimIdGenerator {
        override def nextClaimId(): ClaimId = tdAll.claimId
      })
    }
  }

  override def fakeApplication(): Application = new GuiceApplicationBuilder()
    .overrides(GuiceableModule.fromGuiceModules(Seq(overridingsModule)))
    .configure(configMap).build()

  override implicit protected lazy val runningServer: RunningServer =
    TestServerFactory.start(app)

  object TestServerFactory extends DefaultTestServerFactory {
    override protected def serverConfig(app: Application): ServerConfig = {
      val sc = ServerConfig(port    = Some(testServerPort), sslPort = Some(0), mode = Mode.Test, rootDir = app.path)
      sc.copy(configuration = sc.configuration.withFallback(overrideServerConfiguration(app)))
    }
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    webDriver.manage().deleteAllCookies()
    clearDownFailedAttemptDatabase() // we need to clear this as IPAddress stays the same between tests.
  }

  def goToViaPath(path: String): Unit = webDriver.get(s"$webdriverUrl$path")

  def addJourneyIdToSession(journeyId: JourneyId): Unit = {
    val url = s"""/get-an-income-tax-refund/test-only/add-journey-id-to-session/${journeyId.value}"""
    goToViaPath(url)
    webDriver.getCurrentUrl should endWith(url) withClue "assert the endpoint worked fine"
    webDriver.getPageSource should include(s"${journeyId.value} added to session") withClue "assert the endpoint worked fine"
    ()
  }

  def upsertJourneyToDatabase(journey: Journey): Unit = {
    val journeyRepo: JourneyRepo = app.injector.instanceOf[JourneyRepo]
    val encryptedJourney: Journey = app.injector.instanceOf[JourneyCrypto].encryptJourney(journey)
    journeyRepo.upsert(encryptedJourney).futureValue
  }

  def getJourneyFromDatabase(journeyId: JourneyId): Journey = {
    val journeyRepo: JourneyRepo = app.injector.instanceOf[JourneyRepo]
    val journey = journeyRepo.findById(journeyId).futureValue.value
    app.injector.instanceOf[JourneyCrypto].decryptJourney(journey)
  }

  def upsertFailedAttemptToDatabase(attemptInfo: AttemptInfo): Unit = {
    val attemptRepo: FailedVerificationAttemptRepo = app.injector.instanceOf[FailedVerificationAttemptRepo]
    attemptRepo.upsert(attemptInfo).futureValue
  }

  def getFailedAttemptCount(ipAddress: IpAddress = IpAddress("127.0.0.1")): Option[Int] = {
    val attemptRepo: FailedVerificationAttemptRepo = app.injector.instanceOf[FailedVerificationAttemptRepo]
    attemptRepo.findByIpAddress(ipAddress).futureValue.map(_.numberOfFailedAttempts.value)
  }

  def clearDownFailedAttemptDatabase(): Unit = {
    val attemptRepo: FailedVerificationAttemptRepo = app.injector.instanceOf[FailedVerificationAttemptRepo]
    attemptRepo.drop().futureValue
  }

  lazy val pages = new Pages(baseUrl)

  implicit class JourneyShouldBeOps(j: Journey) {
    /**
     * Asserts the journey matches expected Journey ignoring `createdAt`
     */
    def shouldBeLike(expected: Journey): Assertion = {
      j.copy(createdAt = expected.createdAt) shouldBe expected
    }
  }

}

object ItSpec {
  val testServerPort = 19001
}
