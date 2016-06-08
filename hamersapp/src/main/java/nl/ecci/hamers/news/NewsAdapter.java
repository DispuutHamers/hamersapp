package nl.ecci.hamers.news;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

import nl.ecci.hamers.MainActivity;
import nl.ecci.hamers.R;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {
    private final ArrayList<NewsItem> dataSet;

    public NewsAdapter(ArrayList<NewsItem> itemsArrayList) {
        this.dataSet = itemsArrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_card, parent, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.title.setText(dataSet.get(position).getTitle());
        holder.body.setText(dataSet.get(position).getBody());

        Date date = dataSet.get(position).getDate();
        if (date != null) {
            holder.date.setText(MainActivity.appDF2.format(date));
        } else {
            holder.date.setText(R.string.date_unknown);
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView title;
        public final TextView body;
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
