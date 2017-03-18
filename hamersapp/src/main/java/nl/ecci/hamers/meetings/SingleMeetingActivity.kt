package nl.ecci.hamers.meetings

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_detail_item.*
import kotlinx.android.synthetic.main.stub_detail_meeting.*
import nl.ecci.hamers.MainActivity
import nl.ecci.hamers.R
import nl.ecci.hamers.helpers.DataUtils
import nl.ecci.hamers.helpers.HamersActivity

class SingleMeetingActivity : HamersActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_item)

        initToolbar()

        stub_detail_item.layoutResource = R.layout.stub_detail_meeting
        stub_detail_item.inflate()

        val meeting = DataUtils.getMeeting(MainActivity.prefs, intent.getIntExtra(Meeting.ID, -1))

        meeting_subject.text = meeting.subject
        meeting_date.text = MainActivity.appDF.format(meeting.date)
        meeting_user.text = String.format("Genotuleerd door: %s", DataUtils.getUser(this, meeting.userID).name)
        meeting_agenda.loadMarkdown(meeting.agenda)
        meeting_notes.loadMarkdown(meeting.notes)
    }
}
