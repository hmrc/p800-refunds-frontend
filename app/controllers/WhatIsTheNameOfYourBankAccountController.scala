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
import models.ecospend.BankId
import models.ecospend.BanksSelectOptions
import models.forms.WhatIsTheNameOfYourBankAccountForm
import models.journeymodels._
import play.api.Logger
import play.api.mvc._
import requests.RequestSupport
import services.{EcospendService, JourneyService}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
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
      case j: JourneyWhatIsTheNameOfYourBankAccount       => getResult(Some(j.bankId))
      // case j: JAfterWhatIsTheNameOfYourBankAccount        => getResult(Some(j.bankId))
    }
  }

  private def getResult(maybeBankId: Option[BankId])(implicit request: JourneyRequest[_]): Future[Result] = {
    maybeBankId.fold(
      getBankSelectOptions(requestSupport.hc).map { banks =>
        Ok(views.whatIsTheNameOfYourBankAccountPage(banks, WhatIsTheNameOfYourBankAccountForm.form))
      }
    ) { bankId =>
        getBankSelectOptions(requestSupport.hc).map { banks =>
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

  // could we split the JAfter traits when the journey splits -
  // i.e. have a JAfterCheckYourAnswersVerified and a JAfterCheckYourAnswersNotVerified - ??!
  // or would that be too confusing?
  // Here it would be helpful as we could handle the unhappy path above and pass the happy path to processForm
  private def processForm(journey: JAfterCheckYourAnswers)(implicit request: JourneyRequest[_]): Future[Result] = {
    WhatIsTheNameOfYourBankAccountForm
      .form
      .bindFromRequest()
      .fold(
        formWithErrors => {
          getBankSelectOptions(hc).map { banks =>
            BadRequest(views.whatIsTheNameOfYourBankAccountPage(banks, formWithErrors))
          }
        },
        bankId => {
          val newJourney = journey match {
            case j: JourneyIdentityNotVerified => j // TODO: Not sure if this is the correct way to handle this?
            case j: JourneyIdentityVerified =>
              j.into[JourneyWhatIsTheNameOfYourBankAccount]
                .withFieldConst(_.bankId, bankId)
                .transform
            case j: JAfterIdentityVerified =>
              j
                .into[JourneyWhatIsTheNameOfYourBankAccount]
                .enableInheritedAccessors
                .withFieldConst(_.bankId, bankId)
                .transform
          }
          journeyService
            .upsert(newJourney)
            .map(_ => Redirect(controllers.routes.GiveYourConsentController.get))
        }
      )
  }

  private def getBankSelectOptions(implicit hc: HeaderCarrier): Future[Seq[BanksSelectOptions]] =
    ecospendService.getBanks(hc).map { banks =>
      if (banks.isEmpty) Logger(getClass).warn("notification critical : Bank list from Ecospend is empty")
      BanksSelectOptions.noBankOption :: banks.map(b => BanksSelectOptions(b.bankId, b.name)).sorted
    }
}

