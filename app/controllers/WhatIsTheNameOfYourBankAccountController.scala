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
import io.scalaland.chimney.dsl._
import models.ecospend.{BankId, BankDescription}
import models.ecospend.BanksSelectOptions
import models.forms.WhatIsTheNameOfYourBankAccountForm
import models.journeymodels._
import play.api.Logger
import play.api.mvc._
import requests.RequestSupport
import services.{EcospendService, JourneyService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import util.Errors
import util.SafeEquals._
import views.Views

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class WhatIsTheNameOfYourBankAccountController @Inject() (
    mcc:             MessagesControllerComponents,
    requestSupport:  RequestSupport,
    journeyService:  JourneyService,
    ecospendService: EcospendService,
    views:           Views,
    actions:         Actions
)(implicit ec: ExecutionContext) extends FrontendController(mcc) {

  import requestSupport._

  val get: Action[AnyContent] = actions.journeyAction.async { implicit request: JourneyRequest[_] =>
    request.journey match {
      case j: JTerminal                                   => JourneyRouter.handleFinalJourneyOnNonFinalPageF(j)
      case j: JourneyDoYouWantYourRefundViaBankTransferNo => JourneyRouter.sendToCorrespondingPageF(j)
      case j: JBeforeIdentityVerified                     => JourneyRouter.sendToCorrespondingPageF(j)
      case j: JourneyIdentityNotVerified                  => JourneyRouter.sendToCorrespondingPageF(j)
      case _: JourneyIdentityVerified                     => getResult(None)
      case j: JourneyWhatIsTheNameOfYourBankAccount       => getResult(Some(j.bankDescription.bankId))
    }
  }

  private def getResult(maybeBankId: Option[BankId])(implicit request: JourneyRequest[_]): Future[Result] = {
    maybeBankId.fold(
      getBankSelectOptions.map { banks =>
        Ok(views.whatIsTheNameOfYourBankAccountPage(banks, WhatIsTheNameOfYourBankAccountForm.form))
      }
    ) { bankId =>
        getBankSelectOptions.map { banks =>
          Ok(views.whatIsTheNameOfYourBankAccountPage(banks, WhatIsTheNameOfYourBankAccountForm.form.fill(bankId)))
        }
      }
  }

  val post: Action[AnyContent] = actions.journeyAction.async { implicit request: JourneyRequest[_] =>
    request.journey match {
      case j: JTerminal                             => JourneyRouter.handleFinalJourneyOnNonFinalPageF(j)
      case j: JBeforeIdentityVerified               => JourneyRouter.sendToCorrespondingPageF(j)
      case j: JourneyIdentityNotVerified            => JourneyRouter.sendToCorrespondingPageF(j)
      case j: JourneyIdentityVerified               => processForm(j)
      case j: JourneyWhatIsTheNameOfYourBankAccount => processForm(j)
    }
  }

  private def processForm(journey: JAfterCheckYourAnswers)(implicit request: JourneyRequest[_]): Future[Result] = {
    WhatIsTheNameOfYourBankAccountForm
      .form
      .bindFromRequest()
      .fold(
        formWithErrors => {
          getBankSelectOptions.map { banks =>
            BadRequest(views.whatIsTheNameOfYourBankAccountPage(banks, formWithErrors))
          }
        },
        bankId => {
          for {
            bankDescription <- bankDescriptionForBankId(bankId)
            newJourney = journey match {
              case _: JourneyIdentityNotVerified =>
                Errors.throwServerErrorException("WhatIsTheNameOfYourBankAccountController reached invalid case. This should never happen!")
              case j: JourneyIdentityVerified =>
                j.into[JourneyWhatIsTheNameOfYourBankAccount]
                  .withFieldConst(_.bankDescription, bankDescription)
                  .transform
              case j: JAfterIdentityVerified =>
                j
                  .into[JourneyWhatIsTheNameOfYourBankAccount]
                  .enableInheritedAccessors
                  .withFieldConst(_.bankDescription, bankDescription)
                  .transform
            }
            response <- journeyService
              .upsert(newJourney)
              .map(_ => Redirect(controllers.routes.GiveYourConsentController.get))
          } yield response
        }
      )
  }

  private def bankDescriptionForBankId(bankId: BankId)(implicit request: JourneyRequest[_]): Future[BankDescription] =
    ecospendService.getBanks.map { banks =>
      if (banks.isEmpty) Logger(getClass).warn("Notification critical : Bank list from Ecospend is empty")

      val xs = banks.filter(_.bankId === bankId)
      if (xs.size === 1) xs.headOption.getOrElse(Errors.throwServerErrorException("Unable to get BankDescription for given BankId"))
      else Errors.throwServerErrorException("Unable to find BankDescription for given BankId")
    }

  private def getBankSelectOptions(implicit request: JourneyRequest[_]): Future[Seq[BanksSelectOptions]] =
    ecospendService.getBanks.map { banks =>
      if (banks.isEmpty) Logger(getClass).warn("Notification critical : Bank list from Ecospend is empty")
      BanksSelectOptions.noBankOption :: banks.map(b => BanksSelectOptions(b.bankId, b.name)).sorted
    }
}

