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

import util.WelshDateUtil._

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

object Messages {

  object TimeoutMessages {
    val `Timeout out page title - You deleted your answers`: Message = Message(
      english = "You deleted your answers - Get an Income Tax refund - GOV.UK",
      welsh   = "Rydych wedi dileu’ch atebion - Cael ad-daliad Treth Incwm - GOVUK"
    )

    val `Timeout out page title - For your security, we deleted your answers`: Message = Message(
      english = "For your security, we deleted your answers - Get an Income Tax refund - GOV.UK",
      welsh   = "Er eich diogelwch, gwnaethom ddileu’ch atebion - Cael ad-daliad Treth Incwm - GOVUK"
    )

    val `For your Security`: Message = Message(
      english = "For your security",
      welsh   = "Rydych ar fin cael eich allgofnodi"
    )

    val `We will delete your answers`: Message = Message(
      english = "We will delete your answers in",
      welsh   = "Er eich diogelwch, byddwn yn eich allgofnodi cyn pen"
    )

    val `Continue with getting income tax refund`: Message = Message(
      english = "Continue with getting your Income Tax refund",
      welsh   = "Mynd yn eich blaen â chael eich ad-daliad treth incwm"
    )

    val `Delete your answers`: Message = Message(
      english = "Delete your answers",
      welsh   = "Dileu’ch atebion"
    )

    val `You deleted your answers`: Message = Message(
      english = "You deleted your answers",
      welsh   = "Rydych wedi dileu’ch atebion"
    )

    val `For your security, we deleted your answers`: Message = Message(
      english = "For your security, we deleted your answers",
      welsh   = "Er eich diogelwch, gwnaethom ddileu’ch atebion"
    )

    val `Start again`: Message = Message(
      english = "Start again",
      welsh   = "Dechrau eto"
    )
  }

  object CommonMessages {
    val back: Message = Message(
      english = "Back",
      welsh   = "Yn ôl"
    )

    val `Error: `: Message = Message(
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
      english = "Try again",
      welsh   = "Rhowch gynnig arall arni"
    )
  }

  object DoYouWantToSignInMessages {
    val `Do you want to sign in?`: Message = Message(
      english = "Do you want to sign in?",
      welsh   = "A ydych am fewngofnodi?"
    )

    val `You’ll have fewer details to enter if you sign in using your Government Gateway user ID.`: Message = Message(
      english = "You’ll have fewer details to enter if you sign in using your Government Gateway user ID.",
      welsh   = "Bydd gennych lai o fanylion i’w nodi os byddwch yn mewngofnodi gan ddefnyddio eich Dynodydd Defnyddiwr (ID) ar gyfer Porth y Llywodraeth."
    )

    val `Yes, sign in`: Message = Message(
      english = "Yes, sign in",
      welsh   = "Iawn, mewngofnodi"
    )

    val `If your tax calculation letter...`: Message = Message(
      english = "If your tax calculation letter (P800) is dated before 3 June 2024, you must sign in to get your refund.",
      welsh   = "Os yw’ch llythyr cyfrifiad treth (P800) wedi’i ddyddio cyn 3 Mehefin 2024, mae’n rhaid i chi fewngofnodi i gael eich ad-daliad."
    )

    val `No, continue without signing in`: Message = Message(
      english = "No, continue without signing in",
      welsh   = "Na, ewch yn eich blaen heb fewngofnodi"
    )

    val `Select yes if you want to sign in to your tax account`: Message = Message(
      english = "Select yes if you want to sign in to your tax account",
      welsh   = "Dewiswch ‘Iawn’ os hoffech fewngofnodi i’ch cyfrif treth"
    )
  }

  object EnterP800ReferenceMessages {
    val `What is your P800 reference?`: Message = Message(
      english = "What is your P800 reference?",
      welsh   = "Beth yw’r cyfeirnod ar eich P800?"
    )

    val `It’s on the letter HMRC sent you about your tax calculation, also known as a ‘P800’, and is up to 10 digits long.`: Message = Message(
      english = "It is on the letter HMRC sent you about your tax calculation, also known as a ‘P800’, and is up to 10 digits long. For example, 1002033400.",
      welsh   = "Mae hwn ar y llythyr y mae CThEF wedi’i anfon atoch ynglŷn â’ch cyfrifiad treth, a elwir hefyd yn ‘P800’, ac mae hyd at 10 digid. Er enghraifft, 1002033400."
    )

    val `Enter your P800 reference`: Message = Message(
      english = "Enter your P800 reference",
      welsh   = "Nodwch eich cyfeirnod P800"
    )

    val `Enter your P800 reference in the correct format`: Message = Message(
      english = "Enter your P800 reference in the correct format",
      welsh   = "Nodwch eich cyfeirnod P800 yn y fformat cywir"
    )
  }

