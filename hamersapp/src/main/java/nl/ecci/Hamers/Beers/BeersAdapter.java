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
import nl.ecci.Hamers.Helpers.DataManager;
import nl.ecci.Hamers.R;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BeersAdapter extends RecyclerView.Adapter<BeersAdapter.ViewHolder> {

    SharedPreferences prefs;
    private Context context;
    private ArrayList<Beer> dataSet;

    public BeersAdapter(ArrayList<Beer> itemsArrayList, Context context) {
        this.dataSet = itemsArrayList;
        this.context = context;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.beer_row, parent, false);

        final ViewHolder vh = new ViewHolder(view);

        vh.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject b = null;
                try {
                    b = DataManager.getBeer(prefs, dataSet.get(vh.getPosition()).getName());
                } catch (NullPointerException e) {
                }
                if (b != null) {
                    try {
                        Intent intent = new Intent(context, SingleBeerActivity.class);
                        intent.putExtra("id", b.getInt("id"));
                        intent.putExtra("name", b.getString("name"));
                        intent.putExtra("soort", b.getString("soort"));
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

        Bitmap image = DataManager.getBeerImage(prefs, dataSet.get(position).getName() + "-thumb");
        holder.picture.setImageBitmap(image);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public TextView title;
        public TextView soort;
        public TextView brewer;
        public TextView rating;
        public TextView info;
        public ImageView picture;

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
