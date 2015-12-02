package com.oczeretko.dsbforsinket.service;

import android.app.*;
import android.content.*;
import android.os.*;
import android.util.*;

import com.oczeretko.dsbforsinket.data.*;
import com.squareup.okhttp.*;

import org.xml.sax.*;

import java.io.*;
import java.text.*;
import java.util.*;

import javax.xml.parsers.*;

public class DeparturesService extends IntentService {

    public final static int RESULT_OK = 1;
    public final static int RESULT_ERROR = -1;
    public final static String KEY_RESULT = "Result";

    private final static String TAG = "DeparturesService";
    private final static String KEY_RECEIVER = "ResultReceiver";
    private final static String KEY_STATION = "Station";
    private final OkHttpClient httpClient;

    public DeparturesService() {
        super(TAG);
        httpClient = new OkHttpClient();
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

            ArrayList<DepartureInfo> departures = fetchDepartures(station);
            Collections.sort(departures, (a, b) -> a.getDepartureTime().compareTo(b.getDepartureTime()));
            Bundle data = new Bundle();
            data.putParcelableArrayList(KEY_RESULT, departures);
            resultReceiver.send(RESULT_OK, data);
            DeparturesCache.getInstance().put(station, departures);

        } catch (Exception e) {
            Log.e(TAG, "", e);
            resultReceiver.send(RESULT_ERROR, null);
        }
    }

    public ArrayList<DepartureInfo> fetchDepartures(String station) throws IOException, ParseException, SAXException, ParserConfigurationException {

        String url = "http://traindata.dsb.dk/stationdeparture/opendataprotocol.svc/Queue()?%24filter=StationUic+eq+%27" + station + "%27";
        Request request = new Request.Builder()
                              .url(url)
                              .build();

        Response response = httpClient.newCall(request).execute();
        InputStream responseStream = response.body().byteStream();
        return new DeparturesParser().parseDepartures(responseStream);
    }
}
