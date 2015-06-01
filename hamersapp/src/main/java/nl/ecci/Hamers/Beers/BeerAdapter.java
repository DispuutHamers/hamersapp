package nl.ecci.Hamers.Beers;

import android.content.Context;
import android.content.Intent;
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
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import nl.ecci.Hamers.Helpers.AnimateFirstDisplayListener;
import nl.ecci.Hamers.Helpers.DataManager;
import nl.ecci.Hamers.R;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BeerAdapter extends RecyclerView.Adapter<BeerAdapter.ViewHolder> {

    private final SharedPreferences prefs;
    private final Context context;
    private final ArrayList<Beer> dataSet;
    private final ImageLoader imageLoader;
    public static ImageLoadingListener animateFirstListener;
    private final DisplayImageOptions options;

    public BeerAdapter(ArrayList<Beer> itemsArrayList, Context context) {
        this.dataSet = itemsArrayList;
        this.context = context;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);

        // Universal Image Loader
        imageLoader = ImageLoader.getInstance();
        animateFirstListener = new AnimateFirstDisplayListener();
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.beer_row, parent, false);

        final ViewHolder vh = new ViewHolder(view);

        vh.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                JSONObject b = null;
                try {
                    b = DataManager.getBeer(prefs, dataSet.get(vh.getAdapterPosition()).getName());
                } catch (NullPointerException ignored) {
                }
                if (b != null) {
                    try {
                        Intent intent = new Intent(context, SingleBeerActivity.class);
                        intent.putExtra("id", b.getInt("id"));
                        intent.putExtra("name", b.getString("name"));
                        intent.putExtra("soort", b.getString("soort"));
                        intent.putExtra("picture", b.getString("picture"));
                        intent.putExtra("percentage", b.getString("percentage"));
                        intent.putExtra("brewer", b.getString("brewer"));
                        intent.putExtra("country", b.getString("country"));
                        intent.putExtra("cijfer", b.getString("cijfer"));
                        context.startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.title.setText(dataSet.get(position).getName());
        holder.soort.setText("Soort: " + dataSet.get(position).getSoort());
        holder.brewer.setText("Brouwer: " + dataSet.get(position).getBrewer());
        holder.rating.setText("Cijfer: " + dataSet.get(position).getRating());
        holder.info.setText((dataSet.get(position).getCountry() + " - " + dataSet.get(position).getPercentage()));

        //imageLoader.displayImage(dataSet.get(position).getImageURL(), holder.picture, options, animateFirstListener);

        String imageURL = dataSet.get(position).getImageURL();

        if (holder.picture.getTag() == null ||
                !holder.picture.getTag().equals(imageURL)) {

            //we only load image if prev. URL and current URL do not match, or tag is null
            ImageAware imageAware = new ImageViewAware(holder.picture, false);
            imageLoader.displayImage(imageURL, imageAware, options, animateFirstListener);
            holder.picture.setTag(imageURL);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView title;
        public final TextView soort;
        public final TextView brewer;
        public final TextView rating;
        public final TextView info;
        public final ImageView picture;

        public ViewHolder(View view) {
            super(view);
            this.view = view;

            title = (TextView) view.findViewById(R.id.beer_name);
            soort = (TextView) view.findViewById(R.id.beer_soort);
            brewer = (TextView) view.findViewById(R.id.beer_brewer);
            rating = (TextView) view.findViewById(R.id.row_beer_rating);
            info = (TextView) view.findViewById(R.id.beer_info);
            picture = (ImageView) view.findViewById(R.id.beer_picture);
        }
    }
}
