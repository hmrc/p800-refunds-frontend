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

package edh

import play.api.libs.json._

final case class GetBankDetailsRiskResultResponse(
    header:                Header,
    bankValidationResults: Option[BankValidationResults],
    overallRiskResult:     OverallRiskResult, //making it mandatory as without it it doesn't make any sense
    riskResults:           Option[List[RuleResult]]
)

object GetBankDetailsRiskResultResponse {
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val format: OFormat[GetBankDetailsRiskResultResponse] = Json.format[GetBankDetailsRiskResultResponse]
}

final case class FailureResponse(reason: String)

object FailureResponse {
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val format: OFormat[FailureResponse] = Json.format[FailureResponse]
}

final case class OverallRiskResult(
    ruleScore:  Int,
    nextAction: NextAction
)

object OverallRiskResult {
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val format: OFormat[OverallRiskResult] = Json.format[OverallRiskResult]
}

sealed trait NextAction

object NextAction {

  case object Pay extends NextAction
  case object DoNotPay extends NextAction

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val format: Format[NextAction] = new Format[NextAction] {
    def reads(json: JsValue): JsResult[NextAction] = json match {
      case JsString("Pay")        => JsSuccess(Pay)
      case JsString("Do Not Pay") => JsSuccess(DoNotPay)
      case _                      => JsError("Invalid NextAction")
    }

    def writes(nextAction: NextAction): JsValue = nextAction match {
      case Pay      => JsString("Pay")
      case DoNotPay => JsString("Do Not Pay")
    }
  }
}

final case class RuleResult(
    ruleId:          String,
    ruleInformation: Option[String],
    ruleScore:       Int,
    nextAction:      NextAction
)

object RuleResult {
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val format: OFormat[RuleResult] = Json.format[RuleResult]
}
