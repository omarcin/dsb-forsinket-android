package com.oczeretko.dsbforsinket.service;

import android.os.*;

public class ResultReceiverListenable extends ResultReceiver {

    private ResultListener listener;

    public ResultReceiverListenable(Handler handler) {
        super(handler);
    }

    public void setResultListener(ResultListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (listener != null) {
            listener.onReceiveResult(resultCode, resultData);
        }
    }

    public interface ResultListener {
        void onReceiveResult(int resultCode, Bundle resultData);
    }
}
