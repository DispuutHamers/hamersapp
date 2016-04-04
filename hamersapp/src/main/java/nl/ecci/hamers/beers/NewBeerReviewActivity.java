package nl.ecci.hamers.beers;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import nl.ecci.hamers.R;
import nl.ecci.hamers.helpers.DataManager;
import nl.ecci.hamers.helpers.fragments.DatePickerFragment;

public class NewBeerReviewActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    private int id;
    private TextView progress;
    private int cijfer;
    private LinearLayout parentLayout;
    private SharedPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_beer_review_activity);

        parentLayout = (LinearLayout) findViewById(R.id.new_beer_review_parent);

        final android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
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
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        date_button.setText(dateFormat.format(calendar.getTime()));

        Bundle extras = getIntent().getExtras();
        id = extras.getInt("id");
        String name = extras.getString("name");

        TextView title = (TextView) findViewById(R.id.review_title);
        title.setText(name);

        cijfer = 1;

        SeekBar sb = (SeekBar) findViewById(R.id.ratingseekbar);
        sb.setOnSeekBarChangeListener(this);
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

    @Override
    public void onProgressChanged(SeekBar seekBar, int rating, boolean fromUser) {
        cijfer = rating + 1;
        progress.setText("Cijfer: " + cijfer);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    public void postReview(View view) {
        try {
            EditText review_body = (EditText) findViewById(R.id.review_body);
            String review = URLEncoder.encode(review_body.getText().toString(), "UTF-8");
            Button date_button = (Button) findViewById(R.id.pick_date_button);
            String date = date_button.getText().toString();

            if (review.length() > 2) {
                String[] dateParts = date.split("-");

                Map<String, String> params = new HashMap<>();
                params.put("review[beer_id]", Integer.toString(id));
                params.put("review[description]", review);
                params.put("review[rating]", Integer.toString(cijfer));
                params.put("review[proefdatum(1i)]", dateParts[2]);
                params.put("review[proefdatum(2i)]", dateParts[1]);
                params.put("review[proefdatum(3i)]", dateParts[0]);
                params.put("review[proefdatum(4i)]", "20");
                params.put("review[proefdatum(5i)]", "00");

                DataManager.postData(this, prefs, DataManager.REVIEWURL, DataManager.REVIEWKEY, params);
            } else {
                Snackbar.make(parentLayout, getString(R.string.missing_fields), Snackbar.LENGTH_LONG).show();
            }
        } catch (UnsupportedEncodingException ignored) {
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
}
