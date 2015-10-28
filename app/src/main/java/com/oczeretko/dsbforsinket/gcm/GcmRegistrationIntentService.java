package com.oczeretko.dsbforsinket.gcm;

import android.app.*;
import android.content.*;
import android.preference.*;
import android.support.v4.app.*;
import android.support.v4.content.*;
import android.util.*;

import com.google.android.gms.gcm.*;
import com.google.android.gms.iid.*;
import com.oczeretko.dsbforsinket.*;
import com.oczeretko.dsbforsinket.R;
import com.oczeretko.dsbforsinket.activity.*;

public class GcmRegistrationIntentService extends IntentService {

    private static final String TAG = "GcmRegIntentService";
    private static final int NOTIFICATION_ID = R.string.notification_gcm_id;

    private static final String KEY_STATION_ID = "stationid";
    private static final String KEY_TIMES = "times";
    private static final String KEY_ACTION = "action";
    private static final int ACTION_REGISTER = 1;
    private static final int ACTION_DEREGISTER = 2;

    public static void requestRegistration(Context context, String station, String[] times) {
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

        startForeground(NOTIFICATION_ID, buildNotification());

        if (action == ACTION_REGISTER) {
            String station = intent.getStringExtra(KEY_STATION_ID);
            String[] times = intent.getStringArrayExtra(KEY_TIMES);
            register(station, times);
        } else {
            deregister();
        }

        stopForeground(true);
        Intent registrationComplete = new Intent(Consts.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void register(String station, String[] times) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        sharedPreferences.edit()
                         .putBoolean(Consts.PREF_POSSIBLY_REGISTERED, true)
                         .apply();
        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String senderId = getString(com.oczeretko.dsbforsinket.R.string.gcm_defaultSenderId);
            String token = instanceID.getToken(senderId, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            Log.d(TAG, "GCM Registration Token: " + token);

            DsbMobileServiceClient client = new DsbMobileServiceClient(this);
            client.register(token, station, times);

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

            DsbMobileServiceClient client = new DsbMobileServiceClient(this);
            client.unregister();

            sharedPreferences.edit()
                             .putBoolean(Consts.PREF_SENT_TOKEN_TO_SERVER, false)
                             .putBoolean(Consts.PREF_POSSIBLY_REGISTERED, false)
                             .apply();

        } catch (Exception e) {
            Log.d(TAG, "Failed to deregister", e);
            // TODO: handle
        }
    }

    private Notification buildNotification() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Builder(getApplicationContext())
                   .setOngoing(true)
                   .setCategory(Notification.CATEGORY_SERVICE)
                   .setOnlyAlertOnce(true)
                   .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                   .setSmallIcon(getNotificationIcon())
                   .setContentTitle(getString(R.string.app_name))
                   .setContentText(getString(R.string.notification_gcm_content))
                   .setContentIntent(pendingIntent)
                   .build();
    }

    private int getNotificationIcon() {
        boolean whiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return whiteIcon ? R.drawable.ic_notification : R.mipmap.ic_launcher;
    }
}
