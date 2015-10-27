package com.oczeretko.dsbforsinket.data;

import java.util.*;

public final class StubData {

    public static List<DepartureInfo> getDepartures() {

        List<DepartureInfo> departures = new ArrayList<>();

        for (int i = 0; i < 25; i++) {

            DepartureInfo departure = new DepartureInfo("Train " + i, "12:34");
            departure.setCancelled(i % 7 == 0);
            if (i % 3 == 0) {
                departure.setDelay(String.valueOf(i + 1));
            }

            departures.add(departure);
        }

        return departures;
    }
}
