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
import org.apache.pekko.http.scaladsl.model.Uri
import connectors.{EcospendAuthServerConnector, EcospendConnector}
import models.ecospend.BankDescription
import models.ecospend.account.BankAccountSummary
import models.ecospend.consent.{BankConsentRequest, BankConsentResponse, ConsentPermission, ConsentReferrerChannel, ConsentCreationReason}
import models.ecospend.verification.{BankVerification, BankVerificationRequest}
import models.journeymodels.Journey
import util.Errors

import java.time.LocalDateTime
import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}

@Singleton()
class EcospendService @Inject() (
    ecospendConnector:           EcospendConnector,
    ecospendAuthServerConnector: EcospendAuthServerConnector
)(implicit ec: ExecutionContext) {

  def getBanks(implicit request: JourneyRequest[_]): Future[List[BankDescription]] = for {
    accessToken <- ecospendAuthServerConnector.accessToken
    getBanksResponse <- ecospendConnector.getListOfAvailableBanks(accessToken)

    banks = getBanksResponse.data.filter(_.serviceStatus).map(_.toFrontendBankDescription)
  } yield banks

  //TODO: remove it and call backend to get the validation result from webhook
  def validate(journey: Journey)(implicit request: JourneyRequest[_]): Future[BankVerification] = for {
    accessToken <- ecospendAuthServerConnector.accessToken
    bankVerificationResponse <- ecospendConnector.validate(accessToken, BankVerificationRequest(journey.getNino.value))
  } yield bankVerificationResponse

  def createConsent(journey: Journey)(implicit request: JourneyRequest[_]): Future[BankConsentResponse] = for {
    accessToken <- ecospendAuthServerConnector.accessToken
    bankConsentResponse <- ecospendConnector.createConsent(accessToken, bankConsentRequestFromJourney(journey))
  } yield bankConsentResponse

  private def bankConsentRequestFromJourney(journey: Journey)(implicit request: JourneyRequest[_]): BankConsentRequest =
    BankConsentRequest(
      bankId           = journey.getBankDescription.bankId,
      redirectUrl      = Uri(controllers.routes.WeAreVerifyingYourBankAccountController.get(None, None, None).absoluteURL()),
      merchantId       = None,
      merchantUserId   = None,
      consentEndDate   = LocalDateTime.now().plusSeconds(30.minutes.toSeconds),
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

  def getAccountSummary(journey: Journey)(implicit request: JourneyRequest[_]): Future[BankAccountSummary] =
    journey.bankAccountSummary match {
      case None =>
        for {
          accessToken <- ecospendAuthServerConnector.accessToken
          consentId = journey.getBankConsent.id
          bankAccountSummaryResponse <- ecospendConnector.getAccountSummary(accessToken, consentId)
        } yield bankAccountSummaryResponse.value.headOption.getOrElse(Errors.throwServerErrorException("Failed to get BankAccountSummary from BankAccountSummaryResponse"))
      case Some(bankAccountSummary) =>
        Future.successful(bankAccountSummary)
    }

}
