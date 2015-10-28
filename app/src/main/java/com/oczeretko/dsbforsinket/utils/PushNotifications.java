package com.oczeretko.dsbforsinket.utils;

import android.app.*;
import android.content.*;
import android.preference.*;
import android.text.format.*;
import android.util.*;

import com.oczeretko.dsbforsinket.*;
import com.oczeretko.dsbforsinket.receiver.*;

public final class PushNotifications {

    public static final int NOTIFICATION_ID = R.string.notification_delay_id;
    private static final String TAG = "PushNotifications";

    public static PendingIntent getSnoozeIntent(Context context) {
        return NotificationActionReceiver.getSnoozeIntent(context);
    }

    public static void notify(Context context, Notification notification) {
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(PushNotifications.NOTIFICATION_ID, notification);
    }

    public static void cancel(Context context) {
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(PushNotifications.NOTIFICATION_ID);
    }

    public static void snooze(Context context) {
        PreferenceManager.getDefaultSharedPreferences(context)
                         .edit()
                         .putLong(Consts.PREF_LAST_SNOOZE_MILLIS, System.currentTimeMillis())
                         .apply();
    }

    public static boolean isSnoozed(Context context) {
        int snoozeTimeMinutes = context.getResources().getInteger(R.integer.notification_snooze_time_minutes);
        long snoozeTimeMillis = DateUtils.MINUTE_IN_MILLIS * snoozeTimeMinutes;

        long lastSnooze = PreferenceManager.getDefaultSharedPreferences(context)
                                           .getLong(Consts.PREF_LAST_SNOOZE_MILLIS, 0);
        long sinceLastSnoozeMillis = System.currentTimeMillis() - lastSnooze;
        return sinceLastSnoozeMillis < snoozeTimeMillis;
    }
}