  object RefundRequestNotSubmitted {
    val `Your refund request has not been submitted`: Message = Message(
      english = "Your refund request has not been submitted",
      welsh   = "Nid yw’ch cais am ad-daliad wedi’i gyflwyno"
    )

    val `We can not process your refund request.`: Message = Message(
      english = "We can not process your refund request.",
      welsh   = "Ni allwn brosesu’ch cais am ad-daliad."
    )

    val `Choose another way to get my refund`: Message = Message(
      english = "Choose another way to get my refund",
      welsh   = "Dewis ffordd arall o gael fy ad-daliad"
    )
  }

  object GiveYourConsent {
    val `Give your consent`: Message = Message(
      english = "Give your consent",
      welsh   = "Rhowch eich caniatâd"
    )

    def `By choosing approve`(bankName: BankFriendlyName, amount: AmountInPence, changeBankLink: String): Message = Message(
      // language=HTML
      english = s"""By choosing approve, you will be transferred to <strong>${bankName.value}</strong> to securely sign in and approve your refund of <strong>${amount.gdsFormatInPounds}</strong>. <a href="$changeBankLink" id="change-bank" class="govuk-link">Change my bank</a>.""",
      // language=HTML
      welsh = s"""Drwy ddewis cymeradwyo, byddwch yn cael eich trosglwyddo i <strong>${bankName.value}</strong> i fewngofnodi a chymeradwyo’ch ad-daliad o <strong>${amount.gdsFormatInPounds}</strong>. <a href="$changeBankLink" id="change-bank" class="govuk-link">Newid fy manc</a>."""
    )

    val `This is a service provided by Ecospend`: Message = Message(
      english = "This is a service provided by Ecospend, an authorised payment institution regulated by the Financial Conduct Authority (FCA). Ecospend will check your bank details so HMRC can send the refund to your bank account.",
      welsh   = "Gwasanaeth a ddarperir gan Ecospend, sefydliad talu awdurdodedig sydd wedi’i reoli gan yr Awdurdod Ymddygiad Ariannol yw hwn. Bydd Ecospend yn gwirio’ch manylion banc fel y gall CThEF anfon yr ad-daliad i’ch cyfrif banc."
    )

    val `Ecospend will have one-off access to`: Message = Message(
      english = "Ecospend will have one-off access to:",
      welsh   = "Bydd gan Ecospend fynediad untro i’r canlynol:"
    )

    val `the name on your account`: Message = Message(
      english = "the name on your account",
      welsh   = "yr enw ar eich cyfrif"
    )

    val `your account number and sort code`: Message = Message(
      english = "your account number and sort code",
      welsh   = "rhif eich cyfrif a’r cod didoli"
    )

    val `your transactions`: Message = Message(
      english = "your transactions (this is just to confirm that your bank account is real and protect your security).",
      welsh   = "eich trafodion (dim ond i gadarnhau bod eich cyfrif banc yn real ac amddiffyn eich diogelwch)."
    )

    val `Ecospend will not store`: Message = Message(
      english = "Ecospend will not store or share any of your data.",
      welsh   = "Ni fydd Ecospend yn storio nac yn rhannu unrhyw ran o’ch data."
    )

    val `HMRC cannot see your transactions`: Message = Message(
      english = "HMRC cannot see your transactions or online bank account.",
      welsh   = "Ni all CThEF weld eich trafodion na’ch cyfrif banc ar-lein."
    )

    val `Approve this refund`: Message = Message(
      english = "Approve this refund",
      welsh   = "Cymeradwyo’r ad-daliad hwn"
    )

    val `Choose another way to get my money`: Message = Message(
      english = "Choose another way to get my refund",
      welsh   = "Dewis ffordd arall o gael fy ad-daliad"
    )

  }

  object VerifyBankAccount {
    val `We are verifying your bank account`: Message = Message(
      english = "We are verifying your bank account",
      welsh   = "Rydym yn gwirio’ch cyfrif banc"
    )

    def `This can take up to a minute`(refreshLink: String): Message = Message(
      english = s"""This can take up to a minute. You can <a href="$refreshLink" id="refresh-this-page" class="govuk-link">refresh this page</a> if it does not update automatically.""",
      welsh   = s"""Mae hyn fel arfer yn cymryd ychydig eiliadau. Gallwch <a href="$refreshLink" id="refresh-this-page" class="govuk-link">adnewyddu’r dudalen hon</a> os nad yw’n diweddaru’n awtomatig."""
    )
  }

  object ChooseAnotherWayToGetYourRefund {
    val `Choose another way to get your refund`: Message = Message(
      english = "Choose another way to get your refund",
      welsh   = "Dewiswch ffordd arall o gael fy ad-daliad"
    )

