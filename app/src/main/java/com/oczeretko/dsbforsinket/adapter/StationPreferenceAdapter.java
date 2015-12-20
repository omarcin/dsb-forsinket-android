package com.oczeretko.dsbforsinket.adapter;

import android.animation.*;
import android.content.*;
import android.content.res.*;
import android.os.*;
import android.support.v7.widget.*;
import android.view.*;
import android.view.animation.*;
import android.widget.*;

import com.oczeretko.dsbforsinket.*;
import com.oczeretko.dsbforsinket.data.*;
import com.oczeretko.dsbforsinket.utils.*;

import java.util.*;

import static com.oczeretko.dsbforsinket.utils.ViewUtils.*;

public class StationPreferenceAdapter extends RecyclerView.Adapter<StationPreferenceAdapter.ViewHolder> {
    private static final int LAYOUT_RESOURCE = R.layout.fragment_preferences_item;
    private static final int TAG_HOLDER = R.string.tag_holder;

    private final List<StationPreference> items;
    private final int animationDuration;
    private final int expandedElevation;

    private Listener listener;
    private int expandedId;
    private int INVALID_ID = -100;
    private ViewHolder expandedViewHolder;
    private float expandDeceleration;
    private float collapseDeceleration;
    private int bottomHeight;

    public StationPreferenceAdapter(Context context, List<StationPreference> items) {
        this.items = items;
        Resources resources = context.getResources();
        animationDuration = resources.getInteger(R.integer.animation_duration_expand);
        expandedElevation = resources.getInteger(R.integer.elevation_item);
        expandDeceleration = ((float)resources.getInteger(R.integer.animation_expand_deceleration)) / 10f;
        collapseDeceleration = ((float)resources.getInteger(R.integer.animation_collapse_deceleration)) / 10f;
        bottomHeight = (int)resources.getDimension(R.dimen.fragment_preferences_item_bottom_height);
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

        holder.name.setText(holder.preference.getName());
        holder.expand.setTag(TAG_HOLDER, holder);
        holder.expand.setOnClickListener(this::onExpandCollapseClick);
        holder.delete.setTag(TAG_HOLDER, holder);
        holder.delete.setOnClickListener(this::onDeleteClick);

        boolean isItemExpanded = isExpanded(holder.preference);
        bindExpandedLayout(holder, isItemExpanded);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.itemView.setElevation(isItemExpanded ? expandedElevation : 0);
        }
    }

    private void bindExpandedLayout(ViewHolder holder, boolean isItemExpanded) {
        holder.itemView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        holder.getExpandedLayoutParams().setMargins(0, 0, 0, bottomHeight);
        if (isItemExpanded) {
            expandedViewHolder = holder;
            holder.expandedLayout.setVisibility(View.VISIBLE);
            holder.delete.setVisibility(View.VISIBLE);
            holder.delete.setAlpha(1f);
            holder.status.setVisibility(View.GONE);
            holder.expand.setRotation(180f);
        } else {
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
            holder.getExpandedLayoutParams().setMargins(0, -distance, 0, bottomHeight);
            holder.itemView.requestLayout();

            ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f).setDuration(animationDuration);
            animator.setInterpolator(new DecelerateInterpolator(expandDeceleration));
            animator.addUpdateListener(AnimUtils.<Float>onUpdate(value -> {
                holder.itemView.getLayoutParams().height = (int)(value * distance + startingHeight);
                holder.getExpandedLayoutParams().setMargins(0, (int)((value - 1) * distance), 0, bottomHeight);
                holder.expand.setRotation(180f * value);
                holder.status.setAlpha(1 - value);
                holder.delete.setAlpha(value);
                holder.itemView.requestLayout();
            }));
            animator.addListener(AnimUtils.onEnd(() -> bindExpandedLayout(holder, true)));

            animator.start();
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
            final int bottomHeight = holder.bottomLayout.getHeight();

            holder.expandedLayout.setVisibility(View.VISIBLE);
            holder.delete.setVisibility(View.GONE);
            holder.status.setVisibility(View.VISIBLE);
            holder.status.setAlpha(1);

            ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f).setDuration(animationDuration);
            animator.setInterpolator(new DecelerateInterpolator(collapseDeceleration));
            animator.addUpdateListener(AnimUtils.<Float>onUpdate(value -> {
                holder.itemView.getLayoutParams().height = (int)(value * distance + startingHeight);
                holder.getExpandedLayoutParams().setMargins(0, (int)(value * distance), 0, bottomHeight);
                holder.expand.setRotation(180f * (1 - value));
                holder.delete.setAlpha(1 - value);
                holder.status.setAlpha(value);
                holder.itemView.requestLayout();
            }));
            animator.addListener(AnimUtils.onEnd(() -> bindExpandedLayout(holder, false)));

            animator.start();

            return false;
        });
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        protected final TextView name;
        protected final View root;
        protected final View expandedLayout;
        protected final View bottomLayout;
        protected final ImageButton expand;
        protected final ImageButton delete;
        protected final View status;
        protected StationPreference preference;

        public ViewHolder(View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.fragment_preferences_item_root);
            name = (TextView)itemView.findViewById(R.id.fragment_preferences_item_station_name);
            expandedLayout = itemView.findViewById(R.id.fragment_preferences_item_expanded_layout);
            bottomLayout = itemView.findViewById(R.id.fragment_preferences_item_bottom_layout);
            expand = (ImageButton)itemView.findViewById(R.id.fragment_preferences_item_expand_btn);
            delete = (ImageButton)itemView.findViewById(R.id.fragment_preferences_item_delete_btn);
            status = itemView.findViewById(R.id.fragment_preferences_item_notification_status);
        }

        public FrameLayout.LayoutParams getExpandedLayoutParams() {
            return (FrameLayout.LayoutParams)expandedLayout.getLayoutParams();
        }
    }

    public interface Listener {
        void onDeleteClick(int adapterPosition, StationPreference preference);
    }
}
