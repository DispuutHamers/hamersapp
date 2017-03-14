package nl.ecci.hamers.events

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import com.android.volley.VolleyError
import kotlinx.android.synthetic.main.activity_new_item.*
import kotlinx.android.synthetic.main.stub_new_event.*
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

class NewEventActivity : NewItemActivity(), TimePickerDialog.OnTimeSetListener {

    private val fragmentManager = supportFragmentManager
    private var eventID: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_item)

        initToolbar()

        stub.layoutResource = R.layout.stub_new_event
        stub.inflate()


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

    override fun onDateSet(datePicker: DatePicker?, year: Int, month: Int, day: Int) {
        val date = day.toString() + "-" + (month + 1) + "-" + year

        if (supportFragmentManager.findFragmentByTag("date") != null) {
            event_date_button?.text = date
        }
        if (supportFragmentManager.findFragmentByTag("end_date") != null) {
            event_end_date_button?.text = date
        }
        if (supportFragmentManager.findFragmentByTag("deadline_date") != null) {
            event_deadline_date_button?.text = date
        }
    }

    override fun onTimeSet(view: TimePicker, hour: Int, minute: Int) {
        val builder = StringBuilder()
        builder.append(hour).append(":")

        if (minute < 10) {
            builder.append(0).append(minute)
        } else {
            builder.append(minute)
        }

        val time = builder.toString()

        if (supportFragmentManager.findFragmentByTag("time") != null) {
            event_time_button?.text = time
        }
        if (supportFragmentManager.findFragmentByTag("end_time") != null) {
            event_end_time_button?.text = time
        }
        if (supportFragmentManager.findFragmentByTag("deadline_time") != null) {
            event_deadline_time_button?.text = time
        }
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
