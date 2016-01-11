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

public class PreferencesFragment extends Fragment implements StationPreferenceAdapter.Listener {

    private static final String TAG_STATION_PICKER = "STATIONS";
    private RecyclerView recycler;
    private FloatingActionButton addButton;
    private Realm realm;
    private StationPreferenceAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        android.preference.PreferenceManager.getDefaultSharedPreferences(getActivity())
                                            .edit()
                                            .putBoolean(Consts.PREF_VISITED_SETTINGS, true)
                                            .commit();
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
        RealmConfiguration configuration = new RealmConfiguration.Builder(getActivity())
                                               .deleteRealmIfMigrationNeeded()
                                               .build();
        realm = Realm.getInstance(configuration);
        RealmResults<StationPreference> stations = realm.where(StationPreference.class).findAllSorted("id");
        adapter.setStations(stations);
    }

    @Override
    public void onStop() {
        super.onStop();
        recycler.setAdapter(null);
        adapter = null;
        realm.close();
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        adapter = new StationPreferenceAdapter(getContext());
        adapter.setListener(this);
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
        Number maxId = realm.where(StationPreference.class).max("id");
        int newId = maxId != null ? maxId.intValue() + 1 : 1;
        StationPreference preference = realm.createObject(StationPreference.class);
        preference.setId(newId);
        preference.setName(Stations.getStationNameById(getContext(), stationId));
        preference.setStationId(stationId);
        realm.commitTransaction();

        adapter.notifyItemInserted(adapter.getItemCount() - 1);
        recycler.smoothScrollToPosition(adapter.getItemCount() - 1);
    }

    @Override
    public void onDeleteClick(int adapterPosition, StationPreference preference) {
        realm.beginTransaction();
        preference.removeFromRealm();
        realm.commitTransaction();
        adapter.notifyItemRemoved(adapterPosition);
    }

    @Override
    public void onNotificationChange(int adapterPosition, StationPreference preference, boolean isEnabled) {
        realm.beginTransaction();
        preference.setNotificationEnabled(isEnabled);
        realm.copyToRealmOrUpdate(preference);
        realm.commitTransaction();
        adapter.notifyItemChanged(adapterPosition);
    }
}
