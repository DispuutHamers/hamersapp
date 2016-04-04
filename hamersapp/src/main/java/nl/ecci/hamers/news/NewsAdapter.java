package nl.ecci.hamers.news;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import nl.ecci.hamers.R;
import nl.ecci.hamers.events.SingleEventActivity;
import nl.ecci.hamers.helpers.DataManager;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private final SharedPreferences prefs;
    private final Context context;
    private final ArrayList<NewsItem> dataSet;

    public NewsAdapter(Context context, ArrayList<NewsItem> itemsArrayList) {
        this.context = context;
        this.dataSet = itemsArrayList;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.newsitem_card, parent, false);

        final ViewHolder vh = new ViewHolder(view);

        vh.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                JSONObject e = DataManager.getEvent(prefs, dataSet.get(vh.getAdapterPosition()).getTitle(), dataSet.get(vh.getAdapterPosition()).getDate());
                if (e != null) {
                    try {
                        Intent intent = new Intent(context, SingleEventActivity.class);
                        intent.putExtra("title", e.getString("title"));
                        intent.putExtra("body", e.getString("body"));
                        intent.putExtra("category", e.getString("category"));
                        intent.putExtra("date", e.getString("date"));
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
        holder.body.setText(dataSet.get(position).getBody());

        DateFormat appDF = new SimpleDateFormat("EEEE dd MMMM yyyy", new Locale("nl"));
        Date date = dataSet.get(position).getDate();
        if (date != null) {
            holder.date.setText(appDF.format(date));
        } else {
            holder.date.setText("Datum niet bekend");
        }
    }

    // Return the size of your dataSet (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView title;
        public final TextView body;
        public final TextView date;
        public TextView category;

        public ViewHolder(View view) {
            super(view);
            this.view = view;

            title = (TextView) view.findViewById(R.id.newsitem_title);
            body = (TextView) view.findViewById(R.id.newsitem_body);
            date = (TextView) view.findViewById(R.id.newsitem_date);
        }
    }
}
