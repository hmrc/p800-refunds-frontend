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

    val `It’s on the letter HMRC sent you about your tax calculation, also known as a ‘P800’.`: Message = Message(
      english = "It’s on the letter HMRC sent you about your tax calculation, also known as a ‘P800’. For example, ‘P800REFNO1’."
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

  object CheckYourReferenceMessages {
    val `Check your reference`: Message = Message(
      english = "Check your reference"
    )

    def `You entered X`(reference: String): Message = Message(
      english = s"You entered <strong>$reference</strong>."
    )

    val `Is this correct?`: Message = Message(
      english = "Is this correct?"
    )

    val `Yes`: Message = Message(
      english = "Yes"
    )

    val `No, I need to change it`: Message = Message(
      english = "No, I need to change it"
    )

    val `Select yes if you entered the correct reference`: Message = Message(
      english = "Select yes if you entered the correct reference"
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

  object WhatIsYourNationalInsuranceNumber {
    val `What is your National Insurance number`: Message = Message(
      english = "What is your National Insurance number?"
    )

    val `It’s on your National Insurance`: Message = Message(
      english = "It’s on your National Insurance card or letter, benefit letter, payslip or P60. For example, ‘QQ 12 34 56 C’."
    )

    val `I do not know my National Insurance number`: Message = Message(
      english = "I do not know my National Insurance number"
    )

    val `You can get help to find a lost National`: Message = Message(
      english = s"""You can get help to <a id="govuk-link" href="#" class="govuk-link">find a lost National Insurance number (opens in new tab).</a>"""
    )

    val `Enter your National Insurance number`: Message = Message(
      english = "Enter your National Insurance number"
    )

    val `Enter your National Insurance number in the correct format`: Message = Message(
      english = "Enter your National Insurance number in the correct format"
    )
  }

  object WhatIsYourDateOfBirth {
    val `What is your date of birth`: Message = Message(
      english = "What is your date of birth?"
    )

    val `For example`: Message = Message(
      english = "For example, 27 3 2007"
    )

    object Errors {
      val `Enter your date of birth`: Message = Message(
        english = "Enter your date of birth"
      )

      val `You must enter a real date`: Message = Message(
        english = "You must enter a real date"
      )
      val `Enter a year which contains 4 numbers`: Message = Message(
        english = "Enter a year which contains 4 numbers"
      )
      val `Date of birth must be in the past`: Message = Message(
        english = "Date of birth must be in the past"
      )
      def `Date of birth must be on or before`(sixteenYearsAgo: String): Message = Message(
        english = s"Date of birth must be on or before $sixteenYearsAgo"
      )
      def `Date of birth must be on or after`(oneHundredAndTenYearsAgo: String): Message = Message(
        english = s"Date of birth must be on or after $oneHundredAndTenYearsAgo"
      )
      def `Date of birth must include a ...`(incompleteFields: String): Message = Message(
        english = s"Date of birth must include a $incompleteFields"
      )
    }

  }

  object WhatIsYourFullName {
    val `What is your full name`: Message = Message(
      english = "What is your full name?"
    )

    val `Enter your name as it appears on your tax calculation`: Message = Message(
      english = "Enter your name as it appears on your tax calculation letter or ‘P800’."
    )

    val `Enter your full name`: Message = Message(
      english = "Enter your full name"
    )

    val `Full name must be 2 characters or more`: Message = Message(
      english = "Full name must be 2 characters or more"
    )

    val `Full name must be 160 characters or less`: Message = Message(
      english = "Full name must be 160 characters or less"
    )

    def `Name must not include X`(x: String): Message = Message(
      english = s"Name must not include $x"
    )

    val `Full name must only include letters a to z, and special characters such as hyphens, spaces and apostrophes`: Message = Message(
      english = "Full name must only include letters a to z, and special characters such as hyphens, spaces and apostrophes"
    )
  }

  object ConfirmIdentity {
    val `We need you to confirm your identity`: Message = Message(
      english = "We need you to confirm your identity"
    )

    val `Before we pay your refund`: Message = Message(
      english = "Before we pay your refund, we need to ask you some security questions to confirm your identity."
    )

    val `We will need to ask you for your`: Message = Message(
      english = "We will need to ask you for your:"
    )

    val `name`: Message = Message(
      english = "name"
    )

    val `address`: Message = Message(
      english = "address"
    )

    val `date of birth`: Message = Message(
      english = "date of birth"
    )

    val `National Insurance number`: Message = Message(
      english = "National Insurance number"
    )

    val `We do this to protect your security`: Message = Message(
      english = "We do this to protect your security."
    )

  }

  object WeHaveConfirmedYourIdentity {
    val `We have confirmed your identity`: Message = Message(
      english = "We have confirmed your identity"
    )

    val `Enter your name as it appears on your tax calculation`: Message = Message(
      english = "Enter your name as it appears on your tax calculation letter or ‘P800’."
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

    val `What do you think`: Message = Message(
      english = "What did you think of this service?"
    )

    val `takes 30 seconds`: Message = Message(
      english = "(takes 30 seconds)"
    )

    def `If you don’t receive your refund you can call or write...`(generalEnquiriesLink: String): Message = Message(
      english =
        s"""If you don’t receive your refund you can <a id="general-enquiries-link" target="_blank" class="govuk-link" href="$generalEnquiriesLink">
           |call or write to the Income Tax helpline (opens in new tab)</a>. You will need your P800 reference.""".stripMargin
    )
  }

  object DoYouWantYourRefundViaBankTransfer {

    val `Do you want your refund by bank transfer?`: Message = Message(
      english = "Do you want your refund by bank transfer?"
    )

    val `Bank transfers are faster, safer, ...`: Message = Message(
      english = "Bank transfers are faster, safer and better for the environment. You'll need to have your online or mobile banking details ready."
    )

    val `Yes`: Message = Message(
      english = "Yes"
    )

    val `No, I want a cheque`: Message = Message(
      english = "No, I want a cheque"
    )

    val `Select if you want to receive a bank transfer or a cheque`: Message = Message(
      english = "Select if you want to receive a bank transfer or a cheque"
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
