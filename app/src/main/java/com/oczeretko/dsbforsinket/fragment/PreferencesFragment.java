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

public class PreferencesFragment extends Fragment
    implements StationPreferenceAdapter.Listener, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "PreferencesFragment";

    private static final String TAG_STATION_PICKER = "STATIONS";
    private static final String TAG_TIME_PICKER = "TIMES";

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
        stations = null;
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
        StationPickerFragment.newInstance(this::addStation)
                             .show(getChildFragmentManager(), TAG_STATION_PICKER);
    }

    private void addStation(String stationId) {
        realm.beginTransaction();
        Number maxId = realm.where(StationPreference.class).max("id");
        int newId = maxId != null ? maxId.intValue() + 1 : 1;
        StationPreference preference = realm.createObject(StationPreference.class);
        preference.setId(newId);
        preference.setName(Stations.getStationNameById(getContext(), stationId));
        preference.setStationId(stationId);
        preference.setTimes(Times.getDefault(getContext()));
        realm.commitTransaction();

        adapter.notifyItemInserted(adapter.getItemCount() - 1);
        recycler.smoothScrollToPosition(adapter.getItemCount() - 1);
    }

    @Override
    public void onDeleteClick(int adapterPosition, StationPreference preference) {
        boolean updateRegistrations = preference.isNotificationEnabled();
        realm.beginTransaction();
        preference.removeFromRealm();
        realm.commitTransaction();
        adapter.notifyItemRemoved(adapterPosition);

        if (updateRegistrations) {
            updateRegistrations();
        }
    }

    @Override
    public void onNotificationChange(int adapterPosition, StationPreference preference, boolean isEnabled) {

        Log.d(TAG, "onNotificationChange");

        if (isEnabled) {
            uncheckCurrentlyCheckedNotification();
        }

        realm.beginTransaction();
        preference.setNotificationEnabled(isEnabled);
        realm.copyToRealmOrUpdate(preference);
        realm.commitTransaction();
        adapter.notifyItemChanged(adapterPosition);

        updateRegistrations();
    }

    @Override
    public void onTimesClick(int adapterPosition, StationPreference preference) {
        TimePickerFragment.newInstance(this::onTimesPicked, preference.getId(), preference.getTimes())
                          .show(getChildFragmentManager(), TAG_TIME_PICKER);
    }

    private void onTimesPicked(int stationId, String[] times) {
        realm.beginTransaction();
        StationPreference stationPreference = realm.where(StationPreference.class).equalTo("id", stationId).findFirst();
        stationPreference.setTimes(times);
        realm.commitTransaction();
        adapter.notifyItemChanged(stationPreference);
        updateRegistrations();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(Consts.PREF_UNHANDLED_REGISTRATION_ERROR) && sharedPreferences.getBoolean(Consts.PREF_UNHANDLED_REGISTRATION_ERROR, false)) {
            Log.d(TAG, "onSharedPrefChange");
            uncheckCurrentlyCheckedNotification();
        }
    }

    private void uncheckCurrentlyCheckedNotification() {
        Log.d(TAG, "uncheckCurrentlyCheckedNotification");
        StationPreference previousStation = realm.where(StationPreference.class).equalTo("notificationEnabled", true).findFirst();
        if (previousStation != null) {
            realm.beginTransaction();
            previousStation.setNotificationEnabled(false);
            adapter.notifyItemChanged(previousStation);
            realm.commitTransaction();
        }
    }

    private void updateRegistrations() {
        Log.d(TAG, "updateRegistrations");
        StationPreference stationToNotify = firstOrNull(stations, s -> s.isNotificationEnabled());
        String[] times = stationToNotify == null ? null : stationToNotify.getTimes();
        if (times != null && times.length > 0 && checkPlayServices(true)) {
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
}
