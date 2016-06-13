package nl.ecci.hamers.meetings;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import nl.ecci.hamers.MainActivity;
import nl.ecci.hamers.R;
import nl.ecci.hamers.helpers.DataManager;
import nl.ecci.hamers.helpers.fragments.DatePickerFragment;

public class NewMeetingActivity extends AppCompatActivity {

    Button date_button;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meetings_new_acitivity);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        // Set date to current date
        date_button = (Button) findViewById(R.id.meeting_date_button);
        if (date_button != null) {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", MainActivity.locale);
            date_button.setText(dateFormat.format(calendar.getTime()));
        }
    }

    public void postMeeting(View view) {
        EditText meeting_subject = (EditText) findViewById(R.id.meeting_subject);
        EditText meeting_agenda = (EditText) findViewById(R.id.meeting_agenda);
        EditText meeting_notes = (EditText) findViewById(R.id.meeting_notes);

        if (meeting_subject != null && meeting_agenda != null && meeting_notes != null && date_button != null) {
            String subject = meeting_subject.getText().toString();
            String agenda = meeting_agenda.getText().toString();
            String notes = meeting_notes.getText().toString();
            String date = date_button.getText().toString();

            JSONObject body = new JSONObject();
            try {
                body.put("subject", subject);
                body.put("agenda", agenda);
                body.put("notes", notes);
                body.put("date", MainActivity.dbDF.parse(date));
            } catch (JSONException | ParseException ignored) {
            }
            DataManager.postData(this, MainActivity.prefs, DataManager.MEETINGURL, DataManager.MEETINGKEY, body);
        }
    }

    public void showDatePickerDialog(View v) {
        DialogFragment datePicker = new DatePickerFragment();
        datePicker.show(getSupportFragmentManager(), "vergaderdatum");
    }
}

