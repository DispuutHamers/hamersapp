package nl.ecci.hamers.events

import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.Toast
import com.android.volley.VolleyError
import kotlinx.android.synthetic.main.event_new_activity.*
import nl.ecci.hamers.MainActivity
import nl.ecci.hamers.R
import nl.ecci.hamers.helpers.DatePickerFragment
import nl.ecci.hamers.helpers.NewItemActivity
import nl.ecci.hamers.helpers.TimePickerFragment
import nl.ecci.hamers.helpers.Utils
import nl.ecci.hamers.loader.Loader
import nl.ecci.hamers.loader.PostCallback
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat

class NewEventActivity : NewItemActivity() {

    private val fragmentManager = supportFragmentManager
    private var eventID: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.event_new_activity)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeButtonEnabled(true)
        }

        val timeFormat = SimpleDateFormat("HH:mm", MainActivity.locale)
        val dateFormat = SimpleDateFormat("dd-mm-yyyy", MainActivity.locale)

        eventID = intent.getIntExtra(Event.EVENT, -1)
        if (eventID != -1) {
            val event = Utils.getEvent(MainActivity.prefs, eventID)
            event_title.setText(event.title)
            event_location.setText(event.location)
            event_beschrijving.setText(event.description)
            event_time_button.text = timeFormat.format(event.date)
            event_end_time_button.text = timeFormat.format(event.endDate)
            event_date_button.text = dateFormat.format(event.date)
            event_end_date_button.text = dateFormat.format(event.endDate)
            event_deadline_time_button.text = timeFormat.format(event.deadline)
            event_deadline_date_button.text = dateFormat.format(event.deadline)
        }
    }

    override fun onResume() {
        super.onResume()
        setTitle(R.string.new_event)
    }

    fun showDatePickerDialog(v: View) {
        val picker = DatePickerFragment()
        picker.show(fragmentManager, "date")
    }

    fun showEndDatePickerDialog(v: View) {
        val picker = DatePickerFragment()
        picker.show(fragmentManager, "end_date")
    }

    fun showTimePickerDialog(v: View) {
        val picker = TimePickerFragment()
        picker.show(fragmentManager, "time")
    }

    fun showEndTimePickerDialog(v: View) {
        val picker = TimePickerFragment()
        picker.show(fragmentManager, "end_time")
    }

    fun showDeadlineTimePickerDialog(v: View) {
        val picker = TimePickerFragment()
        picker.show(fragmentManager, "deadline_time")
    }

    fun showDeadlineDatePickerDialog(v: View) {
        val picker = DatePickerFragment()
        picker.show(fragmentManager, "deadline_date")
    }

    /**
     * Posts event
     */
    override fun postItem() {
        val title = event_title.text.toString()
        val location = event_location.text.toString()
        val description = event_beschrijving.text.toString()
        val eventTime = event_time_button.text.toString()
        val eventEndTime = event_end_time_button.text.toString()
        val eventDate = event_date_button.text.toString()
        val eventEndDate = event_end_date_button.text.toString()
        val deadlineTime = event_deadline_time_button.text.toString()
        val deadlineDate = event_deadline_date_button.text.toString()

        if (!eventDate.contains("Datum") &&
                title.isNotBlank() &&
                description.isNotBlank() &&
                !eventTime.contains("Tijd") &&
                !eventEndDate.contains("Datum") &&
                !eventEndTime.contains("Tijd") &&
                !deadlineDate.contains("Datum") &&
                !deadlineTime.contains("Tijd")) {

            val body = JSONObject()
            try {
                body.put("title", title)
                body.put("beschrijving", description)
                body.put("location", location)
                body.put("end_time", MainActivity.parseDate(eventEndDate + " " + eventEndTime))
                body.put("deadline", MainActivity.parseDate(deadlineDate + " " + deadlineTime))
                body.put("date", MainActivity.parseDate(eventDate + " " + eventTime))
            } catch (ignored: JSONException) {
            }

            Loader.postOrPatchData(this, Loader.EVENTURL, body, eventID, object : PostCallback {
                override fun onSuccess(response: JSONObject) {
                    finish()
                }

                override fun onError(error: VolleyError) {
                    disableLoadingAnimation()
                }
            })
        } else {
            disableLoadingAnimation()
            Utils.showToast(this, getString(R.string.missing_fields), Toast.LENGTH_SHORT)
        }
    }
}