    val `You can claim your refund by bank transfer or cheque...`: Message = Message(
      english = "You can claim your refund by bank transfer or cheque. If you want your refund by bank transfer, you will have fewer details to enter if you sign in using your Government Gateway user ID.",
      welsh   = "Gallwch hawlio’ch ad-daliad drwy drosglwyddiad banc neu siec. Os ydych am gael eich ad-daliad drwy drosglwyddiad banc, bydd gennych lai o fanylion i’w nodi os byddwch yn mewngofnodi gan ddefnyddio’ch Dynodydd Defnyddiwr (ID) ar gyfer Porth y Llywodraeth."
    )

    val `Do you want your refund by bank transfer or cheque?`: Message = Message(
      english = "Do you want your refund by bank transfer or cheque?",
      welsh   = "A ydych am eich ad-daliad drwy drosglwyddiad banc neu siec?"
    )

    val `Bank transfer using your Government Gateway user ID to sign in`: Message = Message(
      english = "Bank transfer using your Government Gateway user ID to sign in",
      welsh   = "Gwneud trosglwyddiad banc gan ddefnyddio’ch Dynodydd Defnyddiwr (ID) ar gyfer Porth y Llywodraeth i fewngofnodi"
    )

    val `Cheque`: Message = Message(
      english = "Cheque",
      welsh   = "Siec"
    )

    val `Select if you want to receive a bank transfer or a cheque`: Message = Message(
      english = "Select if you want to receive a bank transfer or a cheque",
      welsh   = "Dewiswch os ydych am gael trosglwyddiad banc neu siec"
    )
  }

  object CheckYourAnswersMessages {

    val `P800 reference`: Message = Message(
      english = "P800 reference",
      welsh   = "Eich cyfeirnod P800"
    )

    val `Check your answers`: Message = Message(
      english = "Check your answers",
      welsh   = "Gwiriwch eich atebion"
    )

    val `Date of birth`: Message = Message(
      english = "Date of birth",
      welsh   = "Dyddiad geni"
    )

    val `National insurance number`: Message = Message(
      english = "National Insurance number",
      welsh   = "Eich rhif Yswiriant Gwladol"
    )

    val `Change`: Message = Message(
      english = "Change",
      welsh   = "Newid"
    )

  }

  object ConfirmYourIdentity {
    val `We need you to confirm your identity`: Message = Message(
      english = "We need you to confirm your identity",
      welsh   = "Mae angen i chi gadarnhau pwy ydych"
    )

    val `Before we pay your refund`: Message = Message(
      english = "Before we pay your refund, we need to ask you some security questions to confirm your identity.",
      welsh   = "Cyn i ni dalu’ch ad-daliad, mae angen i ni ofyn rhai cwestiynau diogelwch i gadarnhau pwy ydych."
    )

    val `We will need to ask you for your`: Message = Message(
      english = "We will need to ask you for your:",
      welsh   = "Bydd angen i ni ofyn i chi am y canlynol:"
    )

    val `P800 reference`: Message = Message(
      english = "P800 reference",
      welsh   = "eich cyfeirnod P800"
    )

    val `date of birth`: Message = Message(
      english = "date of birth",
      welsh   = "ei ddyddiad geni"
    )

    val `National Insurance number`: Message = Message(
      english = "National Insurance number",
      welsh   = "eich rhif Yswiriant Gwladol"
    )

    val `We do this to protect your security`: Message = Message(
      english = "We do this to protect your security.",
      welsh   = "Rydym yn gwneud hyn er mwyn diogelu’ch diogelwch."
    )

    val `If you do not know your P800 reference`: Message = Message(
      english = "If you do not know your P800 reference,",
      welsh   = "Os nad ydych yn gwybod eich cyfeirnod P800,"
    )

    def `sign in using your Government Gateway user ID to claim your refund.`(link: String): Message = Message(
      english = s"""<a id="personal-tax-account-sign-in" class="govuk-link" href="$link">sign in using your Government Gateway user ID</a> to claim your refund.""",
      welsh   = s"""<a id="personal-tax-account-sign-in" class="govuk-link" href="$link">mewngofnodwch gan ddefnyddio’ch Dynodydd Defnyddiwr (ID) ar gyfer Porth y Llywodraeth</a> i gael eich ad-daliad."""
    )

    val `If you do not know your National Insurance number`: Message = Message(
      english = "If you do not know your National Insurance number, ",
      welsh   = "Os nad ydych yn gwybod eich rhif Yswiriant Gwladol, "
    )

    def `get help to find it`(link: String): Message = Message(
      english = s"""you can <a id="lost-national-insurance-number-link" rel="noreferrer noopener" target="_blank" class="govuk-link" href="$link">get help to find it (opens in new tab)</a>.""",
      welsh   = s"""gallwch <a id="lost-national-insurance-number-link" rel="noreferrer noopener" target="_blank" class="govuk-link" href="$link">gael help i ddod o hyd iddo (yn agor tab newydd)</a>."""
    )
  }

