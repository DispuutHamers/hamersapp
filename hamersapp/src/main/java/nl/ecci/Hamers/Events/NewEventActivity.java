package nl.ecci.Hamers.Events;

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
import android.widget.LinearLayout;
import nl.ecci.Hamers.Helpers.Fragments.DatePickerFragment;
import nl.ecci.Hamers.Helpers.Fragments.TimePickerFragment;
import nl.ecci.Hamers.Helpers.SendPostRequest;
import nl.ecci.Hamers.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class NewEventActivity extends AppCompatActivity {
    private final FragmentManager fragmanager = getSupportFragmentManager();
    private LinearLayout parentLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_event_activity);

        parentLayout = (LinearLayout) findViewById(R.id.new_event_parent);

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

            String title = event_title.getText().toString();
            String location = URLEncoder.encode(event_location.getText().toString(), "UTF-8");
            String description = URLEncoder.encode(event_beschrijving.getText().toString(), "UTF-8");
            String eventTime = eventTimeButton.getText().toString();
            String eventEndTime = eventEndTimeButton.getText().toString();
            String eventDate = eventDateButton.getText().toString();
            String eventEndDate = eventEndDateButton.getText().toString();
            String deadlineTime = deadlineTimeButton.getText().toString();
            String deadlineDate = deadlineDateButton.getText().toString();

            if (!eventDate.contains("Datum") && !title.equals("") && !description.equals("") &&
                    !eventTime.contains("Tijd") && !eventEndDate.contains("Datum") && !eventEndTime.contains("Tijd") &&
                    !deadlineDate.contains("Datum") && !deadlineTime.contains("Tijd")) {
                String[] dateParts = eventDate.split("-");
                int eventStartDay = Integer.parseInt(dateParts[0]);
                int eventStartMonth = Integer.parseInt(dateParts[1]);
                int eventStartYear = Integer.parseInt(dateParts[2]);

                String[] endDateParts = eventEndDate.split("-");
                int eventEndDay = Integer.parseInt(endDateParts[0]);
                int eventEndMonth = Integer.parseInt(endDateParts[1]);
                int eventEndYear = Integer.parseInt(endDateParts[2]);

                String[] timeParts = eventTime.split(":");
                int eventStartHour = Integer.parseInt(timeParts[0]);
                int eventStartMinutes = Integer.parseInt(timeParts[1]);

                String[] endTimeParts = eventEndTime.split(":");
                int eventEndHour = Integer.parseInt(endTimeParts[0]);
                int eventEndMinutes = Integer.parseInt(endTimeParts[1]);

                String[] deadlineTimeParts = deadlineTime.split(":");
                int eventDeadlineHour = Integer.parseInt(deadlineTimeParts[0]);
                int eventDeadlineMinutes = Integer.parseInt(deadlineTimeParts[1]);

                String[] deadlineDateParts = deadlineDate.split("-");
                int eventDeadlineDay = Integer.parseInt(deadlineDateParts[0]);
                int eventDeadlineMonth = Integer.parseInt(deadlineDateParts[1]);
                int eventDeadlineYear = Integer.parseInt(deadlineDateParts[2]);

                String arguments = "&event[title]=" + title + "&event[beschrijving]=" + description + "&event[location]=" + location
                        + "&event[end_time(5i)]=" + eventEndMinutes + "&event[end_time(4i)]=" + eventEndHour + "&event[end_time(3i)]=" + eventEndDay + "&event[end_time(2i)]=" + eventEndMonth + "&event[end_time(1i)]=" + eventEndYear
                        + "&event[deadline(5i)]=" + eventDeadlineMinutes + "&event[deadline(4i)]=" + eventDeadlineHour + "&event[deadline(3i)]=" + eventDeadlineDay + "&event[deadline(2i)]=" + eventDeadlineMonth + "&event[deadline(1i)]=" + eventDeadlineYear
                        + "&event[date(5i)]=" + eventStartMinutes + "&event[date(4i)]=" + eventStartHour + "&event[date(3i)]=" + eventStartDay + "&event[date(2i)]=" + eventStartMonth + "&event[date(1i)]=" + eventStartYear;
                SendPostRequest req = new SendPostRequest(this, null, EventFragment.parentLayout, SendPostRequest.EVENTURL, PreferenceManager.getDefaultSharedPreferences(this), arguments);
                req.execute();
            } else {
                Snackbar.make(parentLayout, getResources().getString(R.string.missing_fields), Snackbar.LENGTH_SHORT).show();
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
