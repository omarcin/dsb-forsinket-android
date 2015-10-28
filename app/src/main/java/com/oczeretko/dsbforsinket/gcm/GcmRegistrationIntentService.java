package com.oczeretko.dsbforsinket.gcm;

import android.app.*;
import android.content.*;
import android.preference.*;
import android.support.annotation.*;
import android.support.v4.content.*;
import android.util.*;

import com.google.android.gms.gcm.*;
import com.google.android.gms.iid.*;
import com.microsoft.windowsazure.mobileservices.*;
import com.oczeretko.dsbforsinket.*;

import java.util.*;

public class GcmRegistrationIntentService extends IntentService {

    private static final String TAG = "GcmRegIntentService";

    private static final String KEY_STATION_ID = "stationid";
    private static final String KEY_TIMES = "times";
    private static final String KEY_ACTION = "action";
    private static final int ACTION_REGISTER = 1;
    private static final int ACTION_DEREGISTER = 2;

    private static final String SEPARATOR = "-";
    private static final String PREFIX_STATION = "station" + SEPARATOR;
    private static final String PREFIX_TIME = "time" + SEPARATOR;

    private static final String[] REGISTRATION_TAGS = {"time-8:00", "station-8600856", "8600856-8:00"};

    public static void requestRegistration(Context context, String station, String[] times) {
        if (times.length == 0) {
            return;
        }

        Intent intent = new Intent(context, GcmRegistrationIntentService.class);
        intent.putExtra(KEY_STATION_ID, station);
        intent.putExtra(KEY_TIMES, times);
        intent.putExtra(KEY_ACTION, ACTION_REGISTER);
        context.startService(intent);
    }

    public static void requestDeregistration(Context context) {
        Intent intent = new Intent(context, GcmRegistrationIntentService.class);
        intent.putExtra(KEY_ACTION, ACTION_DEREGISTER);
        context.startService(intent);
    }

    public GcmRegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "received intent");

        int action = intent.getIntExtra(KEY_ACTION, ACTION_DEREGISTER);

        if (action == ACTION_REGISTER) {
            String station = intent.getStringExtra(KEY_STATION_ID);
            String[] times = intent.getStringArrayExtra(KEY_TIMES);
            register(station, times);
        } else {
            deregister();
        }

        Intent registrationComplete = new Intent(Consts.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void register(String station, String[] times) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String[] tags = createRegistrationTags(station, times);

        sharedPreferences.edit()
                         .putBoolean(Consts.PREF_POSSIBLY_REGISTERED, true)
                         .apply();
        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String senderId = getString(com.oczeretko.dsbforsinket.R.string.gcm_defaultSenderId);
            String token = instanceID.getToken(senderId, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            Log.d(TAG, "GCM Registration Token: " + token);

            MobileServiceClient client = new MobileServiceClient(Consts.APP_URL, Consts.APP_KEY, this);
            client.getPush().register(token, tags).get();

            sharedPreferences.edit().putBoolean(Consts.PREF_SENT_TOKEN_TO_SERVER, true).apply();

        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            sharedPreferences.edit().putBoolean(Consts.PREF_SENT_TOKEN_TO_SERVER, false).apply();
            // TODO: handle
        }
    }

    private void deregister() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String senderId = getString(com.oczeretko.dsbforsinket.R.string.gcm_defaultSenderId);
            instanceID.deleteToken(senderId, GoogleCloudMessaging.INSTANCE_ID_SCOPE);

            MobileServiceClient client = new MobileServiceClient(Consts.APP_URL, Consts.APP_KEY, this);
            client.getPush().unregister().get();

            sharedPreferences.edit()
                             .putBoolean(Consts.PREF_SENT_TOKEN_TO_SERVER, false)
                             .putBoolean(Consts.PREF_POSSIBLY_REGISTERED, false)
                             .apply();

        } catch (Exception e) {
            Log.d(TAG, "Failed to deregister", e);
            // TODO: handle
        }
    }

    @NonNull
    private String[] createRegistrationTags(String station, String[] times) {
        ArrayList<String> tagList = new ArrayList<>(2 * times.length + 1);
        tagList.add(PREFIX_STATION + station);
        for (String time : times) {
            tagList.add(PREFIX_TIME + time);
            tagList.add(station + SEPARATOR + time);
        }
        return tagList.toArray(new String[0]);
    }
}
