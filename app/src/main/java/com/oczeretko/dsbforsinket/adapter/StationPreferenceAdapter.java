package com.oczeretko.dsbforsinket.adapter;

import android.support.v7.widget.*;
import android.view.*;
import android.widget.*;

import com.oczeretko.dsbforsinket.*;
import com.oczeretko.dsbforsinket.data.*;

import java.util.*;

public class StationPreferenceAdapter extends RecyclerView.Adapter<StationPreferenceAdapter.ViewHolder> {
    private static final int LAYOUT_RESOURCE = R.layout.fragment_preferences_item;
    private final List<StationPreference> items;

    public StationPreferenceAdapter(List<StationPreference> items) {
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(LAYOUT_RESOURCE, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        StationPreference preference = items.get(position);
        holder.name.setText(preference.getName());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        protected final TextView name;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView)itemView.findViewById(R.id.fragment_preferences_item_station_name);
        }
    }
}
