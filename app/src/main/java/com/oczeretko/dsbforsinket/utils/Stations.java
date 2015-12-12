package com.oczeretko.dsbforsinket.utils;

import android.content.*;
import android.support.v4.util.*;
import android.support.v7.preference.*;

import com.oczeretko.dsbforsinket.*;
import com.oczeretko.dsbforsinket.R;

import java.util.*;

import static com.oczeretko.dsbforsinket.utils.CollectionsUtils.*;

public final class Stations {

    private static String[] stations;
    private static String[] names;
    private static Map<String, String> namesById;
    private static List<Pair<String, String>> stationNames;

    private Stations() {
    }

    public static String[] getSelectedStationIds(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String key = context.getString(R.string.preferences_stations_key);
        return preferences.getStringSet(key, Consts.STATIONS_DEFAULT).toArray(new String[0]);
    }

    public static String getStationNameById(Context context, String stationId) {
        if (stations == null) {
            initialize(context);
        }

        return namesById.get(stationId);
    }

    public static List<Pair<String,String>> getStations(Context context) {
        if (stations == null) {
            initialize(context);
        }

        return stationNames;
    }

    private static void initialize(Context context) {
        stations = context.getResources().getStringArray(R.array.stations_uics);
        names = context.getResources().getStringArray(R.array.stations_names);
        stationNames = new ArrayList<>(zip(stations, names));
        namesById = toMap(stationNames, sn -> sn.first, sn -> sn.second);
    }
}
