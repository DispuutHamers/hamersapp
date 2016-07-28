package nl.ecci.hamers.beers;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import nl.ecci.hamers.MainActivity;
import nl.ecci.hamers.R;
import nl.ecci.hamers.helpers.DataManager;
import nl.ecci.hamers.helpers.DatePickerFragment;
import nl.ecci.hamers.helpers.HamersActivity;

public class NewBeerReviewActivity extends HamersActivity {

    private Beer beer;
    private Review review;
    private TextView progress;
    private int rating = 1;
    private LinearLayout parentLayout;
    private EditText review_body;
    private Button date_button;
    private SharedPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_new_activity);

        review_body = (EditText) findViewById(R.id.review_body);
        date_button = (Button) findViewById(R.id.pick_date_button);
        parentLayout = (LinearLayout) findViewById(R.id.new_beer_review_parent);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        progress = (TextView) findViewById(R.id.rating);

        Button date_button = (Button) findViewById(R.id.pick_date_button);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", MainActivity.locale);

        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        beer = gson.fromJson(getIntent().getStringExtra(Beer.BEER), Beer.class);
        review = gson.fromJson(getIntent().getStringExtra(Review.REVIEW), Review.class);

        TextView title = (TextView) findViewById(R.id.review_title);
        title.setText(beer.getName());

        SeekBar sb = (SeekBar) findViewById(R.id.ratingseekbar);
        if (sb != null) {
            sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int rating, boolean fromUser) {
                    NewBeerReviewActivity.this.rating = rating + 1;
                    progress.setText(String.format("Cijfer: %s", NewBeerReviewActivity.this.rating));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }

        if (review != null && sb != null) {
            review_body.setText(review.getDescription());
            sb.setProgress(review.getRating() - 1);
            date_button.setText(dateFormat.format(review.getProefdatum()));
        } else {
            date_button.setText(dateFormat.format(calendar.getTime()));
        }
    }

    public void showDatePickerDialog(View v) {
        DialogFragment datePicker = new DatePickerFragment();
        datePicker.show(getSupportFragmentManager(), "proefdatum");
    }

    public void postReview(View view) {
        String review_body = this.review_body.getText().toString();
        String date = date_button.getText().toString();

        if (review_body.length() > 2) {
            JSONObject body = new JSONObject();
            try {
                body.put("beer_id", beer.getID());
                body.put("description", review_body);
                body.put("rating", rating);
                body.put("proefdatum", MainActivity.parseDate(date));
                if (review != null) {
                    DataManager.postOrPatchData(this, prefs, DataManager.REVIEWURL, review.getID(), DataManager.REVIEWKEY, body);
                } else {
                    DataManager.postOrPatchData(this, prefs, DataManager.REVIEWURL, -1, DataManager.REVIEWKEY, body);
                }
            } catch (JSONException ignored) {
            }
        } else {
            Snackbar.make(parentLayout, getString(R.string.missing_fields), Snackbar.LENGTH_LONG).show();
        }
    }
}
