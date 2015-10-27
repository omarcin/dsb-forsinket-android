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
    private final static String KEY_RESULT = "Result";

    private final static String TAG = "DeparturesService";
    private final static String KEY_RECEIVER = "ResultReceiver";
    private final static String KEY_STATION = "Station";
    private final OkHttpClient httpClient;

    public DeparturesService() {
        super(TAG);
        httpClient = new OkHttpClient();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ResultReceiver resultReceiver = intent.getParcelableExtra(KEY_RECEIVER);
        String station = intent.getStringExtra(KEY_STATION);

        try {

            ArrayList<DepartureInfo> departures = fetchDepartures(station);
            Bundle data = new Bundle();
            data.putParcelableArrayList(KEY_RESULT, departures);
            resultReceiver.send(RESULT_OK, data);

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
