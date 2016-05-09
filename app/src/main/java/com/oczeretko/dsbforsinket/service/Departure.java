package com.oczeretko.dsbforsinket.service;

import android.os.*;

import com.google.gson.annotations.*;

public class Departure implements Parcelable {
    private String name;
    private String type;
    private String stop;
    private String time;
    private String date;
    private String messages;
    private String track;
    @SerializedName("rtTime")
    private String updatedTime;
    @SerializedName("rtDate")
    private String updatedDate;
    @SerializedName("rtTrack")
    private String updatedTrack;
    private boolean cancelled;
    private String state;
    private String finalStop;
    private String direction;

    public String getName() {
        return name;
    }

    public String getLineName() {
        switch (type){
            case "S":
                return name;
            case "M":
                return name.replace("Metro ", "");
            default:
                return "";
        }
    }

    public String getType() {
        return type;
    }

    public String getStop() {
        return stop;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public String getMessages() {
        return messages;
    }

    public String getTrack() {
        return track;
    }

    public String getUpdatedTime() {
        return updatedTime;
    }

    public String getUpdatedDate() {
        return updatedDate;
    }

    public String getUpdatedTrack() {
        return updatedTrack;
    }

    public String getFinalStop() {
        return finalStop;
    }

    public String getDirection() {
        return direction;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public String getState() {
        return state;
    }

    protected Departure(Parcel in) {
        name = in.readString();
        type = in.readString();
        stop = in.readString();
        time = in.readString();
        date = in.readString();
        messages = in.readString();
        track = in.readString();
        updatedTime = in.readString();
        updatedDate = in.readString();
        updatedTrack = in.readString();
        cancelled = in.readByte() != 0x00;
        state = in.readString();
        finalStop = in.readString();
        direction = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(type);
        dest.writeString(stop);
        dest.writeString(time);
        dest.writeString(date);
        dest.writeString(messages);
        dest.writeString(track);
        dest.writeString(updatedTime);
        dest.writeString(updatedDate);
        dest.writeString(updatedTrack);
        dest.writeByte((byte)(cancelled ? 0x01 : 0x00));
        dest.writeString(state);
        dest.writeString(finalStop);
        dest.writeString(direction);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Departure> CREATOR = new Parcelable.Creator<Departure>() {
        @Override
        public Departure createFromParcel(Parcel in) {
            return new Departure(in);
        }

        @Override
        public Departure[] newArray(int size) {
            return new Departure[size];
        }
    };
}