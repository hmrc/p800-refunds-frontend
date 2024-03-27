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

import testsupport.UnitSpec
//TODO: Jake, next time in backend move this over (next ticket)
class NpsErrorCodesSpec extends UnitSpec {

  "Error codes should match specification" in {
    NpsErrorCodes.`Account is not Live` shouldBe "63477"
    NpsErrorCodes.`Overpayment has already been claimed` shouldBe "63480"
    NpsErrorCodes.`Overpayment is no longer available` shouldBe "63483"
    NpsErrorCodes.`Overpayment is suspended` shouldBe "63481"
  }
}
