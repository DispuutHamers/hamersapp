package nl.ecci.hamers.quotes;

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

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Date;

import nl.ecci.hamers.MainActivity;
import nl.ecci.hamers.R;
import nl.ecci.hamers.helpers.AnimateFirstDisplayListener;
import nl.ecci.hamers.helpers.DataManager;
import nl.ecci.hamers.users.SingleUserActivity;
import nl.ecci.hamers.users.User;

public class QuoteAdapter extends RecyclerView.Adapter<QuoteAdapter.ViewHolder> implements Filterable {

    private static AnimateFirstDisplayListener animateFirstListener;
    private final ArrayList<Quote> dataSet;
    private final ImageLoader imageLoader;
    private final Context context;
    private ArrayList<Quote> filteredDataSet;

    public QuoteAdapter(ArrayList<Quote> itemsArrayList, Context context) {
        this.dataSet = itemsArrayList;
        this.filteredDataSet = itemsArrayList;
        this.context = context;

        // Universal Image Loader
        imageLoader = ImageLoader.getInstance();
        animateFirstListener = new AnimateFirstDisplayListener();
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quote_row, parent, false);
        final ViewHolder vh = new ViewHolder(view);
        final View imageView = view.findViewById(R.id.quote_image);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int position = vh.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Intent intent = new Intent(context, SingleUserActivity.class);
                    intent.putExtra(User.USER_ID, filteredDataSet.get(vh.getAdapterPosition()).getUserID());
                    context.startActivity(intent);
                }
            }
        });

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.body.setText(filteredDataSet.get(position).getText());
        holder.user.setText(DataManager.getUser(MainActivity.prefs, filteredDataSet.get(position).getUserID()).getName());

        Date date = filteredDataSet.get(position).getDate();
        if (date != null) {
            holder.date.setText(MainActivity.appDF.format(date));
        }

        User user = DataManager.getUser(MainActivity.prefs, filteredDataSet.get(position).getUserID());
        if (user != null) {
            imageLoader.displayImage(DataManager.getGravatarURL(user.getEmail()), holder.userImage, animateFirstListener);
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
                        if (quote.getText().toLowerCase().contains(charSequence) ||
                                DataManager.getUser(MainActivity.prefs , quote.getUserID()).getName().toLowerCase().contains(charSequence)) {
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
        public final TextView body;
        public final TextView date;
        public final TextView user;
        public final ImageView userImage;

        public ViewHolder(View view) {
            super(view);

            body = (TextView) view.findViewById(R.id.quote_body);
            date = (TextView) view.findViewById(R.id.quote_date);
            user = (TextView) view.findViewById(R.id.quote_user);
            userImage = (ImageView) view.findViewById(R.id.quote_image);
        }
    }
}
