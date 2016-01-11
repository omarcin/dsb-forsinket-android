package com.oczeretko.dsbforsinket.adapter;

import android.view.*;
import android.widget.*;

public class LayoutParamsWrapper {

    private final ViewGroup.LayoutParams layoutParams;

    public static LayoutParamsWrapper from(ViewGroup.LayoutParams layoutParams) {
        return new LayoutParamsWrapper(layoutParams);
    }

    public LayoutParamsWrapper(ViewGroup.LayoutParams layoutParams) {
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
        return ((FrameLayout.LayoutParams)layoutParams).topMargin;
    }

    public void setMarginTop(int marginTop) {
        ((FrameLayout.LayoutParams)layoutParams).topMargin = marginTop;
    }

    public int getMarginBottom() {
        return ((FrameLayout.LayoutParams)layoutParams).bottomMargin;
    }

    public void setMarginBottom(int bottomMargin) {
        ((FrameLayout.LayoutParams)layoutParams).bottomMargin = bottomMargin;
    }
}
