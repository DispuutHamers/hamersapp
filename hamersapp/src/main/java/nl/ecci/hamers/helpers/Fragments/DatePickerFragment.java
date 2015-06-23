package nl.ecci.hamers.helpers.Fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.Button;
import android.widget.DatePicker;
import nl.ecci.hamers.R;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        Button eventDateButton = (Button) getActivity().findViewById(R.id.event_date_button);
        Button eventEndDateButton = (Button) getActivity().findViewById(R.id.end_date_button);
        Button beerDateButton = (Button) getActivity().findViewById(R.id.pick_date_button);
        Button deadlineDateButton = (Button) getActivity().findViewById(R.id.deadline_date_button);

        String date = String.valueOf(day) + "-" + (month + 1) + "-" + year;

        if (getActivity().getSupportFragmentManager().findFragmentByTag("date") != null) {
            eventDateButton.setText(date);
        }

        if (getActivity().getSupportFragmentManager().findFragmentByTag("end_date") != null) {
            eventEndDateButton.setText(date);
        }

        if (getActivity().getSupportFragmentManager().findFragmentByTag("proefdatum") != null) {
            beerDateButton.setText(date);
        }
        if (getActivity().getSupportFragmentManager().findFragmentByTag("deadline_date") != null) {
            deadlineDateButton.setText(date);
        }
    }
}