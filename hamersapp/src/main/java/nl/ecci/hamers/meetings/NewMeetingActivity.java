package nl.ecci.hamers.meetings;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import nl.ecci.hamers.MainActivity;
import nl.ecci.hamers.R;
import nl.ecci.hamers.helpers.DataManager;
import nl.ecci.hamers.helpers.HamersActivity;
import nl.ecci.hamers.helpers.DatePickerFragment;

public class NewMeetingActivity extends HamersActivity {

    private Meeting meeting;
    private Button date_button;

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

        EditText meeting_subject = (EditText) findViewById(R.id.meeting_subject);
        EditText meeting_agenda = (EditText) findViewById(R.id.meeting_agenda);

        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        meeting = gson.fromJson(getIntent().getStringExtra(Meeting.ID), Meeting.class);

        date_button = (Button) findViewById(R.id.meeting_date_button);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", MainActivity.locale);

        if (meeting != null && meeting_subject != null && meeting_agenda != null) {
            meeting_subject.setText(meeting.getSubject());
            meeting_agenda.setText(meeting.getAgenda());
            date_button.setText(dateFormat.format(meeting.getDate()));
        } else {
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
                body.put("onderwerp", subject);
                body.put("agenda", agenda);
                body.put("notes", notes);
                body.put("date", MainActivity.parseDate(date));
                if (meeting != null) {
                    DataManager.postOrPatchData(this, MainActivity.prefs, DataManager.MEETINGURL, meeting.getID(), DataManager.MEETINGKEY, body);
                } else {
                    DataManager.postOrPatchData(this, MainActivity.prefs, DataManager.MEETINGURL, -1, DataManager.MEETINGKEY, body);
                }
            } catch (JSONException ignored) {
            }
        }
    }

    public void showDatePickerDialog(View v) {
        DialogFragment datePicker = new DatePickerFragment();
        datePicker.show(getSupportFragmentManager(), "vergaderdatum");
    }
}

