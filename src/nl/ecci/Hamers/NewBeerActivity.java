package nl.ecci.Hamers;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import nl.ecci.Hamers.Fragments.DatePickerFragment;

public class NewBeerActivity extends ActionBarActivity implements SeekBar.OnSeekBarChangeListener {
    private SeekBar sb;
    private TextView textProgress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_beer_activity);

        sb = (SeekBar)findViewById(R.id.ratingseekbar); // make seekbar object
        sb.setOnSeekBarChangeListener(this); // set seekbar listener.
        // since we are using this class as the listener the class is "this"

        // make text label for progress value
        textProgress = (TextView)findViewById(R.id.rating);
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int rating, boolean fromUser) {
        // change progress text label with current seekbar value
        textProgress.setText("Cijfer: " + rating);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }
}