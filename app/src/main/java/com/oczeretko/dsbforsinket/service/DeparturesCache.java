package com.oczeretko.dsbforsinket.service;

import android.text.format.*;
import android.util.*;

import com.oczeretko.dsbforsinket.data.*;

import java.util.*;

public class DeparturesCache {

    private static final int MAX_CACHE_SIZE = 10;
    private static final long CACHE_VALIDITY_MILLIS = 30 * DateUtils.SECOND_IN_MILLIS;

    private static DeparturesCache instance = new DeparturesCache(MAX_CACHE_SIZE, CACHE_VALIDITY_MILLIS);
    private final LruCache cache;
    private final long validityInMillis;

    public static DeparturesCache getInstance() {
        return instance;
    }

    private DeparturesCache(int cacheSize, long validityInMillis) {
        this.validityInMillis = validityInMillis;
        cache = new LruCache(cacheSize);
    }

    public CachedDepartures get(String station) {
        CachedDepartures cached = (CachedDepartures)cache.get(station);

        if (cached == null) {
            return null;
        }

        if (System.currentTimeMillis() - cached.getTimestamp() < validityInMillis) {
            return cached;
        } else {
            cache.remove(station);
            return null;
        }
    }

    public void put(String station, ArrayList<DepartureInfo> departures) {
        cache.put(station, new CachedDepartures(System.currentTimeMillis(), departures));
    }

    public static class CachedDepartures {
        private final long timestamp;
        private final ArrayList<DepartureInfo> departures;

        public CachedDepartures(long timestamp, ArrayList<DepartureInfo> departures) {
            this.timestamp = timestamp;
            this.departures = departures;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public ArrayList<DepartureInfo> getDepartures() {
            return departures;
        }
    }
}
