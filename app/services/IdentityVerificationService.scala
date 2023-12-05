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

import connectors.IdentityVerificationConnector
import models.{IdentityVerificationRequest, IdentityVerificationResponse, NationalInsuranceNumber}
import play.api.http.Status.OK
import play.api.mvc.RequestHeader
import uk.gov.hmrc.http.{HttpResponse, UpstreamErrorResponse}
import util.HttpResponseUtils.HttpResponseOps

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

//todo maybe rename this, not sure what to call it at the moment, is it nps or something?
@Singleton
class IdentityVerificationService @Inject() (identityVerificationConnector: IdentityVerificationConnector)(implicit ec: ExecutionContext) {

  def verifyIdentity(nationalInsuranceNumber: NationalInsuranceNumber)(implicit requestHeader: RequestHeader): Future[IdentityVerificationResponse] = {
    identityVerificationConnector
      .verifyIdentity(IdentityVerificationRequest(nationalInsuranceNumber))
      .map { httpResponse: HttpResponse =>
        httpResponse.status match {
          case OK =>
            httpResponse.parseJSON[IdentityVerificationResponse]
              .getOrElse(throw UpstreamErrorResponse(httpResponse.body, httpResponse.status))
          case _ =>
            throw UpstreamErrorResponse(httpResponse.body, httpResponse.status)
        }
      }
  }

}
