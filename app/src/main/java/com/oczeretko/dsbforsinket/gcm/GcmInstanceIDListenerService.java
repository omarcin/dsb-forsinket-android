package com.oczeretko.dsbforsinket.gcm;

import android.content.*;

import com.google.android.gms.iid.*;

public class GcmInstanceIDListenerService extends InstanceIDListenerService {
    @Override
    public void onTokenRefresh() {
        Intent intent = new Intent(this, GcmRegistrationIntentService.class);
        startService(intent);
    }
}
