package com.oczeretko.dsbforsinket.data;

import io.realm.*;
import io.realm.annotations.*;

public class StationPreference extends RealmObject{

    @PrimaryKey
    private int position;
    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public static int compare(StationPreference first, StationPreference second){
        return first.getPosition() - second.getPosition();
    }
}
