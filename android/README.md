# Example Project Application for Android

## Introduction

Mobile Suite Example is a sample Android application designed to demonstrate
the basics of creating an Android application based on the Mobile Suite SDK.

The application demonstrates the following behavior:

* How to create a Token Device (requires a configured EPS).
* How to list tokens.
* How to generate an OTP that can be verified by a server.
* How to register and unregister Secure Messenger client.
* How to fetch and send a message.

## Prerequisites

* A Linux, macOS or Windows development platform
* Android Studio 3.0+
* Android 4.4 or above with at least one platform target setup.
* Ezio Mobile SDK library
* Mobile Suite SDK library

## Setup the project

* Update the `targetSdkVersion` to the Android SDK version you are using(the setting is in `./app/build.gradle`).
* Update the Ezio Mobile and Mobile Suite SDK root directory by modifying below line inside `app/build.gradle` file

  ``` groovy
  // Modify this absolute path according to your own path environment.
  project.ext {
      distDir = "<path-to-package>/android"
  }
  ```

  **Note**: the 'Debug' version of the library should be used in case Tls is configured to allow insecure connection (http), hostname mismatch or self-signed certificate.

* Include the `libjnidispatch.so` shared library for all the Android ABIs that your project supports.
  * Navigate to [JNA libraries](https://github.com/java-native-access/jna/releases).
  * Under Version 4.5.0, download the zip archive
  * Unzip the package, navigate to `jna-4.5.0/dist/` directory. `libjnidispatch.so` for different ABIs can be extracted from respective jar file. The mapping is as below illustrated in below table.

    | JNA ABI             | Android ABI   |
    | ------------------- | ------------- |
    | android-aarch64.jar | arm64-v8a     |
    | android-armv7.jar   | armeabi-v7a   |
    | android-x86-64.jar  | x86_64        |
    | android-x86.jar     | x86           |

  * Put the `libjnidispatch.so` into the mapped Android ABI folder, for example, `arm64-v8a`, `armeabi-v7a`, `x86` and `x86_64`.

## Open the example project

* Launch Android Studio
* Select *`Open an existing Android Studio project`*
* Select the location of the Mobile Suite SDK Example
* Click on OK
* Click *`Sync Project with Gradle Files`* icon in the tool bar if the project to not automatically synchronized.

## Configure the App

The application will not work as provided.  It first must be setup to be
provisioned with the user credentials.  To enable this, perform the following
steps:

* Perform an *`Enrollment`* operation in the EPS to get the PIN and
  registration code. For OOB, perform a *`Registration`* operation in the OOBS to get the client id and registration code.
  See Appendix for how to do this in a test environment.
* Edit `MobileSuiteApplication.java` to add the activationCode to enable the Mobile Messenger feature.
* Edit `ProtectorConfigurations.java` to add your EPS settings.
* Edit `MessengerConfigurations.java` to add your OOBS settings.

The application demonstrates the following Mobile Suite SDK features:
* Mobile Protector
    * How to create a token device.
    * How to generate OTP by PIN.
    * How to change the PIN of token device.
    * How to activate and deactivate token device for Biometric usage.
    * How to generate OTP by Biometric.
    * How to list a token.
    * How to remove a token.
* Mobile Messenger
    * How register a client to OOB server.
    * How to fetch a message.
    * How to acknowledge a message.
    * How to respond to a Transaction Verification message.
    * How to respond to a Transaction Signing message using OCRA OTP.
    * How to unregister the client.

## Run the Example App
It is recommended to uninstall all related example applications installed
previously before running this App, or removed the tokens generated by those Apps.

### Mobile Protector
1. Retrieved the registration code and PIN from EPS through enrollment process.
2. Create a token with entering the registration code and pressing `Provision` button.
   After the token is created, the token name will appear on the drop down list.
3. Press `OTP with PIN` button to generate OTP by using PIN.
4. Change the PIN with user PIN with pressing `Change PIN` button.
5. To be able to generate the OTP using Biometric, press `Activate Biometric` button. The biometric fingerprint is required be enrolled on the device. Press `OTP with Biometric` afterwards.
6. Press `Activate Biometric` to disable the OTP generation using Biometric.
7. Press `List TokenDevice` to list all the token device available on the device.
8. To remove the token, press `Remove Token`. To be able to generate the OTP again, it is required to do another provisoning with different registration code.

### Mobile Messenger
1. Do the registration on OOB Server to retrieved the an oob registration code.
2. Register the client with entering the oob registration code.
3. Dispatch a message from Bank Server to OOB Server.
4. Fetch the message

    There are 3 types of messages which could be received by the client on this example application, 
    for more details refer to OOB Integration Guide :

    1. Generic message: A normal message. This message will ask the user whether to acknowledge or ignore it.
    2. Transaction verify message: A confirmation message to ask the user whether to accept or reject the transaction.
    3. Transaction signing message: A signing message, similar with Trasaction verify message but when user accept the message,
       user is required to enter the pin of the token device and the otp will be generated and send to server.
       This message type is required to have an OCRA Token Device available. It could be done by doing OCRA provisioning on Mobile Protector.
    
5. Unregister the client from OOB Server

## Appendix

1. EPS Enrollment

   NOTE: This only serves as a quick reference.  See the EPS 2.X Integration Guide for further details.

   The following URL can be used to request the EPS to enroll the user with a new device.  

   ``` url
   https://localhost/enroller/api/enrollment/oath/enroll
   ```

   However, the following must be modified:

   * The path up to `https://localhost/` must be changed to point to your EPS.

   The following JSON payload must be sent as the data portion of the HTTP POST.

   ``` json
   {
        "format": "5.0",
        "authorization": {
            "userId": "your_user_id"
        },
        "body": {
            "keyProtections": [
                {
                    "name": "pin"
                }
            ],
            "tokens": [
                {
                    "name": "seed",
                    "expirationDate": "2032-01-01T12:00:00Z",
                    "activationState": "ACTIVE",
                    "manufacturerUniqueId": "your_8_digit_manufacturer_id",
                    "extension": {
                        "authserver.sas.oath.key": "your_oath_key_in_base64",
                        "authserver.sas.oathDeviceType": "your_oath_device_type_in_base64",
                        "authserver.sas.device.type": "your_device_type_in_base64",
                        "authserver.sas.oath.policy": "your_oath_policy_in_base64"
                    }
                }
            ]
        }
    }
   ```



   However, the following must be modified:

   * userId - The name of the user to enroll
   * userTokenId - The token ID (must be unique for the user)
   * domain - The EPS domain for OATH TOTP
   * key (see externalProvisioningMeta -> propertyEntry) - Additional provisioning information depending on the verification server. See the EPS 2.X Integration Guide for further details.
   * value (see externalProvisioningMeta -> propertyEntry) - Additional provisioning information depending on the verification server. See the EPS 2.X Integration Guide for further details.

   Also note that

   * The `Content-Type` must be set to `application/xml`
   * The response will contain the PIN and registration code if successful.

   Example:

   ``` bash
   wget --header="Content-Type: application/xml" --post-data "<data>" <url> -O -
   ```

2. OOB Server Registration

   NOTE: This only serves as a quick reference.  See the OOBS Integration Guide for further details.

   The following URL can be used to request the OOBS to register the user with a new device.  

   ``` html
   https://localhost/oobs-dispatcher/domains/default/users/my-user/applications/oobsTest/register
   ```

   However, the following must be modified:

   * The path up to `https://localhost/` must be changed to point to your OOBS.
   * Make sure the domain('default') and the application('oobsTest') has been properly setup on server. 

   Please refer to the OOBS integration guide for more details. If the domain name and application name are changed, 'default' and 'oobsTest' must be replaced with the new domain name and application name respectively.

   The following XML payload must be sent as the data portion of the HTTP POST.

   ``` xml
   <?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>
    <RegisterRequest xmlns=\"http://gemalto.com/ipms/dispatcher/api/transport\" >
     <registrationSecurityMethod>REG_CODE</registrationSecurityMethod>
     <registrationCode>TODO</registrationCode>
     <validityPeriodSecs>600</validityPeriodSecs>
     <notificationProfiles />
    </RegisterRequest>
   ```

   However, the following must be modified:
   * registrationCode - The registration code. OOBS should return the same registration code if request succeed.
   * The "Content-Type" must be set to "application/xml"
   * The "BASIC Authorization" is used.
   * The response will contain the registration code and client Id if successful.

   Example:

   ``` bash
    wget --header="Authorization: Basic XXXXXXXXXXXXXXXX" --header="Content-Type: application/xml" --post-data "<data>" <url> -O -
   ```

   *XXXXXXXXXXXXXXXX should be replaced with your "user name:password" String in base-64 format.