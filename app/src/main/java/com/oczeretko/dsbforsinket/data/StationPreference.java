package com.oczeretko.dsbforsinket.data;

import io.realm.*;
import io.realm.annotations.*;

public class StationPreference extends RealmObject{

    @PrimaryKey
    private int id;
    private String stationId;
    private String name;

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static int compare(StationPreference first, StationPreference second){
        return first.getId() - second.getId();
    }
}
