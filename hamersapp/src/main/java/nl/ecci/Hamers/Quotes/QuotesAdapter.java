package nl.ecci.Hamers.Quotes;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import nl.ecci.Hamers.Helpers.DataManager;
import nl.ecci.Hamers.R;

import java.util.ArrayList;

public class QuotesAdapter extends RecyclerView.Adapter<QuotesAdapter.ViewHolder> {

    private final ArrayList<Quote> dataSet;
    SharedPreferences prefs;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public TextView body;
        public TextView date;
        public TextView user;
        public ImageView userImage;

        public ViewHolder(View view) {
            super(view);
            this.view = view;

            body = (TextView) view.findViewById(R.id.quote_body);
            date = (TextView) view.findViewById(R.id.quote_date);
            user = (TextView) view.findViewById(R.id.quote_user);
            userImage = (ImageView) view.findViewById(R.id.quote_image);
        }
    }

    public QuotesAdapter(Context context, ArrayList<Quote> itemsArrayList) {
        this.dataSet = itemsArrayList;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.quote_row, parent, false);

        final ViewHolder vh = new ViewHolder(view);

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.body.setText(dataSet.get(position).getBody());
        holder.date.setText(dataSet.get(position).getDate());
        holder.user.setText(dataSet.get(position).getUser());
        holder.userImage.setImageBitmap(DataManager.getUserImage(prefs, dataSet.get(position).getUserID()));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
