package nl.ecci.Hamers.Beers;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import nl.ecci.Hamers.Helpers.Fragments.DatePickerFragment;
import nl.ecci.Hamers.Helpers.SendPostRequest;
import nl.ecci.Hamers.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.text.Html.escapeHtml;

public class NewBeerReviewActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {
    private int id;
    private TextView progress;
    private int cijfer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_beer_review_activity);

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

        getSupportActionBar().setHomeButtonEnabled(true);
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
        EditText review_body = (EditText) findViewById(R.id.review_body);
        String review = escapeHtml(review_body.getText().toString());
        Button date_button = (Button) findViewById(R.id.pick_date_button);
        String date = date_button.getText().toString();

        if (review.length() > 2) {
            String[] dateParts = date.split("-");
            int proefdag = Integer.parseInt(dateParts[0]);
            int proefmaand = Integer.parseInt(dateParts[1]);
            int proefjaar = Integer.parseInt(dateParts[2]);

            String arguments = "&review[beer_id]=" + id + "&review[description]=" + review + "&review[rating]=" + cijfer
                    + "&review[proefdatum(1i)]=" + proefjaar + "&review[proefdatum(2i)]=" + proefmaand + "&review[proefdatum(3i)]=" + proefdag + "&review[proefdatum(4i)]=20" + "&review[proefdatum(5i)]=00";

            SendPostRequest req = new SendPostRequest(this, SendPostRequest.REVIEWURL, PreferenceManager.getDefaultSharedPreferences(this), arguments);

            Intent intent = new Intent();
            setResult(RESULT_OK, intent);

            req.execute();
        } else {
            Toast.makeText(this, "Vul alle velden in!", Toast.LENGTH_LONG).show();
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
