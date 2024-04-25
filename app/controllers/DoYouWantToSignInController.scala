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

import action.Actions
import config.AppConfig
import models.forms.DoYouWantToSignInForm
import models.forms.enumsforforms.DoYouWantToSignInFormValue
import play.api.mvc._
import requests.RequestSupport
import services.{AuditService, FailedVerificationAttemptService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.Views
import models.audit.{Login, IpAddressLockedout}

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class DoYouWantToSignInController @Inject() (
    mcc:                              MessagesControllerComponents,
    requestSupport:                   RequestSupport,
    auditService:                     AuditService,
    failedVerificationAttemptService: FailedVerificationAttemptService,
    views:                            Views,
    actions:                          Actions,
    appConfig:                        AppConfig
)(implicit ec: ExecutionContext) extends FrontendController(mcc) {

  import requestSupport._

  def get: Action[AnyContent] = actions.journeyInProgress{ implicit request =>
    Ok(views.doYouWantToSignInPage(
      form = DoYouWantToSignInForm.form
    ))
  }

  def post: Action[AnyContent] = actions.journeyInProgress.async { implicit request =>
    for {
      userIsLockedOut <- failedVerificationAttemptService.shouldBeLockedOut()
    } yield {
      DoYouWantToSignInForm.form.bindFromRequest().fold(
        formWithErrors => BadRequest(views.doYouWantToSignInPage(
          form = formWithErrors
        )), {
          case DoYouWantToSignInFormValue.Yes => {
            auditService.auditUserLoginSelection(Login(true), IpAddressLockedout(userIsLockedOut))(hc)
            Redirect(appConfig.personalTaxAccountSignInUrl)
          }
          case DoYouWantToSignInFormValue.No if userIsLockedOut =>
            auditService.auditUserLoginSelection(Login(false), IpAddressLockedout(userIsLockedOut))(hc)
            Redirect(controllers.routes.YouCannotConfirmYourIdentityYetController.get)
          case DoYouWantToSignInFormValue.No =>
            auditService.auditUserLoginSelection(Login(false), IpAddressLockedout(userIsLockedOut))(hc)
            Redirect(controllers.routes.DoYouWantYourRefundViaBankTransferController.get)
        }
      )

    }

  }

}
