package com.oczeretko.dsbforsinket.data;

import android.os.*;

public class DepartureInfo implements Parcelable {
    private String trainName;
    private String departureTime;
    private String delay = "";
    private boolean cancelled;
    private String trainLine;

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

    public String getTrainLine() {
        return trainLine;
    }

    public void setTrainLine(String trainLine) {
        this.trainLine = trainLine;
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

    public boolean isDelayed() {
        return this.delay != "";
    }

    protected DepartureInfo(Parcel in) {
        trainName = in.readString();
        trainLine = in.readString();
        departureTime = in.readString();
        delay = in.readString();
        cancelled = in.readByte() != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(trainName);
        dest.writeString(trainLine);
        dest.writeString(departureTime);
        dest.writeString(delay);
        dest.writeByte((byte)(cancelled ? 0x01 : 0x00));
    }

    public static final Parcelable.Creator<DepartureInfo> CREATOR = new Parcelable.Creator<DepartureInfo>() {
        @Override
        public DepartureInfo createFromParcel(Parcel in) {
            return new DepartureInfo(in);
        }

        @Override
        public DepartureInfo[] newArray(int size) {
            return new DepartureInfo[size];
        }
    };
}