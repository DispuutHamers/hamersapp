package nl.ecci.hamers.ui.activities

import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import com.android.volley.VolleyError
import kotlinx.android.synthetic.main.activity_new_item.*
import kotlinx.android.synthetic.main.stub_new_meeting.*
import nl.ecci.hamers.R
import nl.ecci.hamers.data.Loader
import nl.ecci.hamers.data.PostCallback
import nl.ecci.hamers.models.Meeting
import nl.ecci.hamers.ui.activities.MainActivity
import nl.ecci.hamers.ui.fragments.DatePickerFragment
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class NewMeetingActivity : NewItemActivity() {

    private var meeting: Meeting? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_item)

        initToolbar()

        stub_new_item.layoutResource = R.layout.stub_new_meeting
        stub_new_item.inflate()

        meeting = gson.fromJson(intent.getStringExtra(Meeting.MEETING), Meeting::class.java)

        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", MainActivity.Companion.locale)

        if (meeting != null && meeting_subject != null && meeting_agenda != null) {
            meeting_subject.setText(meeting!!.subject)
            meeting_agenda.setText(meeting!!.agenda)
            meeting_date_button.text = dateFormat.format(meeting!!.date)
        } else {
            meeting_date_button.text = dateFormat.format(calendar.time)
        }
    }

    override fun postItem() {
        if (meeting_date_button != null) {
            val subject = meeting_subject.text.toString()
            val agenda = meeting_agenda.text.toString()
            val notes = meeting_notes.text.toString()
            val date = meeting_date_button.text.toString()

            val body = JSONObject()
            try {
                body.put("onderwerp", subject)
                body.put("agenda", agenda)
                body.put("notes", notes)
                body.put("date", date)
                if (meeting != null) {
                    Loader.postOrPatchData(this, Loader.MEETINGURL, body, meeting!!.id, object : PostCallback {
                        override fun onSuccess(response: JSONObject) {
                            finish()
                        }

                        override fun onError(error: VolleyError) {
                            disableLoadingAnimation()
                        }
                    })
                } else {
                    Loader.postOrPatchData(this, Loader.MEETINGURL, body, -1, object : PostCallback {
                        override fun onSuccess(response: JSONObject) {
                            finish()
                        }

                        override fun onError(error: VolleyError) {
                            disableLoadingAnimation()
                        }
                    })
                }
            } catch (ignored: JSONException) {
            }

        }
    }

    fun showDatePickerDialog(v: View) {
        DatePickerFragment().show(supportFragmentManager, "vergaderdatum")
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        val date = day.toString() + "-" + (month + 1) + "-" + year

        if (supportFragmentManager.findFragmentByTag("vergaderdatum") != null) {
            meeting_date_button?.text = date
        }
    }
}

