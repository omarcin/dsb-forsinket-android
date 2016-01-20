package com.oczeretko.dsbforsinket.data;

import com.oczeretko.dsbforsinket.utils.*;

import io.realm.*;
import io.realm.annotations.*;

public class StationPreference extends RealmObject {

    private static final String DELIMITER_TIMES = ";";

    @Ignore
    private String times; // dummy field, @Ignore does not work with methods

    @PrimaryKey
    private int id;

    private String stationId;
    private String name;
    private boolean notificationEnabled;
    private String timesConcatenated;


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

    public static int compare(StationPreference first, StationPreference second) {
        return first.getId() - second.getId();
    }

    public boolean isNotificationEnabled() {
        return notificationEnabled;
    }

    public void setNotificationEnabled(boolean notificationEnabled) {
        this.notificationEnabled = notificationEnabled;
    }

    public String getTimesConcatenated() {
        return timesConcatenated;
    }

    public void setTimesConcatenated(String timesConcatenated) {
        this.timesConcatenated = timesConcatenated;
    }

    public void setTimes(String[] times) {
        setTimesConcatenated(StringUtils.join(DELIMITER_TIMES, times));
    }

    public String[] getTimes() {
        // TODO: bug when nothing is selected
        return getTimesConcatenated().split(DELIMITER_TIMES);
    }
}
