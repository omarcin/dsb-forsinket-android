package com.oczeretko.dsbforsinket.service;

import android.app.*;
import android.content.*;
import android.os.*;
import android.util.*;

import com.oczeretko.dsbforsinket.data.*;
import com.squareup.okhttp.*;
import com.squareup.okhttp.Response;

import org.xml.sax.*;

import java.io.*;
import java.text.*;
import java.util.*;

import javax.xml.parsers.*;

import retrofit2.*;
import retrofit2.Call;
import retrofit2.converter.gson.*;

public class DeparturesService extends IntentService {

    public final static int RESULT_OK = 1;
    public final static int RESULT_ERROR = -1;
    public final static String KEY_RESULT = "Result";

    private final static String TAG = "DeparturesService";
    private final static String KEY_RECEIVER = "ResultReceiver";
    private final static String KEY_STATION = "Station";
    private final OkHttpClient httpClient;
    private final RejseplannenApi rejseplannenApi;


    public DeparturesService() {
        super(TAG);
        httpClient = new OkHttpClient();
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(RejseplannenApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
        rejseplannenApi = retrofit.create(RejseplannenApi.class);
    }

    public static void requestData(Context context, ResultReceiver resultReceiver, String station) {
        Intent intent = new Intent(context, DeparturesService.class);
        intent.putExtra(KEY_RECEIVER, resultReceiver);
        intent.putExtra(KEY_STATION, station);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "intent received");
        ResultReceiver resultReceiver = intent.getParcelableExtra(KEY_RECEIVER);
        String station = intent.getStringExtra(KEY_STATION);

        try {

            ArrayList<Departure> departures = fetchDepartures(station);
            Collections.sort(departures, (a, b) -> (a.getDate() + a.getTime()).compareTo(b.getDate() + b.getTime()));
            Bundle data = new Bundle();
            data.putParcelableArrayList(KEY_RESULT, departures);
            resultReceiver.send(RESULT_OK, data);
            DeparturesCache.getInstance().put(station, departures);

        } catch (Exception e) {
            Log.e(TAG, "", e);
            resultReceiver.send(RESULT_ERROR, null);
        }
    }

    public ArrayList<Departure> fetchDepartures(String station) throws Exception {

        Call<DepartureResult> departuresCall = rejseplannenApi.getDepartures(station);
        retrofit2.Response<DepartureResult> response = departuresCall.execute();
        if(!response.isSuccessful()){
            Log.e(TAG, response.errorBody().string());
            throw new Exception(response.message());
        }
        else {
            return response.body().getDepartureBoard().getDepartures();
        }
    }
}
