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

package language

import models.AmountInPence

object Messages {

  object CommonMessages {
    val back: Message = Message(
      english = "Back",
      welsh   = "Yn ôl"
    )

    val error: Message = Message(
      english = "Error: ",
      welsh   = "Gwall: "
    )

    val continue: Message = Message(
      english = "Continue",
      welsh   = "Yn eich blaen"
    )

    val `There is a problem`: Message = Message(
      english = "There is a problem",
      welsh   = "Mae problem wedi codi"
    )

    val `Try again`: Message = Message(
      english = "Try again"
    )
  }

  object DoYouWantToSignInMessages {
    val `Do you want to sign in?`: Message = Message(
      english = "Do you want to sign in?"
    )

    val `Sign in with your Government Gateway user ID.`: Message = Message(
      english = "Sign in with your Government Gateway user ID. You’ll have fewer details to enter this way."
    )

    val `Yes, sign in`: Message = Message(
      english = "Yes, sign in"
    )

    val `No, continue without signing in`: Message = Message(
      english = "No, continue without signing in"
    )

    val `Select yes if you want to sign in to your tax account`: Message = Message(
      english = "Select yes if you want to sign in to your tax account"
    )
  }

  object EnterP800ReferenceMessages {
    val `What is your P800 reference?`: Message = Message(
      english = "What is your P800 reference?"
    )

    val `It’s on the letter HMRC sent you about your tax calculation, also known as a ’P800’.`: Message = Message(
      english = "It’s on the letter HMRC sent you about your tax calculation, also known as a ’P800’. For example, ’P800REFNO1’."
    )

    val `If you do not know your P800 reference`: Message = Message(
      english = "If you do not know your P800 reference"
    )

    def `Sign in or create a personal tax account to claim your refund.`(link: String): Message = Message(
      english = s"""<a id="personal-tax-account-sign-in" class="govuk-link" href="$link">Sign in or create a personal tax account</a> to claim your refund."""
    )

    def `Call or write to the Income Tax helpline (opens in new tab) if you cannot create a personal tax account.`(link: String): Message = Message(
      english = s"""<a id="income-tax-general-enquiries" class="govuk-link" href="$link" rel="noreferrer noopener" target="_blank">Call or write to the Income Tax helpline (opens in new tab)</a> if you cannot create a personal tax account."""
    )

    val `Enter your P800 reference`: Message = Message(
      english = "Enter your P800 reference"
    )

    val `Enter your P800 reference in the correct format`: Message = Message(
      english = "Enter your P800 reference in the correct format"
    )
  }

  object CannotConfirmReference {
    val `We cannot confirm your reference`: Message = Message(
      english = "We cannot confirm your reference"
    )

    val `You can find it on your P800 letter`: Message = Message(
      english = "You can find it on your P800 letter."
    )

    def `You have X more attempts`(attempts: String): Message = Message(
      english = s"""You have <strong>$attempts more attempts</strong> to request a bank transfer this way."""
    )

    val `Or you can sign in to request your refund`: Message = Message(
      english = "Or you can sign in to request your refund."
    )

  }

  object YourChequeWillBePostedToYou {
    val `Your cheque will be posted to you`: Message = Message(
      english = "Your cheque will be posted to you"
    )

    val `Your cheque will be sent to the same address as your p800 letter.`: Message = Message(
      english = "Your cheque will be sent to the same address as your p800 letter."
    )

    val `My cheque needs to go to a different address`: Message = Message(
      english = "My cheque needs to go to a different address"
    )

    val `To update your address you need to:`: Message = Message(
      english = "To update your address you need to:"
    )

    def `Contact HMRC to tell us that you have changed address.`(contactHmrcLink: String): Message = Message(
      english = s"""<a id="contact-hmrc-link" href="$contactHmrcLink" class="govuk-link">Contact HMRC</a> to tell us that you have changed address."""
    )

    val `Wait two days for HMRC to update your details.`: Message = Message(
      english = "Wait two days for HMRC to update your details."
    )

    val `Restart your refund request.`: Message = Message(
      english = "Restart your refund request."
    )

    val `Submit refund request`: Message = Message(
      english = "Submit refund request"
    )
  }

  object ChequeRequestReceived {
    val `Request received`: Message = Message(
      english = "Request received"
    )

    val `P800 reference`: Message = Message(
      english = "P800 reference"
    )

    def `Your refund of £x.xx will now be processed.`(amountInPence: AmountInPence): Message = Message(
      english = s"Your refund of <strong>${amountInPence.gdsFormatInPounds}</strong> will now be processed."
    )

    val `You should allow up to 6 weeks for your cheque to arrive in the post.`: Message = Message(
      english = "You should allow up to 6 weeks for your cheque to arrive in the post."
    )

    val `Print this page`: Message = Message(
      english = "Print this page"
    )

    val `What happens next`: Message = Message(
      english = "What happens next"
    )

    def `If you don’t receive your refund you can call or write...`(generalEnquiriesLink: String): Message = Message(
      english =
        s"""If you don’t receive your refund you can <a id="general-enquiries-link" class="govuk-link" href="$generalEnquiriesLink">
           |call or write to the Income Tax helpline (opens in new tab)</a>. You will need your P800 reference.""".stripMargin
    )
  }

  object ServicePhase {

    val serviceName: Message = Message(
      english = "Claim an income tax refund"
    )

    val serviceNameTestOnly: Message = Message(
      english = "Test Only - Claim an income tax refund"
    )

    val beta: Message = Message(
      english = "beta",
      welsh   = "beta"
    )

    def bannerText(link: String): Message = Message(
      english = s"""This is a new service – your <a class="govuk-link" href="$link">feedback</a> will help us to improve it.""",
      welsh   = s"""Mae hwn yn wasanaeth newydd – bydd eich <a class="govuk-link" href="$link">adborth</a> yn ein helpu i’w wella."""
    )
  }

}
