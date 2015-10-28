package com.oczeretko.dsbforsinket.fragment;


import android.content.*;
import android.os.*;
import android.preference.*;
import android.support.v4.app.*;
import android.support.v7.widget.*;
import android.text.format.*;
import android.util.*;
import android.view.*;
import android.widget.*;

import com.oczeretko.dsbforsinket.*;
import com.oczeretko.dsbforsinket.adapter.*;
import com.oczeretko.dsbforsinket.data.*;
import com.oczeretko.dsbforsinket.service.*;

import java.util.*;


public class DeparturesFragment extends Fragment implements ResultReceiverListenable.ResultListener {

    public static final long REFRESH_INTERVAL = DateUtils.MINUTE_IN_MILLIS;
    private static final String TAG = "DeparturesFragment";

    private View toolbarLoadingIndicator;
    private View loadingIndicator;
    private View errorIndicator;
    private Button retryButton;
    private RecyclerView recyclerView;

    private DeparturesAdapter adapter;
    private ResultReceiverListenable resultReceiver;

    private String station;

    private ArrayList<DepartureInfo> departures;
    private long departuresTimestamp;
    private String departuresStation;

    private Handler refreshHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "refresh handler received message");
            refreshData();
        }
    };

    public DeparturesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_departures, container, false);
        resultReceiver = new ResultReceiverListenable(new Handler());
        recyclerView = (RecyclerView)view.findViewById(R.id.fragment_departures_recycler);
        loadingIndicator = view.findViewById(R.id.fragment_departures_loading_indicator);
        errorIndicator = view.findViewById(R.id.fragment_departures_error_indicator);
        retryButton = (Button)view.findViewById(R.id.fragment_departures_retry);
        retryButton.setOnClickListener(_1 -> refreshData());
        toolbarLoadingIndicator = getActivity().findViewById(R.id.main_activity_toolbar_progress_bar);
        setupRecyclerView();
        return view;
    }

    private void setupRecyclerView() {
        adapter = new DeparturesAdapter(getActivity());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        resultReceiver.setResultListener(this);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        station = preferences.getString(getString(R.string.preferences_station_key), Consts.STATION_DEFAULT);

        if (departures != null && station.equals(departuresStation)) {
            long dataAge = System.currentTimeMillis() - departuresTimestamp;
            setData(departures);
            if (dataAge > REFRESH_INTERVAL) {
                refreshData();
            } else {
                scheduleRefresh();
            }
        } else {
            // refreshData(); TODO: commented out for testing
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        cancelRefresh();
        resultReceiver.setResultListener(null);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        Log.d(TAG, "received result");
        switch (resultCode) {
            case DeparturesService.RESULT_OK:
                ArrayList<DepartureInfo> receivedDepartures = resultData.getParcelableArrayList(DeparturesService.KEY_RESULT);
                setData(receivedDepartures);
                scheduleRefresh();
                break;
            case DeparturesService.RESULT_ERROR:
                departures = null;
                recyclerView.setVisibility(View.GONE);
                loadingIndicator.setVisibility(View.GONE);
                toolbarLoadingIndicator.setVisibility(View.GONE);
                errorIndicator.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void scheduleRefresh() {
        refreshHandler.sendEmptyMessageDelayed(0, REFRESH_INTERVAL);
    }

    private void cancelRefresh() {
        refreshHandler.removeMessages(0);
    }

    private void refreshData() {
        DeparturesService.requestData(getActivity(), resultReceiver, Consts.STATION_DEFAULT);
        if (departures == null) {
            recyclerView.setVisibility(View.GONE);
            errorIndicator.setVisibility(View.GONE);
            loadingIndicator.setVisibility(View.VISIBLE);
            toolbarLoadingIndicator.setVisibility(View.GONE);
        } else {
            toolbarLoadingIndicator.setVisibility(View.VISIBLE);
        }
    }

    private void setData(ArrayList<DepartureInfo> departures) {
        this.departures = departures;
        departuresTimestamp = System.currentTimeMillis();
        departuresStation = station;
        adapter.setDepartures(departures);
        recyclerView.setVisibility(View.VISIBLE);
        loadingIndicator.setVisibility(View.GONE);
        toolbarLoadingIndicator.setVisibility(View.GONE);
        errorIndicator.setVisibility(View.GONE);
    }
}
