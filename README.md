
# :construction: p800-refunds-frontend

This service allows taxpayers to request refunds for overpaid tax.

The application is currently being developed.


# Project setup in intellij

![img.png](readme/intellij-sbt-setup.png)

## Project specific sbt commands

### Turn off strict building

In sbt command in intellij:
```
sbt> relax
```
This will turn off strict building for this sbt session.
When you restart it or you build on jenkins, this will be turned on.

### Run with test only endpoints

```
sbt> runTestOnly
```


## Application architecture

### Journey States
Journey states correspond to the result of the submission on pages (or endpoints).

```mermaid

flowchart TD

    govukpage[\Claim an income tax refund a gov.uk page\] 
    -- User clicked `/start` link on gov.uk page.
    A new journey is created and user is navigated to the JourneyDoYouWantToSignIn page
    --> JourneyStarted   

    JourneyStarted 
    -- Answered `Yes` 
    on DoYouWantToSignIn page 
    --> JourneyDoYouWantToSignInYes

    JourneyStarted 
    -- Answered `No` 
    on DoYouWantToSignIn page 
    --> JourneyDoYouWantToSignInNo

    JourneyDoYouWantToSignInNo 
    -- entered semivalid reference 
    on WhatIsYourP800Reference page 
    --> JourneyWhatIsYourP800Reference

    JourneyWhatIsYourP800Reference
    -- selected `Yes` 
    on CheckYourReference page
    --> ValidateReferenceApiCall{
        API Call 
        Check Rererence 
    }

    ValidateReferenceApiCall 
    -- valid
    --> JourneyCheckYourReferenceValid

    ValidateReferenceApiCall
    -- not valid
    --> JourneyCheckYourReferenceInvalid

    JourneyCheckYourReferenceInvalid
    -- clicked `Try again` 
    on WeCannotConfirmYourReference Page 
    and navigated to WhatIsYourP800Reference page
    --> JourneyWhatIsYourP800Reference
    
    
    JourneyCheckYourReferenceValid
    -- selected `Yes` 
    on DoYouWantYourRefundViaBankTransfer page
    --> JourneyDoYouWantYourRefundViaBankTransferYes

    JourneyCheckYourReferenceValid
    -- selected `No` 
    on DoYouWantYourRefundViaBankTransfer page
    --> JourneyDoYouWantYourRefundViaBankTransferNo

    JourneyDoYouWantYourRefundViaBankTransferNo
    -- clicked `Continue`
    on YourChequeWillBePostedToYou page
    --> JourneyYourChequeWillBePostedToYou

    JourneyDoYouWantYourRefundViaBankTransferYes 
    -- tbc ...
    --> TBC...
```



### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
