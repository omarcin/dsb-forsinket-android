package com.oczeretko.dsbforsinket.fragment;

import android.content.*;
import android.os.*;
import android.support.annotation.*;
import android.support.design.widget.*;
import android.support.v4.app.*;
import android.support.v4.view.*;
import android.view.*;

import com.oczeretko.dsbforsinket.*;
import com.oczeretko.dsbforsinket.utils.*;

public class DeparturesPagerFragment extends Fragment {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private DeparturesPagerAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String[] stationIds = Stations.getSelectedStationIds(getContext());
        adapter = new DeparturesPagerAdapter(getContext(), getChildFragmentManager(), stationIds);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_departures_pager, container, false);
        viewPager = (ViewPager)view.findViewById(R.id.departures_pager_pager);
        tabLayout = (TabLayout)view.findViewById(R.id.departures_pager_tabs);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(getResources().getInteger(R.integer.pager_offscreen_limit));
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }

    public void showTab(int tab) {
        if (viewPager.getAdapter().getCount() > tab) {
            viewPager.setCurrentItem(tab, true);
        }
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
