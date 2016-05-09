package com.oczeretko.dsbforsinket.service;

import com.google.gson.annotations.*;

import java.util.*;

public class DepartureBoard {
    @SerializedName("Departure")
    private ArrayList<Departure> departures;

    public ArrayList<Departure> getDepartures() {
        return departures;
    }
}
