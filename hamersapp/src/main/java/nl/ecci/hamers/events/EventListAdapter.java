package nl.ecci.hamers.events;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

import nl.ecci.hamers.MainActivity;
import nl.ecci.hamers.R;

import static nl.ecci.hamers.helpers.Utils.getOwnUser;

class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.ViewHolder> implements Filterable {

    private final Context context;
    private final ArrayList<Event> dataSet;
    private ArrayList<Event> filteredDataSet;

    EventListAdapter(Context context, ArrayList<Event> dataSet) {
        this.context = context;
        this.dataSet = dataSet;
        this.filteredDataSet = dataSet;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_row, parent, false);
        final ViewHolder vh = new ViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int position = vh.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Intent intent = new Intent(context, SingleEventActivity.class);
                    intent.putExtra(Event.Companion.getEVENT(), filteredDataSet.get(vh.getAdapterPosition()).getId());
                    context.startActivity(intent);
                }
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.title.setText(filteredDataSet.get(position).getTitle());
        holder.description.setText(filteredDataSet.get(position).getDescription());

        Date date = filteredDataSet.get(position).getDate();
        holder.date.setText(MainActivity.appDF2.format(date));

        if (filteredDataSet.get(position).getLocation() == null || filteredDataSet.get(position).getLocation().equals("null")) {
            holder.location.setVisibility(View.GONE);
        } else {

            holder.location.setText(filteredDataSet.get(position).getLocation());
        }

        ArrayList signups = filteredDataSet.get(position).getSignUps();
        if (signups != null) {
            int userID = getOwnUser(MainActivity.prefs).getId();
            Boolean aanwezig = null;
            for (int i = 0; i < signups.size(); i++) {
                Event.SignUp signUp = (Event.SignUp) signups.get(i);

                if (signUp.getUserID() == userID) {
                    aanwezig = signUp.isAttending();
                }
            }

            if (aanwezig != null) {
                if (aanwezig) {
                    holder.thumbs.setImageResource(R.drawable.ic_thumbs_up);
                } else {
                    holder.thumbs.setImageResource(R.drawable.ic_thumbs_down);
                }
            } else {
                holder.thumbs.setImageResource(R.drawable.ic_questionmark);
            }
        }
    }

    @Override
    public int getItemCount() {
        return filteredDataSet.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults results = new FilterResults();

                //If there's nothing to filter on, return the original data for your list
                if (charSequence == null || charSequence.length() == 0) {
                    results.values = dataSet;
                    results.count = dataSet.size();
                } else {
                    ArrayList<Event> filterResultsData = new ArrayList<>();
                    for (Event event : dataSet) {
                        if (event.getTitle().toLowerCase().contains(charSequence) || event.getDescription().toLowerCase().contains(charSequence)) {
                            filterResultsData.add(event);
                        }
                    }
                    results.values = filterResultsData;
                    results.count = filterResultsData.size();
                }
                return results;
            }

            @Override
            @SuppressWarnings("unchecked")
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredDataSet = (ArrayList<Event>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView title;
        public final TextView description;
        public final TextView date;
        final TextView location;
        final ImageView thumbs;

        ViewHolder(View view) {
            super(view);
            this.view = view;

            title = (TextView) view.findViewById(R.id.event_title);
            description = (TextView) view.findViewById(R.id.event_beschrijving);
            date = (TextView) view.findViewById(R.id.event_date);
            location = (TextView) view.findViewById(R.id.event_location);
            thumbs = (ImageView) view.findViewById(R.id.thumbs);
        }
    }
}
