package com.oczeretko.dsbforsinket.adapter;

import android.content.*;
import android.support.v4.content.*;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.*;

import com.oczeretko.dsbforsinket.*;
import com.oczeretko.dsbforsinket.data.*;

import java.util.*;

public class DeparturesAdapter extends RecyclerView.Adapter<DeparturesAdapter.ViewHolder> {

    private static final Map<String, Integer> LINE_TO_BACKGROUND = new HashMap<>();

    {
        LINE_TO_BACKGROUND.put("A", R.drawable.line_a);
        LINE_TO_BACKGROUND.put("B", R.drawable.line_b);
        LINE_TO_BACKGROUND.put("C", R.drawable.line_c);
        LINE_TO_BACKGROUND.put("E", R.drawable.line_e);
        LINE_TO_BACKGROUND.put("F", R.drawable.line_f);
        LINE_TO_BACKGROUND.put("H", R.drawable.line_h);
        LINE_TO_BACKGROUND.put("", R.drawable.line_train);
    }

    private static final int LAYOUT_RESOURCE = R.layout.fragment_departures_item;
    private final Context context;
    private final List<DepartureInfo> departures;

    public DeparturesAdapter(Context context) {
        this.context = context;
        this.departures = new ArrayList<>();
    }

    public void setDepartures(List<DepartureInfo> departures) {
        this.departures.clear();
        this.departures.addAll(departures);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(LAYOUT_RESOURCE, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DepartureInfo departureInfo = this.departures.get(position);
        holder.destinationName.setText(departureInfo.getDestinationName());
        holder.departureTime.setText(departureInfo.getDepartureTime());
        holder.strikeThrough.setVisibility(departureInfo.isCancelled() ? View.VISIBLE : View.INVISIBLE);
        holder.delay.setText(departureInfo.getDelay());

        if (position % 2 == 1) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorOddRow));
        } else {
            holder.itemView.setBackgroundColor(0);
        }

        String trainLineUpper = departureInfo.getTrainLine().toUpperCase();
        holder.lineName.setText(trainLineUpper);

        if (LINE_TO_BACKGROUND.containsKey(trainLineUpper)) {
            holder.lineBackgroundView.setBackgroundResource(LINE_TO_BACKGROUND.get(trainLineUpper));
        } else {
            holder.lineBackgroundView.setBackgroundResource(R.drawable.line_train);
        }

    }

    @Override
    public int getItemCount() {
        return departures.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected final TextView destinationName;
        protected final TextView departureTime;
        protected final View strikeThrough;
        protected final TextView delay;
        protected final TextView lineName;
        private final View lineBackgroundView;

        public ViewHolder(View itemView) {
            super(itemView);
            lineBackgroundView = itemView.findViewById(R.id.trainLineLayout);
            lineName = (TextView)itemView.findViewById(R.id.trainLine);
            destinationName = (TextView)itemView.findViewById(R.id.trainDestination);
            departureTime = (TextView)itemView.findViewById(R.id.departureTime);
            strikeThrough = itemView.findViewById(R.id.line);
            delay = (TextView)itemView.findViewById(R.id.delay);
        }
    }
}
