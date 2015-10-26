package com.oczeretko.dsbforsinket.gcm;

import android.app.*;
import android.content.*;
import android.preference.*;
import android.support.v4.content.*;
import android.util.*;

import com.google.android.gms.gcm.*;
import com.google.android.gms.iid.*;
import com.google.common.util.concurrent.*;
import com.microsoft.windowsazure.mobileservices.*;
import com.microsoft.windowsazure.mobileservices.notifications.*;
import com.oczeretko.dsbforsinket.*;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class GcmRegistrationIntentService extends IntentService {

    private static final String TAG = "GcmRegIntentService";

    private static final String[] REGISTRATION_TAGS = { "time-8:00", "station-8600856", "8600856-8:00" };

    public GcmRegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String senderId = getString(com.oczeretko.dsbforsinket.R.string.gcm_defaultSenderId);
            String token = instanceID.getToken(senderId, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            Log.d(TAG, "GCM Registration Token: " + token);

            sendRegistrationToServer(token);
            sharedPreferences.edit().putBoolean(AppPreferences.SENT_TOKEN_TO_SERVER, true).apply();

        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            sharedPreferences.edit().putBoolean(AppPreferences.SENT_TOKEN_TO_SERVER, false).apply();
        }

        Intent registrationComplete = new Intent(AppPreferences.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void sendRegistrationToServer(String token) throws ExecutionException, InterruptedException, MalformedURLException {
        MobileServiceClient client = new MobileServiceClient(Consts.APP_URL, Consts.APP_KEY, this);
        client.getPush().register(token, REGISTRATION_TAGS).get();
    }
}
