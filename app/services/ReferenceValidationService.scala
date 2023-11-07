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

import connectors.ReferenceValidationConnector
import models.{P800Reference, ReferenceValidationRequest, ReferenceValidationResponse}
import play.api.http.Status.OK
import play.api.mvc.RequestHeader
import uk.gov.hmrc.http.{HttpResponse, UpstreamErrorResponse}
import util.HttpResponseUtils.HttpResponseOps

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ReferenceValidationService @Inject() (
    referenceValidationConnector: ReferenceValidationConnector
)(implicit ec: ExecutionContext) {

  def validateReference(p800Reference: P800Reference)(implicit requestHeader: RequestHeader): Future[ReferenceValidationResponse] = {
    val request = ReferenceValidationRequest(p800Reference.value)

    referenceValidationConnector.validateReference(request).map { httpResponse: HttpResponse =>
      httpResponse.status match {
        case OK =>
          httpResponse.parseJSON[ReferenceValidationResponse]
            .getOrElse(throw UpstreamErrorResponse(httpResponse.body, httpResponse.status))
        case _ =>
          throw UpstreamErrorResponse(httpResponse.body, httpResponse.status)
      }
    }
  }
}
