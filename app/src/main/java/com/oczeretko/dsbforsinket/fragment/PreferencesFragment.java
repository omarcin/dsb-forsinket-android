package com.oczeretko.dsbforsinket.fragment;


import android.os.*;
import android.support.annotation.*;
import android.support.design.widget.*;
import android.support.v4.app.*;
import android.support.v7.widget.*;
import android.view.*;

import com.oczeretko.dsbforsinket.*;
import com.oczeretko.dsbforsinket.adapter.*;
import com.oczeretko.dsbforsinket.data.*;
import com.oczeretko.dsbforsinket.oss.*;
import com.oczeretko.dsbforsinket.utils.*;

import io.realm.*;

public class PreferencesFragment extends Fragment {

    private static final String TAG_STATION_PICKER = "STATIONS";
    private RecyclerView recycler;
    private FloatingActionButton addButton;
    private Realm realm;
    private StationPreferenceAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getInstance(getActivity()); // TODO
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_preferences, container, false);
        addButton = (FloatingActionButton)view.findViewById(R.id.fragment_preferences_add);
        addButton.setOnClickListener(this::onAddClick);
        recycler = (RecyclerView)view.findViewById(R.id.fragment_preferences_recycler);
        setupRecyclerView();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        realm = Realm.getInstance(getActivity());
    }

    @Override
    public void onStop() {
        super.onStop();
        realm.close();
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        RealmResults<StationPreference> stations = realm.where(StationPreference.class).findAllSorted("position");
        adapter = new StationPreferenceAdapter(stations);
        recycler.setLayoutManager(layoutManager);
        recycler.setAdapter(adapter);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST);
        recycler.addItemDecoration(itemDecoration);
    }

    private void onAddClick(View view) {
        StationPickerFragment picker = new StationPickerFragment();
        picker.setListener(this::addStation);
        picker.show(getChildFragmentManager(), TAG_STATION_PICKER);
    }

    private void addStation(String stationId) {
        realm.beginTransaction();
        int position = (int)(realm.where(StationPreference.class).count() + 1);
        StationPreference preference = realm.createObject(StationPreference.class);
        preference.setPosition(position);
        preference.setName(Stations.getStationNameById(getContext(), stationId));
        preference.setId(stationId);
        realm.commitTransaction();
        adapter.notifyItemInserted(position);
        recycler.smoothScrollToPosition(adapter.getItemCount() - 1);
    }
}
