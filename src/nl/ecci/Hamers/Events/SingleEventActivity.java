package nl.ecci.Hamers.Events;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;
import nl.ecci.Hamers.R;

import java.text.ParseException;

import static nl.ecci.Hamers.MainActivity.parseDate;

public class SingleEventActivity extends ActionBarActivity {
    String beschrijving;
    String date;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.single_event);

        TextView beschrijvingTV = (TextView) findViewById(R.id.event_beschrijving);
        TextView dateTV = (TextView) findViewById(R.id.event_date);

        Bundle extras = getIntent().getExtras();

        beschrijving = extras.getString("beschrijving");
        try {
            System.out.println("--------------" + extras.getString("date"));
            date = parseDate(extras.getString("date"));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        beschrijvingTV.setText(beschrijving);
        dateTV.setText(date);
    }

}
