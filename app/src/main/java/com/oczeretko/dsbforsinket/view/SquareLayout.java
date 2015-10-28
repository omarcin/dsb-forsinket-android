package com.oczeretko.dsbforsinket.view;

import android.content.*;
import android.util.*;
import android.widget.*;

/**
 * http://stackoverflow.com/a/28673171
 * CC BY-SA 3.0, http://creativecommons.org/licenses/by-sa/3.0/
 */
public class SquareLayout extends FrameLayout {

    public SquareLayout(Context context) {
        super(context);
    }

    public SquareLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, heightMeasureSpec);
    }
}