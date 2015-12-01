package com.oczeretko.dsbforsinket.service;

import android.util.*;

import com.oczeretko.dsbforsinket.data.*;

import java.util.*;

class DeparturesCache {

    private final LruCache cache;
    private final long validityInMillis;

    public DeparturesCache(int cacheSize, long validityInMillis) {
        this.validityInMillis = validityInMillis;
        cache = new LruCache(cacheSize);
    }

    public ArrayList<DepartureInfo> get(String station) {
        CachedItem cached = (CachedItem)cache.get(station);

        if (cached == null) {
            return null;
        }

        if (System.currentTimeMillis() - cached.getTimestamp() < validityInMillis) {
            return cached.getDepartures();
        } else {
            cache.remove(station);
            return null;
        }
    }

    public void put(String station, ArrayList<DepartureInfo> departures) {
        cache.put(station, new CachedItem(System.currentTimeMillis(), departures));
    }

    private static class CachedItem {
        private final long timestamp;
        private final ArrayList<DepartureInfo> departures;

        public CachedItem(long timestamp, ArrayList<DepartureInfo> departures) {
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
