// ==UserScript==
// @name         P800 refunds quick journey
// @namespace    http://tampermonkey.net/
// @version      2023-12-12
// @description  allow fast track through journey pages - no more typing!
// @author       jbarnes
// @match        *://*/get-an-income-tax-refund*
// @downloadURL  https://raw.githubusercontent.com/hmrc/p800-refunds-frontend/main/tampermonkey/quickJourney.js
// @updateURL    https://raw.githubusercontent.com/hmrc/p800-refunds-frontend/main/tampermonkey/quickJourney.js
// @grant        none
// ==/UserScript==

(function() {
    'use strict'
    document.body.appendChild(setup())
})()


function setup() {
    let panel = document.createElement('div')
    panel.style.position = 'absolute'
    panel.style.top = '50px'
    panel.style.lineHeight = '200%'
    panel.appendChild(createQuickButton())

    return panel
}

function createQuickButton() {
    let button = document.createElement('button')
    button.id='quickSubmit'

    if (!!document.getElementById('global-header')) {
        button.classList.add('button-start', 'govuk-!-display-none-print')
    } else {
        button.classList.add('govuk-button','govuk-!-display-none-print')
    }

    button.innerHTML = 'Quick Submit'
    button.onclick = () => continueJourney()

    return button
}

const currentPageIs = (path) => {
    return window.location.pathname.match(RegExp(path))
}

const clickContinue = () => {
    let continueButton = document.getElementById('submit');
    if(continueButton) {
        continueButton.click()
    }
    else {
        document.getElementsByClassName('govuk-button')[0].click()
    }
}

/* ########################  p800 refunds pages  ######################## */

const doYouWantToSignIn = () => {
    if (currentPageIs('/get-an-income-tax-refund/do-you-want-to-sign-in')) {
        document.getElementById('sign-in-2').checked = true
        clickContinue()
    }
}

const whatIsYourP800Reference = () => {
    if (currentPageIs('/get-an-income-tax-refund/enter-P800-reference')) {
        document.getElementById('reference').value = 'ABCDEFG'
        clickContinue()
    }
}

const checkYourReference = () => {
    if (currentPageIs('/get-an-income-tax-refund/check-your-reference')) {
        document.getElementById('reference-check').checked = true
        clickContinue()
    }
}

const doYouWantARefundByBankTransfer = () => {
    if (currentPageIs('/get-an-income-tax-refund/do-you-want-your-refund-via-bank-transfer')) {
        document.getElementById('do-you-want-your-refund-via-bank-transfer').checked = true
        clickContinue()
    }
}

const weNeedToConfirmYourIdentity = () => {
    if (currentPageIs('/get-an-income-tax-refund/we-need-you-to-confirm-your-identity')) {
        clickContinue()
    }
}

const whatIsYourFullName = () => {
    if (currentPageIs('/get-an-income-tax-refund/what-is-your-full-name')) {
        document.getElementById('fullName').value = 'Bob Ross'
        clickContinue()
    }
}

const whatIsYourDob = () => {
    if (currentPageIs('/get-an-income-tax-refund/what-is-your-date-of-birth')) {
        document.getElementById('date.day').value = '1'
        document.getElementById('date.month').value = '1'
        document.getElementById('date.year').value = '1993'
        clickContinue()
    }
}

const whatIsYourNino = () => {
    if (currentPageIs('/get-an-income-tax-refund/what-is-your-national-insurance-number')) {
        document.getElementById('nationalInsuranceNumber').value = 'LM001014C'
        clickContinue()
    }
}

const checkYourAnswers = () => {
    if (currentPageIs('/get-an-income-tax-refund/check-your-answers')) {
        clickContinue()
    }
}

const idenityConfirmed = () => {
    if (currentPageIs('/get-an-income-tax-refund/we-have-confirmed-your-identity')) {
        clickContinue()
    }
}



/* ########################     MAIN FUNCTION     ########################## */
function continueJourney() {
    doYouWantToSignIn()
    whatIsYourP800Reference()
    checkYourReference()
    doYouWantARefundByBankTransfer()
    weNeedToConfirmYourIdentity()
    whatIsYourFullName()
    whatIsYourDob()
    whatIsYourNino()
    checkYourAnswers()
    idenityConfirmed()
}