/*
 * Copyright 2024 HM Revenue & Customs
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

package nps

import nps.models.IssuePayableOrderRequest
import play.api.test.FakeRequest
import testsupport.ItSpec
import testsupport.stubs.NpsIssuePayableOrderStub

class IssuePayableOrderConnectorSpec extends ItSpec {

  "test" in {

    val connector = app.injector.instanceOf[IssuePayableOrderConnector]

    val p = tdAll.Cheque.AfterReferenceCheck.journeyReferenceChecked.getP800ReferenceChecked(FakeRequest())
    val issuePayableOrderRequest = IssuePayableOrderRequest(
      customerAccountNumber   = p.customerAccountNumber,
      associatedPayableNumber = p.associatedPayableNumber,
      currentOptimisticLock   = p.currentOptimisticLock
    )

    NpsIssuePayableOrderStub.issuePayableOrderRefundAlreadyTaken(
      tdAll.nino,
      tdAll.p800Reference,
      issuePayableOrderRequest
    )

    val result = connector.issuePayableOrder(
      nino                     = tdAll.nino,
      p800Reference            = tdAll.p800Reference,
      issuePayableOrderRequest = issuePayableOrderRequest
    )(FakeRequest()).futureValue

    result shouldBe ()
  }

}
