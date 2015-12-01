package com.oczeretko.dsbforsinket.fragment;

import android.content.*;
import android.os.*;
import android.support.annotation.*;
import android.support.design.widget.*;
import android.support.v4.app.*;
import android.support.v4.view.*;
import android.support.v7.preference.*;
import android.view.*;

import com.oczeretko.dsbforsinket.*;
import com.oczeretko.dsbforsinket.R;
import com.oczeretko.dsbforsinket.utils.*;

public class DeparturesPagerFragment extends Fragment {

    private static final String KEY_STATION_IDS = "STATIONS";

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private DeparturesPagerAdapter adapter;

    public static DeparturesPagerFragment newInstance(String[] stationIds) {
        Bundle args = new Bundle();
        args.putStringArray(KEY_STATION_IDS, stationIds);
        DeparturesPagerFragment fragment = new DeparturesPagerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String[] stationIds = preferences.getStringSet(getString(R.string.preferences_stations_key), Consts.STATIONS_DEFAULT).toArray(new String[0]);

        adapter = new DeparturesPagerAdapter(getContext(), getFragmentManager(), stationIds);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_departures_pager, container, false);
        viewPager = (ViewPager)view.findViewById(R.id.departures_pager_pager);
        tabLayout = (TabLayout)view.findViewById(R.id.departures_pager_tabs);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }

    private static class DeparturesPagerAdapter extends FragmentStatePagerAdapter {

        private final Context context;
        private final String[] stationIds;

        public DeparturesPagerAdapter(Context context, FragmentManager fm, String[] stationIds) {
            super(fm);
            this.context = context;
            this.stationIds = stationIds;
        }

        @Override
        public Fragment getItem(int position) {
            return DeparturesFragment.newInstance(stationIds[position]);
        }

        @Override
        public int getCount() {
            return stationIds.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return Stations.getStationNameById(context, stationIds[position]);
        }
    }
}
