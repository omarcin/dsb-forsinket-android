package com.oczeretko.dsbforsinket.utils;

import android.content.*;
import android.content.res.*;

import com.oczeretko.dsbforsinket.*;

import java.util.*;

import static com.oczeretko.dsbforsinket.utils.CollectionsUtils.*;

public final class Times {

    private static String[] timesValues;
    private static String[] timesNames;
    private static String[] timesDefault;
    private static Map<String, String> nameByValue;

    private Times() {
    }

    public static String[] getDefault(Context context) {
        initialize(context);
        return timesDefault;
    }

    public static String getNameByValue(Context context, String value) {
        initialize(context);
        return nameByValue.get(value);
    }

    public static String[] getTimesNames(Context context) {
        initialize(context);
        return timesNames;
    }

    public static String[] getTimesValues(Context context) {
        initialize(context);
        return timesValues;
    }

    private static void initialize(Context context) {
        if (timesValues != null) {
            return;
        }

        Resources resources = context.getResources();
        timesNames = resources.getStringArray(R.array.times_names);
        timesValues = resources.getStringArray(R.array.times_values);
        timesDefault = resources.getStringArray(R.array.times_default);

        nameByValue = toMap(zip(timesNames, timesValues), p -> p.second, p -> p.first);
    }
}
