package nl.ecci.Hamers.Events;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import nl.ecci.Hamers.Helpers.Fragments.DatePickerFragment;
import nl.ecci.Hamers.Helpers.Fragments.TimePickerFragment;
import nl.ecci.Hamers.R;

public class NewEventActivity extends ActionBarActivity {
    FragmentManager fragmanager = getSupportFragmentManager();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_event_activity);

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
        datePicker.show(fragmanager, "date");
    }

    public void showEndDatePickerDialog(View v) {
        DialogFragment datePicker = new DatePickerFragment();
        datePicker.show(fragmanager, "end_date");
    }

    public void showTimePickerDialog(View v) {
        DialogFragment datePicker = new TimePickerFragment();
        datePicker.show(fragmanager, "time");
    }

    public void showEndTimePickerDialog(View v) {
        DialogFragment datePicker = new TimePickerFragment();
        datePicker.show(fragmanager, "end_time");
    }
}
