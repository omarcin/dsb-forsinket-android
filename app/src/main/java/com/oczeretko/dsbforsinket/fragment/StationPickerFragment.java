package com.oczeretko.dsbforsinket.fragment;


import android.app.*;
import android.content.*;
import android.os.*;
import android.support.annotation.*;
import android.support.v4.app.DialogFragment;
import android.support.v4.util.*;
import android.support.v7.app.AlertDialog;
import android.view.*;
import android.widget.*;

import com.oczeretko.dsbforsinket.*;
import com.oczeretko.dsbforsinket.utils.*;

import java.util.*;

public class StationPickerFragment extends DialogFragment {

    private Listener listener;
    private Adapter adapter;

    public static StationPickerFragment newInstance(Listener listener){
        StationPickerFragment fragment = new StationPickerFragment();
        fragment.setListener(listener);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.preferences_station_picker_title);
        builder.setCancelable(true);

        List<Pair<String, String>> stationNames = Stations.getStations(getContext());
        adapter = new Adapter(getContext(), stationNames);
        builder.setAdapter(adapter, this::onItemSelected);

        return builder.create();
    }

    private void onItemSelected(DialogInterface dialog, int position) {
        if (listener != null) {
            Pair<String, String> selectedItem = adapter.getItem(position);
            listener.onStationPicked(selectedItem.first);
        }

        dialog.dismiss();
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        void onStationPicked(String stationId);
    }

    private static class Adapter extends ArrayAdapter<Pair<String, String>> {
        private static final int LAYOUT_RESOURCE = android.R.layout.simple_list_item_1;

        public Adapter(Context context, List<Pair<String, String>> objects) {
            super(context, LAYOUT_RESOURCE, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView)convertView;
            if (convertView == null) {
                view = (TextView)LayoutInflater.from(getContext()).inflate(LAYOUT_RESOURCE, parent, false);
            }

            view.setText(getItem(position).second);
            return view;
        }
    }
}
