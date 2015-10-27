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
        holder.train_name.setText(departureInfo.getTrainName());
        holder.departure_time.setText(departureInfo.getDepartureTime());
        holder.strikeThrough.setVisibility(departureInfo.isCancelled() ? View.VISIBLE : View.INVISIBLE);
        holder.delay.setText(departureInfo.getDelay());

        if (position % 2 == 1) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorOddRow));
        } else {
            holder.itemView.setBackgroundColor(0);
        }
    }

    @Override
    public int getItemCount() {
        return departures.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected final TextView train_name;
        protected final TextView departure_time;
        protected final View strikeThrough;
        protected final TextView delay;

        public ViewHolder(View itemView) {
            super(itemView);
            train_name = (TextView)itemView.findViewById(R.id.trainName);
            departure_time = (TextView)itemView.findViewById(R.id.departureTime);
            strikeThrough = itemView.findViewById(R.id.line);
            delay = (TextView)itemView.findViewById(R.id.delay);
        }
    }
}
