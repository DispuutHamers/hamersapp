package nl.ecci.hamers.beers;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import nl.ecci.hamers.helpers.Fragments.DatePickerFragment;
import nl.ecci.hamers.helpers.SendPostRequest;
import nl.ecci.hamers.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class NewBeerReviewActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    private int id;
    private TextView progress;
    private int cijfer;
    private LinearLayout parentLayout;

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
                int proefdag = Integer.parseInt(dateParts[0]);
                int proefmaand = Integer.parseInt(dateParts[1]);
                int proefjaar = Integer.parseInt(dateParts[2]);

                String arguments = "&review[beer_id]=" + id + "&review[description]=" + review + "&review[rating]=" + cijfer
                        + "&review[proefdatum(1i)]=" + proefjaar + "&review[proefdatum(2i)]=" + proefmaand + "&review[proefdatum(3i)]=" + proefdag + "&review[proefdatum(4i)]=20" + "&review[proefdatum(5i)]=00";

                SendPostRequest req = new SendPostRequest(this, null, SingleBeerActivity.parentLayout, SendPostRequest.REVIEWURL, PreferenceManager.getDefaultSharedPreferences(this), arguments);
                req.execute();
            } else {
                Snackbar.make(parentLayout, getString(R.string.missing_fields), Snackbar.LENGTH_LONG).show();
            }
        } catch (UnsupportedEncodingException e) {
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
