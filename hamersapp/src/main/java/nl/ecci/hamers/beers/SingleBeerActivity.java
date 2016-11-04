package nl.ecci.hamers.beers;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
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
import nl.ecci.hamers.loader.Loader;

import static nl.ecci.hamers.MainActivity.prefs;
import static nl.ecci.hamers.helpers.Utils.getBeer;
import static nl.ecci.hamers.helpers.Utils.getOwnUser;
import static nl.ecci.hamers.helpers.Utils.getUser;

public class SingleBeerActivity extends HamersActivity {

    private LayoutInflater inflater;
    private Beer beer;
    private Gson gson;
    private Button reviewButton;
    private ViewGroup reviewViewGroup;
    private Review ownReview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_beer);

        inflater = getLayoutInflater();

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        gson = gsonBuilder.create();

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

        beer = getBeer(MainActivity.prefs, getIntent().getIntExtra(Beer.BEER, -1));

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

        getReviews();
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
                if (review.getUserID() == getOwnUser(MainActivity.prefs).getID()) {
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
    private void createReview(Review review) {
        Intent intent = new Intent(this, NewReviewActivity.class);
        intent.putExtra(Beer.BEER, beer.getID());

        if (review != null) {
            intent.putExtra(Review.REVIEW, gson.toJson(review, Review.class));
        }

        int requestCode = 1;
        startActivityForResult(intent, requestCode);
    }

    private void insertReview(Review review) {
        LinearLayout insertPoint = (LinearLayout) findViewById(R.id.review_insert_point);
        View view = inflater.inflate(R.layout.review_row, insertPoint, false);
        View divider = inflater.inflate(R.layout.divider, insertPoint, false);

        TextView title = (TextView) view.findViewById(R.id.review_title);
        TextView body = (TextView) view.findViewById(R.id.review_body);
        TextView date = (TextView) view.findViewById(R.id.review_date);
        TextView ratingTV = (TextView) view.findViewById(R.id.review_rating);

        title.setText(String.format("%s: ", getUser(MainActivity.prefs, review.getUserID()).getName()));
        body.setText(review.getDescription());
        date.setText(MainActivity.appDF2.format(review.getProefdatum()));
        ratingTV.setText(String.format("Cijfer: %s", review.getRating()));

        // Insert into view
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
                startActivity(intent);
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

    private void refreshActivity() {
        finish();
        startActivity(getIntent());
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        getReviews();
        refreshActivity();
    }

    private void fillRow(View view, final String title, final String description) {
        TextView titleView = (TextView) view.findViewById(R.id.row_title);
        titleView.setText(title);

        TextView descriptionView = (TextView) view.findViewById(R.id.row_description);
        descriptionView.setText(description);
    }
}
