package com.oczeretko.dsbforsinket.gcm;

import android.content.*;
import android.provider.*;
import android.support.annotation.*;

import com.microsoft.windowsazure.mobileservices.*;
import com.microsoft.windowsazure.mobileservices.notifications.*;
import com.oczeretko.dsbforsinket.*;

import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class DsbMobileServiceClient {

    private static final String SEPARATOR = "-";
    private static final String PREFIX_STATION = "station" + SEPARATOR;
    private static final String PREFIX_TIME = "time" + SEPARATOR;

    private final int timeTagHash;

    private final MobileServicePush client;

    public DsbMobileServiceClient(Context context) throws MalformedURLException {
        client = new MobileServiceClient(Consts.APP_URL, Consts.APP_KEY, context).getPush();
        String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        timeTagHash = androidId != null ? Math.abs(androidId.hashCode() % 9) : 9;
    }

    public void unregister() throws ExecutionException, InterruptedException {
        client.unregister().get();
    }

    public void register(String token, String station, String[] times) throws ExecutionException, InterruptedException {
        String[] tags = createRegistrationTags(station, times);
        client.register(token, tags).get();
    }

    @NonNull
    private String[] createRegistrationTags(String station, String[] times) {
        ArrayList<String> tagList = new ArrayList<>(2 * times.length + 1);
        tagList.add(PREFIX_STATION + station);

        for (String time : times) {
            tagList.add(PREFIX_TIME + time + SEPARATOR + timeTagHash);
            tagList.add(station + SEPARATOR + time);
        }
        return tagList.toArray(new String[0]);
    }
}
