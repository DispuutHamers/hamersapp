package nl.ecci.hamers.quotes;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import nl.ecci.hamers.MainActivity;
import nl.ecci.hamers.R;
import nl.ecci.hamers.helpers.AnimateFirstDisplayListener;
import nl.ecci.hamers.helpers.DataManager;
import nl.ecci.hamers.helpers.Utils;

public class QuoteAdapter extends RecyclerView.Adapter<QuoteAdapter.ViewHolder> implements Filterable {

    private static AnimateFirstDisplayListener animateFirstListener;
    private final ArrayList<Quote> dataSet;
    private final ImageLoader imageLoader;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss - dd MMM yyyy", MainActivity.locale);
    private ArrayList<Quote> filteredDataSet;

    public QuoteAdapter(ArrayList<Quote> itemsArrayList) {
        this.dataSet = itemsArrayList;
        this.filteredDataSet = itemsArrayList;

        // Universal Image Loader
        imageLoader = ImageLoader.getInstance();
        animateFirstListener = new AnimateFirstDisplayListener();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quote_row, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.body.setText(filteredDataSet.get(position).getBody());
        holder.user.setText(filteredDataSet.get(position).getUser());

        Date date = filteredDataSet.get(position).getDate();
        if (date != null) {
            holder.date.setText(dateFormat.format(date));
        }

        String email = DataManager.IDToEmail(MainActivity.prefs, filteredDataSet.get(position).getUserID());
        if (email != null) {
            String url = "http://gravatar.com/avatar/" + Utils.md5Hex(email) + "?s=200";
            imageLoader.displayImage(url, holder.userImage, animateFirstListener);
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
                    ArrayList<Quote> filterResultsData = new ArrayList<>();
                    for (Quote quote : dataSet) {
                        if (quote.getBody().toLowerCase().contains(charSequence) ||
                                quote.getUser().toLowerCase().contains(charSequence)) {
                            filterResultsData.add(quote);
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
                filteredDataSet = (ArrayList<Quote>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView body;
        public final TextView date;
        public final TextView user;
        public final ImageView userImage;

        public ViewHolder(View view) {
            super(view);
            this.view = view;

            body = (TextView) view.findViewById(R.id.quote_body);
            date = (TextView) view.findViewById(R.id.quote_date);
            user = (TextView) view.findViewById(R.id.quote_user);
            userImage = (ImageView) view.findViewById(R.id.quote_image);
        }
    }
}
