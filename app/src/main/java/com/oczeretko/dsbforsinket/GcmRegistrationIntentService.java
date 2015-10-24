package com.oczeretko.dsbforsinket;

import android.app.*;
import android.content.*;
import android.preference.*;
import android.support.v4.content.*;
import android.util.*;

import com.google.android.gms.gcm.*;
import com.google.android.gms.iid.*;
import com.microsoft.windowsazure.mobileservices.*;

import java.io.*;
import java.net.*;

public class GcmRegistrationIntentService extends IntentService {

    private static final String TAG = "GcmRegIntentService";

    public GcmRegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String senderId = getString(R.string.gcm_defaultSenderId);
            String token = instanceID.getToken(senderId, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            Log.d(TAG, "GCM Registration Token: " + token);

            sendRegistrationToServer(token);
            sharedPreferences.edit().putBoolean(AppPreferences.SENT_TOKEN_TO_SERVER, true).apply();

        } catch (IOException e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            sharedPreferences.edit().putBoolean(AppPreferences.SENT_TOKEN_TO_SERVER, false).apply();
        }

        Intent registrationComplete = new Intent(AppPreferences.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void sendRegistrationToServer(String token) throws MalformedURLException {
        MobileServiceClient client = new MobileServiceClient("https://dsbforsinket.azure-mobile.net/",
                                                             "zpHixUDmWfMvtyqSjnNSkxwxypDFKu50",
                                                             this);
        client.getPush().register(token, null);
    }
}
