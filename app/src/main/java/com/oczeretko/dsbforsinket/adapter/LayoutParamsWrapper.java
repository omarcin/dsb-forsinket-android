package com.oczeretko.dsbforsinket.adapter;

import android.view.*;
import android.widget.*;

public class LayoutParamsWrapper {

    private final FrameLayout.LayoutParams layoutParams;

    public static LayoutParamsWrapper from(ViewGroup.LayoutParams layoutParams) {
        return from((FrameLayout.LayoutParams)layoutParams);
    }

    public static LayoutParamsWrapper from(FrameLayout.LayoutParams layoutParams) {
        return new LayoutParamsWrapper(layoutParams);
    }

    public LayoutParamsWrapper(FrameLayout.LayoutParams layoutParams) {
        this.layoutParams = layoutParams;
    }

    public int getHeight() {
        return layoutParams.height;
    }

    public void setHeight(int height) {
        layoutParams.height = height;
    }

    public int getWidth() {
        return layoutParams.width;
    }

    public void setWidth(int width) {
        layoutParams.width = width;
    }

    public int getMarginTop() {
        return layoutParams.topMargin;
    }

    public void setMarginTop(int marginTop) {
        layoutParams.topMargin = marginTop;
    }

    public int getMarginBottom() {
        return layoutParams.bottomMargin;
    }

    public void setMarginBottom(int bottomMargin) {
        layoutParams.bottomMargin = bottomMargin;
    }
}
