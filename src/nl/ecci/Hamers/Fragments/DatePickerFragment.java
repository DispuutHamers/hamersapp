package nl.ecci.Hamers.Fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.Button;
import android.widget.DatePicker;
import nl.ecci.Hamers.R;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

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
        Button eventEndTimeButton = (Button) getActivity().findViewById(R.id.end_time_button);
        Button beerDateButton = (Button) getActivity().findViewById(R.id.pick_date_button);

        StringBuilder builder = new StringBuilder();
        builder.append(day).append("-")
                .append(month).append("-")
                .append(year);
        String date = builder.toString();

        if(getActivity().getSupportFragmentManager().findFragmentByTag("date") != null) {
            eventDateButton.setText(date);
        }

        if(getActivity().getSupportFragmentManager().findFragmentByTag("end_time") != null) {
            eventEndTimeButton.setText(date);
        }

        if(getActivity().getSupportFragmentManager().findFragmentByTag("proefdatum") != null) {
            beerDateButton.setText(date);
        }
    }
}