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

package controllers

import action.{Actions, JourneyRequest}
import models.ecospend.{BankDescription, BankId, BanksSelectOptions}
import models.forms.WhatIsTheNameOfYourBankAccountForm
import models.journeymodels._
import play.api.data.Form
import play.api.mvc._
import requests.RequestSupport
import services.{EcospendService, JourneyService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import util.SafeEquals._
import util.{Errors, JourneyLogger}
import views.Views

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EnterTheNameOfYourBankController @Inject() (
    mcc:             MessagesControllerComponents,
    requestSupport:  RequestSupport,
    journeyService:  JourneyService,
    ecospendService: EcospendService,
    views:           Views,
    actions:         Actions
)(implicit ec: ExecutionContext) extends FrontendController(mcc) {

  import requestSupport._

  def get: Action[AnyContent] = actions.journeyInProgress.async { implicit request: JourneyRequest[_] =>
    val journey: Journey = request.journey
    Errors.require(request.journey.getJourneyType === JourneyType.BankTransfer, "This endpoint supports only BankTransfer journey")

    val form: Form[BankId] = journey
      .bankDescription
      .map(_.bankId)
      .fold(WhatIsTheNameOfYourBankAccountForm.form)(WhatIsTheNameOfYourBankAccountForm.form.fill)

    getBankSelectOptions.map { banks =>
      Ok(views.enterTheNameOfYourBankPage(banks, form))
    }
  }

  def post: Action[AnyContent] = actions.journeyInProgress.async { implicit request: JourneyRequest[_] =>
    val journey: Journey = request.journey
    Errors.require(request.journey.getJourneyType === JourneyType.BankTransfer, "This endpoint supports only BankTransfer journey")

    WhatIsTheNameOfYourBankAccountForm
      .form
      .bindFromRequest()
      .fold(
        formWithErrors => {
          getBankSelectOptions.map { banks =>
            BadRequest(views.enterTheNameOfYourBankPage(banks, formWithErrors))
          }
        },
        bankId => {
          for {
            bankDescription <- bankDescriptionForBankId(bankId)
            newJourney = journey.update(
              bankDescription = bankDescription
            )
            _ <- journeyService.upsert(newJourney)
          } yield Redirect(controllers.routes.GiveYourPermissionController.get)
        }
      )

  }

  private def bankDescriptionForBankId(bankId: BankId)(implicit request: JourneyRequest[_]): Future[BankDescription] =
    ecospendService.getBanks.map { banks =>
      if (banks.isEmpty) JourneyLogger.error("Notification critical : Bank list from Ecospend is empty")

      val xs: List[BankDescription] = banks.filter(_.bankId === bankId)
      if (xs.size === 1) xs.headOption.getOrElse(Errors.throwServerErrorException("Unable to get BankDescription for given BankId"))
      else Errors.throwServerErrorException("Unable to find BankDescription for given BankId")
    }

  private def getBankSelectOptions(implicit request: JourneyRequest[_]): Future[Seq[BanksSelectOptions]] =
    ecospendService.getBanks.map { banks =>
      if (banks.isEmpty) JourneyLogger.error("Notification critical : Bank list from Ecospend is empty")
      BanksSelectOptions.noBankOption :: banks.map(b => BanksSelectOptions(b.bankId, b.name)).sorted
    }
}

