package nl.ecci.hamers.meetings

import android.os.Bundle
import android.view.View
import com.android.volley.VolleyError
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_new_item.*
import kotlinx.android.synthetic.main.stub_new_meeting.*
import nl.ecci.hamers.MainActivity
import nl.ecci.hamers.R
import nl.ecci.hamers.helpers.DatePickerFragment
import nl.ecci.hamers.helpers.NewItemActivity
import nl.ecci.hamers.loader.Loader
import nl.ecci.hamers.loader.PostCallback
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

        val gson = GsonBuilder().create()
        meeting = gson.fromJson(intent.getStringExtra(Meeting.ID), Meeting::class.java)

        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", MainActivity.locale)

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
                body.put("date", MainActivity.parseDate(date))
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
}

