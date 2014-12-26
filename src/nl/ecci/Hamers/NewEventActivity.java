package nl.ecci.Hamers;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import nl.ecci.Hamers.Fragments.DatePickerFragment;

import java.util.Calendar;

public class NewEventActivity extends ActionBarActivity {
    FragmentManager fragmanager = getSupportFragmentManager();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_event_activity);
    }

    public void showDatePickerDialog(View v) {
        DialogFragment datePicker = new DatePickerFragment();
        datePicker.show(fragmanager, "date");
    }

    public void showEndTimePickerDialog(View v) {
        DialogFragment datePicker = new DatePickerFragment();
        datePicker.show(fragmanager, "end_time");
    }
}
