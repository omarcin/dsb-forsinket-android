package com.oczeretko.dsbforsinket.service;


import retrofit2.*;
import retrofit2.http.*;

public interface RejseplannenApi {
    String BASE_URL = "http://xmlopen.rejseplanen.dk/bin/rest.exe/";

    @GET("departureBoard?useBus=0&offsetTime=0&format=json")
    Call<DepartureResult> getDepartures(@Query("id") String stationId);
}
