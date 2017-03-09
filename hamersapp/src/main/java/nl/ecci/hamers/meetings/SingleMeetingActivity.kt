package nl.ecci.hamers.meetings

import android.os.Bundle
import android.widget.TextView
import kotlinx.android.synthetic.main.detail_meeting.*
import nl.ecci.hamers.MainActivity
import nl.ecci.hamers.R
import nl.ecci.hamers.helpers.HamersActivity
import nl.ecci.hamers.helpers.Utils
import us.feras.mdv.MarkdownView

class SingleMeetingActivity : HamersActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.detail_meeting)

        initToolbar()

        val meeting = Utils.getMeeting(MainActivity.prefs, intent.getIntExtra(Meeting.ID, -1))

        meeting_subject.text = meeting.subject
        meeting_date.text = MainActivity.appDF.format(meeting.date)
        meeting_user.text = String.format("Genotuleerd door: %s", Utils.getUser(this, meeting.userID).name)
        meeting_agenda.loadMarkdown(meeting.agenda)
        meeting_notes.loadMarkdown(meeting.notes)
    }
}
