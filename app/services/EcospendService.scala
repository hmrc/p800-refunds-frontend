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
import connectors.{EcospendAuthServerConnector, EcospendConnector}
import models.ecospend.BankDescription
import models.ecospend.verification.{BankVerification, BankVerificationRequest}
import models.journeymodels.Journey

import javax.inject.{Inject, Singleton}
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
    getBanksResponse <- ecospendConnector.validate(accessToken, BankVerificationRequest(journey.getNationalInsuranceNumber.value))
  } yield getBanksResponse

}
