package com.oczeretko.dsbforsinket.fragment;

import android.app.*;
import android.content.*;
import android.os.*;
import android.support.annotation.*;
import android.support.v4.app.DialogFragment;
import android.widget.*;

import com.oczeretko.dsbforsinket.*;
import com.oczeretko.dsbforsinket.utils.*;

import java.util.*;

public class TimePickerFragment extends DialogFragment implements DialogInterface.OnClickListener {

    private final static String ARG_STATION_ID = "StationId";
    private final static String ARG_TIMES = "Times";

    private Adapter adapter;
    private boolean[] checkedItems;
    private int stationId;
    private Listener listener;

    public static TimePickerFragment newInstance(Listener listener, int stationId, String[] times) {
        TimePickerFragment fragment = new TimePickerFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_STATION_ID, stationId);
        bundle.putStringArray(ARG_TIMES, times);
        fragment.setArguments(bundle);
        fragment.setListener(listener);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getContext());
        builder.setTitle(R.string.preferences_notification_times_title);
        builder.setCancelable(true);

        Bundle arguments = getArguments();

        stationId = arguments.getInt(ARG_STATION_ID);
        String[] timesSelected = arguments.getStringArray(ARG_TIMES);
        String[] timesValues = Times.getTimesValues(getContext());
        String[] timesNames = Times.getTimesNames(getContext());
        checkedItems = new boolean[timesNames.length];
        for (String time : timesSelected) {
            int index = CollectionsUtils.indexOf(timesValues, time);
            if (index > 0) {
                checkedItems[index] = true;
            }
        }

        builder.setMultiChoiceItems(timesNames, checkedItems, (dialog, which, isChecked) -> {
            checkedItems[which] = isChecked;
        });

        builder.setPositiveButton(R.string.preferences_notification_times_ok, this);
        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

        String[] timesValues = Times.getTimesValues(getContext());
        ArrayList<String> pickedTimes = new ArrayList<>();

        for (int i = 0; i < checkedItems.length; i++) {
            if (checkedItems[i]) {
                pickedTimes.add(timesValues[i]);
            }
        }

        listener.onTimesPicked(stationId, pickedTimes.toArray(new String[pickedTimes.size()]));
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        void onTimesPicked(int stationId, String[] times);
    }
}
