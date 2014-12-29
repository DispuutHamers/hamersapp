package nl.ecci.Hamers;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
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
        DialogFragment datePicker = new DatePickerFragment();
        datePicker.show(fragmanager, "date");
        //unpostQuote(1, "test", "testactivity", 1, 1, 1, 1, 2015, 2, 1, 2, 1, 2015, 1, 1, 1, 1, 2015);
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

    private void postQuote(int userid, String title, String description,
                           int eventStartMinutes,  int eventStartHour, int eventStartDay, int eventStartMonth, int eventStartYear,
                           int eventEndMinutes,  int eventEndHour, int eventEndDay, int eventEndMonth, int eventEndYear,
                           int eventDeadlineMinutes,  int eventDeadlineHour, int eventDeadlineDay, int eventDeadlineMonth, int eventDeadlineYear) {
        String arguments = "event[user_id]=" + userid + "&event[title]=" + title + "&event[beschrijving]=" + description
                            + "&event[end_time(5i)]=" + eventEndMinutes + "&event[end_time(4i)]=" + eventEndHour + " &event[end_time(3i)]=" + eventEndDay + "&event[end_time(2i)]=" + eventEndMonth + "&event[end_time(1i)]=" +eventEndYear
                            + "&event[deadline(5i)]=" +eventDeadlineMinutes + "&event[deadline(4i)]=" + eventDeadlineHour + "&event[deadline(3i)]="  + eventDeadlineDay + "&event[deadline(2i)]=" +eventDeadlineMonth + "&event[deadline(1i)]=" + eventDeadlineYear
                            + "&event[date(5i)]=" +eventStartMinutes + "&event[date(4i)]="  + eventStartHour + "&event[date(3i)]=" + eventStartDay + "&event[date(2i)]="+ eventStartMonth + "&event[date(1i)]=" + eventStartYear;


        SendPostRequest req = new SendPostRequest(this, SendPostRequest.EVENTUTL, PreferenceManager.getDefaultSharedPreferences(this), arguments);
        req.execute();
    }
}

