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
import models.ecospend.BankFriendlyName

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

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

    val `You’ll have fewer details to enter if you sign in using your Government Gateway user ID.`: Message = Message(
      english = "You’ll have fewer details to enter if you sign in using your Government Gateway user ID."
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

    val `It’s on the letter HMRC sent you about your tax calculation, also known as a ‘P800’, and is up to 10 digits long.`: Message = Message(
      english = "It is on the letter HMRC sent you about your tax calculation, also known as a ‘P800’, and is up to 10 digits long. For example, 1002033400."
    )

    val `If you do not know your P800 reference`: Message = Message(
      english = "If you do not know your P800 reference"
    )

    def `Sign in using your Government Gateway user ID to claim your refund.`(link: String): Message = Message(
      english = s"""<a id="personal-tax-account-sign-in" class="govuk-link" href="$link">Sign in using your Government Gateway user ID</a> to claim your refund."""
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

  object RequestNotSubmitted {
    val `Your refund request has not been submitted`: Message = Message(
      english = "Your refund request has not been submitted"
    )

    val `We can not process your refund request.`: Message = Message(
      english = "We can not process your refund request."
    )

    val `Choose another way to get my money`: Message = Message(
      english = "Choose another way to get my money"
    )
  }

  object GiveYourPermission {
    val `Give your permission`: Message = Message(
      english = "Give your permission"
    )

    def `By choosing approve`(bankName: BankFriendlyName, amount: AmountInPence, changeBankLink: String): Message = Message(
      // language=HTML
      english = s"""By choosing approve, you will be redirected to <strong>${bankName.value}</strong> to securely log in and approve your refund of <strong>${amount.gdsFormatInPounds}</strong>. <a href="$changeBankLink" id="change-bank" class="govuk-link">Change my bank</a>."""
    )

    val `This is a service provided by Ecospend`: Message = Message(
      english = "This is a service provided by Ecospend, an authorised payment institution regulated by the Financial Conduct Authority (FCA), which will initiate a refund directly from HMRC to your bank."
    )

    val `Ecospend will have one-off access to`: Message = Message(
      english = "Ecospend will have one-off access to:"
    )

    val `the name on your account`: Message = Message(
      english = "the name on your account"
    )

    val `your account number and sort code`: Message = Message(
      english = "your account number and sort code"
    )

    val `your transactions`: Message = Message(
      english = "your transactions (this is just to confirm that your bank account is real and protect your security)."
    )

    val `Ecospend will not store`: Message = Message(
      english = "Ecospend will not store or share any of your data."
    )

    val `HMRC cannot see your transactions`: Message = Message(
      english = "HMRC cannot see your transactions or online bank account."
    )

    val `Approve this refund`: Message = Message(
      english = "Approve this refund"
    )

    val `Choose another way to get my money`: Message = Message(
      english = "Choose another way to get my money"
    )

  }

  object VerifyBankAccount {
    val `We are verifying your bank account`: Message = Message(
      english = "We are verifying your bank account"
    )

    def `This usually takes a few seconds`(refreshLink: String): Message = Message(
      english = s"""This usually takes a few seconds. You can <a href="$refreshLink" id="refresh-this-page" class="govuk-link">refresh this page</a> if it doesn't update automatically."""
    )
  }

  object ChooseAnotherWayToReceiveYourRefund {
    val `Choose another way to receive your refund`: Message = Message(
      english = "Choose another way to receive your refund"
    )

    val `Bank transfer using your Government Gateway user ID to sign in`: Message = Message(
      english = "Bank transfer using your Government Gateway user ID to sign in"
    )

    val `You will have fewer details to enter if you sign in using your Government Gateway user ID.`: Message = Message(
      english = "You will have fewer details to enter if you sign in using your Government Gateway user ID."
    )

    val `Cheque`: Message = Message(
      english = "Cheque"
    )

    val `Select the way you want to receive your refund`: Message = Message(
      english = "Select the way you want to receive your refund"
    )

    //TODO: confirm with Content designer this text
    val `Select if you want to receive a bank transfer via your personal tax account, or a bank transfer logged out`: Message = Message(
      english = "Select if you want to receive a bank transfer via your personal tax account, or a bank transfer logged out"
    )
  }

  object CheckYourAnswersMessages {

    val `Reference`: Message = Message(
      english = "Reference"
    )

    val `Check your answers`: Message = Message(
      english = "Check your answers"
    )

    val `Date of birth`: Message = Message(
      english = "Date of birth"
    )

    val `National insurance number`: Message = Message(
      english = "National insurance number"
    )

    val `Change`: Message = Message(
      english = "Change"
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

  object WeNeedToConfirmYourIdentity {
    val `We need you to confirm your identity`: Message = Message(
      english = "We need you to confirm your identity"
    )

    val `Before we pay your refund`: Message = Message(
      english = "Before we pay your refund, we need to ask you some security questions to confirm your identity."
    )

    val `We will need to ask you for your`: Message = Message(
      english = "We will need to ask you for your:"
    )

    val `P800 reference`: Message = Message(
      english = "P800 reference"
    )

    val `date of birth`: Message = Message(
      english = "date of birth"
    )

    val `national insurance number`: Message = Message(
      english = "national insurance number"
    )

    val `We do this to protect your security`: Message = Message(
      english = "We do this to protect your security."
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

    def `You can get help to find a lost National`(link: String): Message = Message(
      english = s"""You can get help to <a id="lost-national-insurance-number-link" href="$link" target="_blank" class="govuk-link">find a lost National Insurance number (opens in new tab).</a>"""
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

  object WeHaveConfirmedYourIdentity {
    val `We have confirmed your identity`: Message = Message(
      english = "We have confirmed your identity"
    )

    val `Enter your name as it appears on your tax calculation`: Message = Message(
      english = "Enter your name as it appears on your tax calculation letter or ‘P800’."
    )
  }

  object WeCannotConfirmYourIdentity {
    val `We cannot confirm your identity`: Message = Message(
      english = "We cannot confirm your identity"
    )
    val `The information you have provided does not match our records.`: Message = Message(
      english = "The information you have provided does not match our records."
    )
    val `Try gain`: Message = Message(
      english = "Try again"
    )
    val `Choose another method`: Message = Message(
      english = "Choose another method"
    )
  }

  object WhatIsTheNameOfYourBankAccount {
    val `What is the name of your bank?`: Message = Message(
      english = "What is the name of your bank?"
    )

    val `Start typing the name of a UK bank that you want your refund to be sent to.`: Message = Message(
      english = "Start typing the name of a UK bank that you want your refund to be sent to."
    )

    val `My bank is not listed`: Message = Message(
      english = "My bank is not listed"
    )

    val `Select a bank from the list`: Message = Message(
      english = "Select a bank from the list"
    )
  }

  object YourChequeWillBePostedToYou {
    val `Complete your refund request to get your cheque`: Message = Message(
      english = "Complete your refund request to get your cheque"
    )

    val `Your cheque will be sent to the same address as your tax calculation letter.`: Message = Message(
      english = "Your cheque will be sent to the same address as your tax calculation letter."
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

    val `Complete refund request`: Message = Message(
      english = "Complete refund request"
    )
  }

  object ChequeRequestReceived {
    val `Cheque request received`: Message = Message(
      english = "Cheque request received"
    )

    val `Your P800 reference:`: Message = Message(
      english = "Your P800 reference:"
    )

    private val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.UK)
    def `Your refund of £x.xx will arrive in the post by DATE.`(amountInPence: AmountInPence, chequeArriveByDate: LocalDate): Message = Message(
      english = s"Your refund of <strong>${amountInPence.gdsFormatInPounds}</strong> will arrive in the post by <strong>${chequeArriveByDate.format(formatter)}</strong>."
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

    def `If you do not receive your refund you can call or write...`(generalEnquiriesLink: String): Message = Message(
      english =
        s"""If you do not receive your refund you can <a id="general-enquiries-link" target="_blank" class="govuk-link" href="$generalEnquiriesLink">
           |call or write to the Income Tax helpline (opens in new tab)</a>. You will need your P800 reference.""".stripMargin
    )
  }

  object DoYouWantYourRefundViaBankTransfer {

    val `Do you want your refund by bank transfer?`: Message = Message(
      english = "Do you want your refund by bank transfer?"
    )

    val `Bank transfers are faster and safer. ...`: Message = Message(
      english = "Bank transfers are faster and safer. You’ll need to have your online or mobile banking details ready."
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

  object BankTransferRequestReceived {
    val `Request received`: Message = Message(
      english = "Request received"
    )

    val `P800 reference`: Message = Message(
      english = "P800 reference"
    )

    def `Your refund of £x.xx will now be processed and paid by...`(amountInPence: AmountInPence, date: String): Message = Message(
      //language=html
      english = s"Your refund of <strong>${amountInPence.gdsFormatInPounds}</strong> will now be paid by <strong>$date</strong>."
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
  object ServicePhase {

    val serviceName: Message = Message(
      english = "Get an Income Tax refund"
    )

    val serviceNameTestOnly: Message = Message(
      english = "Test Only - Get an Income Tax refund"
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
