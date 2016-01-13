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
import com.oczeretko.dsbforsinket.data.*;
import com.oczeretko.dsbforsinket.fragment.*;
import com.oczeretko.dsbforsinket.receivers.*;
import com.oczeretko.dsbforsinket.utils.*;

import io.realm.*;

import static com.oczeretko.dsbforsinket.utils.CollectionsUtils.*;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, RealmChangeListener {

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
    private Realm realm;
    private RealmResults<StationPreference> stations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Stations.createDefaultIfNeeded(this);
        setContentView(R.layout.activity_main);

        realm = Realm.getInstance(this);
        stations = realm.where(StationPreference.class).findAllSorted("id");
        stations.addChangeListener(this);

        findViews();
        setupViews();
        setupDrawerMenu();
        setupFragment();

        if (!checkSettingsVisited()) {
            showIntroSnackbarDelayed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stations.removeChangeListeners();
        realm.close();
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

    private void setupDrawerMenu() {
        SubMenu departuresSubMenu = navigationView.getMenu().findItem(R.id.drawer_departures).getSubMenu();
        departuresSubMenu.clear();

        for (int i = 0; i < stations.size() && i < Consts.IDS.length; i++) {
            departuresSubMenu.add(0, Consts.IDS[i], 0, stations.get(i).getName());
        }
    }

    private void setupFragment() {
        Fragment fragment = getShownFragment();
        if (getIntent().getBooleanExtra(EXTRA_SHOW_SETTINGS, false)) {
            showFragment(PreferencesFragment.class);
        } else if (fragment != null) {
            currentFragmentClass = fragment.getClass();
        } else {
            showFragment(DeparturesPagerFragment.class);
        }
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
        snackbar.setAction(R.string.snackbar_first_visit_action_settings, _1 -> showFragment(PreferencesFragment.class));
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
            showFragment(PreferencesFragment.class);
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

        int tab = indexOf(Consts.IDS, menuItem.getItemId());
        if (tab >= 0) {
            DeparturesPagerFragment fragment = showFragment(DeparturesPagerFragment.class);
            handler.postDelayed(() -> fragment.showTab(tab), 100);
            return true;
        } else if (menuItem.getItemId() == R.id.drawer_settings) {
            showFragment(PreferencesFragment.class);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (currentFragmentClass == PreferencesFragment.class) {
            showFragment(DeparturesPagerFragment.class);
            menuItemDepartures.setChecked(true);
        } else {
            super.onBackPressed();
        }
    }

    private <F extends Fragment> F showFragment(Class<F> fragmentClass) {
        if (currentFragmentClass == fragmentClass) {
            return (F)getShownFragment();
        }

        try {
            Fragment fragment = fragmentClass.newInstance();
            getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_activity_content, fragment, TAG_FRAGMENT)
                .commit();
            currentFragmentClass = fragmentClass;
            dismissSnackbarIfShown();
            return (F)fragment;
        } catch (InstantiationException e) {
            Log.e(TAG, "", e);
        } catch (IllegalAccessException e) {
            Log.e(TAG, "", e);
        }

        return null; // should not happen
    }

    private Fragment getShownFragment() {
        return getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT);
    }

    @Override
    public void onChange() {
        setupDrawerMenu();
    }
}