  object WhatIsYourNationalInsuranceNumber {
    val `What is your National Insurance number`: Message = Message(
      english = "What is your National Insurance number?",
      welsh   = "Beth yw eich rhif Yswiriant Gwladol?"
    )

    val `It’s on your National Insurance`: Message = Message(
      english = "It’s on your National Insurance card or letter, benefit letter, payslip or P60. For example, ‘QQ 12 34 56 C’.",
      welsh   = "Mae hwn ar eich cerdyn neu lythyr Yswiriant Gwladol, llythyr ynghylch budd-daliadau, slip cyflog neu P60. Er enghraifft, ‘QQ 12 34 56 C’."
    )

    val `Enter your National Insurance number`: Message = Message(
      english = "Enter your National Insurance number",
      welsh   = "Nodwch eich rhif Yswiriant Gwladol"
    )

    val `Enter your National Insurance number in the correct format`: Message = Message(
      english = "Enter your National Insurance number in the correct format",
      welsh   = "Nodwch eich rhif Yswiriant Gwladol yn y fformat cywir"
    )
  }

  object EnterYourDateOfBirth {
    val `What is your date of birth`: Message = Message(
      english = "What is your date of birth?",
      welsh   = "Beth yw’ch dyddiad geni?"
    )

    val `For example`: Message = Message(
      english = "For example, 27 3 2007",
      welsh   = "Er enghraifft, 27 3 2007"
    )

    object Errors {
      val `Enter your date of birth`: Message = Message(
        english = "Enter your date of birth",
        welsh   = "Nodwch eich dyddiad geni"
      )

      val `You must enter a real date`: Message = Message(
        english = "You must enter a real date",
        welsh   = "Mae’n rhaid i chi nodi dyddiad go iawn"
      )
      val `Enter a year which contains 4 numbers`: Message = Message(
        english = "Enter a year which contains 4 numbers",
        welsh   = "Nodwch flwyddyn sy’n cynnwys 4 rhif"
      )
      val `Date of birth must be in the past`: Message = Message(
        english = "Date of birth must be in the past",
        welsh   = "Mae’n rhaid i’r dyddiad geni fod yn y gorffennol"
      )

      def `Date of birth must be on or before`(sixteenYearsAgo: String): Message = Message(
        english = s"Date of birth must be on or before $sixteenYearsAgo",
        welsh   = s"Mae’n rhaid i’r dyddiad geni fod ar neu cyn ${sixteenYearsAgo.welshMonth}"
      )

      def `Date of birth must be on or after`(oneHundredAndTenYearsAgo: String): Message = Message(
        english = s"Date of birth must be on or after $oneHundredAndTenYearsAgo",
        welsh   = s"Mae’n rhaid i’r dyddiad geni fod ar neu ar ôl ${oneHundredAndTenYearsAgo.welshMonth}"
      )

      def `Date of birth must include a ...`(incompleteFields: Message): Message = Message(
        english = s"Date of birth must include a ${incompleteFields.english}",
        welsh   = s"Mae’n rhaid i’r dyddiad gynnwys ${incompleteFields.welsh.getOrElse(incompleteFields.english)}"
      )
    }

  }

  object YourIdentityIsConfirmed {
    val `We have confirmed your identity`: Message = Message(
      english = "We have confirmed your identity",
      welsh   = "Rydym wedi cadarnhau pwy ydych"
    )
  }

  object WeCannotConfirmYourIdentity {
    val `We cannot confirm your identity`: Message = Message(
      english = "We cannot confirm your identity",
      welsh   = "Ni allwn gadarnhau pwy ydych"
    )
    val `The information you have provided does not match our records.`: Message = Message(
      english = "The information you have provided does not match our records.",
      welsh   = "Nid yw’r wybodaeth yr ydych wedi’i roi yn cyd-fynd â’n cofnodion."
    )
    val `Try gain`: Message = Message(
      english = "Try again",
      welsh   = "Rhowch gynnig arall arni"
    )
    val `Choose another way to get my refund`: Message = Message(
      english = "Choose another way to get my refund",
      welsh   = "Dewis ffordd arall o gael fy ad-daliad"
    )
    val `Claim your refund by bank transfer`: Message = Message(
      english = "Claim your refund by bank transfer",
      welsh   = "Hawliwch eich ad-daliad drwy drosglwyddiad banc"
    )
  }

  object NoMoreAttemptsLeftToConfirmYourIdentityMessages {
    val `You have entered information that does not match ...`: Message = Message(
      english = "You have entered information that does not match our records too many times. For security reasons, you must wait <strong>24 hours</strong> and then try again.",
      welsh   = "Rydych wedi nodi gwybodaeth sydd ddim yn cyd-fynd â’n cofnodion gormod o weithiau. Am resymau diogelwch, mae’n rhaid i chi aros <strong>24 awr</strong> ac yna rhoi cynnig arall arni."
    )

