package com.oczeretko.dsbforsinket.service;

import com.google.gson.annotations.*;

public class DepartureResult {
    @SerializedName("DepartureBoard")
    private DepartureBoard departureBoard;

    public DepartureBoard getDepartureBoard() {
        return departureBoard;
    }
}
