package nl.ecci.hamers.meetings;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import nl.ecci.hamers.MainActivity;
import nl.ecci.hamers.R;
import nl.ecci.hamers.helpers.HamersActivity;
import us.feras.mdv.MarkdownView;

import static nl.ecci.hamers.helpers.Utils.getMeeting;
import static nl.ecci.hamers.helpers.Utils.getUser;

public class SingleMeetingActivity extends HamersActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.meeting_detail);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        TextView subjectTV = (TextView) findViewById(R.id.meeting_subject);
        TextView dateTV = (TextView) findViewById(R.id.meeting_date);
        TextView userTV = (TextView) findViewById(R.id.meeting_user);
        MarkdownView agendaMV = (MarkdownView) findViewById(R.id.meeting_agenda);
        MarkdownView notesMV = (MarkdownView) findViewById(R.id.meeting_notes);

        Meeting meeting = getMeeting(MainActivity.prefs, getIntent().getIntExtra(Meeting.ID, -1));

        if (subjectTV != null && dateTV != null && userTV != null && agendaMV != null && notesMV != null) {
            subjectTV.setText(meeting.getSubject());
            dateTV.setText(MainActivity.appDF.format(meeting.getDate()));
            userTV.setText(String.format("Genotuleerd door: %s", getUser(MainActivity.prefs, meeting.getUserID()).getName()));
            agendaMV.loadMarkdown(meeting.getAgenda());
            notesMV.loadMarkdown(meeting.getNotes());
        }
    }
}
