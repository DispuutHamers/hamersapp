package nl.ecci.hamers.beers;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.lang.reflect.Type;
import java.util.ArrayList;

import nl.ecci.hamers.MainActivity;
import nl.ecci.hamers.R;
import nl.ecci.hamers.helpers.HamersActivity;
import nl.ecci.hamers.helpers.SingleImageActivity;
import nl.ecci.hamers.helpers.Utils;
import nl.ecci.hamers.loader.Loader;

import static nl.ecci.hamers.R.id.review_body;
import static nl.ecci.hamers.R.id.review_rating;

public class SingleBeerActivity extends HamersActivity {

    private SharedPreferences prefs;
    private Beer beer;
    private Gson gson;
    private LinearLayout insertPoint;
    private Button reviewButton;
    private ViewGroup reviewViewGroup;
    private Review ownReview;

    // Activity for result
    // Review
    int reviewRequestCode = 1;
    public static final String reviewRating = "reviewRating";
    public static final String reviewBody = "reviewBody";
    // Beer
    int beerRequestCode = 2;
    public static final String beerName = "beerName";
    public static final String beerKind = "beerKind";
    public static final String beerPercentage = "beerPercentage";
    public static final String beerBrewer = "beerBrewer";
    public static final String beerCountry = "beerCountry";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beer_detail);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat(MainActivity.dbDF.toPattern());
        gson = gsonBuilder.create();

        reviewViewGroup = (ViewGroup) findViewById(R.id.reviews);

        final ImageView beerImage = (ImageView) findViewById(R.id.beer_image);
        reviewButton = (Button) findViewById(R.id.review_create_button);
        if (reviewButton != null) {
            reviewButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateReview(ownReview);
                }
            });
        }

        beer = Utils.INSTANCE.getBeer(prefs, getIntent().getIntExtra(Beer.BEER, -1));

        setValues();

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

        getReviews();
    }

    private void setValues() {
        TextView nameTV = (TextView) findViewById(R.id.beer_name);
        View ratingRow = findViewById(R.id.row_rating);

        fillDetailRow(findViewById(R.id.row_kind), getString(R.string.beer_soort), beer.getKind());
        fillDetailRow(findViewById(R.id.row_alc), getString(R.string.beer_alc), beer.getPercentage());
        fillDetailRow(findViewById(R.id.row_brewer), getString(R.string.beer_brewer), beer.getBrewer());
        fillDetailRow(findViewById(R.id.row_country), getString(R.string.beer_country), beer.getCountry());

        nameTV.setText(beer.getName());

        if (beer.getRating() == null) {
            fillDetailRow(ratingRow, getString(R.string.beer_rating), "Nog niet bekend");
        } else {
            fillDetailRow(ratingRow, getString(R.string.beer_rating), beer.getRating());
        }
    }

    private void getReviews() {
        ArrayList<Review> reviewList;
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        Type type = new TypeToken<ArrayList<Review>>() {
        }.getType();

        boolean hasReviews = false;
        reviewList = gson.fromJson(prefs.getString(Loader.REVIEWURL, null), type);

        for (Review review : reviewList) {
            if (review.getBeerID() == beer.getID()) {
                hasReviews = true;
                if (review.getUserID() == Utils.INSTANCE.getOwnUser(prefs).getId()) {
                    reviewButton.setText(R.string.edit_review);
                    ownReview = review;
                }
                insertReview(review);
            }
        }

        if (!hasReviews) {
            reviewViewGroup.removeAllViews();
        }
    }

    /**
     * Called when the user clicks the button to create a new beer review,
     * starts NewBeerActivity.
     */
    private void updateReview(Review review) {
        Intent intent = new Intent(this, NewReviewActivity.class);
        intent.putExtra(Beer.BEER, beer.getID());

        if (review != null) {
            intent.putExtra(Review.REVIEW, gson.toJson(review, Review.class));
        }


        startActivityForResult(intent, reviewRequestCode);
    }

    private void insertReview(Review review) {
        insertPoint = (LinearLayout) findViewById(R.id.review_insert_point);
        View view = getLayoutInflater().inflate(R.layout.review_row, insertPoint, false);
        View divider = getLayoutInflater().inflate(R.layout.divider, insertPoint, false);

        TextView title = (TextView) view.findViewById(R.id.review_title);
        TextView body = (TextView) view.findViewById(review_body);
        TextView date = (TextView) view.findViewById(R.id.review_date);
        TextView ratingTV = (TextView) view.findViewById(R.id.review_rating);

        title.setText(String.format("%s: ", Utils.INSTANCE.getUser(prefs, review.getUserID()).getName()));
        body.setText(review.getDescription());
        date.setText(MainActivity.appDF2.format(review.getProefdatum()));
        ratingTV.setText(String.format("Cijfer: %s", review.getRating()));

        // Insert into view
        if (insertPoint != null) {
            insertPoint.addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            insertPoint.addView(divider);
        }
        if (Utils.INSTANCE.getOwnUser(prefs).getId() == review.getUserID()) {
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
                updateReview(ownReview);
                return true;
            case R.id.review_delete:
                deleteReview();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_item:
                Intent intent = new Intent(this, NewBeerActivity.class);
                if (beer != null) {
                    intent.putExtra(Beer.BEER, beer.getID());
                }
                startActivityForResult(intent, beerRequestCode);
                return true;
        }
        return super.onOptionsItemSelected(item);
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == reviewRequestCode) {
                String newBody = data.getStringExtra(reviewBody);
                int newRating = data.getIntExtra(reviewRating, -1);

                if (ownReview != null) {
                    for (int i = 0; i < insertPoint.getChildCount(); i++) {
                        View view = insertPoint.getChildAt(i);
                        TextView bodyTextView = (TextView) view.findViewById(review_body);
                        TextView ratingTextView = (TextView) view.findViewById(review_rating);
                        if (bodyTextView != null && ratingTextView != null) {
                            if (bodyTextView.getText() == ownReview.getDescription()) {
                                bodyTextView.setText(newBody);
                                ratingTextView.setText(String.format("Cijfer: %s", newRating));
                            }
                        }
                    }
                }
            } else if (requestCode == beerRequestCode) {
                beer.setName(data.getStringExtra(beerName));
                beer.setKind(data.getStringExtra(beerKind));
                beer.setPercentage(data.getStringExtra(beerPercentage));
                beer.setPercentage(data.getStringExtra(beerPercentage));
                beer.setBrewer(data.getStringExtra(beerBrewer));
                beer.setCountry(data.getStringExtra(beerCountry));
                beer.setRating(beer.getRating() + " (Nog niet bijgewerkt)");

                setValues();
            }
        }
    }
}
