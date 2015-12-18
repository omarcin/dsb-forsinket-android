package com.oczeretko.dsbforsinket.adapter;

import android.content.*;
import android.content.res.*;
import android.os.*;
import android.support.v7.widget.*;
import android.util.*;
import android.view.*;
import android.widget.*;

import com.oczeretko.dsbforsinket.*;
import com.oczeretko.dsbforsinket.data.*;

import java.util.*;

import static com.oczeretko.dsbforsinket.utils.AnimUtils.*;

public class StationPreferenceAdapter extends RecyclerView.Adapter<StationPreferenceAdapter.ViewHolder> {
    private static final int LAYOUT_RESOURCE = R.layout.fragment_preferences_item;
    private static final int IMG_UP = R.drawable.ic_chevron_up_grey600_24dp;
    private static final int IMG_DOWN = R.drawable.ic_chevron_down_grey600_24dp;
    private static final int TAG_HOLDER = R.string.tag_holder;
    private static final int TAG_ITEM = R.string.tag_item;

    private final List<StationPreference> items;
    private final SparseBooleanArray isExpanded;
    private final int animationDuration;
    private final int expandedElevation;

    private Listener listener;

    public StationPreferenceAdapter(Context context, List<StationPreference> items) {
        this.items = items;
        isExpanded = new SparseBooleanArray();
        Resources resources = context.getResources();
        animationDuration = resources.getInteger(R.integer.animation_duration_expand);
        expandedElevation = resources.getInteger(R.integer.elevation_item);
    }

    @Override
    public int getItemCount() {
        return items.size();
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
        boolean isItemExpanded = isExpanded.get(preference.getPosition());
        bindExpandedLayoutState(holder, isItemExpanded);

        holder.expand.setImageResource(isItemExpanded ? IMG_UP : IMG_DOWN);
        holder.expand.setTag(TAG_ITEM, preference);
        holder.expand.setTag(TAG_HOLDER, holder);
        holder.expand.setOnClickListener(this::onExpandCollapseClick);

        holder.delete.setOnClickListener(this::onDeleteClick);
        holder.delete.setTag(TAG_ITEM, preference);
    }

    private void onExpandCollapseClick(View view) {
        ViewHolder holder = (ViewHolder)view.getTag(TAG_HOLDER);
        StationPreference preference = (StationPreference)view.getTag(TAG_ITEM);
        boolean wasExpanded = isExpanded.get(preference.getPosition());
        isExpanded.put(preference.getPosition(), !wasExpanded);
        bindExpandedLayoutState(holder, !wasExpanded);
        // holder.expand.animate()
        //              .rotation(180f)
        //              .setDuration(animationDuration)
        //              .setListener(onEnd(() -> holder.expand.setImageResource(wasExpanded ? IMG_DOWN : IMG_UP)))
        //              .start();
        holder.expand.setImageResource(wasExpanded ? IMG_DOWN : IMG_UP);
    }

    private void onDeleteClick(View view) {
        StationPreference preference = (StationPreference)view.getTag(TAG_ITEM);
        int itemPosition = Collections.binarySearch(items, preference, StationPreference::compare);
        if (listener != null) {
            listener.onDeleteClick(itemPosition, preference);
        }
    }

    private void bindExpandedLayoutState(ViewHolder holder, boolean isItemExpanded) {
        holder.expandedLayout.setVisibility(isItemExpanded ? View.VISIBLE : View.GONE);
        holder.delete.setVisibility(isItemExpanded ? View.VISIBLE : View.GONE);
        holder.status.setVisibility(isItemExpanded ? View.GONE : View.VISIBLE);
        // TODO: holder.itemView.setBackgroundResource();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.expandedLayout.setElevation(isItemExpanded ? expandedElevation : 0);
        }
    }

    public void setListener(Listener listener) {
        this.listener = listener;
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

    public interface Listener {
        void onDeleteClick(int adapterPosition, StationPreference preference);
    }
}
