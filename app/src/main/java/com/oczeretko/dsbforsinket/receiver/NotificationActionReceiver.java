package com.oczeretko.dsbforsinket.receiver;

import android.app.*;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.*;
import android.preference.*;
import android.text.format.*;
import android.util.*;

import com.oczeretko.dsbforsinket.*;
import com.oczeretko.dsbforsinket.utils.*;

public class NotificationActionReceiver extends BroadcastReceiver {

    private static final String ACTION_SNOOZE = "com.oczeretko.dsbforsinket.receiver.ACTION_SNOOZE";
    private static final String TAG = "NotifActionRec";

    public static PendingIntent getSnoozeIntent(Context context) {
        return PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SNOOZE), PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "received intent");
        if (intent == null || intent.getAction() == null) {
            return;
        }

        switch (intent.getAction()) {
            case ACTION_SNOOZE:
                PushNotifications.snooze(context);
                PushNotifications.cancel(context);
                break;
        }
    }
}
