package nl.ecci.hamers.events;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import nl.ecci.hamers.R;
import nl.ecci.hamers.helpers.DataManager;
import nl.ecci.hamers.helpers.fragments.DatePickerFragment;
import nl.ecci.hamers.helpers.fragments.TimePickerFragment;

public class NewEventActivity extends AppCompatActivity {
    private final FragmentManager fragmanager = getSupportFragmentManager();
    private RelativeLayout parentLayout;
    private SharedPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_event_activity);

        parentLayout = (RelativeLayout) findViewById(R.id.new_event_parent);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
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
        DialogFragment picker = new DatePickerFragment();
        picker.show(fragmanager, "date");
    }

    public void showEndDatePickerDialog(View v) {
        DialogFragment picker = new DatePickerFragment();
        picker.show(fragmanager, "end_date");
    }

    public void showTimePickerDialog(View v) {
        DialogFragment picker = new TimePickerFragment();
        picker.show(fragmanager, "time");
    }

    public void showEndTimePickerDialog(View v) {
        DialogFragment picker = new TimePickerFragment();
        picker.show(fragmanager, "end_time");
    }

    public void showDeadlineTimePickerDialog(View v) {
        DialogFragment picker = new TimePickerFragment();
        picker.show(fragmanager, "deadline_time");
    }

    public void showDeadlineDatePickerDialog(View v) {
        DialogFragment picker = new DatePickerFragment();
        picker.show(fragmanager, "deadline_date");
    }

    /**
     * Posts event
     *
     * @param v
     */
    public void postEvent(View v) {
        try {
            EditText event_title = (EditText) findViewById(R.id.event_title);
            EditText event_location = (EditText) findViewById(R.id.event_location);
            EditText event_beschrijving = (EditText) findViewById(R.id.event_beschrijving);
            Button eventTimeButton = (Button) findViewById(R.id.event_time_button);
            Button eventEndTimeButton = (Button) findViewById(R.id.end_time_button);
            Button eventDateButton = (Button) findViewById(R.id.event_date_button);
            Button eventEndDateButton = (Button) findViewById(R.id.end_date_button);
            Button deadlineTimeButton = (Button) findViewById(R.id.deadline_time_button);
            Button deadlineDateButton = (Button) findViewById(R.id.deadline_date_button);

            String title = URLEncoder.encode(event_title.getText().toString(), "UTF-8");
            String location = URLEncoder.encode(event_location.getText().toString(), "UTF-8");
            String description = URLEncoder.encode(event_beschrijving.getText().toString(), "UTF-8");
            String eventTime = eventTimeButton.getText().toString();
            String eventEndTime = eventEndTimeButton.getText().toString();
            String eventDate = eventDateButton.getText().toString();
            String eventEndDate = eventEndDateButton.getText().toString();
            String deadlineTime = deadlineTimeButton.getText().toString();
            String deadlineDate = deadlineDateButton.getText().toString();

            if (!eventDate.contains("Datum") &&
                    !title.equals("") &&
                    !description.equals("") &&
                    !eventTime.contains("Tijd") &&
                    !eventEndDate.contains("Datum") &&
                    !eventEndTime.contains("Tijd") &&
                    !deadlineDate.contains("Datum") &&
                    !deadlineTime.contains("Tijd")) {

                String[] dateParts = eventDate.split("-");
                String[] endDateParts = eventEndDate.split("-");
                String[] timeParts = eventTime.split(":");
                String[] endTimeParts = eventEndTime.split(":");
                String[] deadlineTimeParts = deadlineTime.split(":");
                String[] deadlineDateParts = deadlineDate.split("-");

                Map<String, String> params = new HashMap<>();
                params.put("event[title]", title);
                params.put("event[beschrijving]", description);
                params.put("event[location]", location);
                params.put("event[end_time(5i)]", endTimeParts[1]);
                params.put("event[end_time(4i)]", endTimeParts[0]);
                params.put("event[end_time(3i)]", endDateParts[0]);
                params.put("event[end_time(2i)]", endDateParts[1]);
                params.put("event[end_time(1i)]", endDateParts[2]);
                params.put("event[deadline(5i)]", deadlineTimeParts[1]);
                params.put("event[deadline(4i)]", deadlineTimeParts[0]);
                params.put("event[deadline(3i)]",  deadlineDateParts[0]);
                params.put("event[deadline(2i)]",  deadlineDateParts[1]);
                params.put("event[deadline(1i)]",  deadlineDateParts[2]);
                params.put("event[date(5i)]", timeParts[1]);
                params.put("event[date(4i)]", timeParts[0]);
                params.put("event[date(3i)]", dateParts[0]);
                params.put("event[date(2i)]", dateParts[1]);
                params.put("event[date(1i)]", dateParts[2]);

                DataManager.postData(this, prefs, DataManager.EVENTURL, DataManager.EVENTKEY, params);
            } else {
                Snackbar.make(parentLayout, getResources().getString(R.string.missing_fields), Snackbar.LENGTH_SHORT).show();
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
