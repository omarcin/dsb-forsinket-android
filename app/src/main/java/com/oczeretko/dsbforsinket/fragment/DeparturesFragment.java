package com.oczeretko.dsbforsinket.fragment;


import android.os.*;
import android.support.v4.app.*;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.*;

import com.oczeretko.dsbforsinket.*;
import com.oczeretko.dsbforsinket.adapter.*;
import com.oczeretko.dsbforsinket.data.*;
import com.oczeretko.dsbforsinket.service.*;

import java.util.*;


public class DeparturesFragment extends Fragment implements ResultReceiverListenable.ResultListener {

    private static final String KEY_RECEIVER = "Receiver";
    private static final String KEY_DEPARTURES = "Departures";

    private View loadingIndicator;
    private View errorIndicator;
    private Button retryButton;
    private RecyclerView recyclerView;
    private DeparturesAdapter adapter;
    private ResultReceiverListenable resultReceiver;
    public static final String STATION = "8600856";
    private ArrayList<DepartureInfo> departures;

    public DeparturesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_departures, container, false);

        if (savedInstanceState != null) {
            resultReceiver = savedInstanceState.getParcelable(KEY_RECEIVER);
            departures = savedInstanceState.getParcelableArrayList(KEY_DEPARTURES);
        } else {
            resultReceiver = new ResultReceiverListenable(new Handler());
        }

        recyclerView = (RecyclerView)view.findViewById(R.id.fragment_departures_recycler);
        loadingIndicator = view.findViewById(R.id.fragment_departures_loading_indicator);
        errorIndicator = view.findViewById(R.id.fragment_departures_error_indicator);
        retryButton = (Button)view.findViewById(R.id.fragment_departures_retry);
        retryButton.setOnClickListener(_1 -> refreshData());
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

        if (departures != null) {
            setData(departures);
        } else {
            // refreshData(); TODO: commented out for testing
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        resultReceiver.setResultListener(null);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(KEY_RECEIVER, resultReceiver);
        outState.putParcelableArrayList(KEY_DEPARTURES, departures);
        super.onSaveInstanceState(outState);
    }

    private void refreshData() {
        recyclerView.setVisibility(View.GONE);
        errorIndicator.setVisibility(View.GONE);
        loadingIndicator.setVisibility(View.VISIBLE);
        DeparturesService.requestData(getActivity(), resultReceiver, STATION);
    }

    private void setData(ArrayList<DepartureInfo> departures) {
        this.departures = departures;
        adapter.setDepartures(departures);
        recyclerView.setVisibility(View.VISIBLE);
        loadingIndicator.setVisibility(View.GONE);
        errorIndicator.setVisibility(View.GONE);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case DeparturesService.RESULT_OK:
                ArrayList<DepartureInfo> receivedDepartures = resultData.getParcelableArrayList(DeparturesService.KEY_RESULT);
                setData(receivedDepartures);
                break;
            case DeparturesService.RESULT_ERROR:
                recyclerView.setVisibility(View.GONE);
                loadingIndicator.setVisibility(View.GONE);
                errorIndicator.setVisibility(View.VISIBLE);
                break;
        }
    }
}
