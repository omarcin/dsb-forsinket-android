package com.oczeretko.dsbforsinket;

import java.util.*;

public final class Consts {

    public static final String APP_URL = "https://dsbpendler.azure-mobile.net";
    public static final String APP_KEY = "bMdzCAtzmdRntrVRKUpXVkGHmihhhY49";

    public static final String INTENT_ACTION_REGISTRATION_UPDATE = "ACTION_REGISTRATION";
    public static final String INTENT_EXTRA_REGISTRATION_COMPLETE = "EXTRA_REGISTRATION_COMPLETE";

    public static final String STATION_DEFAULT = "8600626"; // Central station
    public static final Set<String> TIMES_DEFAULT = new HashSet<>();

    public static final String PREF_POSSIBLY_REGISTERED = "REGISTERED_MAYBE";
    public static final String PREF_LAST_SNOOZE_MILLIS = "SNOOZE";
    public static final String PREF_VISITED_SETTINGS = "VISITED_SETTINGS";
    public static final String PREF_REGISTRATION_ERROR = "REGISTRATION_ERROR";
    public static final String PREF_UNHANDLED_REGISTRATION_ERROR = "UNHANDLED_REGISTRATION_ERROR";

    {
        TIMES_DEFAULT.add("7:15");
    }

    private Consts() {
    }
}
