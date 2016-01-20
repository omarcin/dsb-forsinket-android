package com.oczeretko.dsbforsinket.adapter;

import android.animation.*;
import android.content.*;
import android.content.res.*;
import android.os.*;
import android.support.v4.content.*;
import android.support.v4.view.*;
import android.support.v7.widget.*;
import android.view.*;
import android.view.animation.*;
import android.widget.*;

import com.oczeretko.dsbforsinket.*;
import com.oczeretko.dsbforsinket.data.*;
import com.oczeretko.dsbforsinket.utils.*;

import java.util.*;

import static com.oczeretko.dsbforsinket.utils.CollectionsUtils.*;
import static com.oczeretko.dsbforsinket.utils.ViewUtils.*;

public class StationPreferenceAdapter extends RecyclerView.Adapter<StationPreferenceAdapter.ViewHolder> {
    private static final int LAYOUT_RESOURCE = R.layout.fragment_preferences_item;
    private static final int TAG_HOLDER = R.string.tag_holder;

    private List<StationPreference> items = new ArrayList<>();
    private final int animationDuration;
    private final int expandedElevation;
    private final Resources resources;
    private final Context context;

    private Listener listener;
    private int expandedId;
    private int INVALID_ID = -100;
    private ViewHolder expandedViewHolder;
    private float expandDeceleration;
    private float collapseDeceleration;

