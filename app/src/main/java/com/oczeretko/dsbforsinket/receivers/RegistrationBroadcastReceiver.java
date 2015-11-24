package com.oczeretko.dsbforsinket.receivers;

import android.content.*;
import android.support.v4.content.*;
import android.view.*;

import com.oczeretko.dsbforsinket.*;

public class RegistrationBroadcastReceiver extends BroadcastReceiver {

    private final View toolbarLoadingIndicator;

    public static void sendRegistrationStartedBroadcast(Context context) {
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(context);
        Intent registrationStarted = new Intent(Consts.INTENT_ACTION_REGISTRATION_UPDATE);
        registrationStarted.putExtra(Consts.INTENT_EXTRA_REGISTRATION_COMPLETE, false);
        broadcastManager.sendBroadcast(registrationStarted);
    }

    public static void sendRegistrationCompleteBroadcast(Context context) {
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(context);
        Intent registrationComplete = new Intent(Consts.INTENT_ACTION_REGISTRATION_UPDATE);
        registrationComplete.putExtra(Consts.INTENT_EXTRA_REGISTRATION_COMPLETE, true);
        broadcastManager.sendBroadcast(registrationComplete);
    }

    public RegistrationBroadcastReceiver(View toolbarLoadingIndicator) {
        this.toolbarLoadingIndicator = toolbarLoadingIndicator;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isFinished = intent.getBooleanExtra(Consts.INTENT_EXTRA_REGISTRATION_COMPLETE, true);
        toolbarLoadingIndicator.setVisibility(isFinished ? View.GONE : View.VISIBLE);
    }
}