    def `Alternatively you can sign in to your HMRC online account`(signInLink: String, contactUsLink: String): Message = Message(
      english =
        s"""Alternatively you can <a id="sign-in-to-your-hmrc-online-account" href="$signInLink" class="govuk-link">sign in
           |to your HMRC online account</a> to request your refund. If you continue having problems with confirming your identity,
           |you need to <a id="contact-hmrc" href="$contactUsLink" class="govuk-link">contact us</a>.
           |""".stripMargin,
      welsh   =
        s"""Fel arall, gallwch <a id="sign-in-to-your-hmrc-online-account" href="$signInLink" class="govuk-link">fewngofnodi i’ch cyfrif ar-lein CThEF</a> i
           |ofyn am eich ad-daliad. Os ydych chi’n dal i gael trafferth cadarnhau pwy ydych, mae angen i chi <a id="contact-hmrc" href="$contactUsLink" class="govuk-link">gysylltu â ni</a>.""".stripMargin
    )
  }

  object WhatIsTheNameOfYourBankAccount {
    val `What is the name of your bank?`: Message = Message(
      english = "What is the name of your bank?",
      welsh   = "Beth yw enw’ch banc?"
    )

    val `Start typing the name of a UK bank that you want your refund to be sent to.`: Message = Message(
      english = "Start typing the name of a UK bank that you want your refund to be sent to. The name on your bank account must match the name on your P800 letter.",
      welsh   = "Dechreuwch deipio enw banc yn y DU yr ydych am i’ch ad-daliad gael ei anfon ato."
    )

    val `My bank is not listed`: Message = Message(
      english = "My bank is not listed",
      welsh   = "Nid yw fy manc wedi’i restru"
    )

    val `Select a bank from the list`: Message = Message(
      english = "Select a bank from the list",
      welsh   = "Dewiswch fanc o’r rhestr"
    )
  }

  object ChequeRequestReceived {
    val `Cheque request received`: Message = Message(
      english = "Cheque request received",
      welsh   = "Cais am siec wedi dod i law"
    )

    val `Your P800 reference:`: Message = Message(
      english = "Your P800 reference:",
      welsh   = "Eich cyfeirnod P800:"
    )

    private val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.UK)

    def `Your refund of £x.xx will arrive in the post by DATE.`(amountInPence: AmountInPence, chequeArriveByDate: LocalDate): Message = Message(
      english = s"Your refund of <strong>${amountInPence.gdsFormatInPounds}</strong> will arrive in the post by <strong>${chequeArriveByDate.format(formatter)}</strong>.",
      welsh   = s"Bydd eich ad-daliad o <strong>${amountInPence.gdsFormatInPounds}</strong> yn cyrraedd drwy’r post erbyn <strong>${chequeArriveByDate.format(formatter).welshMonth}</strong>."
    )

    val `Print this page`: Message = Message(
      english = "Print this page",
      welsh   = "Argraffwch y dudalen hon"
    )

    val `What happens next`: Message = Message(
      english = "What happens next",
      welsh   = "Yr hyn sy’n digwydd nesaf"
    )

    val `What do you think`: Message = Message(
      english = "What did you think of this service?",
      welsh   = "Beth oedd eich barn am y gwasanaeth hwn?"
    )

    val `takes 30 seconds`: Message = Message(
      english = "(takes 30 seconds)",
      welsh   = "(mae’n cymryd 30 eiliad)"
    )

    def `If you do not receive your refund you can call or write...`(generalEnquiriesLink: String): Message = Message(
      english =
        s"""If you do not receive your refund you can <a id="general-enquiries-link" target="_blank" class="govuk-link" href="$generalEnquiriesLink">
           |write to us or call the Income Tax helpline (opens in new tab)</a>. You will need your P800 reference.""".stripMargin,
      welsh   =
        s"""Os nad ydych yn cael eich ad-daliad, gallwch <a id="general-enquiries-link" target="_blank" class="govuk-link" href="$generalEnquiriesLink">
           |ysgrifennu atom neu ffonio’r llinell gymorth Treth Incwm (yn agor tab newydd)</a>. Bydd angen eich cyfeirnod P800 arnoch.""".stripMargin
    )

    val `Print If you do not receive your refund you can call or write...`: Message = Message(
      english = "If you do not receive your refund you can call the Income Tax helpline. You will need your P800 reference.",
      welsh   = "Os na fydd eich ad-daliad yn eich cyrraedd, ffoniwch Wasanaeth Cwsmeriaid Cymraeg CThEF. Bydd angen eich cyfeirnod P800 arnoch. "
    )

