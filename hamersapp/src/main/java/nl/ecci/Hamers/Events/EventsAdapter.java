package nl.ecci.Hamers.Events;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import nl.ecci.Hamers.Helpers.DataManager;
import nl.ecci.Hamers.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {

    private final SharedPreferences prefs;
    private final Context context;
    private final ArrayList<Event> dataSet;

    public EventsAdapter(Context context, ArrayList<Event> itemsArrayList) {
        this.context = context;
        this.dataSet = itemsArrayList;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_card, parent, false);

        final ViewHolder vh = new ViewHolder(view);

        vh.view.setOnClickListener(view1 -> {
            JSONObject e = DataManager.getEvent(prefs, dataSet.get(vh.getAdapterPosition()).getTitle(), dataSet.get(vh.getAdapterPosition()).getDate());
            if (e != null) {
                try {
                    Intent intent = new Intent(context, SingleEventActivity.class);
                    intent.putExtra("id", e.getInt("id"));
                    intent.putExtra("title", e.getString("title"));
                    intent.putExtra("beschrijving", e.getString("beschrijving"));
                    intent.putExtra("location", e.getString("location"));
                    intent.putExtra("date", e.getString("date"));

                    ArrayList<String> aanwezig = new ArrayList<>();
                    ArrayList<String> afwezig = new ArrayList<>();

                    JSONArray signups = e.getJSONArray("signups");

                    for (int i = 0; i < signups.length(); i++) {
                        JSONObject signup = signups.getJSONObject(i);
                        if (signup.getBoolean("status")) {
                            aanwezig.add(DataManager.getUser(prefs, signup.getInt("user_id")).getString("name"));
                        } else {
                            afwezig.add(DataManager.getUser(prefs, signup.getInt("user_id")).getString("name"));
                        }
                    }
                    intent.putStringArrayListExtra("aanwezig", aanwezig);
                    intent.putStringArrayListExtra("afwezig", afwezig);
                    context.startActivity(intent);
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
        });

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.title.setText(dataSet.get(position).getTitle());
        holder.beschrijving.setText(dataSet.get(position).getBeschrijving());


        Date date = dataSet.get(position).getDate();
        Date end_time = dataSet.get(position).getEnd_time();
        CardView card = (CardView) holder.view;
        if (dateChecker(date, true) && dateChecker(end_time, false)) {
            card.setCardBackgroundColor(Color.parseColor("#c5e1a5"));
        } else if (dateChecker(date, true)) {
            card.setCardBackgroundColor(Color.LTGRAY);
        } else {
            card.setCardBackgroundColor(Color.WHITE);
        }

        DateFormat appDF = new SimpleDateFormat("EEE dd MMM yyyy HH:mm");
        holder.date.setText(appDF.format(date));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView title;
        public final TextView beschrijving;
        public final TextView date;

        public ViewHolder(View view) {
            super(view);
            this.view = view;

            title = (TextView) view.findViewById(R.id.event_title);
            beschrijving = (TextView) view.findViewById(R.id.event_beschrijving);
            date = (TextView) view.findViewById(R.id.event_date);
        }
    }

    private boolean dateChecker(Date date, boolean beginDate) {
        if (beginDate) {
            if (System.currentTimeMillis() > date.getTime()) {
                return true;
            }
        } else if (System.currentTimeMillis() < date.getTime()) {
            return true;
        }
        return false;
    }
}
