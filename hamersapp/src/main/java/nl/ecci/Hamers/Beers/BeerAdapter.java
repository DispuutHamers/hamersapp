package nl.ecci.Hamers.Beers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
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
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import nl.ecci.Hamers.Helpers.AnimateFirstDisplayListener;
import nl.ecci.Hamers.Helpers.DataManager;
import nl.ecci.Hamers.Helpers.SingleImageActivity;
import nl.ecci.Hamers.R;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BeerAdapter extends RecyclerView.Adapter<BeerAdapter.ViewHolder> implements Filterable {

    private static ImageLoadingListener animateFirstListener;
    private final SharedPreferences prefs;
    private final Context context;
    private final View parentLayout;
    private final ArrayList<Beer> dataSet;
    private final ImageLoader imageLoader;
    private final DisplayImageOptions options;
    private ArrayList<Beer> filteredDataSet;

    public BeerAdapter(ArrayList<Beer> itemsArrayList, Context context, View parentLayout) {
        this.dataSet = itemsArrayList;
        filteredDataSet = itemsArrayList;
        this.context = context;
        this.parentLayout = parentLayout;
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
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.beer_row, parent, false);

        final ViewHolder vh = new ViewHolder(view);
        final View beerView = view.findViewById(R.id.beer_image);

        vh.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v1) {
                try {
                    JSONObject b = DataManager.getBeer(prefs, filteredDataSet.get(vh.getAdapterPosition()).getName());
                    Activity activity = (Activity) context;
                    String imageTransitionName = context.getString(R.string.transition_single_image);
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, beerView, imageTransitionName);
                    assert b != null;
                    Intent intent = new Intent(context, SingleBeerActivity.class);
                    intent.putExtra(Beer.BEER_ID, b.getInt("id"));
                    intent.putExtra(Beer.BEER_NAME, b.getString("name"));
                    intent.putExtra(Beer.BEER_KIND, b.getString("soort"));
                    intent.putExtra(Beer.BEER_URL, b.getString("picture"));
                    intent.putExtra(Beer.BEER_PERCENTAGE, b.getString("percentage"));
                    intent.putExtra(Beer.BEER_BREWER, b.getString("brewer"));
                    intent.putExtra(Beer.BEER_COUNTRY, b.getString("country"));
                    intent.putExtra(Beer.BEER_RATING, b.getString("cijfer"));
                    ActivityCompat.startActivity(activity, intent, options.toBundle());
                } catch (JSONException | NullPointerException ignored) {
                    Snackbar.make(view, context.getString(R.string.snackbar_error), Snackbar.LENGTH_LONG).show();
                }
            }
        });

        beerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject b = DataManager.getBeer(prefs, filteredDataSet.get(vh.getAdapterPosition()).getName());
                    Activity activity = (Activity) context;
                    String transitionName = context.getString(R.string.transition_single_image);
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, beerView, transitionName);
                    assert b != null;
                    if (!b.getString("picture").equals("")) {
                        Intent intent = new Intent(context, SingleImageActivity.class);
                        intent.putExtra(Beer.BEER_NAME, b.getString("name"));
                        intent.putExtra(Beer.BEER_URL, b.getString("picture"));
                        ActivityCompat.startActivity(activity, intent, options.toBundle());
                    } else {
                        Snackbar.make(parentLayout, context.getString(R.string.no_image), Snackbar.LENGTH_SHORT).show();
                    }
                } catch (JSONException | NullPointerException ignored) {
                    Snackbar.make(view, context.getString(R.string.snackbar_error), Snackbar.LENGTH_LONG).show();
                }
            }

        });

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.title.setText(filteredDataSet.get(position).getName());
        holder.soort.setText("Soort: " + filteredDataSet.get(position).getSoort());
        holder.brewer.setText("Brouwer: " + filteredDataSet.get(position).getBrewer());
        holder.rating.setText("Cijfer: " + filteredDataSet.get(position).getRating());
        holder.info.setText((filteredDataSet.get(position).getCountry() + " - " + filteredDataSet.get(position).getPercentage()));

        String imageURL = filteredDataSet.get(position).getImageURL();

        if (holder.picture.getTag() == null || !holder.picture.getTag().equals(imageURL) && !imageURL.equals(null)) {

            //we only load image if prev. URL and current URL do not match, or tag is null
            ImageAware imageAware = new ImageViewAware(holder.picture, false);
            imageLoader.displayImage(imageURL, imageAware, options, animateFirstListener);
            holder.picture.setTag(imageURL);
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
                    ArrayList<Beer> filterResultsData = new ArrayList<>();
                    for (Beer beer : dataSet) {
                        if (beer.getName().toLowerCase().contains(charSequence) || beer.getBrewer().toLowerCase().contains(charSequence)
                                || beer.getBrewer().toLowerCase().contains(charSequence) || beer.getPercentage().toLowerCase().contains(charSequence)
                                || beer.getSoort().toLowerCase().contains(charSequence))
                        {
                            filterResultsData.add(beer);
                        }
                    }

                    results.values = filterResultsData;
                    results.count = filterResultsData.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredDataSet = (ArrayList<Beer>) filterResults.values;
                notifyDataSetChanged();
            }
        };
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
            picture = (ImageView) view.findViewById(R.id.beer_image);
        }
    }
}
