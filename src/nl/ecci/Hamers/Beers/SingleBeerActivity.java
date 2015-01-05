package nl.ecci.Hamers.Beers;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import nl.ecci.Hamers.Helpers.DataManager;
import nl.ecci.Hamers.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static nl.ecci.Hamers.Helpers.DataManager.getJsonArray;

public class SingleBeerActivity extends ActionBarActivity {
    int id;
    String name;
    String soort;
    String percentage;
    String brewer;
    String country;
    SharedPreferences prefs;
    ArrayList<Review> reviewItems = new ArrayList<Review>();
    ArrayAdapter<Review> adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.single_beer_item);

        getSupportActionBar().setHomeButtonEnabled(true);

        TextView nameTV = (TextView) findViewById(R.id.beer_name);
        TextView soortTV = (TextView) findViewById(R.id.beer_soort);
        TextView percentageTV = (TextView) findViewById(R.id.beer_alc);
        TextView brewerTV = (TextView) findViewById(R.id.beer_brewer);
        TextView countryTV = (TextView) findViewById(R.id.beer_country);
        ImageView beerImage = (ImageView) findViewById(R.id.beer_image);
        ListView reviews_list = (ListView) findViewById(R.id.reviews);

        Bundle extras = getIntent().getExtras();

        id = extras.getInt("id");
        name = extras.getString("name");
        soort = extras.getString("soort");
        percentage = extras.getString("percentage");
        brewer = extras.getString("brewer");
        country = extras.getString("country");

        adapter = new ReviewAdapter(this, reviewItems);
        reviews_list.setAdapter(adapter);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        getReviews();
        beerImage.setImageBitmap(DataManager.getBeerImage(prefs, name));

        nameTV.setText(name);
        soortTV.setText(soort);
        percentageTV.setText(percentage);
        brewerTV.setText(brewer);
        countryTV.setText(country);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private JSONObject getReviews() {
        JSONArray reviews;
        try {
            if ((reviews = getJsonArray(prefs, DataManager.REVIEWKEY)) != null) {
                for (int i = 0; i < reviews.length(); i++) {
                    JSONObject review = reviews.getJSONObject(i);
                    if (review.getInt("beer_id") == id) {
                        Review tempReview = new Review(review.getInt("beer_id"), review.getInt("user_id"), review.getString("description"), review.getInt("rating"), review.getString("created_at"), review.getString("proefdatum"));
                        reviewItems.add(tempReview);
                        if (adapter != null) {
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        } catch (JSONException e) {
            return null;
        }
        return null;
    }

    /**
     * Called when the user clicks the button to create a new beerreview,
     * starts NewBeerActivity.
     *
     * @param view
     */
    public void createReview(View view) {
        Intent intent = new Intent(this, NewBeerReviewActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }
}
