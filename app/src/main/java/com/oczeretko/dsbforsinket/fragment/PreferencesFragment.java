package com.oczeretko.dsbforsinket.fragment;


import android.os.*;
import android.support.design.widget.*;
import android.support.v4.app.*;
import android.support.v7.widget.*;
import android.view.*;

import com.oczeretko.dsbforsinket.*;
import com.oczeretko.dsbforsinket.adapter.*;

public class PreferencesFragment extends Fragment {

    private RecyclerView recycler;
    private FloatingActionButton addButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_preferences, container, false);
        addButton = (FloatingActionButton)view.findViewById(R.id.fragment_preferences_add);
        addButton.setOnClickListener(this::onAddClick);
        recycler = (RecyclerView)view.findViewById(R.id.fragment_preferences_recycler);
        setupRecyclerView();
        return view;
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        StationPreferenceAdapter adapter = new StationPreferenceAdapter();
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(layoutManager);
        recycler.setAdapter(adapter);
    }

    private void onAddClick(View view) {
    }
}
