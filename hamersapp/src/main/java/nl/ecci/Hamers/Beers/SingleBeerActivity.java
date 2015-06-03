package nl.ecci.Hamers.Beers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import nl.ecci.Hamers.Helpers.DataManager;
import nl.ecci.Hamers.Helpers.SingleImageActivity;
import nl.ecci.Hamers.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

import static nl.ecci.Hamers.Helpers.DataManager.getJsonArray;
import static nl.ecci.Hamers.Helpers.DataManager.getUser;
import static nl.ecci.Hamers.MainActivity.parseDate;

public class SingleBeerActivity extends AppCompatActivity {

    private int id;
    private String name;
    private SharedPreferences prefs;
    public static LinearLayout parentLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.single_beer_item);

        parentLayout = (LinearLayout) findViewById(R.id.single_beer_parent);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        TextView nameTV = (TextView) findViewById(R.id.beer_name);
        TextView soortTV = (TextView) findViewById(R.id.beer_soort);
        TextView percentageTV = (TextView) findViewById(R.id.beer_alc);
        TextView brewerTV = (TextView) findViewById(R.id.beer_brewer);
        TextView countryTV = (TextView) findViewById(R.id.beer_country);
        TextView cijferTV = (TextView) findViewById(R.id.beer_rating);
        final ImageView beerImage = (ImageView) findViewById(R.id.beer_image);

        Bundle extras = getIntent().getExtras();

        id = extras.getInt(Beer.BEER_ID);
        name = extras.getString(Beer.BEER_NAME);
        final String kind = extras.getString(Beer.BEER_KIND);
        final String url = extras.getString(Beer.BEER_URL);
        final String percentage = extras.getString(Beer.BEER_PERCENTAGE);
        final String brewer = extras.getString(Beer.BEER_PERCENTAGE);
        final String country = extras.getString(Beer.BEER_COUNTRY);
        final String rating = extras.getString(Beer.BEER_RATING);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        nameTV.setText(name);
        soortTV.setText(kind);
        percentageTV.setText(percentage);
        brewerTV.setText(brewer);
        countryTV.setText(country);

        if (rating.equals("null")) {
            cijferTV.setText("nog niet bekend");
        } else {
            cijferTV.setText(rating);
        }

        // Universal Image Loader
        ImageLoader imageLoader = ImageLoader.getInstance();
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        imageLoader.displayImage(url, beerImage, options);

        beerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SingleBeerActivity.this, SingleImageActivity.class);
                String transitionName = getString(R.string.transition_single_image);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(SingleBeerActivity.this, beerImage, transitionName);
                intent.putExtra(Beer.BEER_NAME, name);
                intent.putExtra(Beer.BEER_URL, url);
                ActivityCompat.startActivity(SingleBeerActivity.this, intent, options.toBundle());
            }
        });

        if (prefs != null) {
            getReviews();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getReviews() {
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
            Toast.makeText(this, getString(R.string.snackbar_reviewloaderror), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
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

        int requestCode = 1; // Or some number you choose
        startActivityForResult(intent, requestCode);
    }

    private void insertReview(Review review) {
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.review_row, null);

        TextView title = (TextView) view.findViewById(R.id.review_title);
        TextView body = (TextView) view.findViewById(R.id.review_body);
        TextView date = (TextView) view.findViewById(R.id.review_date);
        TextView ratingTV = (TextView) view.findViewById(R.id.review_rating);

        String name = null;
        try {
            name = getUser(prefs, review.getUser_id()).getString("name");
        } catch (JSONException | NullPointerException e) {
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
        insertPoint.addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void refreshActivity() {
        finish();
        startActivity(getIntent());
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        refreshActivity();
    }
}
