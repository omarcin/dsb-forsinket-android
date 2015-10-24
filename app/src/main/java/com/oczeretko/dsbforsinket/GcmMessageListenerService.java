package com.oczeretko.dsbforsinket;

import android.app.*;
import android.content.*;
import android.media.*;
import android.net.*;
import android.os.*;
import android.support.v4.app.*;
import android.util.*;

import com.google.android.gms.gcm.*;

public class GcmMessageListenerService extends GcmListenerService {

    private static final String TAG = "GcmMessageListenerServ";

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);
        sendNotification(message);
    }

    private void sendNotification(String message) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                                                                0 /* Request code */,
                                                                intent,
                                                                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                                                             .setSmallIcon(R.mipmap.ic_launcher)
                                                             .setContentTitle("GCM Message")
                                                             .setContentText(message)
                                                             .setAutoCancel(true)
                                                             .setSound(defaultSoundUri)
                                                             .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
