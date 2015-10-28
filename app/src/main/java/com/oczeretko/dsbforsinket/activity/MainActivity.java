package com.oczeretko.dsbforsinket.activity;

import android.content.*;
import android.os.*;
import android.preference.*;
import android.support.design.widget.*;
import android.support.v4.app.*;
import android.support.v4.content.*;
import android.support.v4.view.*;
import android.support.v4.widget.*;
import android.support.v7.app.*;
import android.support.v7.widget.Toolbar;
import android.util.*;
import android.view.*;
import android.widget.*;

import com.oczeretko.dsbforsinket.*;
import com.oczeretko.dsbforsinket.fragment.*;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    private static final String TAG_FRAGMENT = "Fragment";

    private Toolbar toolbar;
    private ProgressBar toolbarLoadingIndicator;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private Class<? extends Fragment> currentFragmentClass;

    private BroadcastReceiver registrationBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            boolean sentToken = sharedPreferences.getBoolean(Consts.PREF_SENT_TOKEN_TO_SERVER, false);
            toolbarLoadingIndicator.setVisibility(View.GONE);
            Log.d(TAG, "sentToken = " + sentToken);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        setContentView(R.layout.activity_main);

        findViews();
        setupViews();

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT);
        if (fragment != null) {
            currentFragmentClass = fragment.getClass();
        } else {
            showFragment(DeparturesFragment.class);
        }
    }

    private void findViews() {
        toolbar = (Toolbar)findViewById(R.id.main_activity_toolbar);
        drawerLayout = (DrawerLayout)findViewById(R.id.main_activity_drawer);
        navigationView = (NavigationView)findViewById(R.id.main_activity_navigation);
        toolbarLoadingIndicator = (ProgressBar) findViewById(R.id.main_activity_toolbar_progress_bar);
    }

    private void setupViews() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this)
                             .registerReceiver(registrationBroadcastReceiver,
                                               new IntentFilter(Consts.REGISTRATION_COMPLETE));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this)
                             .unregisterReceiver(registrationBroadcastReceiver);
        super.onPause();
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
                showFragment(DeparturesFragment.class);
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
        } catch (InstantiationException e) {
            Log.e(TAG, "", e);
        } catch (IllegalAccessException e) {
            Log.e(TAG, "", e);
        }
    }
}
