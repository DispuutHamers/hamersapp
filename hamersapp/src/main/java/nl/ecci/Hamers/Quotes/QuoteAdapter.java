package nl.ecci.Hamers.Quotes;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import nl.ecci.Hamers.Helpers.AnimateFirstDisplayListener;
import nl.ecci.Hamers.Helpers.DataManager;
import nl.ecci.Hamers.Helpers.Utils;
import nl.ecci.Hamers.R;

import java.util.ArrayList;

public class QuoteAdapter extends RecyclerView.Adapter<QuoteAdapter.ViewHolder> {

    private static AnimateFirstDisplayListener animateFirstListener;
    private final ArrayList<Quote> dataSet;
    private final SharedPreferences prefs;
    private final ImageLoader imageLoader;
    private final DisplayImageOptions options;

    public QuoteAdapter(Context context, ArrayList<Quote> itemsArrayList) {
        this.dataSet = itemsArrayList;
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
        holder.body.setText(dataSet.get(position).getBody());
        holder.date.setText(dataSet.get(position).getDate());
        holder.user.setText(dataSet.get(position).getUser());

        String email = DataManager.IDToEmail(prefs, dataSet.get(position).getUserID());
        if (email != null) {
            String url = "http://gravatar.com/avatar/" + Utils.md5Hex(email) + "?s=200";
            imageLoader.displayImage(url, holder.userImage, options, animateFirstListener);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dataSet.size();
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
