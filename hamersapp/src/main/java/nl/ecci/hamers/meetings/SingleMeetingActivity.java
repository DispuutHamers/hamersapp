package nl.ecci.hamers.meetings;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import nl.ecci.hamers.MainActivity;
import nl.ecci.hamers.R;

import static nl.ecci.hamers.helpers.DataManager.getMeeting;

public class SingleMeetingActivity extends AppCompatActivity {

    private Meeting meeting;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.single_meeting);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        TextView subjectTV = (TextView) findViewById(R.id.meeting_subject);
        TextView dateTV = (TextView) findViewById(R.id.meeting_date);
        TextView agendaTV = (TextView) findViewById(R.id.meeting_agenda);
        TextView notesTV = (TextView) findViewById(R.id.meeting_notes);

        meeting = getMeeting(MainActivity.prefs, getIntent().getIntExtra(Meeting.ID, -1));

        if (subjectTV != null && dateTV != null && agendaTV != null && notesTV != null) {
            subjectTV.setText(meeting.getSubject());
            dateTV.setText(MainActivity.appDF.format(meeting.getDate()));
            agendaTV.setText(meeting.getAgenda());
            notesTV.setText(meeting.getNotes());
        }
    }
}
