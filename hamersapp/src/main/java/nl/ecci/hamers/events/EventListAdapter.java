package nl.ecci.hamers.events;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import nl.ecci.hamers.MainActivity;
import nl.ecci.hamers.R;
import nl.ecci.hamers.helpers.DataManager;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<Event> dataSet;

    public EventListAdapter(Context context, ArrayList<Event> itemsArrayList) {
        this.context = context;
        this.dataSet = itemsArrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_card, parent, false);
        final ViewHolder vh = new ViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int position = vh.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Intent intent = new Intent(context, SingleEventActivity.class);
                    intent.putExtra("id", dataSet.get(position).getId());
                    context.startActivity(intent);
                }
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.title.setText(dataSet.get(position).getTitle());
        holder.beschrijving.setText(dataSet.get(position).getBeschrijving());

        Date date = dataSet.get(position).getDate();
        Date end_time = dataSet.get(position).getEnd_time();
        holder.date.setText(MainActivity.appDF2.format(date));

        CardView card = (CardView) holder.view;
        if (dateChecker(date, true) && dateChecker(end_time, false)) {
            card.setCardBackgroundColor(Color.parseColor("#c5e1a5"));
        } else if (dateChecker(date, true)) {
            card.setCardBackgroundColor(Color.LTGRAY);
        } else {
            card.setCardBackgroundColor(Color.WHITE);
        }

        if (!dataSet.get(position).getLocation().isEmpty()) {
            holder.location.setText(dataSet.get(position).getLocation());
        }

        try {
            JSONArray signups = dataSet.get(position).getSignups();
            int userID = DataManager.getOwnUser(MainActivity.prefs).getUserID();
            Boolean aanwezig = null;
            for (int i = 0; i < signups.length(); i++) {
                JSONObject signup = signups.getJSONObject(i);

                if (signup.getInt("user_id") == userID) {
                    aanwezig = signup.getBoolean("status");
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
        } catch (JSONException ignored) {
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    private boolean dateChecker(Date date, boolean beginDate) {
        if (date != null && beginDate) {
            if (System.currentTimeMillis() > date.getTime()) {
                return true;
            }
        } else if (date != null && System.currentTimeMillis() < date.getTime()) {
            return true;
        }
        return false;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView title;
        public final TextView beschrijving;
        public final TextView date;
        public final TextView location;
        public final ImageView thumbs;

        public ViewHolder(View view) {
            super(view);
            this.view = view;

            title = (TextView) view.findViewById(R.id.event_title);
            beschrijving = (TextView) view.findViewById(R.id.event_beschrijving);
            date = (TextView) view.findViewById(R.id.event_date);
            location = (TextView) view.findViewById(R.id.event_location);
            thumbs = (ImageView) view.findViewById(R.id.thumbs);
        }
    }
}
