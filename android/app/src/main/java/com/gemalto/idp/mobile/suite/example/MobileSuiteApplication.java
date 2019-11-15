package com.gemalto.idp.mobile.suite.example;

import android.app.Application;
import android.content.Context;

import com.gemalto.idp.mobile.suite.MobileSuite;
import com.gemalto.idp.mobile.suite.messenger.TransactionSignatureKey;

import java.util.Collections;

/**
 * This class will help to keep the instance of the main activity without having it as a singleton to avoid any memory
 * leaks.
 */
public class MobileSuiteApplication extends Application {

    public static String FULL_ACTIVATION_CODE = "YOUR_ACTIVATION_CODE";

    @Override
    public void onCreate() {
        super.onCreate();

        // 1.1 required for Dexguard decryption
        System.setProperty("java.io.tmpdir", getDir("files", Context.MODE_PRIVATE).getPath());

        TransactionSignatureKey signKey = new TransactionSignatureKey(
                MessengerConfigurations.getSignaturePublicKey(),
                MessengerConfigurations.getSignatureP(),
                MessengerConfigurations.getSignatureQ(),
                MessengerConfigurations.getSignatureG()
        );

        // 1.2 This is entry point for initialization the Mobile Suite API
        new MobileSuite.Builder(this.getApplicationContext())
                .withActivationCode(FULL_ACTIVATION_CODE)
                .withMspObfuscationKeys(Collections.singletonList(MessengerConfigurations.getObfuscationKey()))
                .withMspVerificationKeys(Collections.singletonList(signKey))
                .build();
    }
}
