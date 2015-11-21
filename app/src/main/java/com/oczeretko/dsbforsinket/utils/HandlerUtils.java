package com.oczeretko.dsbforsinket.utils;

import android.os.*;

public final class HandlerUtils {
    private HandlerUtils() {
    }

    public static Handler.Callback toHandlerCallback(Runnable r) {
        return msg -> {
            r.run();
            return true;
        };
    }
}