    public StationPreferenceAdapter(Context context) {
        this.context = context;
        resources = context.getResources();
        animationDuration = resources.getInteger(R.integer.animation_duration_expand);
        expandedElevation = resources.getInteger(R.integer.elevation_item);
        expandDeceleration = ((float)resources.getInteger(R.integer.animation_expand_deceleration)) / 10f;
        collapseDeceleration = ((float)resources.getInteger(R.integer.animation_collapse_deceleration)) / 10f;
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
        holder.preference = items.get(position);

        holder.itemView.setTag(TAG_HOLDER, holder);
        holder.itemView.setOnClickListener(this::onExpandCollapseClick);

        holder.name.setText(holder.preference.getName());
        holder.expand.setTag(TAG_HOLDER, holder);
        holder.expand.setOnClickListener(this::onExpandCollapseClick);
        holder.delete.setTag(TAG_HOLDER, holder);
        holder.delete.setOnClickListener(this::onDeleteClick);

        if (holder.preference.isNotificationEnabled()) {
            holder.status.setText("TODO CHANGE ME");
        } else {
            holder.status.setText(R.string.status_notification_disabled);
        }

        holder.notifications.setOnCheckedChangeListener(null);
        holder.notifications.setChecked(holder.preference.isNotificationEnabled());
        holder.notifications.setTag(TAG_HOLDER, holder);
        holder.notifications.setOnCheckedChangeListener(this::onNotificationCheckedChange);

        holder.notificationTimes.setTag(TAG_HOLDER, holder);
        holder.notificationTimes.setOnClickListener(this::onTimesClick);

        if (holder.preference.isNotificationEnabled()) {
            if (holder.preference.getTimes().length == 0) {
                holder.notificationTimes.setText("Set the times");
            } else {
                List<String> strings = map(Arrays.asList(holder.preference.getTimes()), v -> Times.getNameByValue(context, v));
                holder.notificationTimes.setText(StringUtils.join(" ", strings));
            }
            holder.notificationTimes.setVisibility(View.VISIBLE);
        } else {
            holder.notificationTimes.setVisibility(View.GONE);
        }

        boolean isItemExpanded = isExpanded(holder.preference);
        bindExpandedLayout(holder, isItemExpanded);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.itemView.setElevation(isItemExpanded ? expandedElevation : 0);
        }
    }

    private void onTimesClick(View view) {
        ViewHolder holder = (ViewHolder)view.getTag(TAG_HOLDER);
        if (listener != null) {
            int position = findCurrentAdapterPosition(holder.preference);
            listener.onTimesClick(position, holder.preference);
        }
    }

    private void onNotificationCheckedChange(View view, boolean isChecked) {
        ViewHolder holder = (ViewHolder)view.getTag(TAG_HOLDER);
        if (listener != null) {
            int position = findCurrentAdapterPosition(holder.preference);
            listener.onNotificationChange(position, holder.preference, isChecked);
        }
    }

    private void bindExpandedLayout(ViewHolder holder, boolean isItemExpanded) {
        holder.itemView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        holder.getExpandedLayoutParams().setMarginTop(0);
        if (isItemExpanded) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.colorPreferenceAccent));
            expandedViewHolder = holder;
            holder.expandedLayout.setVisibility(View.VISIBLE);
            holder.delete.setVisibility(View.VISIBLE);
            holder.delete.setAlpha(1f);
            holder.status.setVisibility(View.GONE);
            holder.expand.setRotation(180f);
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.white));
            holder.expandedLayout.setVisibility(View.GONE);
            holder.delete.setVisibility(View.GONE);
            holder.status.setVisibility(View.VISIBLE);
            holder.status.setAlpha(1f);
            holder.expand.setRotation(0f);
        }
    }

    private void onExpandCollapseClick(View view) {
        ViewHolder holder = (ViewHolder)view.getTag(TAG_HOLDER);
        boolean isExpanded = isExpanded(holder.preference);

        if (!isExpanded) {
            expandItem(holder);
        } else {
            collapseItem(holder);
        }
    }

    private boolean isExpanded(StationPreference preference) {
        return expandedId == preference.getId();
    }

    private void onDeleteClick(View view) {
        ViewHolder holder = (ViewHolder)view.getTag(TAG_HOLDER);
        if (listener != null) {
            int itemPosition = findCurrentAdapterPosition(holder.preference);
            listener.onDeleteClick(itemPosition, holder.preference);
        }
    }

    public void notifyItemChanged(StationPreference station) {
        notifyItemChanged(findCurrentAdapterPosition(station));
    }

    private int findCurrentAdapterPosition(StationPreference preference) {
        return Collections.binarySearch(items, preference, StationPreference::compare);
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
        if (expandedViewHolder == holder) {
            expandedViewHolder = null;
        }
    }

    private void expandItem(final ViewHolder holder) {

        if (expandedViewHolder != null && expandedViewHolder != holder && expandedId != holder.preference.getId()) {
            collapseItem(expandedViewHolder);
        }

        expandedId = holder.preference.getId();
        expandedViewHolder = holder;

        final int startingHeight = holder.itemView.getHeight();
        holder.expandedLayout.setVisibility(View.VISIBLE);
        holder.delete.setVisibility(View.VISIBLE);

        final ViewTreeObserver observer = holder.itemView.getViewTreeObserver();
        onPreDrawExecuteOnce(observer, () -> {

            final int endingHeight = holder.itemView.getHeight();
            final int distance = endingHeight - startingHeight;

            holder.itemView.getLayoutParams().height = startingHeight;
            holder.getExpandedLayoutParams().setMarginTop(-distance);
            holder.itemView.requestLayout();
            ViewCompat.setHasTransientState(holder.itemView, true);

            AnimatorSet set = new AnimatorSet();
            set.setDuration(animationDuration).setInterpolator(new DecelerateInterpolator(expandDeceleration));
            ObjectAnimator itemHeight = ObjectAnimator.ofInt(LayoutParamsWrapper.from(holder.itemView.getLayoutParams()), "height", startingHeight, startingHeight + distance);
            itemHeight.addUpdateListener(AnimUtils.onUpdate(holder.itemView::requestLayout));
            ObjectAnimator buttonRotation = ObjectAnimator.ofFloat(holder.expand, "rotation", 0f, 180f);
            ObjectAnimator statusAlpha = ObjectAnimator.ofFloat(holder.status, "alpha", 1f, 0f);
            ObjectAnimator deleteAlpha = ObjectAnimator.ofFloat(holder.delete, "alpha", 0f, 1f);
            ObjectAnimator backgroundColor = AnimatorCompat.ofArgb(holder.itemView, "backgroundColor", 0, ContextCompat.getColor(holder.itemView.getContext(), R.color.colorPreferenceAccent));
            ObjectAnimator expandMargins = ObjectAnimator.ofInt(holder.getExpandedLayoutParams(), "marginTop", -distance, 0);
            ObjectAnimator elevation = ObjectAnimator.ofFloat(holder.itemView, "elevation", 0f, expandedElevation);
            set.playTogether(backgroundColor, elevation, expandMargins, buttonRotation, statusAlpha, deleteAlpha, itemHeight);
            set.addListener(AnimUtils.onEnd(() -> {
                bindExpandedLayout(holder, true);
                ViewCompat.setHasTransientState(holder.itemView, false);
            }));
            set.start();
            return false;
        });
    }

    private void collapseItem(ViewHolder holder) {
        expandedId = INVALID_ID;
        expandedViewHolder = null;

        final int startingHeight = holder.itemView.getHeight();
        holder.expandedLayout.setVisibility(View.GONE);

        final ViewTreeObserver observer = holder.itemView.getViewTreeObserver();
        onPreDrawExecuteOnce(observer, () -> {

            final int endingHeight = holder.itemView.getHeight();
            final int distance = endingHeight - startingHeight;

            holder.expandedLayout.setVisibility(View.VISIBLE);
            holder.delete.setVisibility(View.GONE);
            holder.status.setVisibility(View.VISIBLE);
            holder.status.setAlpha(1f);
            ViewCompat.setHasTransientState(holder.itemView, true);

            AnimatorSet set = new AnimatorSet();
            set.setDuration(animationDuration).setInterpolator(new DecelerateInterpolator(collapseDeceleration));
            ObjectAnimator itemHeight = ObjectAnimator.ofInt(LayoutParamsWrapper.from(holder.itemView.getLayoutParams()), "height", startingHeight, startingHeight + distance);
            itemHeight.addUpdateListener(AnimUtils.onUpdate(holder.itemView::requestLayout));
            ObjectAnimator buttonRotation = ObjectAnimator.ofFloat(holder.expand, "rotation", 0f);
            ObjectAnimator statusAlpha = ObjectAnimator.ofFloat(holder.status, "alpha", 1f);
            ObjectAnimator deleteAlpha = ObjectAnimator.ofFloat(holder.delete, "alpha", 0f);
            ObjectAnimator backgroundColor = AnimatorCompat.ofArgb(holder.itemView, "backgroundColor", ContextCompat.getColor(holder.itemView.getContext(), R.color.colorPreferenceAccent), 0);
            ObjectAnimator expandMargins = ObjectAnimator.ofInt(holder.getExpandedLayoutParams(), "marginTop", -distance, 0);
            ObjectAnimator itemElevation = ObjectAnimator.ofFloat(holder.itemView, "elevation", expandedElevation, 0f);
            set.playTogether(backgroundColor, itemElevation, expandMargins, buttonRotation, statusAlpha, deleteAlpha, itemHeight);
            set.addListener(AnimUtils.onEnd(() -> {
                bindExpandedLayout(holder, false);
                ViewCompat.setHasTransientState(holder.itemView, false);
            }));
            set.start();
            return false;
        });
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void setStations(List<StationPreference> stations) {
        items = stations;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        protected final TextView name;
        protected final View root;
        protected final View expandedLayout;
        protected final View bottomLayout;
        protected final ImageButton expand;
        protected final ImageButton delete;
        protected final TextView status;
        protected final CheckBox notifications;
        protected final Button notificationTimes;
        protected StationPreference preference;

        public ViewHolder(View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.fragment_preferences_item_root);
            name = (TextView)itemView.findViewById(R.id.fragment_preferences_item_station_name);
            expandedLayout = itemView.findViewById(R.id.fragment_preferences_item_expanded_layout);
            bottomLayout = itemView.findViewById(R.id.fragment_preferences_item_bottom_layout);
            expand = (ImageButton)itemView.findViewById(R.id.fragment_preferences_item_expand_btn);
            delete = (ImageButton)itemView.findViewById(R.id.fragment_preferences_item_delete_btn);
            status = (TextView)itemView.findViewById(R.id.fragment_preferences_item_notification_status);
            notifications = (CheckBox)itemView.findViewById(R.id.fragment_preferences_item_notifications);
            notificationTimes = (Button)itemView.findViewById(R.id.fragment_preferences_item_times);
        }

        public LayoutParamsWrapper getExpandedLayoutParams() {
            return LayoutParamsWrapper.from(expandedLayout.getLayoutParams());
        }
    }

    public interface Listener {
        void onDeleteClick(int adapterPosition, StationPreference preference);

        void onNotificationChange(int adapterPosition, StationPreference preference, boolean isEnabled);

        void onTimesClick(int adapterPosition, StationPreference preference);
    }
}
