package com.oczeretko.dsbforsinket.gcm;

import android.app.*;
import android.content.*;
import android.media.*;
import android.net.*;
import android.preference.*;
import android.support.v4.app.*;
import android.support.v4.content.*;
import android.util.*;

import com.google.android.gms.gcm.*;
import com.google.android.gms.iid.*;
import com.oczeretko.dsbforsinket.*;
import com.oczeretko.dsbforsinket.R;
import com.oczeretko.dsbforsinket.activity.*;
import com.oczeretko.dsbforsinket.data.*;
import com.oczeretko.dsbforsinket.receivers.*;

import io.realm.*;

public class GcmRegistrationIntentService extends IntentService {

    private static final String TAG = "GcmRegIntentService";
    private static final int NOTIFICATION_ID = R.string.notification_gcm_id;
    private static final int NOTIFICATION_FAILURE_ID = R.string.notification_gcm_failure_id;

    private static final String KEY_STATION_ID = "stationid";
    private static final String KEY_TIMES = "times";
    private static final String KEY_ACTION = "action";

    private static final int ACTION_ERROR = -1;
    private static final int ACTION_REGISTER = 1;
    private static final int ACTION_DEREGISTER = 2;

    private SharedPreferences sharedPreferences;

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

    private static void requestErrorNotification(Context context) {
        Intent intent = new Intent(context, GcmRegistrationIntentService.class);
        intent.putExtra(KEY_ACTION, ACTION_ERROR);
        context.startService(intent);
    }

    public GcmRegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int action = intent.getIntExtra(KEY_ACTION, ACTION_DEREGISTER);
        Log.d(TAG, "onHandleIntent: " + action);

        if (action == ACTION_ERROR) {
            handleRegistrationError();
            return;
        }

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isInUnhandledErrorState = sharedPreferences.getBoolean(Consts.PREF_UNHANDLED_REGISTRATION_ERROR, false);

        if (isInUnhandledErrorState) {
            return;
        }

        startForeground(NOTIFICATION_ID, buildNotification());
        NotificationManagerCompat.from(this).cancel(NOTIFICATION_FAILURE_ID);
        RegistrationBroadcastReceiver.sendRegistrationStartedBroadcast(this);

        switch (action) {
            case ACTION_REGISTER:
                String station = intent.getStringExtra(KEY_STATION_ID);
                String[] times = intent.getStringArrayExtra(KEY_TIMES);
                register(station, times);
                break;
            case ACTION_DEREGISTER:
                deregister();
                break;
        }

        stopForeground(true);
        RegistrationBroadcastReceiver.sendRegistrationCompleteBroadcast(this);
    }

    private void register(String station, String[] times) {

        sharedPreferences.edit().putBoolean(Consts.PREF_POSSIBLY_REGISTERED, true).apply();

        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String senderId = getString(com.oczeretko.dsbforsinket.R.string.gcm_defaultSenderId);
            String token = instanceID.getToken(senderId, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            DsbMobileServiceClient client = new DsbMobileServiceClient(this);
            client.register(token, station, times);

            sharedPreferences.edit().putBoolean(Consts.PREF_REGISTRATION_ERROR, false).apply();

        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);

            sharedPreferences.edit()
                             .putBoolean(Consts.PREF_REGISTRATION_ERROR, true)
                             .putBoolean(Consts.PREF_UNHANDLED_REGISTRATION_ERROR, true)
                             .putBoolean(Consts.PREF_POSSIBLY_REGISTERED, true)
                             .apply();

            Realm realm = Realm.getInstance(this);
            StationPreference previousStation = realm.where(StationPreference.class).equalTo("notificationEnabled", true).findFirst();
            if (previousStation != null) {
                realm.beginTransaction();
                previousStation.setNotificationEnabled(false);
                realm.commitTransaction();
            }
            realm.close();

            requestErrorNotification(this);
        }
    }

    private void deregister() {

        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String senderId = getString(com.oczeretko.dsbforsinket.R.string.gcm_defaultSenderId);
            instanceID.deleteToken(senderId, GoogleCloudMessaging.INSTANCE_ID_SCOPE);

            DsbMobileServiceClient client = new DsbMobileServiceClient(this);
            client.unregister();

            sharedPreferences.edit()
                             .putBoolean(Consts.PREF_POSSIBLY_REGISTERED, false)
                             .putBoolean(Consts.PREF_REGISTRATION_ERROR, false)
                             .apply();

        } catch (Exception e) {
            Log.d(TAG, "Failed to deregister", e);
            sharedPreferences.edit()
                             .putBoolean(Consts.PREF_POSSIBLY_REGISTERED, true)
                             .putBoolean(Consts.PREF_REGISTRATION_ERROR, true)
                             .apply();
            // will retry to deregister when the next push message is received
        }
    }

    private void handleRegistrationError() {
        sharedPreferences.edit()
                         .putBoolean(Consts.PREF_UNHANDLED_REGISTRATION_ERROR, false)
                         .apply();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFICATION_FAILURE_ID, buildFailureNotification());
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

    private Notification buildFailureNotification() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_SHOW_SETTINGS, true);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        return new NotificationCompat.Builder(getApplicationContext())
                   .setCategory(Notification.CATEGORY_SERVICE)
                   .setOnlyAlertOnce(true)
                   .setAutoCancel(true)
                   .setColor(ContextCompat.getColor(this, R.color.colorError))
                   .setSound(defaultSoundUri)
                   .setSmallIcon(getNotificationIcon())
                   .setContentTitle(getString(R.string.app_name))
                   .setContentText(getString(R.string.notification_gcm_failure_content))
                   .setContentIntent(pendingIntent)
                   .build();
    }

    private int getNotificationIcon() {
        boolean whiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return whiteIcon ? R.drawable.ic_notification : R.mipmap.ic_launcher;
    }
}
