package com.oczeretko.dsbforsinket.gcm;

import android.app.*;
import android.content.*;
import android.media.*;
import android.net.*;
import android.os.*;
import android.support.v4.app.*;
import android.support.v4.content.*;
import android.util.*;

import com.google.android.gms.gcm.*;
import com.oczeretko.dsbforsinket.R;
import com.oczeretko.dsbforsinket.activity.*;

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
                                                             .setSmallIcon(getNotificationIcon())
                                                             .setContentTitle(getString(R.string.app_name))
                                                             .setContentText(message)
                                                             .setAutoCancel(true)
                                                             .setSound(defaultSoundUri)
                                                             .setColor(ContextCompat.getColor(this, R.color.colorAccent))
                                                             .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private int getNotificationIcon() {
        boolean whiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return whiteIcon ? R.drawable.ic_notification : R.mipmap.ic_launcher;
    }
}
