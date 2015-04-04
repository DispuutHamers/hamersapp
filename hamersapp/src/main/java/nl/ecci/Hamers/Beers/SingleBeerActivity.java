package nl.ecci.Hamers.Beers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import nl.ecci.Hamers.Helpers.DataManager;
import nl.ecci.Hamers.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

import static nl.ecci.Hamers.Helpers.DataManager.getJsonArray;
import static nl.ecci.Hamers.Helpers.DataManager.getUser;
import static nl.ecci.Hamers.MainActivity.parseDate;

public class SingleBeerActivity extends ActionBarActivity {
    int id;
    String name;
    String soort;
    String percentage;
    String brewer;
    String country;
    String cijfer;
    SharedPreferences prefs;

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
        TextView cijferTV = (TextView) findViewById(R.id.beer_rating);
        ImageView beerImage = (ImageView) findViewById(R.id.beer_image);

        Bundle extras = getIntent().getExtras();

        id = extras.getInt("id");
        name = extras.getString("name");
        soort = extras.getString("soort");
        percentage = extras.getString("percentage");
        brewer = extras.getString("brewer");
        country = extras.getString("country");
        cijfer = extras.getString("cijfer");

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        getReviews();
        beerImage.setImageBitmap(DataManager.getBeerImage(prefs, name));

        nameTV.setText(name);
        soortTV.setText(soort);
        percentageTV.setText(percentage);
        brewerTV.setText(brewer);
        countryTV.setText(country);

        if (cijfer.equals("null")) {
            cijferTV.setText("nog niet bekend");
        } else {
            cijferTV.setText(cijfer);
        }
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
                        Review tempReview = new Review(review.getInt("beer_id"), review.getInt("user_id"), review.getString("description"), review.getString("rating"), review.getString("created_at"), review.getString("proefdatum"));
                        insertReview(tempReview);
                    }
                }
            }
        } catch (JSONException e) {
            Toast.makeText(this, getString(R.string.toast_reviewloaderror), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
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
        intent.putExtra("name", name);
        startActivity(intent);
    }

    public void insertReview(Review review) {
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.review_row, null);

        TextView title = (TextView) view.findViewById(R.id.review_title);
        TextView body = (TextView) view.findViewById(R.id.review_body);
        TextView date = (TextView) view.findViewById(R.id.review_date);
        TextView ratingTV = (TextView) view.findViewById(R.id.review_rating);

        String name = null;
        try {
            name = getUser(prefs, review.getUser_id()).getString("name").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String datum = null;
        try {
            datum = parseDate(review.getProefdatum());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        title.setText(name + ": ");
        body.setText(review.getDescription());
        date.setText(datum);
        ratingTV.setText("Cijfer: " + review.getRating());

        // Insert into view
        ViewGroup insertPoint = (ViewGroup) findViewById(R.id.review_insert_point);
        insertPoint.addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
}
