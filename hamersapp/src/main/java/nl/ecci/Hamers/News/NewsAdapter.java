package nl.ecci.Hamers.News;

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
import nl.ecci.Hamers.Events.SingleEventActivity;
import nl.ecci.Hamers.Helpers.DataManager;
import nl.ecci.Hamers.R;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

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

        vh.view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject e = DataManager.getEvent(prefs, dataSet.get(vh.getPosition()).getTitle(), dataSet.get(vh.getPosition()).getDate());
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

        DateFormat appDF = new SimpleDateFormat("EEEE dd MMMM yyyy");
        holder.date.setText(appDF.format(dataSet.get(position).getDate()));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView title;
        public final TextView body;
        public TextView category;
        public final TextView date;

        public ViewHolder(View view) {
            super(view);
            this.view = view;

            title = (TextView) view.findViewById(R.id.newsitem_title);
            body = (TextView) view.findViewById(R.id.newsitem_body);
            date = (TextView) view.findViewById(R.id.newsitem_date);
        }
    }
}
