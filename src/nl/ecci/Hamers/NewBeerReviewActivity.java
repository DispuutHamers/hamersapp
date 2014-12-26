package nl.ecci.Hamers;

import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.SeekBar;
import android.widget.TextView;
import nl.ecci.Hamers.Fragments.DatePickerFragment;

import java.util.Calendar;

public class NewBeerReviewActivity extends ActionBarActivity implements SeekBar.OnSeekBarChangeListener {
    private SeekBar sb;
    private TextView progress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_beer_review_activity);

        // Seekbar
        sb = (SeekBar) findViewById(R.id.ratingseekbar);
        sb.setOnSeekBarChangeListener(this);
        progress = (TextView) findViewById(R.id.rating);
    }

    public void showDatePickerDialog(View v) {
        DialogFragment datePicker = new DatePickerFragment();
        datePicker.show(getSupportFragmentManager(), "proefdatum");
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int rating, boolean fromUser) {
        // change progress text label with current seekbar value
        progress.setText("Cijfer: " + rating);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }
}