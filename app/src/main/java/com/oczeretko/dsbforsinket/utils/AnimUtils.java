package com.oczeretko.dsbforsinket.utils;

import android.animation.*;

import com.oczeretko.dsbforsinket.oss.ArgbEvaluator;

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

    public static ValueAnimator.AnimatorUpdateListener onUpdate(final Runnable action) {
        return _1 -> action.run();
    }

    public static <T> ValueAnimator.AnimatorUpdateListener onUpdate(final Action<T> action) {
        return animation -> {
            T value = (T)animation.getAnimatedValue();
            action.invoke(value);
        };
    }
}

