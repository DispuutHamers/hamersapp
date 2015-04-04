package nl.ecci.Hamers.Events;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import nl.ecci.Hamers.Helpers.DataManager;
import nl.ecci.Hamers.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {

    SharedPreferences prefs;
    private Context context;
    private ArrayList<Event> dataSet;

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

        vh.view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                JSONObject e = DataManager.getEvent(prefs, dataSet.get(vh.getPosition()).getTitle(), dataSet.get(vh.getPosition()).getDate());
                if (e != null) {
                    try {
                        Intent intent = new Intent(context, SingleEventActivity.class);
                        intent.putExtra("id", e.getInt("id"));
                        intent.putExtra("title", e.getString("title"));
                        intent.putExtra("beschrijving", e.getString("beschrijving"));
                        intent.putExtra("location", e.getString("location"));
                        intent.putExtra("date", e.getString("date"));

                        ArrayList<String> aanwezig = new ArrayList<String>();
                        ArrayList<String> afwezig = new ArrayList<String>();

                        JSONArray signups = e.getJSONArray("signups");

                        for (int i = 0; i < signups.length(); i++) {
                            JSONObject signup = signups.getJSONObject(i);
                            if (signup.getBoolean("status") == true) {
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
            }
        });

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.title.setText(dataSet.get(position).getTitle());
        holder.beschrijving.setText(dataSet.get(position).getBeschrijving());
        holder.date.setText(dataSet.get(position).getDate());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public TextView title;
        public TextView beschrijving;
        public TextView date;

        public ViewHolder(View view) {
            super(view);
            this.view = view;

            title = (TextView) view.findViewById(R.id.event_title);
            beschrijving = (TextView) view.findViewById(R.id.event_beschrijving);
            date = (TextView) view.findViewById(R.id.event_date);
        }
    }
}
