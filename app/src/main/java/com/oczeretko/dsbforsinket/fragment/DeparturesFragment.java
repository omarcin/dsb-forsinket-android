package com.oczeretko.dsbforsinket.fragment;


import android.os.Bundle;
import android.support.v4.app.*;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oczeretko.dsbforsinket.*;


public class DeparturesFragment extends Fragment {

    public DeparturesFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_departures, container, false);
    }
}
