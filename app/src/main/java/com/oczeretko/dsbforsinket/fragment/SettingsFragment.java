package com.oczeretko.dsbforsinket.fragment;


import android.content.*;
import android.os.*;
import android.support.v7.preference.*;
import android.util.*;
import android.view.*;
import android.widget.*;

import com.google.android.gms.common.*;
import com.oczeretko.dsbforsinket.*;
import com.oczeretko.dsbforsinket.R;
import com.oczeretko.dsbforsinket.gcm.*;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "SettingsFragment";

    private ProgressBar toolbarLoadingIndicator;
    private String keyNotification;
    private String keyStation;

    public SettingsFragment() {
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.preferences);
        keyNotification = getString(R.string.preferences_notification_key);
        keyStation = getString(R.string.preferences_station_key);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        toolbarLoadingIndicator = (ProgressBar)getActivity().findViewById(R.id.main_activity_toolbar_progress_bar);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (!key.equals(keyNotification) && !key.equals(keyStation)) {
            return;
        }

        if (sharedPreferences.getBoolean(keyNotification, false)) {
            if (checkPlayServices(true)) {
                String station = sharedPreferences.getString(keyStation, Consts.STATION_DEFAULT);
                GcmRegistrationIntentService.requestRegistration(getActivity(), station, Consts.TIMES_DEFAULT);
                toolbarLoadingIndicator.setVisibility(View.VISIBLE);
            }
        } else if (sharedPreferences.getBoolean(Consts.PREF_POSSIBLY_REGISTERED, false) && checkPlayServices(false)) {
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
