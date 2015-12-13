package com.oczeretko.dsbforsinket.adapter;

import android.support.v7.widget.*;
import android.util.*;
import android.view.*;
import android.widget.*;

import com.oczeretko.dsbforsinket.*;
import com.oczeretko.dsbforsinket.data.*;

import java.util.*;

public class StationPreferenceAdapter extends RecyclerView.Adapter<StationPreferenceAdapter.ViewHolder> {
    private static final int LAYOUT_RESOURCE = R.layout.fragment_preferences_item;
    private static final int TAG_POSITION = R.string.tag_position;
    private static final int TAG_HOLDER = R.string.tag_holder;

    private final List<StationPreference> items;
    private final SparseBooleanArray isExpanded;

    public StationPreferenceAdapter(List<StationPreference> items) {
        this.items = items;
        isExpanded = new SparseBooleanArray();
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
        boolean isItemExpanded = isExpanded.get(position);
        bindExpandedLayoutState(holder, isItemExpanded);

        holder.expand.setTag(TAG_POSITION, position);
        holder.expand.setTag(TAG_HOLDER, holder);
        holder.expand.setOnClickListener(this::onExpandCollapseClick);
    }

    private void onExpandCollapseClick(View view) {
        int position = (int)view.getTag(TAG_POSITION);
        ViewHolder holder = (ViewHolder)view.getTag(TAG_HOLDER);
        boolean wasExpanded = isExpanded.get(position);
        isExpanded.put(position, !wasExpanded);
        bindExpandedLayoutState(holder, !wasExpanded);
    }

    private void bindExpandedLayoutState(ViewHolder holder, boolean isItemExpanded) {
        holder.expandedLayout.setVisibility(isItemExpanded ? View.VISIBLE : View.GONE);
        holder.delete.setVisibility(isItemExpanded ? View.VISIBLE : View.GONE);
        holder.expand.setImageResource(isItemExpanded ? R.drawable.ic_chevron_up_grey600_24dp : R.drawable.ic_chevron_down_grey600_24dp);
        holder.status.setVisibility(isItemExpanded ? View.GONE : View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        protected final TextView name;
        protected final View expandedLayout;
        protected final ImageButton expand;
        protected final ImageButton delete;
        protected final View status;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView)itemView.findViewById(R.id.fragment_preferences_item_station_name);
            expandedLayout = itemView.findViewById(R.id.fragment_preferences_item_expanded_layout);
            expand = (ImageButton)itemView.findViewById(R.id.fragment_preferences_item_expand_btn);
            delete = (ImageButton)itemView.findViewById(R.id.fragment_preferences_item_delete_btn);
            status = itemView.findViewById(R.id.fragment_preferences_item_notification_status);
        }
    }
}