    val `Telephone:`: Message = Message(
      english = "Telephone:",
      welsh   = "Ffôn:"
    )

    val `0300 200 3300`: Message = Message(
      english = "0300 200 3300",
      welsh   = "0300 200 1900"
    )

    val `Outside UK:`: Message = Message(
      english = "Outside UK:",
      welsh   = ""
    )

    val `+44 135 535 9022`: Message = Message(
      english = "+44 135 535 9022",
      welsh   = ""
    )

    val `Opening times:`: Message = Message(
      english = "Opening times:",
      welsh   = "Oriau agor:"
    )

    val `Monday to Friday: 8am to 6pm`: Message = Message(
      english = "Monday to Friday: 8am to 6pm",
      welsh   = "Dydd Llun i ddydd Gwener: 8:30am i 5pm"
    )

    val `Closed on weekends and bank holidays.`: Message = Message(
      english = "Closed on weekends and bank holidays.",
      welsh   = "Ar gau ar benwythnosau a gwyliau banc."
    )
  }

  object DoYouWantYourRefundViaBankTransfer {

    val `Do you want your refund by bank transfer?`: Message = Message(
      english = "Do you want your refund by bank transfer?",
      welsh   = "A ydych am gael eich ad-daliad drwy drosglwyddiad banc?"
    )

    val `Bank transfers are faster and safer. ...`: Message = Message(
      english = "Bank transfers are faster and safer. You’ll be transferred to your bank to sign in and select the account you want the refund to be sent to.",
      welsh   = "Mae trosglwyddiadau banc yn gyflymach ac yn fwy diogel. Bydd angen i chi gael eich manylion bancio ar-lein neu symudol yn barod."
    )

    val `Yes`: Message = Message(
      english = "Yes",
      welsh   = "Iawn"
    )

    val `No, I want a cheque`: Message = Message(
      english = "No, I want a cheque",
      welsh   = "Na, dwi eisiau siec"
    )

    val `Select if you want to receive a bank transfer or a cheque`: Message = Message(
      english = "Select if you want to receive a bank transfer or a cheque",
      welsh   = "Dewiswch os ydych am gael trosglwyddiad banc neu siec"
    )

  }

  object BankTransferRequestReceived {
    val `Bank transfer request received`: Message = Message(
      english = "Bank transfer request received",
      welsh   = "Cais wedi dod i law am drosglwyddiad banc"
    )

    val `Your P800 reference:`: Message = Message(
      english = "Your P800 reference:",
      welsh   = "Eich cyfeirnod P800:"
    )

    private val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.UK)

    val `Print If you do not receive your refund you can call or write...`: Message = Message(
      english = "If you do not receive your refund you can call the Income Tax helpline. You will need your P800 reference.",
      welsh   = "Os na fydd eich ad-daliad yn eich cyrraedd, ffoniwch Wasanaeth Cwsmeriaid Cymraeg CThEF. Bydd angen eich cyfeirnod P800 arnoch. "
    )

    val `Telephone:`: Message = Message(
      english = "Telephone:",
      welsh   = "Ffôn:"
    )

    val `0300 200 3300`: Message = Message(
      english = "0300 200 3300",
      welsh   = "0300 200 1900"
    )

    val `Outside UK:`: Message = Message(
      english = "Outside UK:",
      welsh   = ""
    )

    val `+44 135 535 9022`: Message = Message(
      english = "+44 135 535 9022",
      welsh   = ""
    )

    val `Opening times:`: Message = Message(
      english = "Opening times:",
      welsh   = "Oriau agor:"
    )

    val `Monday to Friday: 8am to 6pm`: Message = Message(
      english = "Monday to Friday: 8am to 6pm",
      welsh   = "Dydd Llun i ddydd Gwener: 8:30am i 5pm"
    )

    val `Closed on weekends and bank holidays.`: Message = Message(
      english = "Closed on weekends and bank holidays.",
      welsh   = "Ar gau ar benwythnosau a gwyliau banc."
    )

    def `Your refund of £x.xx will now be processed and paid by...`(amountInPence: AmountInPence, date: LocalDate): Message = Message(
      english = s"Your refund of <strong>${amountInPence.gdsFormatInPounds}</strong> will now be paid by <strong>${date.format(formatter)}</strong>.",
      welsh   = s"Bydd eich ad-daliad o <strong>${amountInPence.gdsFormatInPounds}</strong> yn cael ei dalu erbyn <strong>${date.format(formatter).welshMonth}</strong>."
    )

    val `Print this page`: Message = Message(
      english = "Print this page",
      welsh   = "Argraffwch y dudalen hon"
    )

    val `What happens next`: Message = Message(
      english = "What happens next",
      welsh   = "Yr hyn sy’n digwydd nesaf"
    )

    val `What do you think`: Message = Message(
      english = "What did you think of this service?",
      welsh   = "Beth oedd eich barn am y gwasanaeth hwn?"
    )

    val `takes 30 seconds`: Message = Message(
      english = "(takes 30 seconds)",
      welsh   = "(mae’n cymryd 30 eiliad)"
    )

    def `If you do not receive your refund you can call or write...`(generalEnquiriesLink: String): Message = Message(
      english =
        s"""If you do not receive your refund you can <a id="general-enquiries-link" target="_blank" class="govuk-link" href="$generalEnquiriesLink">
           |write to us or call the Income Tax helpline (opens in new tab)</a>. You will need your P800 reference.""".stripMargin,
      welsh   =
        s"""Os nad ydych yn cael eich ad-daliad, gallwch <a id="general-enquiries-link" target="_blank" class="govuk-link" href="$generalEnquiriesLink">
           |ysgrifennu atom neu ffonio’r llinell gymorth Treth Incwm (yn agor tab newydd)</a>. Bydd angen eich cyfeirnod P800 arnoch.""".stripMargin
    )
  }

  object ServicePhase {

    val serviceName: Message = Message(
      english = "Get an Income Tax refund",
      welsh   = "Cael ad-daliad Treth Incwm"
    )

    val serviceNameTestOnly: Message = Message(
      english = "Test Only - Get an Income Tax refund"
    )

    val Beta: Message = Message(
      english = "Beta",
      welsh   = "Beta"
    )

    def bannerText(link: String): Message = Message(
      english = s"""This is a new service – your <a class="govuk-link" href="$link">feedback</a> will help us to improve it.""",
      welsh   = s"""Mae hwn yn wasanaeth newydd – bydd eich <a class="govuk-link" href="$link">adborth</a> yn ein helpu i’w wella."""
    )
  }

  object YourRefundRequestHasNotBeenSubmitted {
    val yourRefundRequest: Message = Message(
      english = "Your refund request has not been submitted",
      welsh   = "Nid yw’ch cais am ad-daliad wedi’i gyflwyno"
    )

    val technicalIssue: Message = Message(
      english = "There has been a technical issue, you can:",
      welsh   = "Mae problem dechnegol wedi codi, gallwch wneud y canlynol:"
    )

    val tryAgain: Message = Message(
      english = "try again",
      welsh   = "rhoi cynnig arall arni"
    )

    val chooseAnother: Message = Message(
      english = "choose another way to get your refund",
      welsh   = "dewiswch ffordd arall o gael fy ad-daliad"
    )

    val calculateTax: Message = Message(
      english = "return to tax calculation letter (P800) guidance ",
      welsh   = "dychwelyd i arweiniad llythyr cyfrifiad treth (P800) "
    )

    val andTryAgain: Message = Message(
      english = "and try again later",
      welsh   = "a rhoi cynnig arall arni yn nes ymlaen"
    )

  }

  object YouCannotConfirmIdentityYet {
    val `You cannot confirm your identity yet`: Message = Message(
      english = "You cannot confirm your identity yet",
      welsh   = "Ni allwch gadarnhau pwy ydych eto"
    )

    def youHavePreviously(date: String): Message = Message(
      english = s"""You have previously entered information that does not match our records too many times. For security reasons you have been locked out. You can try again after <b>$date</b>.""",
      welsh   = s"""Rydych wedi nodi gwybodaeth yn flaenorol sydd ddim yn cyd-fynd â’n cofnodion gormod o weithiau. Am resymau diogelwch, rydych wedi cael eich cloi allan. Gallwch roi cynnig arall arni ar ôl <b>${date.welshMonth}</b>."""
    )

    def alternatively(linkSignIn: String, linkContact: String): Message = Message(
      english = s"""Alternatively you can <a class="govuk-link" href="$linkSignIn">sign in to your HMRC online account</a> to request your refund. If you continue having problems with confirming your identity, you need to <a class="govuk-link" href="$linkContact">contact us</a>.""".stripMargin,
      welsh   = s"""Fel arall, gallwch <a class="govuk-link" href="$linkSignIn">fewngofnodi i’ch cyfrif ar-lein CThEF</a> i ofyn am eich ad-daliad. Os ydych chi’n dal i gael trafferth cadarnhau pwy ydych, mae angen i chi <a class="govuk-link" href="$linkContact">gysylltu â ni</a>.""".stripMargin
    )
  }

  object ClaimYourRefundByBankTransfer {
    val `Claim your refund by bank transfer`: Message = Message(
      english = "Claim your refund by bank transfer",
      welsh   = "Hawliwch eich ad-daliad drwy drosglwyddiad banc"
    )

    val `Choose to sign in using your Government Gateway user ID to claim your refund and you will have fewer details to enter.`: Message = Message(
      english = "Choose to sign in using your Government Gateway user ID to claim your refund and you will have fewer details to enter.",
      welsh   = "Dewiswch fewngofnodi gan ddefnyddio’ch Dynodydd Defnyddiwr (ID) ar gyfer Porth y Llywodraeth i hawlio’ch ad-daliad a bydd gennych lai o fanylion i fynd nodi."
    )

    val `Do you want to sign in?`: Message = Message(
      english = "Do you want to sign in?",
      welsh   = "A ydych am fewngofnodi?"
    )

    val `Yes`: Message = Message(
      english = "Yes",
      welsh   = "Iawn"
    )

    val `No`: Message = Message(
      english = "No",
      welsh   = "Na"
    )

  }

  object ThereIsAProblem {
    val `There is a problem`: Message = Message(
      english = "There is a problem",
      welsh   = "Mae problem wedi codi"
    )

    def `We cannot process your refund request online, so we need you to...`(linkContact: String): Message = Message(
      english = s"""We cannot process your refund request online, so we need you to <a id="contact-hmrc-link" class="govuk-link" href="$linkContact">contact us</a>.""",
      welsh   = s"""Ni allwn brosesu’ch cais am ad-daliad ar-lein, felly mae angen i chi <a id="contact-hmrc-link" class="govuk-link" href="$linkContact">gysylltu â ni</a>.""",
    )

  }

  object RefundCancelled {
    val `Refund cancelled`: Message = Message(
      english = "Refund cancelled",
      welsh   = "Ad-daliad wedi’i ganslo"
    )

    val `You have cancelled`: Message = Message(
      english = "You have cancelled your refund request.",
      welsh   = "Rydych wedi canslo’ch cais am ad-daliad."
    )

    val `No refund`: Message = Message(
      english = "No refund has been sent to your bank account.",
      welsh   = "Ni dderbyniwyd ad-daliad i’ch cyfrif banc."
    )

    val `Choose another`: Message = Message(
      english = "Choose another way to get my refund",
      welsh   = "Dewis ffordd arall o gael fy ad-daliad"
    )

    def `We cannot process your refund request online, so we need you to...`(linkContact: String): Message = Message(
      english = s"""We cannot process your refund request online, so we need you to <a id="contact-hmrc-link" class="govuk-link" href="$linkContact">contact us</a>.""",
      welsh   = s"""Ni allwn brosesu’ch cais am ad-daliad ar-lein, felly mae angen i chi <a id="contact-hmrc-link" class="govuk-link" href="$linkContact">gysylltu â ni</a>."""
    )

  }

  object UpdateYourAddress {
    val `Update your address`: Message = Message(
      english = "Update your address",
      welsh   = "Diweddaru’ch cyfeiriad"
    )

    val `To update your address you need to:`: Message = Message(
      english = "To update your address you need to:",
      welsh   = "I ddiweddaru’ch cyfeiriad mae angen i chi wneud y canlynol:"
    )

    def contactHMRC(contactLink: String): Message = Message(
      english = s"""<a class="govuk-link" id="contact-hmrc-link" href="$contactLink">Contact HMRC</a> to tell us that you have changed address.""".stripMargin,
      welsh   = s"""<a class="govuk-link" id="contact-hmrc-link" href="$contactLink">Cysylltwch â CThEF</a> i roi gwybod i ni eich bod wedi newid cyfeiriad."""
    )

    val `Wait two days for HMRC to update your details.`: Message = Message(
      english = "Wait 2 days for HMRC to update your details.",
      welsh   = "Arhoswch 2 ddiwrnod i CThEF ddiweddaru’ch manylion."
    )

    val `Restart your refund request.`: Message = Message(
      english = "Restart your refund request.",
      welsh   = "Ailgychwynwch eich cais am ad-daliad."
    )

  }

  object IsYourAddressUpToDate {
    val `Is your address up to date?`: Message = Message(
      english = "Is your address up to date?",
      welsh   = "A yw’ch cyfeiriad yn gyfredol?"
    )

    val `Your cheque will be sent to the same address as your tax calculation letter.`: Message = Message(
      english = "Your cheque will be sent to the same address as your tax calculation letter.",
      welsh   = "Bydd eich siec yn cael ei hanfon i’r un cyfeiriad â’ch llythyr cyfrifiad treth."
    )

    val `Confirm and continue`: Message = Message(
      english = "Confirm and continue",
      welsh   = "Cadarnhau ac yn eich blaen"
    )

    val yes: Message = Message(
      english = "Yes",
      welsh   = "Iawn"
    )

    val `No, I need to update it`: Message = Message(
      english = "No, I need to update it",
      welsh   = "Na, mae angen i mi ei ddiweddaru"
    )

    val `Select if your address is up to date`: Message = Message(
      english = "Select if your address is up to date",
      welsh   = "Dewiswch os yw’ch cyfeiriad yn gyfredol"
    )

  }

}
