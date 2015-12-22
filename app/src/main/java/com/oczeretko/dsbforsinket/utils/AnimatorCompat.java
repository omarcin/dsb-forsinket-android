package com.oczeretko.dsbforsinket.utils;

import android.animation.*;

public final class AnimatorCompat {
    private AnimatorCompat() {}

    public static ObjectAnimator ofArgb(Object target, String propertyName, int... values) {
        ObjectAnimator animator = ObjectAnimator.ofInt(target, propertyName, values);
        animator.setEvaluator(com.oczeretko.dsbforsinket.oss.ArgbEvaluator.getInstance());
        return animator;
    }

    public static ValueAnimator ofArgb(int... values) {
        ValueAnimator anim = new ValueAnimator();
        anim.setIntValues(values);
        anim.setEvaluator(com.oczeretko.dsbforsinket.oss.ArgbEvaluator.getInstance());
        return anim;
    }
}
