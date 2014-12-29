package nl.ecci.Hamers;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import nl.ecci.Hamers.Fragments.DatePickerFragment;
import nl.ecci.Hamers.Fragments.TimePickerFragment;

public class NewEventActivity extends ActionBarActivity {
    FragmentManager fragmanager = getSupportFragmentManager();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_event_activity);
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
     * @param v
     */
    public void postEvent(View v) {
        EditText event_title_temp = (EditText) findViewById(R.id.event_title);
        EditText event_beschrijving_temp = (EditText) findViewById(R.id.event_beschrijving);
        Button eventTimeButton = (Button) findViewById(R.id.event_time_button);
        Button eventEndTimeButton = (Button) findViewById(R.id.end_time_button);
        Button eventDateButton = (Button) findViewById(R.id.event_date_button);
        Button eventEndDateButton = (Button) findViewById(R.id.end_date_button);
        Button deadlineTimeButton = (Button) findViewById(R.id.deadline_time_button);
        Button deadlineDateButton = (Button) findViewById(R.id.deadline_date_button);

        String title = event_title_temp.getText().toString();
        String description = event_beschrijving_temp.getText().toString();
        String eventTime = eventTimeButton.getText().toString();
        String eventEndTime = eventEndTimeButton.getText().toString();
        String eventDate = eventDateButton.getText().toString();
        String eventEndDate = eventEndDateButton.getText().toString();
        String deadlineTime = deadlineTimeButton.getText().toString();
        String deadlineDate = deadlineDateButton.getText().toString();

        String[] dateParts = eventDate.split("-");
        int eventStartDay = Integer.parseInt(dateParts[0]);
        int eventStartMonth = Integer.parseInt(dateParts[1]);
        int eventStartYear = Integer.parseInt(dateParts[2]);

        String[] endDateParts = eventEndDate.split("-");
        int eventEndDay = Integer.parseInt(endDateParts[0]);
        int eventEndMonth = Integer.parseInt(endDateParts[1]);
        int eventEndYear = Integer.parseInt(endDateParts[2]);

        String[] timeParts = eventTime.split(":");
        int eventStartMinutes = Integer.parseInt(timeParts[0]);
        int eventStartHour = Integer.parseInt(timeParts[1]);

        String[] endTimeParts = eventEndTime.split(":");
        int eventEndMinutes = Integer.parseInt(endTimeParts[0]);
        int eventEndHour = Integer.parseInt(endTimeParts[1]);

        String[] deadlineTimeParts = deadlineTime.split(":");
        int eventDeadlineMinutes = Integer.parseInt(deadlineTimeParts[0]);
        int eventDeadlineHour = Integer.parseInt(deadlineTimeParts[1]);

        String[] deadlineDateParts = deadlineDate.split("-");
        int eventDeadlineDay = Integer.parseInt(deadlineDateParts[0]);
        int eventDeadlineMonth = Integer.parseInt(deadlineDateParts[1]);
        int eventDeadlineYear = Integer.parseInt(deadlineDateParts[2]);

        String arguments = "event[user_id]=" + 1 + "&event[title]=" + title + "&event[beschrijving]=" + description
                            + "&event[end_time(5i)]=" + eventEndMinutes + "&event[end_time(4i)]=" + eventEndHour + " &event[end_time(3i)]=" + eventEndDay + "&event[end_time(2i)]=" + eventEndMonth + "&event[end_time(1i)]=" + eventEndYear
                            + "&event[deadline(5i)]=" + eventDeadlineMinutes + "&event[deadline(4i)]=" + eventDeadlineHour + "&event[deadline(3i)]="  + eventDeadlineDay + "&event[deadline(2i)]=" + eventDeadlineMonth + "&event[deadline(1i)]=" + eventDeadlineYear
                            + "&event[date(5i)]=" + eventStartMinutes + "&event[date(4i)]="  + eventStartHour + "&event[date(3i)]=" + eventStartDay + "&event[date(2i)]="+ eventStartMonth + "&event[date(1i)]=" + eventStartYear;

        SendPostRequest req = new SendPostRequest(this, SendPostRequest.EVENTUTL, PreferenceManager.getDefaultSharedPreferences(this), arguments);
        req.execute();
    }
}

