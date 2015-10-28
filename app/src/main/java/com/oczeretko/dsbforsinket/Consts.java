package com.oczeretko.dsbforsinket;

import java.util.*;

public final class Consts {

    public static final String APP_URL = "https://dsbpendler.azure-mobile.net";
    public static final String APP_KEY = "bMdzCAtzmdRntrVRKUpXVkGHmihhhY49";

    public static final String REGISTRATION_COMPLETE = "REG_COMPL";

    public static final String STATION_DEFAULT = "8600626"; // Central station
    public static final Set<String> TIMES_DEFAULT = new HashSet<>();

    public static final String PREF_SENT_TOKEN_TO_SERVER = "SENT_TOKEN";
    public static final String PREF_POSSIBLY_REGISTERED = "REGISTERED_MAYBE";

    {
        TIMES_DEFAULT.add("8:00");
    }

    private Consts() {
    }
}
