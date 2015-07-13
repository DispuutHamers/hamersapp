package nl.ecci.hamers.quotes;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import nl.ecci.hamers.R;
import nl.ecci.hamers.helpers.AnimateFirstDisplayListener;
import nl.ecci.hamers.helpers.DataManager;
import nl.ecci.hamers.helpers.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class QuoteAdapter extends RecyclerView.Adapter<QuoteAdapter.ViewHolder> implements Filterable {

    private static AnimateFirstDisplayListener animateFirstListener;
    private final ArrayList<Quote> dataSet;
    private ArrayList<Quote> filteredDataSet;
    private final SharedPreferences prefs;
    private final ImageLoader imageLoader;
    private final DisplayImageOptions options;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss - dd MMM yyyy", new Locale("nl"));

    public QuoteAdapter(Context context, ArrayList<Quote> itemsArrayList) {
        this.dataSet = itemsArrayList;
        this.filteredDataSet = itemsArrayList;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);

        // Universal Image Loader
        imageLoader = ImageLoader.getInstance();
        animateFirstListener = new AnimateFirstDisplayListener();
        options = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quote_row, parent, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.body.setText(filteredDataSet.get(position).getBody());
        holder.user.setText(filteredDataSet.get(position).getUser());

        Date date = filteredDataSet.get(position).getDate();
        if (date != null) {
            holder.date.setText(dateFormat.format(date));
        }

        String email = DataManager.IDToEmail(prefs, filteredDataSet.get(position).getUserID());
        if (email != null) {
            String url = "http://gravatar.com/avatar/" + Utils.md5Hex(email) + "?s=200";
            imageLoader.displayImage(url, holder.userImage, options, animateFirstListener);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
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
