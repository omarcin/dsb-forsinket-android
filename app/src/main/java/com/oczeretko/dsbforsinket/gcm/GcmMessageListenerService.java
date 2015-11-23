package com.oczeretko.dsbforsinket.gcm;

import android.app.*;
import android.content.*;
import android.media.*;
import android.net.*;
import android.os.*;
import android.support.v4.app.*;
import android.support.v4.content.*;
import android.text.*;
import android.util.*;

import com.google.android.gms.gcm.*;
import com.oczeretko.dsbforsinket.R;
import com.oczeretko.dsbforsinket.activity.*;
import com.oczeretko.dsbforsinket.utils.*;

import java.util.*;

public class GcmMessageListenerService extends GcmListenerService {

    private static final String TAG = "GcmMessageListenerServ";

    @Override
    public void onMessageReceived(String from, Bundle data) {
        Log.d(TAG, "From: " + from);

        if (PushNotifications.isSnoozed(this)) {
            Log.d(TAG, "Snoozed");
            return;
        }

        PushMessage message = PushMessage.newFromBundle(data);
        sendNotification(message);
    }

    private void sendNotification(PushMessage message) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(  this,
                                                                0 /* Request code */,
                                                                intent,
                                                                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        String title = getResources().getQuantityString(R.plurals.notification_delay_content_title_format,
                                                        message.delayedCount,
                                                        message.delayedCount);

        String delimiter = getString(R.string.notification_delay_content_delimiter);
        List<String> namesTimes = ListUtils.map(message.delays,
                                                d -> d.departureTime.isEmpty()
                                                         ? getString(R.string.notification_delay_content_format_no_time, d.departureName)
                                                         : getString(R.string.notification_delay_content_format, d.departureName, d.departureTime));
        String content = StringUtils.join(delimiter, namesTimes);

        NotificationCompat.Builder notificationBuilder =
            new NotificationCompat.Builder(this)
                .setCategory(Notification.CATEGORY_MESSAGE)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSmallIcon(getNotificationIcon())
                .setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setColor(ContextCompat.getColor(this, R.color.colorAccent))
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_alarm_off_grey600_24dp,
                           getString(R.string.notification_delay_stop),
                           PushNotifications.getSnoozeIntent(this));

        NotificationCompat.InboxStyle inboxStyle =
            new NotificationCompat.InboxStyle().setBigContentTitle(title);

        for (PushMessage.DelayInfo delay : message.delays) {

            String line =
                delay.departureTime.isEmpty()
                    ? getString(R.string.notification_delay_line_format_no_time,
                                delay.departureName,
                                delay.departureDelay)
                    : getString(R.string.notification_delay_line_format,
                                delay.departureName,
                                delay.departureTime,
                                delay.departureDelay);
            inboxStyle.addLine(Html.fromHtml(line));
        }

        notificationBuilder.setStyle(inboxStyle);
        PushNotifications.notify(this, notificationBuilder.build());
    }

    private int getNotificationIcon() {
        boolean whiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return whiteIcon ? R.drawable.ic_notification : R.mipmap.ic_launcher;
    }

    private static final class PushMessage {
        private static final int MAX_LINES = 5;
        public final int delayedCount;
        public final List<DelayInfo> delays;

        private PushMessage(int delayedCount, List<DelayInfo> delays) {
            this.delayedCount = delayedCount;
            this.delays = delays;
        }

        private static class DelayInfo {
            final public String departureName;
            final public String departureTime;
            final public String departureDelay;

            private DelayInfo(String departureName, String departureTime, String departureDelay) {
                this.departureName = departureName;
                this.departureTime = departureTime;
                this.departureDelay = departureDelay;
            }
        }

        public static PushMessage newFromBundle(Bundle bundle) {
            int delayedCount = Integer.valueOf(bundle.getString("delayedCount"));

            List<DelayInfo> delays = new ArrayList<>();
            int lineNumber = 0;
            while (bundle.getString("departureName" + lineNumber) != null && lineNumber < MAX_LINES) {
                String departureName = bundle.getString("departureName" + lineNumber);
                String departureTime = bundle.getString("departureTime" + lineNumber);
                String departureDelay = bundle.getString("departureDelay" + lineNumber);
                delays.add(new DelayInfo(departureName, departureTime, departureDelay));
                lineNumber++;
            }

            return new PushMessage(delayedCount, delays);
        }
    }
}
