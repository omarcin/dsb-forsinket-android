package com.oczeretko.dsbforsinket.fragment;


import android.content.*;
import android.os.*;
import android.support.annotation.*;
import android.support.design.widget.*;
import android.support.v4.app.*;
import android.support.v7.preference.*;
import android.support.v7.widget.*;
import android.util.*;
import android.view.*;
import android.widget.*;

import com.google.android.gms.common.*;
import com.oczeretko.dsbforsinket.*;
import com.oczeretko.dsbforsinket.R;
import com.oczeretko.dsbforsinket.adapter.*;
import com.oczeretko.dsbforsinket.data.*;
import com.oczeretko.dsbforsinket.gcm.*;
import com.oczeretko.dsbforsinket.oss.*;
import com.oczeretko.dsbforsinket.utils.*;

import io.realm.*;

import static com.oczeretko.dsbforsinket.utils.CollectionsUtils.*;

public class PreferencesFragment extends Fragment implements StationPreferenceAdapter.Listener, RealmChangeListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "SettingsFragment";

    private static final String TAG_STATION_PICKER = "STATIONS";
    private RecyclerView recycler;
    private FloatingActionButton addButton;
    private Realm realm;
    private RealmResults<StationPreference> stations;
    private StationPreferenceAdapter adapter;
    private ProgressBar toolbarLoadingIndicator;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        android.preference.PreferenceManager.getDefaultSharedPreferences(getActivity())
                                            .edit()
                                            .putBoolean(Consts.PREF_VISITED_SETTINGS, true)
                                            .commit();
        toolbarLoadingIndicator = (ProgressBar)getActivity().findViewById(R.id.main_activity_toolbar_progress_bar);
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
        realm = Realm.getInstance(getContext());
        stations = realm.where(StationPreference.class).findAllSorted("id");
        stations.addChangeListener(this);
        adapter = new StationPreferenceAdapter(getContext());
        adapter.setStations(stations);
        adapter.setListener(this);
        recycler.setAdapter(adapter);
        PreferenceManager.getDefaultSharedPreferences(getContext())
                         .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        recycler.setAdapter(null);
        adapter = null;
        stations.removeChangeListener(this);
        realm.close();
        PreferenceManager.getDefaultSharedPreferences(getContext())
                         .unregisterOnSharedPreferenceChangeListener(this);
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recycler.setLayoutManager(layoutManager);
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

        if (isEnabled) {
            uncheckCurrentlyCheckedNotification();
        }

        realm.beginTransaction();
        preference.setNotificationEnabled(isEnabled);
        realm.copyToRealmOrUpdate(preference);
        realm.commitTransaction();
        adapter.notifyItemChanged(adapterPosition);
    }

    private void uncheckCurrentlyCheckedNotification() {
        StationPreference previousStation = realm.where(StationPreference.class).equalTo("notificationEnabled", true).findFirst();
        if (previousStation != null) {
            realm.beginTransaction();
            previousStation.setNotificationEnabled(false);
            adapter.notifyItemChanged(previousStation);
            realm.commitTransaction();
        }
    }

    @Override
    public void onChange() {
        StationPreference stationToNotify = firstOrNull(stations, s -> s.isNotificationEnabled());
        if (stationToNotify != null && checkPlayServices(true)) {
            String[] times = {"8:00", "8:15", "8:30", "8:45", "9:00", "10:45"}; // TODO
            GcmRegistrationIntentService.requestRegistration(getActivity(), stationToNotify.getStationId(), times);
            toolbarLoadingIndicator.setVisibility(View.VISIBLE);
        } else if (PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(Consts.PREF_POSSIBLY_REGISTERED, false) && checkPlayServices(false)) {
            GcmRegistrationIntentService.requestDeregistration(getActivity());
            toolbarLoadingIndicator.setVisibility(View.VISIBLE);
        }
    }

    private boolean checkPlayServices(boolean showErrorDialog) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(getActivity());
        if (resultCode != ConnectionResult.SUCCESS && showErrorDialog) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(getActivity(), resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                               .show();
            } else {
                Log.i(TAG, "This device is not supported.");
            }
            return false;
        }
        return true;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(Consts.PREF_UNHANDLED_REGISTRATION_ERROR) && sharedPreferences.getBoolean(Consts.PREF_UNHANDLED_REGISTRATION_ERROR, false)) {
            uncheckCurrentlyCheckedNotification();
        }
    }
}
