package com.oczeretko.dsbforsinket.utils;

import android.content.*;
import android.support.v4.util.*;

import com.oczeretko.dsbforsinket.*;
import com.oczeretko.dsbforsinket.data.*;

import java.util.*;

import io.realm.*;

import static com.oczeretko.dsbforsinket.utils.CollectionsUtils.*;

public final class Stations {

    private static String[] stations;
    private static String[] names;
    private static Map<String, String> namesById;
    private static List<Pair<String, String>> stationNames;

    private Stations() {
    }

    public static void createDefaultIfNeeded(Context context) {
        Realm realm = Realm.getInstance(context);
        if (realm.where(StationPreference.class).count() == 0) {
            realm.beginTransaction();
            String[] defaultStationIds = context.getResources().getStringArray(R.array.stations_default);
            int id = 1;
            for (String stationId : defaultStationIds) {
                StationPreference preference = realm.createObject(StationPreference.class);
                preference.setId(id++);
                preference.setName(Stations.getStationNameById(context, stationId));
                preference.setStationId(stationId);
                preference.setTimes(Times.getDefault(context));
            }
            realm.commitTransaction();
        }
        realm.close();
    }

    public static String getStationNameById(Context context, String stationId) {
        if (stations == null) {
            initialize(context);
        }

        return namesById.get(stationId);
    }

    public static List<Pair<String, String>> getStations(Context context) {
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
