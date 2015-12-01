package com.oczeretko.dsbforsinket.activity;

import android.content.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v4.app.*;
import android.support.v4.content.*;
import android.support.v4.view.*;
import android.support.v4.widget.*;
import android.support.v7.app.*;
import android.support.v7.preference.*;
import android.support.v7.widget.Toolbar;
import android.util.*;
import android.view.*;
import android.widget.*;

import com.oczeretko.dsbforsinket.*;
import com.oczeretko.dsbforsinket.R;
import com.oczeretko.dsbforsinket.fragment.*;
import com.oczeretko.dsbforsinket.receivers.*;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    private static final String TAG_FRAGMENT = "Fragment";
    public static final String EXTRA_SHOW_SETTINGS = "show_settings";

    private Handler handler = new Handler();

    private Toolbar toolbar;
    private ProgressBar toolbarLoadingIndicator;
    private DrawerLayout drawerLayout;
    private CoordinatorLayout coordinatorLayout;
    private NavigationView navigationView;
    private MenuItem menuItemDepartures;
    private Snackbar snackbar;

    private Class<? extends Fragment> currentFragmentClass;

    private BroadcastReceiver registrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        setContentView(R.layout.activity_main);

        findViews();
        setupViews();

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT);

        if (getIntent().getBooleanExtra(EXTRA_SHOW_SETTINGS, false)) {
            showFragment(SettingsFragment.class);
        } else if (fragment != null) {
            currentFragmentClass = fragment.getClass();
        } else {
            showFragment(DeparturesPagerFragment.class);
        }

        if (!checkSettingsVisited()) {
            showIntroSnackbarDelayed();
        }
    }

    private void findViews() {
        toolbar = (Toolbar)findViewById(R.id.main_activity_toolbar);
        drawerLayout = (DrawerLayout)findViewById(R.id.main_activity_drawer);
        navigationView = (NavigationView)findViewById(R.id.main_activity_navigation);
        toolbarLoadingIndicator = (ProgressBar)findViewById(R.id.main_activity_toolbar_progress_bar);
        menuItemDepartures = navigationView.getMenu().findItem(R.id.drawer_departures);
        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.main_activity_coordinator);
    }

    private void setupViews() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private boolean checkSettingsVisited() {
        return PreferenceManager.getDefaultSharedPreferences(this)
                                .getBoolean(Consts.PREF_VISITED_SETTINGS, false);
    }

    private void showIntroSnackbarDelayed() {
        int delay = getResources().getInteger(R.integer.snackbar_first_visit_delay_millis);
        handler.postDelayed(this::showIntroStackbar, delay);
    }

    private void showIntroStackbar() {
        snackbar = Snackbar.make(coordinatorLayout, R.string.snackbar_first_visit, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.snackbar_first_visit_action_settings, _1 -> showFragment(SettingsFragment.class));
        snackbar.show();
    }

    private void dismissSnackbarIfShown() {
        handler.removeCallbacksAndMessages(null);
        if (snackbar != null) {
            snackbar.dismiss();
            snackbar = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registrationBroadcastReceiver = new RegistrationBroadcastReceiver(toolbarLoadingIndicator);
        LocalBroadcastManager.getInstance(this)
                             .registerReceiver(registrationBroadcastReceiver,
                                               new IntentFilter(Consts.INTENT_ACTION_REGISTRATION_UPDATE));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this)
                             .unregisterReceiver(registrationBroadcastReceiver);
        registrationBroadcastReceiver = null;
        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getBooleanExtra(EXTRA_SHOW_SETTINGS, false)) {
            showFragment(SettingsFragment.class);
        } else {
            showFragment(DeparturesPagerFragment.class);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        drawerLayout.closeDrawer(GravityCompat.START);

        switch (menuItem.getItemId()) {
            case R.id.drawer_departures:
                showFragment(DeparturesPagerFragment.class);
                menuItem.setChecked(true);
                return true;
            case R.id.drawer_settings:
                showFragment(SettingsFragment.class);
                menuItem.setChecked(true);
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (currentFragmentClass == SettingsFragment.class) {
            showFragment(DeparturesPagerFragment.class);
            menuItemDepartures.setChecked(true);
        } else {
            super.onBackPressed();
        }
    }

    private void showFragment(Class<? extends Fragment> fragmentClass) {
        if (currentFragmentClass == fragmentClass) {
            return;
        }

        try {
            getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_activity_content, fragmentClass.newInstance(), TAG_FRAGMENT)
                .commit();
            currentFragmentClass = fragmentClass;
            dismissSnackbarIfShown();
        } catch (InstantiationException e) {
            Log.e(TAG, "", e);
        } catch (IllegalAccessException e) {
            Log.e(TAG, "", e);
        }
    }
}
