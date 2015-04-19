package nl.ecci.Hamers.Events;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import nl.ecci.Hamers.Helpers.SendPostRequest;
import nl.ecci.Hamers.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SingleEventActivity extends ActionBarActivity {
    public SwipeRefreshLayout swipeView;
    int id;
    String title;
    String beschrijving;
    String location;
    String date;
    Date dbDatum;
    ArrayList<String> aanwezigItems = new ArrayList<String>();
    ArrayList<String> afwezigItems = new ArrayList<String>();
    ArrayAdapter<String> aanwezigAdapter;
    ArrayAdapter<String> afwezigAadapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.single_event);

        getSupportActionBar().setHomeButtonEnabled(true);

        RelativeLayout rootLayout = (RelativeLayout) findViewById(R.id.single_event_root);
        LinearLayout buttonLayout = (LinearLayout) findViewById(R.id.buttonLayout);
        // Button aanwezigButton = (Button) findViewById(R.id.aanwezig_button);
        // Button afwezigButton = (Button) findViewById(R.id.afwezig_button);

        TextView titleTV = (TextView) findViewById(R.id.event_title);
        TextView beschrijvingTV = (TextView) findViewById(R.id.event_beschrijving);
        TextView dateTV = (TextView) findViewById(R.id.event_date);
        TextView locationTV = (TextView) findViewById(R.id.event_location);
        ListView aanwezig_list = (ListView) findViewById(R.id.event_aanwezig);
        ListView afwezig_list = (ListView) findViewById(R.id.event_afwezig);
        LinearLayout aanwezig_layout = (LinearLayout) findViewById(R.id.aanwezig_layout);

        Bundle extras = getIntent().getExtras();
        id = extras.getInt("id");
        aanwezigItems = getIntent().getStringArrayListExtra("aanwezig");
        afwezigItems = getIntent().getStringArrayListExtra("afwezig");

        aanwezigAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, aanwezigItems);
        afwezigAadapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, afwezigItems);
        aanwezig_list.setAdapter(aanwezigAdapter);
        afwezig_list.setAdapter(afwezigAadapter);

        // Remove headers if no user is enrolled for that category
        if (aanwezigItems.size() == 0) {
            TextView event_aanwezig_tv = (TextView) findViewById(R.id.event_aanwezig_tv);
            event_aanwezig_tv.setVisibility(View.INVISIBLE);
        } else if (afwezigItems.size() == 0) {
            TextView event_afwezig_tv = (TextView) findViewById(R.id.event_afwezig_tv);
            event_afwezig_tv.setVisibility(View.INVISIBLE);
        }

        title = extras.getString("title");
        beschrijving = extras.getString("beschrijving");
        location = extras.getString("location");

        String appDatum = null;
        try {
            // Current date
            Date today = new Date();

            // Event date
            date = extras.getString("date");
            DateFormat dbDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            DateFormat appDF = new SimpleDateFormat("EEE dd MMM yyyy HH:mm");
            dbDatum = dbDF.parse(date);
            appDatum = appDF.format(dbDatum);

            if (today.after(dbDatum)) {
                aanwezig_layout.setVisibility(View.GONE);
                rootLayout.removeView(buttonLayout);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        titleTV.setText(title);
        beschrijvingTV.setText(beschrijving);
        dateTV.setText(appDatum);

        if (!location.equals("null")) {
            locationTV.setText(location);
        } else {
            locationTV.setVisibility(View.GONE);
        }
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

    public void setAanwezig(View view) {
        postSignup(id, "true");
    }

    public void setAfwezig(View view) {
        postSignup(id, "false");
    }

    private void postSignup(int eventid, String status) {
        SendPostRequest req = new SendPostRequest(this, SendPostRequest.SIGNUPURL, PreferenceManager.getDefaultSharedPreferences(this), "signup[event_id]=" + eventid + "&signup[status]=" + status);
        req.execute();

        this.finish();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
}
