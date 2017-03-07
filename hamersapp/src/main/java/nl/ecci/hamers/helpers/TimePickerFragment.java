package nl.ecci.hamers.helpers;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.Button;
import android.widget.TimePicker;

import java.util.Calendar;

import nl.ecci.hamers.R;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hour, int minute) {
        Button eventTimeButton = (Button) getActivity().findViewById(R.id.event_time_button);
        Button eventEndTimeButton = (Button) getActivity().findViewById(R.id.event_end_time_button);
        Button deadlineTimeButton = (Button) getActivity().findViewById(R.id.event_deadline_time_button);

        StringBuilder builder = new StringBuilder();
        builder.append(hour).append(":");

        if (minute < 10) {
            builder.append(0).append(minute);
        } else {
            builder.append(minute);
        }

        String time = builder.toString();

        if (getActivity().getSupportFragmentManager().findFragmentByTag("time") != null) {
            eventTimeButton.setText(time);
        }

        if (getActivity().getSupportFragmentManager().findFragmentByTag("end_time") != null) {
            eventEndTimeButton.setText(time);
        }
        if (getActivity().getSupportFragmentManager().findFragmentByTag("deadline_time") != null) {
            deadlineTimeButton.setText(time);
        }
    }
}