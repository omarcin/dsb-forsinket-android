package com.oczeretko.dsbforsinket.data;

public class DepartureInfo {
    private String trainName;
    private String departureTime;
    private String delay = "";
    private boolean cancelled;

    public DepartureInfo(String trainName, String departureTime) {
        this.trainName = trainName;
        this.departureTime = departureTime;
    }

    public String getTrainName() {
        return trainName;
    }

    public void setTrainName(String trainName) {
        this.trainName = trainName;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public String getDelay() {
        return delay;
    }

    public void setDelay(String delay) {
        this.delay = delay;
    }

    public boolean isDelayed(){
        return this.delay != "";
    }
}
