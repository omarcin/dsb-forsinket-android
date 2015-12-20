package com.oczeretko.dsbforsinket.utils;

import android.view.*;

public final class ViewUtils {
    private ViewUtils() {
    }

    public static void onPreDrawExecuteOnce(final ViewTreeObserver observer, final Func0<Boolean> func){
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (observer.isAlive()) {
                    observer.removeOnPreDrawListener(this);
                }

                return func.invoke();
            }
        });
    }

    public static void onLayoutExecuteOnce(final ViewTreeObserver observer, final Runnable runnable){
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (observer.isAlive()) {
                    observer.removeOnGlobalLayoutListener(this);
                }

                runnable.run();
            }
        });
    }
}
