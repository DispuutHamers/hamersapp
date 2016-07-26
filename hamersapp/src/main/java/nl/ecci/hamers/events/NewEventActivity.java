package nl.ecci.hamers.events;

import android.os.Bundle;
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

import org.json.JSONException;
import org.json.JSONObject;

import nl.ecci.hamers.MainActivity;
import nl.ecci.hamers.R;
import nl.ecci.hamers.helpers.DataManager;
import nl.ecci.hamers.helpers.HamersActivity;
import nl.ecci.hamers.helpers.fragments.DatePickerFragment;
import nl.ecci.hamers.helpers.fragments.TimePickerFragment;

public class NewEventActivity extends HamersActivity {
    private final FragmentManager fragmanager = getSupportFragmentManager();
    private RelativeLayout parentLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_new_activity);

        parentLayout = (RelativeLayout) findViewById(R.id.new_event_parent);


        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
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
     */
    public void postEvent(View v) {
        EditText event_title = (EditText) findViewById(R.id.new_event_title);
        EditText event_location = (EditText) findViewById(R.id.event_location);
        EditText event_beschrijving = (EditText) findViewById(R.id.event_beschrijving);
        Button eventTimeButton = (Button) findViewById(R.id.event_time_button);
        Button eventEndTimeButton = (Button) findViewById(R.id.end_time_button);
        Button eventDateButton = (Button) findViewById(R.id.event_date_button);
        Button eventEndDateButton = (Button) findViewById(R.id.end_date_button);
        Button deadlineTimeButton = (Button) findViewById(R.id.deadline_time_button);
        Button deadlineDateButton = (Button) findViewById(R.id.deadline_date_button);

        String title = event_title.getText().toString();
        String location = event_location.getText().toString();
        String description = event_beschrijving.getText().toString();
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

            JSONObject body = new JSONObject();
            try {
                body.put("title", title);
                body.put("beschrijving", description);
                body.put("location", location);
                body.put("end_time", MainActivity.parseDate(eventEndDate + " " + eventEndTime));
                body.put("deadline", MainActivity.parseDate(deadlineDate + " " + deadlineTime));
                body.put("date", MainActivity.parseDate(eventDate + " " + eventTime));
            } catch (JSONException ignored) {
            }

            DataManager.postOrPatchData(this, MainActivity.prefs, DataManager.EVENTURL, -1, DataManager.EVENTKEY, body);
        } else {
            Snackbar.make(parentLayout, getResources().getString(R.string.missing_fields), Snackbar.LENGTH_SHORT).show();
        }
    }
}
