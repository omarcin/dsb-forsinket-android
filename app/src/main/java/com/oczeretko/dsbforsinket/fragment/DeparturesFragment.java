package com.oczeretko.dsbforsinket.fragment;


import android.os.*;
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

import static com.oczeretko.dsbforsinket.utils.HandlerUtils.*;


public class DeparturesFragment extends Fragment implements ResultReceiverListenable.ResultListener {

    public static final long REFRESH_INTERVAL = DateUtils.MINUTE_IN_MILLIS;
    private static final String TAG = "DeparturesFragment";
    private static final String KEY_STATION_ID = "STATION_ID";

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

    private Handler refreshHandler = new Handler(toHandlerCallback(this::refreshData));

    public static DeparturesFragment newInstance(String stationId) {
        Bundle args = new Bundle();
        args.putString(KEY_STATION_ID, stationId);
        DeparturesFragment fragment = new DeparturesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        station = getArguments().getString(KEY_STATION_ID);
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
        if (departures != null) {
            setData(departures);
        }
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
        if (departures != null && station.equals(departuresStation)) {
            long dataAge = System.currentTimeMillis() - departuresTimestamp;
            if (dataAge > REFRESH_INTERVAL) {
                refreshData();
            } else {
                scheduleRefresh();
            }
        } else {
            refreshData();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        cancelRefresh();
        resultReceiver.setResultListener(null);
        loadingIndicator.setVisibility(View.GONE);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        Log.d(TAG, "received result");
        switch (resultCode) {
            case DeparturesService.RESULT_OK:
                ArrayList<DepartureInfo> receivedDepartures = resultData.getParcelableArrayList(DeparturesService.KEY_RESULT);
                departuresTimestamp = System.currentTimeMillis();
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
        DeparturesService.requestData(getActivity(), resultReceiver, station);
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
        departuresStation = station;
        adapter.setDepartures(departures);
        recyclerView.setVisibility(View.VISIBLE);
        loadingIndicator.setVisibility(View.GONE);
        toolbarLoadingIndicator.setVisibility(View.GONE);
        errorIndicator.setVisibility(View.GONE);
    }
}
