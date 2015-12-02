package com.oczeretko.dsbforsinket;

import java.util.*;

public final class Consts {

    public static final String APP_URL = "https://dsbpendler.azure-mobile.net";
    public static final String APP_KEY = "bMdzCAtzmdRntrVRKUpXVkGHmihhhY49";

    public static final String INTENT_ACTION_REGISTRATION_UPDATE = "ACTION_REGISTRATION";
    public static final String INTENT_EXTRA_REGISTRATION_COMPLETE = "EXTRA_REGISTRATION_COMPLETE";

    public static final Set<String> TIMES_DEFAULT = new HashSet<>();
    public static final Set<String> STATIONS_DEFAULT = new HashSet<>();

    public static final String PREF_POSSIBLY_REGISTERED = "REGISTERED_MAYBE";
    public static final String PREF_LAST_SNOOZE_MILLIS = "SNOOZE";
    public static final String PREF_VISITED_SETTINGS = "VISITED_SETTINGS";
    public static final String PREF_REGISTRATION_ERROR = "REGISTRATION_ERROR";
    public static final String PREF_UNHANDLED_REGISTRATION_ERROR = "UNHANDLED_REGISTRATION_ERROR";

    public static final int[] IDS = {
        R.string.id_00, R.string.id_01, R.string.id_02, R.string.id_03,
        R.string.id_04, R.string.id_05, R.string.id_06, R.string.id_07,
        R.string.id_08, R.string.id_09, R.string.id_10, R.string.id_11,
        R.string.id_12, R.string.id_13, R.string.id_14, R.string.id_15
    };

    {
        TIMES_DEFAULT.add("7:15");
        STATIONS_DEFAULT.add("8600626"); // Central station
        STATIONS_DEFAULT.add("8600646"); // Norreport
    }

    private Consts() {
    }
}
