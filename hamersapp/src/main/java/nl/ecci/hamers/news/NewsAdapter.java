package nl.ecci.hamers.news;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

import nl.ecci.hamers.MainActivity;
import nl.ecci.hamers.R;
import nl.ecci.hamers.events.Event;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> implements Filterable {
    private final ArrayList<News> dataSet;
    private ArrayList<News> filteredDataSet;

    public NewsAdapter(ArrayList<News> dataSet) {
        this.dataSet = dataSet;
        this.filteredDataSet = dataSet;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_card, parent, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.title.setText(filteredDataSet.get(position).getTitle());
        holder.body.setText(filteredDataSet.get(position).getBody());

        Date date = filteredDataSet.get(position).getDate();
        if (date != null) {
            holder.date.setText(MainActivity.appDF2.format(date));
        } else {
            holder.date.setText(R.string.date_unknown);
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
                    ArrayList<News> filterResultsData = new ArrayList<>();
                    for (News newsItem : dataSet) {
                        if (newsItem.getTitle().toLowerCase().contains(charSequence) || newsItem.getBody().toLowerCase().contains(charSequence)) {
                            filterResultsData.add(newsItem);
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
                filteredDataSet = (ArrayList<News>) filterResults.values;
                notifyDataSetChanged();
            }
        };
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
