package com.oczeretko.dsbforsinket.utils;

import android.content.*;
import android.support.v4.util.*;

import com.oczeretko.dsbforsinket.*;

import java.util.*;

import static com.oczeretko.dsbforsinket.utils.ListUtils.*;

public final class Stations {

    private static String[] stations;
    private static String[] stationNames;
    private static Map<String, String> namesById;

    private Stations() {
    }

    public static String getStationNameById(Context context, String stationId) {
        if (stations == null) {
            initialize(context);
        }

        return namesById.get(stationId);
    }

    private static void initialize(Context context) {
        stations = context.getResources().getStringArray(R.array.stations_uics);
        stationNames = context.getResources().getStringArray(R.array.stations_names);
        Collection<Pair<String, String>> pairs = zip(stations, stationNames);
        namesById = toMap(pairs, sn -> sn.first, sn -> sn.second);
    }
}
