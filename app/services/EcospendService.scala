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

package services

import action.JourneyRequest
import config.AppConfig
import connectors.{EcospendAuthServerConnector, EcospendConnector}
import models.ecospend.BankDescription
import models.ecospend.account.{BankAccountFormat, BankAccountSummary}
import models.ecospend.consent._
import models.journeymodels.Journey
import org.apache.pekko.http.scaladsl.model.Uri
import play.api.mvc.RequestHeader
import util.SafeEquals.EqualsOps
import util.{Errors, JourneyLogger}

import java.time.LocalDateTime
import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}

@Singleton()
class EcospendService @Inject() (
    appConfig:                   AppConfig,
    ecospendConnector:           EcospendConnector,
    ecospendAuthServerConnector: EcospendAuthServerConnector
)(implicit ec: ExecutionContext) {

  def getBanks(implicit request: JourneyRequest[_]): Future[List[BankDescription]] = for {
    accessToken <- ecospendAuthServerConnector.accessToken
    getBanksResponse <- ecospendConnector.getListOfAvailableBanks(accessToken)

    banks = getBanksResponse.data.filter(_.serviceStatus).map(_.toFrontendBankDescription)
  } yield banks

  def createConsent(journey: Journey)(implicit request: JourneyRequest[_]): Future[BankConsentResponse] = for {
    accessToken <- ecospendAuthServerConnector.accessToken
    bankConsentResponse <- ecospendConnector.createConsent(accessToken, bankConsentRequestFromJourney(journey))
  } yield bankConsentResponse

  private def bankConsentRequestFromJourney(journey: Journey)(implicit request: JourneyRequest[_]): BankConsentRequest = {
    val redirectUrl: Uri = Uri(appConfig.platformFrontendHost +
      controllers.routes.VerifyingYourBankAccountController.get(None, None, None).toString)

    JourneyLogger.info(s"Creating consent with [redirectUrl: ${redirectUrl.toString}]")

    BankConsentRequest(
      bankId           = journey.getBankDescription.bankId,
      redirectUrl      = redirectUrl,
      merchantId       = None,
      merchantUserId   = None,
      consentEndDate   = LocalDateTime.now().plusSeconds(48.hours.toSeconds),
      permissions      = List(
        ConsentPermission.Account,
        ConsentPermission.Balance,
        ConsentPermission.Transactions,
        ConsentPermission.DirectDebits,
        ConsentPermission.StandingOrders,
        ConsentPermission.Parties
      ),
      referrerChannel  = ConsentReferrerChannel.Web,
      additionalParams = None,
      creationReason   = ConsentCreationReason.Algorithm
    )
  }

  def getAccountSummary(journey: Journey)(implicit r: RequestHeader): Future[BankAccountSummary] =
    for {
      accessToken <- ecospendAuthServerConnector.accessToken
      consentId = journey.getBankConsent.id
      bankAccountSummaryResponse <- ecospendConnector.getAccountSummary(accessToken, consentId)
      summary = bankAccountSummaryResponse.value.headOption.getOrElse(Errors.throwServerErrorException("Failed to get BankAccountSummary from BankAccountSummaryResponse"))
      _ = Errors.require(bankAccountSummaryResponse.value.size === 1, s"More then 1 accounts in bankAccountSummaryResponse: [${bankAccountSummaryResponse.value.size.toString}]")
      _ = Errors.require(summary.accountFormat === BankAccountFormat.SortCode, s"Unexpected AccountFormat: [${summary.accountFormat.toString}]")
    } yield summary
}
