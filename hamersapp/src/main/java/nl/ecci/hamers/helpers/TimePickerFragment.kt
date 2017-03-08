package nl.ecci.hamers.helpers

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.text.format.DateFormat
import android.widget.TimePicker
import kotlinx.android.synthetic.main.stub_new_event.*
import java.util.*

class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current time as the default values for the picker
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        // Create a new instance of TimePickerDialog and return it
        return TimePickerDialog(activity, this, hour, minute, DateFormat.is24HourFormat(activity))
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

        if (activity.supportFragmentManager.findFragmentByTag("time") != null) {
            event_time_button.text = time
        }

        if (activity.supportFragmentManager.findFragmentByTag("end_time") != null) {
            event_end_time_button.text = time
        }
        if (activity.supportFragmentManager.findFragmentByTag("deadline_time") != null) {
            event_deadline_time_button.text = time
        }
    }
}