package com.oczeretko.dsbforsinket.data;

public class StationPreference {
    private final String id;
    private final String name;

    public StationPreference(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
