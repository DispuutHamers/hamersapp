package nl.ecci.hamers.beers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import nl.ecci.hamers.MainActivity;
import nl.ecci.hamers.R;
import nl.ecci.hamers.helpers.DataManager;
import nl.ecci.hamers.helpers.SingleImageActivity;

import static nl.ecci.hamers.helpers.DataManager.getJsonArray;
import static nl.ecci.hamers.helpers.DataManager.getOwnUser;
import static nl.ecci.hamers.helpers.DataManager.getUser;

public class SingleBeerActivity extends AppCompatActivity {

    private Gson gson;
    private Beer beer;
    private Button reviewButton;
    private ViewGroup reviewViewGroup;
    private Review ownReview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.single_beer);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        TextView nameTV = (TextView) findViewById(R.id.beer_name);

        View kindRow = findViewById(R.id.row_kind);
        View alcRow = findViewById(R.id.row_alc);
        View brewerRow = findViewById(R.id.row_brewer);
        View countryRow = findViewById(R.id.row_country);
        View ratingRow = findViewById(R.id.row_rating);
        reviewViewGroup = (ViewGroup) findViewById(R.id.reviews);

        final ImageView beerImage = (ImageView) findViewById(R.id.beer_image);
        reviewButton = (Button) findViewById(R.id.review_create_button);
        if (reviewButton != null) {
            reviewButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createReview(ownReview);
                }
            });
        }

        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
        beer = gson.fromJson(getIntent().getStringExtra(Beer.BEER), Beer.class);

        fillRow(kindRow, getString(R.string.beer_soort), beer.getKind());
        fillRow(alcRow, getString(R.string.beer_alc), beer.getPercentage());
        fillRow(brewerRow, getString(R.string.beer_brewer), beer.getBrewer());
        fillRow(countryRow, getString(R.string.beer_country), beer.getCountry());

        nameTV.setText(beer.getName());

        if (beer.getRating() == null) {
            fillRow(ratingRow, getString(R.string.beer_rating), "Nog niet bekend");
        } else {
            fillRow(ratingRow, getString(R.string.beer_rating), beer.getRating());
        }

        // Universal Image Loader
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(beer.getImageURL(), beerImage);

        beerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SingleBeerActivity.this, SingleImageActivity.class);
                String transitionName = getString(R.string.transition_single_image);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(SingleBeerActivity.this, beerImage, transitionName);
                intent.putExtra(Beer.BEER, gson.toJson(beer, Beer.class));
                ActivityCompat.startActivity(SingleBeerActivity.this, intent, options.toBundle());
            }
        });
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
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        JSONArray reviews;
        boolean hasReviews = false;
        try {
            if ((reviews = getJsonArray(MainActivity.prefs, DataManager.REVIEWKEY)) != null) {
                for (int i = 0; i < reviews.length(); i++) {
                    JSONObject jsonObject = reviews.getJSONObject(i);
                    Review review = gson.fromJson(jsonObject.toString(), Review.class);
                    if (review.getBeerID() == beer.getID()) {
                        hasReviews = true;
                        if (review.getUserID() == getOwnUser(MainActivity.prefs).getID()) {
                            reviewButton.setVisibility(View.GONE);
                            ownReview = review;
                        }
                        insertReview(review);
                    }
                }
                if (!hasReviews) {
                    reviewViewGroup.removeAllViews();
                }
            }
        } catch (JSONException e) {
            Toast.makeText(this, getString(R.string.snackbar_reviewloaderror), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Called when the user clicks the button to create a new beer review,
     * starts NewBeerActivity.
     */
    public void createReview(Review review) {
        Intent intent = new Intent(this, NewBeerReviewActivity.class);
        intent.putExtra(Beer.BEER, gson.toJson(beer, Beer.class));

        if (review != null) {
            intent.putExtra(Review.REVIEW, gson.toJson(review, Review.class));
        }

        int requestCode = 1;
        startActivityForResult(intent, requestCode);
    }

    private void insertReview(Review review) {
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.review_row, null);
        View divider = inflater.inflate(R.layout.divider, null);

        TextView title = (TextView) view.findViewById(R.id.review_title);
        TextView body = (TextView) view.findViewById(R.id.review_body);
        TextView date = (TextView) view.findViewById(R.id.review_date);
        TextView ratingTV = (TextView) view.findViewById(R.id.review_rating);

        title.setText(String.format("%s: ", getUser(MainActivity.prefs, review.getUserID()).getName()));
        body.setText(review.getDescription());
        date.setText(MainActivity.appDF2.format(review.getProefdatum()));
        ratingTV.setText(String.format("Cijfer: %s", review.getRating()));

        // Insert into view
        ViewGroup insertPoint = (ViewGroup) findViewById(R.id.review_insert_point);
        if (insertPoint != null) {
            insertPoint.addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            insertPoint.addView(divider);
        }
        if (getOwnUser(MainActivity.prefs).getID() == review.getUserID()) {
            registerForContextMenu(view);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.review_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.review_update:
                createReview(ownReview);
                return true;
            case R.id.review_delete:
                deleteReview();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void deleteReview() {
        new AlertDialog.Builder(SingleBeerActivity.this)
                .setTitle(getString(R.string.review_delete))
                .setMessage(getString(R.string.review_delete_message))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();
    }

    private void refreshActivity() {
        finish();
        startActivity(getIntent());
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        refreshActivity();
    }

    private void fillRow(View view, final String title, final String description) {
        TextView titleView = (TextView) view.findViewById(R.id.row_title);
        titleView.setText(title);

        TextView descriptionView = (TextView) view.findViewById(R.id.row_description);
        descriptionView.setText(description);
    }

    @Override
    public void onResume(){
        super.onResume();
        getReviews();
    }
}
