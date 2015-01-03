package nl.ecci.Hamers.Events;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import nl.ecci.Hamers.R;

import java.text.ParseException;
import java.util.ArrayList;

import static nl.ecci.Hamers.MainActivity.parseDate;

public class SingleEventActivity extends ActionBarActivity {
    String title;
    String beschrijving;
    String date;
    ArrayList<String> aanwezigItems = new ArrayList<String>();
    ArrayList<String> afwezigItems = new ArrayList<String>();
    ArrayAdapter<String> aanwezigAdapter;
    ArrayAdapter<String> afwezigAadapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.single_event);

        getSupportActionBar().setHomeButtonEnabled(true);

        TextView titleTV = (TextView) findViewById(R.id.event_title);
        TextView beschrijvingTV = (TextView) findViewById(R.id.event_beschrijving);
        TextView dateTV = (TextView) findViewById(R.id.event_date);
        ListView aanwezig_list = (ListView) findViewById(R.id.event_aanwezig);
        ListView afwezig_list = (ListView) findViewById(R.id.event_afwezig);

        Bundle extras = getIntent().getExtras();

        aanwezigItems = getIntent().getStringArrayListExtra("aanwezig");
        afwezigItems = getIntent().getStringArrayListExtra("afwezig");

        aanwezigAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, aanwezigItems);
        afwezigAadapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, afwezigItems);
        aanwezig_list.setAdapter(aanwezigAdapter);
        afwezig_list.setAdapter(afwezigAadapter);

        title = extras.getString("title");
        beschrijving = extras.getString("beschrijving");
        try {
            date = parseDate(extras.getString("date"));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        titleTV.setText(title);
        beschrijvingTV.setText(beschrijving);
        dateTV.setText(date);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
