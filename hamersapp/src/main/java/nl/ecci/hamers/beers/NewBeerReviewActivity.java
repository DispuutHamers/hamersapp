package nl.ecci.hamers.beers;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
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
import nl.ecci.hamers.helpers.fragments.DatePickerFragment;

public class NewBeerReviewActivity extends AppCompatActivity {

    private Beer beer;
    private TextView progress;
    private int rating;
    private LinearLayout parentLayout;
    private EditText review_body;
    private Button date_button;
    private SharedPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_beer_review_activity);

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

        // Set date to current date
        Button date_button = (Button) findViewById(R.id.pick_date_button);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", MainActivity.locale);
        date_button.setText(dateFormat.format(calendar.getTime()));

        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        beer = gson.fromJson(getIntent().getStringExtra(Beer.BEER), Beer.class);


        TextView title = (TextView) findViewById(R.id.review_title);
        title.setText(beer.getName());

        rating = 1;

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
        progress = (TextView) findViewById(R.id.rating);
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

    public void showDatePickerDialog(View v) {
        DialogFragment datePicker = new DatePickerFragment();
        datePicker.show(getSupportFragmentManager(), "proefdatum");
    }

    public void postReview(View view) {
        String review = review_body.getText().toString();
        String date = date_button.getText().toString();

        if (review.length() > 2) {
            JSONObject body = new JSONObject();
            try {
                body.put("beer_id", beer.getID());
                body.put("description", review);
                body.put("rating", rating);
                body.put("proefdatum", MainActivity.parseDate(date));
            } catch (JSONException ignored) {
            }

            DataManager.postData(this, prefs, DataManager.REVIEWURL, DataManager.REVIEWKEY, body);
        } else {
            Snackbar.make(parentLayout, getString(R.string.missing_fields), Snackbar.LENGTH_LONG).show();
        }
    }
}
