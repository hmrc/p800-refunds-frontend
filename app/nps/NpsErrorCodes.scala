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

/**
 * Nps Error codes base on the specs
 */
object NpsErrorCodes {
  val `Account is not Live`: String = "63477"
  val `Overpayment has already been claimed`: String = "63480"
  val `Overpayment is suspended`: String = "63481"
  val `Overpayment is no longer available`: String = "63483"
}
