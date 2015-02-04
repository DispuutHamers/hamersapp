package nl.ecci.Hamers.Beers;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import nl.ecci.Hamers.Helpers.Fragments.DatePickerFragment;
import nl.ecci.Hamers.Helpers.SendPostRequest;
import nl.ecci.Hamers.R;

public class NewBeerReviewActivity extends ActionBarActivity implements SeekBar.OnSeekBarChangeListener {
    int id;
    private String review;
    private SeekBar sb;
    private TextView progress;
    private int cijfer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_beer_review_activity);

        Bundle extras = getIntent().getExtras();
        id = extras.getInt("id");

        sb = (SeekBar) findViewById(R.id.ratingseekbar);
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
        Button date_button = (Button) findViewById(R.id.pick_date_button);
        review = review_body.getText().toString();
        String date = date_button.getText().toString();

        String[] dateParts = date.split("-");
        int proefdag = Integer.parseInt(dateParts[0]);
        int proefmaand = Integer.parseInt(dateParts[1]);
        int proefjaar = Integer.parseInt(dateParts[2]);

        String arguments = "&review[beer_id]=" + id + "&review[description]=" + review + "&review[rating]=" + cijfer
                + "&review[proefdatum(1i)]=" + proefjaar + "&review[proefdatum(2i)]=" + proefmaand + "&review[proefdatum(3i)]=" + proefdag + "&review[proefdatum(4i)]=" + 20 + "&review[proefdatum(5i)]=" + 00;
        SendPostRequest req = new SendPostRequest(this, SendPostRequest.REVIEWURL, PreferenceManager.getDefaultSharedPreferences(this), arguments);
        req.execute();
    }
}
