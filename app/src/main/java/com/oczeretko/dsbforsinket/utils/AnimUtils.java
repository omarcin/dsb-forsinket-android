package com.oczeretko.dsbforsinket.utils;

import android.animation.*;

public final class AnimUtils {
    private AnimUtils() {
    }

    public static Animator.AnimatorListener onEnd(Runnable runnable) {
        return new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                runnable.run();
            }
        };
    }
}
